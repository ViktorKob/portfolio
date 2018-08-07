package net.thomas.portfolio.nexus.service.test_utils;

import static java.util.Arrays.asList;
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
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class GraphQlTestModel {
	public static final String SIMPLE_TYPE = "SimpleType";
	public static final String RECURSIVE_TYPE = "RecursiveType";
	public static final String COMPLEX_TYPE = "ComplexType";
	public static final String NON_SIMPLE_REP_TYPE = "NonSimpleRepType";
	public static final String CONTAINER_TYPE = "ContainerType";
	public static final String DOCUMENT_TYPE = "DocumentType";
	public static final String SOME_SIMPLE_REP = "some simple rep";
	public static final Collection<String> DATA_TYPES = asList(SIMPLE_TYPE, RECURSIVE_TYPE, COMPLEX_TYPE, NON_SIMPLE_REP_TYPE, CONTAINER_TYPE, DOCUMENT_TYPE);
	public static final Collection<String> DOCUMENT_TYPES = asList(DOCUMENT_TYPE);
	public static final Collection<String> SELECTOR_TYPES = asList(SIMPLE_TYPE, RECURSIVE_TYPE, COMPLEX_TYPE, NON_SIMPLE_REP_TYPE);
	public static final DocumentInfos SOME_DOCUMENT_INFOS = new DocumentInfos(asList());
	private static int idSeed = 0;
	public static Map<String, DataTypeId> EXAMPLE_IDS = new HashMap<>();

	public static void setUpHbaseAdaptorMock(HbaseIndexModelAdaptor adaptor) {
		when(adaptor.getDataTypes()).thenReturn(DATA_TYPES);
		when(adaptor.getDocumentTypes()).thenReturn(DOCUMENT_TYPES);
		when(adaptor.getSelectorTypes()).thenReturn(SELECTOR_TYPES);
		final List<DocumentInfo> documentInfos = new LinkedList<>();
		for (final String type : DOCUMENT_TYPES) {
			when(adaptor.isDocument(type)).thenReturn(true);
			EXAMPLE_IDS.put(type, EXAMPLE_ID(type));
			documentInfos.add(new DocumentInfo(EXAMPLE_IDS.get(type), new Timestamp(2l), new Timestamp(4l)));
		}
		SOME_DOCUMENT_INFOS.setInfos(documentInfos);
		EXAMPLE_IDS.put(CONTAINER_TYPE, EXAMPLE_ID(CONTAINER_TYPE));
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
		when(adaptor.getFieldsForDataType(any())).thenReturn(new Fields());
	}

	public static final DataTypeId EXAMPLE_ID(String type) {
		final String uid = new Hasher().add(type.getBytes())
			.add(String.valueOf(idSeed++)
				.getBytes())
			.digest();
		return new DataTypeId(type, uid);
	}
}