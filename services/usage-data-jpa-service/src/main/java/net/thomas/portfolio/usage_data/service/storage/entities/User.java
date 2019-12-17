package net.thomas.portfolio.usage_data.service.storage.entities;

import static javax.persistence.GenerationType.AUTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
@Builder
public class User {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = AUTO)
	private long userId;
	@Column(name = "name")
	private String name;
}