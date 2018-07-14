package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import static java.util.Collections.emptyMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class SelectorTraversalTool {
	public Map<String, DataType> grabSelectorsFromSubtree(DataType entity, HbaseIndexSchema schema) {
		if (entity != null) {
			final Map<String, DataType> selectors = new HashMap<>();
			if (schema.getSelectorTypes()
				.contains(entity.getId().type)) {
				selectors.put(entity.getId().uid, entity);
			}
			for (final Field field : schema.getFieldsForDataType(entity.getId().type)) {
				if (field instanceof ReferenceField) {
					if (field.isArray()) {
						for (final Object dataType : (List<?>) entity.get(field.getName())) {
							selectors.putAll(grabSelectorsFromSubtree((DataType) dataType, schema));
						}
					} else {
						selectors.putAll(grabSelectorsFromSubtree(entity.get(field.getName()), schema));
					}
				}
			}
			return selectors;
		} else {
			return emptyMap();
		}
	}
}