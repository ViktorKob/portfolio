package net.sample;

import static net.sample.SampleModel.DATA_TYPE_FIELDS;
import static net.sample.SampleModel.INDEXABLES;
import static net.sample.SampleModel.SELECTOR_TYPES;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.model.DataType;
import net.model.data.Field;
import net.model.data.ReferenceField;
import net.model.meta_data.Indexable;
import net.model.types.Document;

@RunWith(Parameterized.class)
public class SampleDataSetGeneratorDocumentSanityTest {
	@Parameters
	public static Collection<String> selectorTypes() {
		return Arrays.asList("Email", "Sms", "Voice");
	}

	private static SampleStorage samples;
	private final String documentType;

	@BeforeClass
	public static void initializeSampleData() {
		samples = SampleDataSetGenerator.getSampleDataSet();
	}

	public SampleDataSetGeneratorDocumentSanityTest(String documentType) {
		this.documentType = documentType;
	}

	@Test
	public void shouldContainDocuments() {
		final Collection<DataType> documents = samples.getAll(documentType);
		System.out.println("Found " + documents.size() + " " + documentType);
		assertTrue(documents.size() > 0);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldBeAbleToReverseLookupSelectors() {
		final Collection<DataType> documents = samples.getAll(documentType);
		for (final DataType document : documents) {
			for (final Entry<String, Field> documentField : DATA_TYPE_FIELDS.get(documentType).entrySet()) {
				if (documentField.getValue() instanceof ReferenceField) {
					if (documentField.getValue().isArray()) {
						assertCanLookupArraySubFields(document, documentField.getKey(), (ReferenceField) documentField.getValue(),
								(List<DataType>) document.get(documentField.getKey()));
					} else {
						assertCanLookupSubFields(document, documentField.getKey(), (ReferenceField) documentField.getValue(),
								(DataType) document.get(documentField.getKey()));
					}
				}
			}
		}
	}

	private void assertCanLookupArraySubFields(final DataType document, String path, ReferenceField field, List<DataType> fieldValues) {
		for (final DataType fieldValue : fieldValues) {
			assertCanLookupSubFields(document, path, field, fieldValue);
		}
	}

	private void assertCanLookupSubFields(final DataType document, String path, ReferenceField field, DataType fieldValue) {
		final String referenceType = field.getType();
		if (SELECTOR_TYPES.contains(referenceType)) {
			for (final Indexable indexable : INDEXABLES.get(referenceType)) {
				if (indexable.path.equals(path)) {
					final List<Document> documents = samples.invertedIndexLookup(fieldValue, indexable);
					assertTrue(documents.contains(document));
				}
			}
		}
		for (final Entry<String, Field> subFieldEntry : DATA_TYPE_FIELDS.get(field.getType()).entrySet()) {
			if (subFieldEntry.getValue() instanceof ReferenceField) {
				final DataType subFieldValue = (DataType) fieldValue.get(subFieldEntry.getKey());
				if (subFieldValue != null) {
					assertCanLookupSubFields(document, path, (ReferenceField) subFieldEntry.getValue(), subFieldValue);
				}
			}
		}
	}
}
