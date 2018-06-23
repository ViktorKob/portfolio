package net.thomas.portfolio.nexus.graphql.data_proxies;

import java.util.EnumMap;
import java.util.Map;

import net.thomas.portfolio.nexus.graphql.fetchers.GlobalArgumentId;
import net.thomas.portfolio.nexus.graphql.fetchers.LocalArgumentId;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public abstract class DataTypeProxy<CONTENTS, DATA_TYPE_TYPE extends DataType> {

	protected final CONTENTS contents;
	protected final Adaptors adaptors;
	private final Map<GlobalArgumentId, Object> globalArguments;
	private final Map<LocalArgumentId, Object> localArguments;
	private DATA_TYPE_TYPE entity;

	public DataTypeProxy(CONTENTS contents, Adaptors adaptors) {
		this.contents = contents;
		this.adaptors = adaptors;
		globalArguments = new EnumMap<>(GlobalArgumentId.class);
		localArguments = new EnumMap<>(LocalArgumentId.class);
	}

	public DataTypeProxy(DataTypeProxy<?, ?> parent, CONTENTS contents, Adaptors adaptors) {
		this.contents = contents;
		this.adaptors = adaptors;
		globalArguments = new EnumMap<>(parent.globalArguments);
		localArguments = new EnumMap<>(LocalArgumentId.class);
	}

	public void put(GlobalArgumentId id, Object value) {
		if (value != null) {
			globalArguments.put(id, value);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(GlobalArgumentId id) {
		return (T) globalArguments.get(id);
	}

	public void put(LocalArgumentId id, Object value) {
		if (value != null) {
			localArguments.put(id, value);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(LocalArgumentId id) {
		return (T) localArguments.get(id);
	}

	public abstract DataTypeId getId();

	public DATA_TYPE_TYPE getEntity() {
		if (entity == null) {
			entity = _getEntity();
		}
		return entity;
	}

	protected abstract DATA_TYPE_TYPE _getEntity();

	@Override
	public String toString() {
		return "GlobalArgs: " + globalArguments + ", LocalArgs: " + localArguments + ", entity loaded: " + (entity != null) + ", " + contents;
	}
}