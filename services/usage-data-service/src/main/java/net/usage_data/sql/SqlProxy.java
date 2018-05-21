package net.usage_data.sql;

import static java.nio.file.StandardOpenOption.READ;
import static net.usage_data.schema.tables.AccessType.ACCESS_TYPE;
import static net.usage_data.schema.tables.User.USER;
import static net.usage_data.schema.tables.UserAccessedDocument.USER_ACCESSED_DOCUMENT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import net.usage_data.UsageActivityItem;
import net.usage_data.UsageActivityType;
import net.usage_data.service.UsageDataServiceConfiguration.Database;

public class SqlProxy {
	private static final boolean WITHOUT_SCHEMA = false;
	private static final boolean WITH_SCHEMA = true;
	private final Database databaseConfig;

	public SqlProxy(Database databaseConfig) {
		this.databaseConfig = databaseConfig;
	}

	public void ensurePresenceOfSchema() {
		try (Connection connection = createConnection(WITHOUT_SCHEMA)) {
			final boolean databaseIsPresent = checkForDatabase(connection);
			if (!databaseIsPresent) {
				System.out.println("Schema missing, creating from file");
				createAndInitializeDatabase(connection);
			} else {
				System.out.println("Schema present, skipping creation");
			}
		} catch (final SQLException e) {
			throw new RuntimeException("Unable to ensure database is setup correctly", e);
		}
	}

	private boolean checkForDatabase(Connection connection) throws SQLException {
		boolean databaseIsPresent = false;
		try (Statement checkForDatabase = connection.createStatement()) {
			checkForDatabase.execute("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + databaseConfig.getSchema() + "'");
			final ResultSet results = checkForDatabase.getResultSet();
			databaseIsPresent = results.first() && databaseConfig.getSchema()
				.equals(results.getString("SCHEMA_NAME"));
		}
		return databaseIsPresent;
	}

	private void createAndInitializeDatabase(Connection connection) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE DATABASE " + databaseConfig.getSchema());
			statement.execute("USE " + databaseConfig.getSchema());
			final Path schemaPath = Paths.get("schema", databaseConfig.getSchema() + "_schema.sql");
			try (final BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(schemaPath, READ)))) {
				// try (final BufferedReader reader = new BufferedReader(
				// new InputStreamReader(SqlProxy.class.getResourceAsStream("schema/" + databaseConfig.getSchema() + "_schema.sql")))) {
				final String sql = reader.lines()
					.collect(Collectors.joining(" "));
				for (final String sqlStatement : sql.split(";")) {
					statement.execute(sqlStatement);
				}
			} catch (final NullPointerException | IOException e) {
				throw new RuntimeException("Unable to read sql schema from disk", e);
			}
		}
	}

	public void storeUsageActivity(String uid, String type, String username, UsageActivityType accessType, Long timeOfActivity) {
		try (Connection connection = createConnection(WITH_SCHEMA)) {
			final DSLContext create = DSL.using(connection);
			create.transaction(configuration -> {
				final UInteger userId = addOrGetUser(username, configuration);
				final UInteger accessTypeId = addOrGetAccessType(accessType, configuration);
				DSL.using(configuration)
					.insertInto(USER_ACCESSED_DOCUMENT, USER_ACCESSED_DOCUMENT.DOCUMENT_TYPE, USER_ACCESSED_DOCUMENT.DOCUMENT_UID,
							USER_ACCESSED_DOCUMENT.USER_ID, USER_ACCESSED_DOCUMENT.ACCESS_TYPE_ID, USER_ACCESSED_DOCUMENT.TIME_OF_ACCESS)
					.values(type, uid, userId, accessTypeId, new Timestamp(timeOfActivity))
					.execute();
			});
		} catch (final SQLException e) {
			throw new RuntimeException("Unable to fetch activity from database", e);
		}
	}

	private UInteger addOrGetUser(String username, Configuration configuration) {
		DSL.using(configuration)
			.insertInto(USER, USER.NAME)
			.values(username)
			.onDuplicateKeyIgnore()
			.returning(USER.ID)
			.fetchOne();
		return DSL.using(configuration)
			.select(USER.ID)
			.from(USER)
			.where(USER.NAME.eq(username))
			.fetchOne()
			.get(USER.ID);
	}

	private UInteger addOrGetAccessType(UsageActivityType accessType, Configuration configuration) {
		DSL.using(configuration)
			.insertInto(ACCESS_TYPE, ACCESS_TYPE.NAME)
			.values(accessType.name())
			.onDuplicateKeyIgnore()
			.returning(ACCESS_TYPE.ID)
			.fetchOne();
		return DSL.using(configuration)
			.select(ACCESS_TYPE.ID)
			.from(ACCESS_TYPE)
			.where(ACCESS_TYPE.NAME.eq(accessType.name()))
			.fetchOne()
			.get(ACCESS_TYPE.ID);
	}

	public List<UsageActivityItem> fetchUsageActivities(String uid, String type, int offset, int limit) {
		try (Connection connection = createConnection(WITH_SCHEMA)) {
			final DSLContext create = DSL.using(connection);
			final Result<Record3<String, String, Timestamp>> result = create.select(USER.NAME, ACCESS_TYPE.NAME, USER_ACCESSED_DOCUMENT.TIME_OF_ACCESS)
				.from(USER_ACCESSED_DOCUMENT)
				.join(USER)
				.on(USER_ACCESSED_DOCUMENT.USER_ID.eq(USER.ID))
				.join(ACCESS_TYPE)
				.on(USER_ACCESSED_DOCUMENT.ACCESS_TYPE_ID.eq(ACCESS_TYPE.ID))
				.where(USER_ACCESSED_DOCUMENT.DOCUMENT_TYPE.eq(type))
				.and(USER_ACCESSED_DOCUMENT.DOCUMENT_UID.eq(uid))
				.offset(offset)
				.limit(limit)
				.fetch();
			final LinkedList<UsageActivityItem> activities = new LinkedList<>();
			for (final Record3<String, String, Timestamp> activity : result) {
				activities
					.add(new UsageActivityItem(activity.get(USER.NAME), activity.get(ACCESS_TYPE.NAME), activity.get(USER_ACCESSED_DOCUMENT.TIME_OF_ACCESS)));
			}

			return activities;
		} catch (final SQLException e) {
			throw new RuntimeException("Unable to fetch activity from database", e);
		}
	}

	private Connection createConnection(boolean withSchema) throws SQLException {
		return DriverManager.getConnection(databaseConfig.getConnectionString(withSchema), databaseConfig.getUser(), databaseConfig.getPassword());
	}
}
