package net.thomas.portfolio.hbase_index.schema.events;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.Arrays;

import net.thomas.portfolio.hbase_index.schema.annotations.IndexablePath;
import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class Email extends Event {
	@PartOfKey
	public final String subject;
	@PartOfKey
	public final String message;
	@PartOfKey
	@IndexablePath("send")
	public final EmailEndpoint from;
	@IndexablePath("recieved")
	public final EmailEndpoint[] to;
	@IndexablePath("ccReceived")
	public final EmailEndpoint[] cc;
	@IndexablePath("bccReceived")
	public final EmailEndpoint[] bcc;

	public Email(String subject, String message, EmailEndpoint from, EmailEndpoint[] to, EmailEndpoint[] cc, EmailEndpoint[] bcc, Timestamp timeOfEvent,
			Timestamp timeOfInterception) {
		super(timeOfEvent, timeOfInterception);
		this.subject = subject;
		this.message = message;
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(bcc);
		result = prime * result + Arrays.hashCode(cc);
		result = prime * result + (from == null ? 0 : from.hashCode());
		result = prime * result + (message == null ? 0 : message.hashCode());
		result = prime * result + (subject == null ? 0 : subject.hashCode());
		result = prime * result + Arrays.hashCode(to);
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
		if (!(obj instanceof Email)) {
			return false;
		}
		final Email other = (Email) obj;
		if (!Arrays.equals(bcc, other.bcc)) {
			return false;
		}
		if (!Arrays.equals(cc, other.cc)) {
			return false;
		}
		if (from == null) {
			if (other.from != null) {
				return false;
			}
		} else if (!from.equals(other.from)) {
			return false;
		}
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		if (subject == null) {
			if (other.subject != null) {
				return false;
			}
		} else if (!subject.equals(other.subject)) {
			return false;
		}
		if (!Arrays.equals(to, other.to)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}