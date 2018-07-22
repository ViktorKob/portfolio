package net.thomas.portfolio.hbase_index.schema;

import net.thomas.portfolio.hbase_index.schema.annotations.SchemaIgnore;

public class Entity {
	@SchemaIgnore
	public final String uid;

	public Entity(String uid) {
		this.uid = uid;
	}
}