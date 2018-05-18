package net.usage_data.sql;

import static net.usage_data.schema.tables.AccessType.ACCESS_TYPE;
import static net.usage_data.schema.tables.User.USER;
import static net.usage_data.schema.tables.UserAccessedDocument.USER_ACCESSED_DOCUMENT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

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
	private final Database databaseConfig;

	public SqlProxy(Database databaseConfig) {
		this.databaseConfig = databaseConfig;
	}

	public void storeUsageActivity(String uid, String type, String username, UsageActivityType accessType, Long timeOfActivity) {
		try (Connection connection = createConnection()) {
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
		try (Connection connection = createConnection()) {
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

	private Connection createConnection() throws SQLException {
		return DriverManager.getConnection(databaseConfig.getConnectionString(), databaseConfig.getUser(), databaseConfig.getPassword());
	}
}
