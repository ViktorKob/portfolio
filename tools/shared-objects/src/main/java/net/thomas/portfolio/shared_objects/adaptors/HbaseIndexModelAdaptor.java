package net.thomas.portfolio.shared_objects.adaptors;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;

/***
 * This adaptor allows usage of endpoints in the HBASE indexing service as java methods.
 *
 * Implementations should pre-load the schema from the service on creation to allow for quick lookups.
 *
 */
public interface HbaseIndexModelAdaptor {

	/*** Schema ***/
	/***
	 * @param simpleRepresentation
	 *            A simple, human readable representation of a selector
	 * @return A list of possible selectors based on the simple representation, ordered by occurrence in the data followed by relevance guess
	 */
	List<Selector> getSelectorSuggestions(String simpleRepresentation);

	/***
	 * @param type
	 *            Type of the selector
	 * @param simpleRep
	 *            A simple, human readable representation of the selector
	 * @return The ID for the selector in the current schema
	 */
	DataTypeId getIdFromSimpleRep(String type, String simpleRep);

	/***
	 * @param type
	 *            Type of the selector
	 * @return true, if the selector has a simple, human readable representation
	 */
	boolean isSimpleRepresentable(String type);

	/***
	 * @param type
	 *            The data type is question
	 * @return true, if this type is a Selector in the current schema
	 */
	boolean isSelector(String type);

	/***
	 * @param type
	 *            The data type is question
	 * @return true, if this type is a Document in the current schema
	 */
	boolean isDocument(String type);

	/***
	 * @return All data types present in the current schema
	 */
	Collection<String> getDataTypes();

	/***
	 * @return All document types in the current schema
	 */
	Collection<String> getDocumentTypes();

	/***
	 * @return All selector types in the current schema
	 */
	Collection<String> getSelectorTypes();

	/***
	 * @param selectorType
	 *            The selector type in question
	 * @return All document types in the current schema, that can be hit when querying for this selector type
	 */
	Collection<String> getIndexedDocumentTypes(String selectorType);

	/***
	 * @param selectorType
	 *            The selector type in question
	 * @return All types of relations in the current schema, that can be hit when querying for this selector type
	 */
	Collection<String> getIndexedRelations(String selectorType);

	/***
	 * @param dataType
	 *            The data type in question
	 * @return All fields belonging to this type in the current schema
	 */
	Collection<Field> getDataTypeFields(String dataType);

	/*** Data ***/
	/***
	 * @param id
	 *            Id of the data type to load
	 * @return The complete set of values for this data type with all sub-types
	 */
	DataType getDataType(DataTypeId id);

	/***
	 * @param documentId
	 *            The ID of the document to load references for
	 * @return All references for this data type in the index
	 */
	Collection<Reference> getReferences(DataTypeId documentId);

	/***
	 * <B>All queries into the selector statistics are validated using the legal service before the query is executed.</B>
	 *
	 * @param selectorId
	 *            The ID of the selector to load statistics for
	 * @return An occurrence count for how often this selector is seen in the data, grouped by a set of time periods from today backwards
	 */
	Map<StatisticsPeriod, Long> getStatistics(DataTypeId selectorId);

	/***
	 * This method allows for lookups of data using the inverted index using selectors and bounds.<BR>
	 *
	 * <B>All queries into the inverted index are validated using the legal service before the query is executed.</B>
	 *
	 * @param request
	 *            The description of a lookup in the inverted index
	 * @return A list of document information containers ordered by time of event (descending)
	 */
	List<DocumentInfo> lookupSelectorInInvertedIndex(InvertedIndexLookupRequest request);
}