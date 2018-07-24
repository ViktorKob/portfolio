package net.thomas.portfolio.hbase_index.schema.documents;

import net.thomas.portfolio.hbase_index.schema.annotations.IndexablePathAnnotation;
import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class TextMessage extends DocumentEntity {
	@PartOfKey
	public final String message;
	@PartOfKey
	@IndexablePathAnnotation("send")
	public final CommunicationEndpoint sender;
	@PartOfKey
	@IndexablePathAnnotation("received")
	public final CommunicationEndpoint receiver;
	public final GeoLocation senderLocation;
	public final GeoLocation receiverLocation;

	public TextMessage(Timestamp timeOfEvent, Timestamp timeOfInterception, String message, CommunicationEndpoint sender, CommunicationEndpoint receiver,
			GeoLocation senderLocation, GeoLocation receiverLocation) {
		super(timeOfEvent, timeOfInterception);
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.senderLocation = senderLocation;
		this.receiverLocation = receiverLocation;
	}
}