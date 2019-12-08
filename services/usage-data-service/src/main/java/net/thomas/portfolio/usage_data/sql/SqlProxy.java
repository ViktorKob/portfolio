package net.thomas.portfolio.usage_data.sql;

import static java.lang.System.currentTimeMillis;
import static java.nio.file.StandardOpenOption.READ;
import static java.sql.DriverManager.getConnection;
import static net.thomas.portfolio.usage_data.schema.tables.AccessType.ACCESS_TYPE;
import static net.thomas.portfolio.usage_data.schema.tables.User.USER;
import static net.thomas.portfolio.usage_data.schema.tables.UserAccessedDocument.USER_ACCESSED_DOCUMENT;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.slf4j.Logger;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;
import net.thomas.portfolio.usage_data.service.UsageDataServiceConfiguration.Database;

public class SqlProxy {
	private static final Logger LOG = getLogger(SqlProxy.class);

	private Database databaseConfig;

	public void setDatabase(final Database databaseConfig) {
		this.databaseConfig = databaseConfig;
	}

	public void ensurePresenceOfSchema() {
		final File databaseFolder = new File("database");
		if (!databaseFolder.exists()) {
			databaseFolder.mkdirs();
		}
		final File databaseFile = new File("database/" + databaseConfig.getDatabaseName() + ".db");
		if (!databaseFile.exists()) {
			try (Connection connection = createConnection()) {
				createAndInitializeDatabase(connection);
			} catch (final SQLException e) {
				throw new RuntimeException("Unable to ensure database is setup correctly", e);
			}
		}
	}

	private void createAndInitializeDatabase(final Connection connection) throws SQLException {
		final Path schemaPath = determineSchemaPath();
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(schemaPath, READ)))) {
			final String sql = reader.lines().collect(Collectors.joining(" "));
			execute(connection, sql);
		} catch (final NullPointerException | IOException e) {
			throw new RuntimeException("Unable to read sql schema from disk", e);
		}
	}

	private void execute(final Connection connection, final String sql) throws SQLException {
		for (final String sqlStatement : sql.split(";")) {
			try (Statement statement = connection.createStatement()) {
				statement.execute(sqlStatement);
			}
		}
	}

	private Path determineSchemaPath() {
		Path schemaPath = Paths.get("schema", databaseConfig.getDatabaseName() + "_schema.sql");
		if (!schemaPath.toFile().exists()) {
			schemaPath = Paths.get(".", "src", "main", "resources", "schema", "usage_data_schema.sql");
		}
		LOG.info("Using schema path " + schemaPath);
		return schemaPath;
	}

	public void storeUsageActivity(final DataTypeId id, final UsageActivity activity) {
		if (activity.timeOfActivity == null) {
			activity.timeOfActivity = currentTimeMillis();
		}
		try (Connection connection = createConnection()) {
			final DSLContext create = DSL.using(connection);
			create.transaction(configuration -> {
				final Integer userId = addOrGetUser(activity.user, configuration);
				final Integer accessTypeId = addOrGetAccessType(activity.type, configuration);
				DSL.using(configuration)
						.insertInto(USER_ACCESSED_DOCUMENT, USER_ACCESSED_DOCUMENT.DOCUMENT_TYPE, USER_ACCESSED_DOCUMENT.DOCUMENT_UID,
								USER_ACCESSED_DOCUMENT.USER_ID, USER_ACCESSED_DOCUMENT.ACCESS_TYPE_ID, USER_ACCESSED_DOCUMENT.TIME_OF_ACCESS)
						.values(id.type, id.uid, userId, accessTypeId, new Timestamp(activity.timeOfActivity))
						.execute();
			});
		} catch (final SQLException cause) {
			throw new UsageDataAccessException("Unable to fetch activity from database", cause);
		}
	}

	private Integer addOrGetUser(final String username, final Configuration configuration) {
		DSL.using(configuration).insertInto(USER, USER.NAME).values(username).onDuplicateKeyIgnore().execute();
		return DSL.using(configuration).select(USER.ID).from(USER).where(USER.NAME.eq(username)).fetchOne().get(USER.ID);
	}

	private Integer addOrGetAccessType(final UsageActivityType accessType, final Configuration configuration) {
		DSL.using(configuration).insertInto(ACCESS_TYPE, ACCESS_TYPE.NAME).values(accessType.name()).onDuplicateKeyIgnore().execute();
		return DSL.using(configuration).select(ACCESS_TYPE.ID).from(ACCESS_TYPE).where(ACCESS_TYPE.NAME.eq(accessType.name())).fetchOne().get(ACCESS_TYPE.ID);
	}

	public UsageActivities fetchUsageActivities(final DataTypeId id, final Bounds bounds) {
		try (Connection connection = createConnection()) {
			final DSLContext create = DSL.using(connection);
			final Result<Record3<String, String, Timestamp>> result = create.select(USER.NAME, ACCESS_TYPE.NAME, USER_ACCESSED_DOCUMENT.TIME_OF_ACCESS)
					.from(USER_ACCESSED_DOCUMENT)
					.join(USER)
					.on(USER_ACCESSED_DOCUMENT.USER_ID.eq(USER.ID))
					.join(ACCESS_TYPE)
					.on(USER_ACCESSED_DOCUMENT.ACCESS_TYPE_ID.eq(ACCESS_TYPE.ID))
					.where(USER_ACCESSED_DOCUMENT.DOCUMENT_TYPE.eq(id.type))
					.and(USER_ACCESSED_DOCUMENT.DOCUMENT_UID.eq(id.uid))
					.and(USER_ACCESSED_DOCUMENT.TIME_OF_ACCESS.between(new Timestamp(bounds.after), new Timestamp(bounds.before)))
					.orderBy(USER_ACCESSED_DOCUMENT.TIME_OF_ACCESS.desc())
					.offset(bounds.offset)
					.limit(bounds.limit)
					.fetch();
			final LinkedList<UsageActivity> activities = new LinkedList<>();
			for (final Record3<String, String, Timestamp> activity : result) {
				final String username = activity.get(USER.NAME);
				final UsageActivityType activityType = UsageActivityType.valueOf(activity.get(ACCESS_TYPE.NAME));
				final long timeOfActivity = activity.get(USER_ACCESSED_DOCUMENT.TIME_OF_ACCESS).getTime();
				activities.add(new UsageActivity(username, activityType, timeOfActivity));
			}
			return new UsageActivities(activities);
		} catch (final SQLException cause) {
			throw new UsageDataAccessException("Unable to fetch activity from database", cause);
		}
	}

	private Connection createConnection() throws SQLException {
		return getConnection(databaseConfig.getConnectionString());
	}

	public static class UsageDataAccessException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public UsageDataAccessException(String message) {
			super(message);
		}

		public UsageDataAccessException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
