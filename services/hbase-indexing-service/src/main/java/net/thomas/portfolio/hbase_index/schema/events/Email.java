package net.thomas.portfolio.hbase_index.schema.events;

import java.util.Arrays;

import net.thomas.portfolio.annotations.CoverageIgnoredMethod;
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

	public Email(Timestamp timeOfEvent, Timestamp timeOfInterception, String subject, String message, EmailEndpoint from, EmailEndpoint[] to,
			EmailEndpoint[] cc, EmailEndpoint[] bcc) {
		super(timeOfEvent, timeOfInterception);
		this.subject = subject;
		this.message = message;
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
	}

	@Override
	@CoverageIgnoredMethod
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bcc);
		result = prime * result + Arrays.hashCode(cc);
		result = prime * result + (from == null ? 0 : from.hashCode());
		result = prime * result + (message == null ? 0 : message.hashCode());
		result = prime * result + (subject == null ? 0 : subject.hashCode());
		result = prime * result + Arrays.hashCode(to);
		return uid.hashCode();
	}

	@Override
	@CoverageIgnoredMethod
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
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
	@CoverageIgnoredMethod
	public String toString() {
		return "Email [subject=" + subject + ", message=" + message + ", from=" + from + ", to=" + Arrays.toString(to) + ", cc=" + Arrays.toString(cc)
				+ ", bcc=" + Arrays.toString(bcc) + ", timeOfEvent=" + timeOfEvent + ", timeOfInterception=" + timeOfInterception + ", uid=" + uid + "]";
	}
}