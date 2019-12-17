package net.thomas.portfolio.usage_data.service.storage.repositories;

import org.springframework.data.repository.CrudRepository;

import net.thomas.portfolio.usage_data.service.storage.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
}