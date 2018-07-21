package net.thomas.portfolio.service_commons.adaptors.specific;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
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
	 * @return A list of IDs for possible selectors based on the simple representation, ordered by occurrence in the data followed by relevance guess
	 */
	List<DataTypeId> getSelectorSuggestions(String simpleRepresentation);

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
	Set<String> getIndexedDocumentTypes(String selectorType);

	/***
	 * @param selectorType
	 *            The selector type in question
	 * @return All types of relations in the current schema, that can be hit when querying for this selector type
	 */
	Set<String> getIndexedRelations(String selectorType);

	/***
	 * @return All types of relations in the current schema, that can be hit when querying for any selector type
	 */
	Collection<String> getAllIndexedRelations();

	/***
	 * @param dataType
	 *            The data type in question
	 * @return All fields belonging to this type in the current schema
	 */
	Fields getFieldsForDataType(String dataType);

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
	References getReferences(DataTypeId documentId);

	/***
	 * <B>All queries into the selector statistics are validated using the legal service before the query is executed.</B>
	 *
	 * @param selectorId
	 *            The ID of the selector to load statistics for
	 * @return An occurrence count for how often this selector is seen in the data, grouped by a set of time periods from today backwards
	 */
	Statistics getStatistics(DataTypeId selectorId);

	/***
	 * This method allows for lookups of data in the inverted index using a selector, a set of bounds, legal information and a set of optional filtering
	 * values.<BR>
	 *
	 * <B>All queries into the inverted index are validated using the legal service before the query is executed.</B>
	 *
	 * @param request
	 *            The description of a lookup in the inverted index
	 * @return A list of document information containers ordered by time of event (descending)
	 */
	DocumentInfos lookupSelectorInInvertedIndex(InvertedIndexLookupRequest request);
}