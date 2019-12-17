package net.thomas.portfolio.usage_data.service.storage;

import static net.thomas.portfolio.usage_data.service.storage.entities.DocumentUsage.builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.usage_data.service.storage.entities.AccessType;
import net.thomas.portfolio.usage_data.service.storage.entities.DocumentUsage;
import net.thomas.portfolio.usage_data.service.storage.entities.User;
import net.thomas.portfolio.usage_data.service.storage.repositories.DocumentUsageRepository;

@Service
public class UsageDataJpaStorage {
	private final DocumentUsageRepository repository;

	@Autowired
	public UsageDataJpaStorage(DocumentUsageRepository repository) {
		this.repository = repository;
	}

	public void storeActivity(DataTypeId id, UsageActivity activity) {
		final DocumentUsage.DocumentUsageBuilder userAccess = builder();
		userAccess.documentType(id.type).documentUid(id.uid);
		userAccess.userId(User.builder().name(activity.user).build());
		userAccess.accessTypeId(AccessType.builder().type(activity.type).build());
		repository.save(userAccess.build());
	}

	public UsageActivities getActivitiesFor(DataTypeId documentId, Bounds bounds) {
		return asUsageActivities(fetchUsage(documentId, bounds));
	}

	private List<DocumentUsage> fetchUsage(DataTypeId documentId, Bounds bounds) {
		return repository.findByDocumentTypeAndDocumentUidAndTimeOfAccessBetween(documentId.type, documentId.uid, bounds.after, bounds.before,
				PageRequest.of(bounds.offset / bounds.limit, bounds.limit));
	}

	private UsageActivities asUsageActivities(final List<DocumentUsage> activitiesForDocument) {
		final List<UsageActivity> activities = new ArrayList<>();
		for (final DocumentUsage usage : activitiesForDocument) {
			activities.add(new UsageActivity(usage.getUserId().getName(), usage.getAccessTypeId().getType(), usage.getTimeOfAccess()));
		}
		return new UsageActivities(activities);
	}
}