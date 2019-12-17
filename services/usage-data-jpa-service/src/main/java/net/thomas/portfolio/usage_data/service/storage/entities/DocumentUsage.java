package net.thomas.portfolio.usage_data.service.storage.entities;

import static javax.persistence.GenerationType.AUTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "user_accessed_document")
@Data
@Builder
public class DocumentUsage {
	@Id
	@Column(name = "row")
	@GeneratedValue(strategy = AUTO)
	private long row;
	@Column(name = "document_type")
	private String documentType;
	@Column(name = "document_uid")
	private String documentUid;
	@OneToOne
	@JoinColumn(name = "user_id")
	private User userId;
	@OneToOne
	@JoinColumn(name = "access_type_id")
	private AccessType accessTypeId;
	@Column(name = "time_of_access")
	private Long timeOfAccess;
}