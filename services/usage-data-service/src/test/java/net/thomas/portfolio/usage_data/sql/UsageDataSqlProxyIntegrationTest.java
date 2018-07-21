package net.thomas.portfolio.usage_data.sql;

import static java.lang.Integer.MAX_VALUE;
import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.READ_DOCUMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.usage_data.service.UsageDataServiceConfiguration.Database;

public class UsageDataSqlProxyIntegrationTest {
	private static final String DOCUMENT_TYPE = "TEST_TYPE";
	private static final String UID = "00000000";
	private static final String USER = "TEST_USER";
	private static final DataTypeId DOCUMENT_ID = new DataTypeId(DOCUMENT_TYPE, UID);
	private static final Long TIME_OF_ACTIVITY = nowInMillisecondsWithSecondsPrecision();
	private static final long AROUND_A_THOUSAND_YEARS_AGO = -1000l * 60 * 60 * 24 * 365 * 1000;
	private static final long AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW = 1000l * 60 * 60 * 24 * 365 * 8000;
	private static final Bounds EVERYTHING = new Bounds(0, MAX_VALUE, AROUND_A_THOUSAND_YEARS_AGO, AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW);

	private static long nowInMillisecondsWithSecondsPrecision() {
		return System.currentTimeMillis() / 1000 * 1000;
	}

	private SqlProxy sqlProxy;

	@Before
	public void setup() throws SQLException {
		final Database databaseConfig = createTestDatabaseConfig();
		wipeOldTests(databaseConfig);
		sqlProxy = new SqlProxy();
		sqlProxy.setDatabase(databaseConfig);
		sqlProxy.ensurePresenceOfSchema();
	}

	@Test
	public void shouldContainEntryAfterWrite() {
		final UsageActivity activity = new UsageActivity(USER, READ_DOCUMENT, TIME_OF_ACTIVITY);
		sqlProxy.storeUsageActivity(DOCUMENT_ID, activity);
		final UsageActivities activities = sqlProxy.fetchUsageActivities(DOCUMENT_ID, EVERYTHING);
		assertEquals(1, activities.size());
		assertEquals(activity, activities.get(0));
	}

	@Test
	public void shouldAddTimestampWhenMissing() {
		final UsageActivity activity = new UsageActivity(USER, READ_DOCUMENT, null);
		sqlProxy.storeUsageActivity(DOCUMENT_ID, activity);
		final UsageActivities activities = sqlProxy.fetchUsageActivities(DOCUMENT_ID, EVERYTHING);
		assertNotNull(activities.get(0).timeOfActivity);
	}

	@After
	public void teardownDatabase() {
		// The tear-down happens before initialization, to enable debugging on fail and allow the next run to succeed regardless.
	}

	private Database createTestDatabaseConfig() {
		final Database databaseConfig = new Database();
		databaseConfig.setHost("localhost");
		databaseConfig.setPort(3306);
		databaseConfig.setUser("root");
		databaseConfig.setPassword("computer");
		databaseConfig.setSchema("usage_data_test");
		return databaseConfig;
	}

	private void wipeOldTests(Database databaseConfig) throws SQLException {
		try (final Connection connection = DriverManager.getConnection(databaseConfig.getConnectionString(false), databaseConfig.getUser(),
				databaseConfig.getPassword())) {
			try (Statement statement = connection.createStatement()) {
				statement.execute("DELETE FROM usage_data.user_accessed_document WHERE document_type = '" + DOCUMENT_TYPE + "'");
				statement.execute("DELETE FROM usage_data.user WHERE name ='" + USER + "'");
			}
		}
	}
}