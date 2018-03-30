package net.sample;

import static net.model.meta_data.StatisticsPeriod.INFINITY;
import static net.sample.SampleModel.INDEXABLES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.model.DataType;
import net.model.meta_data.Indexable;
import net.model.meta_data.StatisticsPeriod;

@RunWith(Parameterized.class)
public class SampleDataSetGeneratorSelectorSanityTest {
	@Parameters
	public static Collection<String> selectorTypes() {
		return Arrays.asList("Localname", "DisplayedName", "Domain", "EmailAddress", "Pstn", "Imsi");
	}

	@BeforeClass
	public static void initializeSampleData() {
		samples = SampleDataSetGenerator.getSampleDataSet();
	}

	private static SampleStorage samples;
	private final String selectorType;

	public SampleDataSetGeneratorSelectorSanityTest(String selectorType) {
		this.selectorType = selectorType;
	}

	@Test
	public void shouldContainSelectors() {
		final Collection<DataType> selectors = samples.getAll(selectorType);
		System.out.println("Found " + selectors.size() + " " + selectorType);
		assertTrue(selectors.size() > 0);
	}

	@Test
	public void statisticsShouldMatchData() {
		final Collection<DataType> selectors = samples.getAll(selectorType);
		for (final DataType selector : selectors) {
			final long expectedCount = sumOverStatistics(selector);
			final Collection<DataType> documents = fetchDocuments(selector);
			assertEquals(expectedCount, documents.size());
		}
	}

	private long sumOverStatistics(final DataType selector) {
		long expectedCount = 0;
		final Map<StatisticsPeriod, Long> statistics = samples.getStatistics(selector);
		if (statistics != null) {
			expectedCount = statistics.get(INFINITY);
		}
		return expectedCount;
	}

	private Collection<DataType> fetchDocuments(final DataType selector) {
		final Collection<Indexable> indexables = INDEXABLES.get(selectorType);
		final Collection<DataType> documents = new HashSet<>();
		for (final Indexable indexable : indexables) {
			documents.addAll(samples.invertedIndexLookup(selector, indexable));
		}
		return documents;
	}
}