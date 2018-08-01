package net.thomas.portfolio.hbase_index.schema.events;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import net.thomas.portfolio.hbase_index.schema.annotations.IndexablePath;
import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class TextMessage extends Event {
	@PartOfKey
	public final String message;
	@PartOfKey
	@IndexablePath("send")
	public final CommunicationEndpoint sender;
	@PartOfKey
	@IndexablePath("received")
	public final CommunicationEndpoint receiver;
	public final GeoLocation senderLocation;
	public final GeoLocation receiverLocation;

	public TextMessage(String message, CommunicationEndpoint sender, CommunicationEndpoint receiver, GeoLocation senderLocation, GeoLocation receiverLocation,
			Timestamp timeOfEvent, Timestamp timeOfInterception) {
		super(timeOfEvent, timeOfInterception);
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.senderLocation = senderLocation;
		this.receiverLocation = receiverLocation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (message == null ? 0 : message.hashCode());
		result = prime * result + (receiver == null ? 0 : receiver.hashCode());
		result = prime * result + (receiverLocation == null ? 0 : receiverLocation.hashCode());
		result = prime * result + (sender == null ? 0 : sender.hashCode());
		result = prime * result + (senderLocation == null ? 0 : senderLocation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof TextMessage)) {
			return false;
		}
		final TextMessage other = (TextMessage) obj;
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		if (receiver == null) {
			if (other.receiver != null) {
				return false;
			}
		} else if (!receiver.equals(other.receiver)) {
			return false;
		}
		if (receiverLocation == null) {
			if (other.receiverLocation != null) {
				return false;
			}
		} else if (!receiverLocation.equals(other.receiverLocation)) {
			return false;
		}
		if (sender == null) {
			if (other.sender != null) {
				return false;
			}
		} else if (!sender.equals(other.sender)) {
			return false;
		}
		if (senderLocation == null) {
			if (other.senderLocation != null) {
				return false;
			}
		} else if (!senderLocation.equals(other.senderLocation)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}