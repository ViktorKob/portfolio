package net.thomas.portfolio.hbase_index.schema.meta;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;

public class CommunicationEndpoint extends MetaEntity {
	@PartOfKey
	public final PublicId publicId;
	@PartOfKey
	public final PrivateId privateId;

	public CommunicationEndpoint(String uid, PublicId publicId, PrivateId privateId) {
		super(uid);
		this.publicId = publicId;
		this.privateId = privateId;
	}
}