package net.thomas.portfolio.hbase_index.schema.documents;

import net.thomas.portfolio.hbase_index.schema.annotations.IndexablePathAnnotation;
import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class Conversation extends DocumentEntity {
	@PartOfKey
	public final Integer durationIsSeconds;
	@PartOfKey
	@IndexablePathAnnotation("primary")
	public final CommunicationEndpoint primary;
	@IndexablePathAnnotation("secondary")
	public final CommunicationEndpoint secondary;
	public final GeoLocation primaryLocation;
	public final GeoLocation secondaryLocation;

	public Conversation(Timestamp timeOfEvent, Timestamp timeOfInterception, Integer durationIsSeconds, CommunicationEndpoint primary, CommunicationEndpoint secondary,
			GeoLocation primaryLocation, GeoLocation secondaryLocation) {
		super(timeOfEvent, timeOfInterception);
		this.durationIsSeconds = durationIsSeconds;
		this.primary = primary;
		this.secondary = secondary;
		this.primaryLocation = primaryLocation;
		this.secondaryLocation = secondaryLocation;
	}
}