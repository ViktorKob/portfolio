package net.thomas.portfolio.hbase_index.schema.documents;

import net.thomas.portfolio.hbase_index.schema.annotations.IndexablePathAnnotation;
import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class Email extends DocumentEntity {
	@PartOfKey
	public final String subject;
	@PartOfKey
	public final String message;
	@PartOfKey
	@IndexablePathAnnotation("send")
	public final EmailEndpoint from;
	@IndexablePathAnnotation("recieved")
	public final EmailEndpoint[] to;
	@IndexablePathAnnotation("ccReceived")
	public final EmailEndpoint[] cc;
	@IndexablePathAnnotation("bccReceived")
	public final EmailEndpoint[] bcc;

	public Email(String uid, Timestamp timeOfEvent, Timestamp timeOfInterception, String subject, String message, EmailEndpoint from, EmailEndpoint[] to,
			EmailEndpoint[] cc, EmailEndpoint[] bcc) {
		super(uid, timeOfEvent, timeOfInterception);
		this.subject = subject;
		this.message = message;
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
	}
}