package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Iterator;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Fields extends LinkedHashMap<String, Field> implements Iterable<Field> {
	private static final long serialVersionUID = 1L;

	public Fields() {
		super();
	}

	private Fields(LinkedHashMap<String, Field> fields) {
		super();
		putAll(fields);
	}

	public static Fields fields(Field... fields) {
		return new Fields(stream(fields).collect(toMap(Field::getName, identity(), (oldKey, newKey) -> oldKey, LinkedHashMap::new)));
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Fields) {
			return super.equals(object);
		} else {
			return false;
		}
	}

	@Override
	public Iterator<Field> iterator() {
		return values().iterator();
	}
}
