package net.thomas.portfolio.nexus.service.test_utils;

import static java.lang.System.currentTimeMillis;
import static java.security.MessageDigest.getInstance;
import static java.util.Arrays.asList;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.decimal;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.geoLocation;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.integer;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.strings;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.timestamp;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataTypeArray;
import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.ANALYSED_DOCUMENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.FieldsBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter.Iso8601DateConverter;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;

public class GraphQlTestUtil {
	public static final String SOME_STRING = "some string";
	public static final Integer SOME_INTEGER = 1;
	public static final Long SOME_LONG_INTEGER = Long.MAX_VALUE;
	public static final Double SOME_DECIMAL = 3.14;
	public static final GeoLocation SOME_GEO_LOCATION = new GeoLocation(1.2, -1.2);
	public static final Integer SOME_OFFSET = 2;
	public static final Integer SOME_LIMIT = 4;
	public static final Long SOME_TIMESTAMP_VALUE = currentTimeMillis() / 1000 * 1000;
	public static final Timestamp SOME_TIMESTAMP = new Timestamp(SOME_TIMESTAMP_VALUE);
	public static final Iso8601DateConverter DATE_CONVERTER = new DateConverter.Iso8601DateConverter();
	public static final String SOME_FORMATTED_TIMESTAMP = DATE_CONVERTER.format(SOME_TIMESTAMP_VALUE);
	public static final String SOME_FORMATTED_DATE_ONLY_TIMESTAMP = DATE_CONVERTER.formatDate(SOME_TIMESTAMP_VALUE);
	public static final String SOME_INVALID_FORMATTED_TIMESTAMP = "Not really a formatted timestamp";
	public static final String SOME_HEADLINE = "headline";
	public static final String SOME_HTML = "html";
	public static final String SOME_USER = "SomeUser";
	public static final String SOME_SIMPLE_REP = "some simple rep";
	public static final String SOME_MISSING_UID = "AABBCC0011";
	public static final String SIMPLE_TYPE = "SimpleType";
	public static final String RECURSIVE_TYPE = "RecursiveType";
	public static final String COMPLEX_TYPE = "ComplexType";
	public static final String NON_SIMPLE_REP_TYPE = "NonSimpleRepType";
	public static final String RAW_DATA_TYPE = "ContainerType";
	public static final String DOCUMENT_TYPE = "DocumentType";
	public static final Collection<String> DATA_TYPES = asList(SIMPLE_TYPE, RECURSIVE_TYPE, COMPLEX_TYPE, NON_SIMPLE_REP_TYPE, RAW_DATA_TYPE, DOCUMENT_TYPE);
	public static final Collection<String> DOCUMENT_TYPES = asList(DOCUMENT_TYPE);
	public static final Collection<String> SELECTOR_TYPES = asList(SIMPLE_TYPE, RECURSIVE_TYPE, COMPLEX_TYPE, NON_SIMPLE_REP_TYPE);
	public static final DocumentInfos SOME_DOCUMENT_INFOS = new DocumentInfos(asList());
	public static final UsageActivityType SOME_USAGE_ACTIVITY_TYPE = ANALYSED_DOCUMENT;
	public static final UsageActivity SOME_USAGE_ACTIVITY = new UsageActivity(SOME_USER, SOME_USAGE_ACTIVITY_TYPE, SOME_TIMESTAMP_VALUE);
	public static final UsageActivities SOME_USAGE_ACTIVITIES = new UsageActivities();
	public static Map<String, DataTypeId> EXAMPLE_IDS = new HashMap<>();
	public static Map<String, Fields> TYPE_FIELDS = new HashMap<>();
	private static int idSeed = 0;

	public static void setUpHbaseAdaptorMock(final HbaseIndexModelAdaptor adaptor) {
		when(adaptor.getDataTypes()).thenReturn(DATA_TYPES);
		setUpFields(adaptor);
		setUpDocuments(adaptor);
		EXAMPLE_IDS.put(RAW_DATA_TYPE, EXAMPLE_ID(RAW_DATA_TYPE));
		setUpSelectors(adaptor);
		setUpEntities(adaptor);
	}

	private static void setUpFields(final HbaseIndexModelAdaptor adaptor) {
		setFieldsForType(DOCUMENT_TYPE, timestamp("timestamp"));
		setFieldsForType(SIMPLE_TYPE, string("string"), strings("strings"), integer("integer"), integer("long"), decimal("decimal"), timestamp("timestamp"),
				geoLocation("geoLocation"));
		setFieldsForType(COMPLEX_TYPE, dataType("simpleType", SIMPLE_TYPE), dataType("missingSimpleType", SIMPLE_TYPE),
				dataTypeArray("arraySimpleType", SIMPLE_TYPE), dataTypeArray("missingArrayType", SIMPLE_TYPE));
		for (final String dataType : DATA_TYPES) {
			if (TYPE_FIELDS.containsKey(dataType)) {
				when(adaptor.getFieldsForDataType(eq(dataType))).thenReturn(TYPE_FIELDS.get(dataType));
			} else {
				when(adaptor.getFieldsForDataType(eq(dataType))).thenReturn(new Fields());
			}
		}
	}

	private static void setFieldsForType(final String type, final Field... fields) {
		final FieldsBuilder builder = new FieldsBuilder();
		for (final Field field : fields) {
			builder.add(field);
		}
		TYPE_FIELDS.put(type, builder.build());
	}

	private static void setUpDocuments(final HbaseIndexModelAdaptor adaptor) {
		when(adaptor.getDocumentTypes()).thenReturn(DOCUMENT_TYPES);
		final List<DocumentInfo> documentInfos = new LinkedList<>();
		for (final String type : DOCUMENT_TYPES) {
			when(adaptor.isDocument(type)).thenReturn(true);
			EXAMPLE_IDS.put(type, EXAMPLE_ID(type));
			documentInfos.add(new DocumentInfo(EXAMPLE_IDS.get(type), new Timestamp(2l), new Timestamp(4l)));
		}
		SOME_DOCUMENT_INFOS.setInfos(documentInfos);
	}

	private static void setUpSelectors(final HbaseIndexModelAdaptor adaptor) {
		when(adaptor.getSelectorTypes()).thenReturn(SELECTOR_TYPES);
		for (final String type : SELECTOR_TYPES) {
			when(adaptor.isSelector(type)).thenReturn(true);
			if (!NON_SIMPLE_REP_TYPE.equals(type)) {
				when(adaptor.isSimpleRepresentable(type)).thenReturn(true);
			} else {
				when(adaptor.isSimpleRepresentable(type)).thenReturn(false);
			}
			EXAMPLE_IDS.put(type, EXAMPLE_ID(type));
			when(adaptor.getFromSimpleRep(eq(type), eq(SOME_SIMPLE_REP))).thenReturn(new Selector(EXAMPLE_IDS.get(type)));
			when(adaptor.lookupSelectorInInvertedIndex(any())).thenReturn(new DocumentInfos());
		}
	}

	public static final DataTypeId EXAMPLE_ID(final String type) {
		try {
			final MessageDigest hasher = getInstance("MD5");
			hasher.update(type.getBytes());
			hasher.update(String.valueOf(idSeed++).getBytes());
			final String uid = printHexBinary(hasher.digest());
			return new DataTypeId(type, uid);
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to calculate hash", e);
		}
	}

	private static void setUpEntities(final HbaseIndexModelAdaptor adaptor) {
		setUpDocument(adaptor);
		final DataType simpleTypeEntity = setUpSimpleEntity(adaptor);
		setUpComplexEntity(adaptor, simpleTypeEntity);
	}

	private static void setUpDocument(final HbaseIndexModelAdaptor adaptor) {
		final DataTypeId id = EXAMPLE_IDS.get(DOCUMENT_TYPE);
		final Document entity = new Document(id);
		entity.setTimeOfEvent(SOME_TIMESTAMP);
		entity.setTimeOfInterception(SOME_TIMESTAMP);
		entity.put("timestamp", 0L);
		when(adaptor.getDataType(id)).thenReturn(entity);
	}

	private static DataType setUpSimpleEntity(final HbaseIndexModelAdaptor adaptor) {
		final DataTypeId simpleTypeId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		final DataType simpleTypeEntity = new Selector(simpleTypeId);
		simpleTypeEntity.put("string", SOME_STRING);
		simpleTypeEntity.put("strings", asList(SOME_STRING));
		simpleTypeEntity.put("integer", SOME_INTEGER);
		simpleTypeEntity.put("long", SOME_LONG_INTEGER);
		simpleTypeEntity.put("decimal", SOME_DECIMAL);
		simpleTypeEntity.put("geoLocation", SOME_GEO_LOCATION);
		simpleTypeEntity.put("timestamp", SOME_TIMESTAMP);
		when(adaptor.getDataType(simpleTypeId)).thenReturn(simpleTypeEntity);
		return simpleTypeEntity;
	}

	private static void setUpComplexEntity(final HbaseIndexModelAdaptor adaptor, final DataType simpleTypeEntity) {
		final DataTypeId complexTypeId = EXAMPLE_IDS.get(COMPLEX_TYPE);
		final DataType complexTypeEntity = new Selector(complexTypeId);
		complexTypeEntity.put("simpleType", simpleTypeEntity);
		complexTypeEntity.put("arraySimpleType", asList(simpleTypeEntity));
		when(adaptor.getDataType(complexTypeId)).thenReturn(complexTypeEntity);
	}
}