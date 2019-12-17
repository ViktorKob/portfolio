package net.thomas.portfolio.usage_data.service.storage.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.thomas.portfolio.usage_data.service.storage.entities.DocumentUsage;

public interface DocumentUsageRepository extends PagingAndSortingRepository<DocumentUsage, Long> {
	List<DocumentUsage> findByDocumentTypeAndDocumentUidAndTimeOfAccessBetween(String documentType, String documentUid, long after, long before,
			Pageable pageable);
}