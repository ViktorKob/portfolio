package net.thomas.portfolio.usage_data.sql;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.System.currentTimeMillis;
import static java.sql.DriverManager.getConnection;
import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.READ_DOCUMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.usage_data.service.UsageDataServiceConfiguration.Database;

public class UsageDataSqlProxyIntegrationTest {
	private static SqlProxy sqlProxy;

	@BeforeClass
	public static void setUpDatabase() {
		removeSchemaIfPresent();
		sqlProxy = new SqlProxy();
		sqlProxy.setDatabase(DATABASE_CONFIG);
		sqlProxy.ensurePresenceOfSchema();
	}

	private static void removeSchemaIfPresent() {
		final File databaseFolder = new File("database");
		if (databaseFolder.exists()) {
			for (final String fileName : databaseFolder.list()) {
				final File currentFile = new File(databaseFolder.getPath(), fileName);
				currentFile.delete();
			}
		}
	}

	@Before
	public void setUp() throws SQLException {
		wipeOldTests(DATABASE_CONFIG);
	}

	@Test
	public void shouldContainEntryAfterWrite() {
		final UsageActivity activity = new UsageActivity(USER, READ_DOCUMENT, TIME_OF_ACTIVITY);
		sqlProxy.storeUsageActivity(DOCUMENT_ID, activity);
		final UsageActivities activities = sqlProxy.fetchUsageActivities(DOCUMENT_ID, EVERYTHING);
		assertTrue(activities.hasData());
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
	public void tearDownDatabase() {
		// The tear-down happens before initialization, to enable debugging on fail and
		// allow the next run to succeed regardless.
	}

	private void wipeOldTests(Database databaseConfig) throws SQLException {
		try (final Connection connection = getConnection(databaseConfig.getConnectionString())) {
			try (Statement statement = connection.createStatement()) {
				statement.execute("DELETE FROM user_accessed_document WHERE document_type = '" + DOCUMENT_TYPE + "'");
				statement.execute("DELETE FROM user WHERE name ='" + USER + "'");
			}
		}
	}

	private static final String SCHEMA = "usage_data";
	private static final String DOCUMENT_TYPE = "TEST_TYPE";
	private static final String UID = "00000000";
	private static final String USER = "TEST_USER";
	private static final DataTypeId DOCUMENT_ID = new DataTypeId(DOCUMENT_TYPE, UID);
	private static final Long TIME_OF_ACTIVITY = nowInMillisecondsWithSecondsPrecision();
	private static final long AROUND_A_THOUSAND_YEARS_AGO = -1000l * 60 * 60 * 24 * 365 * 1000;
	private static final long AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW = 1000l * 60 * 60 * 24 * 365 * 8000;
	private static final Bounds EVERYTHING = new Bounds(0, MAX_VALUE, AROUND_A_THOUSAND_YEARS_AGO, AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW);
	private static final Database DATABASE_CONFIG = createTestDatabaseConfig();

	private static long nowInMillisecondsWithSecondsPrecision() {
		return currentTimeMillis() / 1000 * 1000;
	}

	private static Database createTestDatabaseConfig() {
		final File databaseHomeDir = new File("database");
		if (!databaseHomeDir.exists()) {
			databaseHomeDir.mkdirs();
		}
		final Database databaseConfig = new Database();
		databaseConfig.setDatabaseName(SCHEMA);
		return databaseConfig;
	}
}