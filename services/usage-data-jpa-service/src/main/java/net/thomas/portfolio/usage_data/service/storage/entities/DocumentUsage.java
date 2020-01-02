package net.thomas.portfolio.usage_data.service.storage.entities;

import static javax.persistence.GenerationType.AUTO;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;

@Entity
@Table(name = "USER_ACCESSED_DOCUMENT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUsage {
	@Id
	@Column(name = "ROW_NUMBER")
	@GeneratedValue(strategy = AUTO)
	private long row;
	@Column(name = "DOCUMENT_TYPE")
	private String documentType;
	@Column(name = "DOCUMENT_UID")
	private String documentUid;
	@OneToOne
	@JoinColumn(name = "USER_ID")
	private User userId;
	@Column(name = "ACCESS_TYPE")
	private UsageActivityType accessType;
	@Column(name = "TIME_OF_ACCESS")
	private Date timeOfAccess;
}