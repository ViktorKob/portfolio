package net.thomas.portfolio.usage_data.service.storage.entities;

import static javax.persistence.GenerationType.AUTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;

@Entity
@Table(name = "access_type")
@Data
@Builder
public class AccessType {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = AUTO)
	private long accessTypeId;
	@Column(name = "name")
	private UsageActivityType type;
}