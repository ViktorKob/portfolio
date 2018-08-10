package net.thomas.portfolio.usage_data.sql;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.Sources.fromFile;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static java.lang.Integer.MAX_VALUE;
import static java.nio.file.Paths.get;
import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.READ_DOCUMENT;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.usage_data.service.UsageDataServiceConfiguration.Database;

public class UsageDataSqlProxyEmbeddedTest {
	private SqlProxy sqlProxy;

	@BeforeClass
	public static void setUpDatabaseForTest() throws IOException {
		MysqldConfig config = aMysqldConfig(Version.v5_5_40).withPort(3306).withUser(USER_NAME, PASSWORD).build();
		mysqld = anEmbeddedMysql(config).addSchema("usage_data", fromFile(SCHEMA_PATH.toFile())).start();
	}

	@Before
	public void setup() throws SQLException {
//		final Database databaseConfig = createTestDatabaseConfig();
//		wipeOldTests(databaseConfig);
//		sqlProxy = new SqlProxy();
//		sqlProxy.setDatabase(databaseConfig);
	}

	@Test
	public void shouldContainEntryAfterWrite() {
		final UsageActivity activity = new UsageActivity(USER, READ_DOCUMENT, TIME_OF_ACTIVITY);
//		sqlProxy.storeUsageActivity(DOCUMENT_ID, activity);
//		final UsageActivities activities = sqlProxy.fetchUsageActivities(DOCUMENT_ID, EVERYTHING);
//		assertEquals(1, activities.size());
//		assertEquals(activity, activities.get(0));
	}

	@Test
	public void shouldAddTimestampWhenMissing() {
//		final UsageActivity activity = new UsageActivity(USER, READ_DOCUMENT, null);
//		sqlProxy.storeUsageActivity(DOCUMENT_ID, activity);
//		final UsageActivities activities = sqlProxy.fetchUsageActivities(DOCUMENT_ID, EVERYTHING);
//		assertNotNull(activities.get(0).timeOfActivity);
	}

	@After
	public void tearDownDatabase() {
		// The tear-down happens before initialization, to enable debugging on fail and
		// allow the next run to succeed regardless.
	}

	@AfterClass
	public static void shutDownDataBase() {
//		mysqld.stop();
	}

	private static final String USER_NAME = "test";
	private static final String PASSWORD = "password";
	private static final Path SCHEMA_PATH = get(".", "src", "main", "resources", "schema", "usage_data_schema.sql");
	private static final String DOCUMENT_TYPE = "TEST_TYPE";
	private static final String UID = "00000000";
	private static final String USER = "TEST_USER";
	private static final DataTypeId DOCUMENT_ID = new DataTypeId(DOCUMENT_TYPE, UID);
	private static final Long TIME_OF_ACTIVITY = nowInMillisecondsWithSecondsPrecision();
	private static final long AROUND_A_THOUSAND_YEARS_AGO = -1000l * 60 * 60 * 24 * 365 * 1000;
	private static final long AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW = 1000l * 60 * 60 * 24 * 365 * 8000;
	private static final Bounds EVERYTHING = new Bounds(0, MAX_VALUE, AROUND_A_THOUSAND_YEARS_AGO,
			AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW);
	private static EmbeddedMysql mysqld;

	private static long nowInMillisecondsWithSecondsPrecision() {
		return System.currentTimeMillis() / 1000 * 1000;
	}

	private Database createTestDatabaseConfig() {
		final Database databaseConfig = new Database();
		databaseConfig.setHost("localhost");
		databaseConfig.setPort(3306);
		databaseConfig.setUser(USER_NAME);
		databaseConfig.setPassword(PASSWORD);
		databaseConfig.setSchema("usage_data");
		return databaseConfig;
	}

	private void wipeOldTests(Database databaseConfig) throws SQLException {
		try (final Connection connection = DriverManager.getConnection(databaseConfig.getConnectionString(false),
				databaseConfig.getUser(), databaseConfig.getPassword())) {
			try (Statement statement = connection.createStatement()) {
				statement.execute(
						"DELETE FROM usage_data.user_accessed_document WHERE document_type = '" + DOCUMENT_TYPE + "'");
				statement.execute("DELETE FROM usage_data.user WHERE name ='" + USER + "'");
			}
		}
	}
}