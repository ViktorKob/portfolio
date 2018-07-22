package net.thomas.portfolio.hbase_index.schema.documents;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public abstract class DocumentEntity {
	@PartOfKey
	public final Timestamp timeOfEvent;
	public final Timestamp timeOfInterception;

	public DocumentEntity(Timestamp timeOfEvent, Timestamp timeOfInterception) {
		this.timeOfEvent = timeOfEvent;
		this.timeOfInterception = timeOfInterception;
	}
}