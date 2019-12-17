package net.thomas.portfolio.usage_data.service.storage.repositories;

import org.springframework.data.repository.CrudRepository;

import net.thomas.portfolio.usage_data.service.storage.entities.AccessType;

public interface AccessTypeRepository extends CrudRepository<AccessType, Long> {
}