package net.thomas.portfolio.nexus.service.test_utils;

import static java.util.Arrays.asList;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.decimal;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.geoLocation;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.integer;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.strings;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.timestamp;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataTypeArray;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.hbase_index.schema.Hasher;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.FieldsBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class GraphQlTestModel {
	public static final String SOME_STRING = "some string";
	public static final Integer SOME_INTEGER = 1;
	public static final Long SOME_LONG_INTEGER = Long.MAX_VALUE;
	public static final Double SOME_DECIMAL = 3.14;
	public static final GeoLocation SOME_GEO_LOCATION = new GeoLocation(1.2, -1.2);
	public static final Timestamp SOME_TIMESTAMP = new Timestamp(3l);
	public static final String SOME_SIMPLE_REP = "some simple rep";
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
	public static Map<String, DataTypeId> EXAMPLE_IDS = new HashMap<>();
	public static Map<String, Fields> TYPE_FIELDS = new HashMap<>();
	private static int idSeed = 0;

	public static void setUpHbaseAdaptorMock(HbaseIndexModelAdaptor adaptor) {
		when(adaptor.getDataTypes()).thenReturn(DATA_TYPES);
		setUpFields(adaptor);
		setUpDocuments(adaptor);
		EXAMPLE_IDS.put(RAW_DATA_TYPE, EXAMPLE_ID(RAW_DATA_TYPE));
		setUpSelectors(adaptor);
		setUpEntities(adaptor);
	}

	private static void setUpFields(HbaseIndexModelAdaptor adaptor) {
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

	private static void setUpDocuments(HbaseIndexModelAdaptor adaptor) {
		when(adaptor.getDocumentTypes()).thenReturn(DOCUMENT_TYPES);
		final List<DocumentInfo> documentInfos = new LinkedList<>();
		for (final String type : DOCUMENT_TYPES) {
			when(adaptor.isDocument(type)).thenReturn(true);
			EXAMPLE_IDS.put(type, EXAMPLE_ID(type));
			documentInfos.add(new DocumentInfo(EXAMPLE_IDS.get(type), new Timestamp(2l), new Timestamp(4l)));
		}
		SOME_DOCUMENT_INFOS.setInfos(documentInfos);
	}

	private static void setUpSelectors(HbaseIndexModelAdaptor adaptor) {
		when(adaptor.getSelectorTypes()).thenReturn(SELECTOR_TYPES);
		for (final String type : SELECTOR_TYPES) {
			when(adaptor.isSelector(type)).thenReturn(true);
			if (!NON_SIMPLE_REP_TYPE.equals(type)) {
				when(adaptor.isSimpleRepresentable(type)).thenReturn(true);
			} else {
				when(adaptor.isSimpleRepresentable(type)).thenReturn(false);
			}
			EXAMPLE_IDS.put(type, EXAMPLE_ID(type));
			when(adaptor.getIdFromSimpleRep(eq(type), eq(SOME_SIMPLE_REP))).thenReturn(EXAMPLE_IDS.get(type));
			when(adaptor.lookupSelectorInInvertedIndex(any())).thenReturn(new DocumentInfos());
		}
	}

	public static final DataTypeId EXAMPLE_ID(String type) {
		final String uid = new Hasher().add(type.getBytes())
			.add(String.valueOf(idSeed++)
				.getBytes())
			.digest();
		return new DataTypeId(type, uid);
	}

	private static void setUpEntities(HbaseIndexModelAdaptor adaptor) {
		final DataType simpleTypeEntity = setUpSimpleEntity(adaptor);
		setUpComplexEntity(adaptor, simpleTypeEntity);
	}

	private static DataType setUpSimpleEntity(HbaseIndexModelAdaptor adaptor) {
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

	private static void setUpComplexEntity(HbaseIndexModelAdaptor adaptor, DataType simpleTypeEntity) {
		final DataTypeId complexTypeId = EXAMPLE_IDS.get(COMPLEX_TYPE);
		final DataType complexTypeEntity = new Selector(complexTypeId);
		complexTypeEntity.put("simpleType", simpleTypeEntity);
		complexTypeEntity.put("arraySimpleType", asList(simpleTypeEntity));
		when(adaptor.getDataType(complexTypeId)).thenReturn(complexTypeEntity);
	}
}