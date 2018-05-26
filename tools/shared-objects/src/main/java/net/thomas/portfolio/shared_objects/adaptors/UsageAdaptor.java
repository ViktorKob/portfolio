package net.thomas.portfolio.shared_objects.adaptors;

import java.util.List;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityItem;

public interface UsageAdaptor {
	boolean storeUsageActivity(DataTypeId documentId, UsageActivityItem item);

	List<UsageActivityItem> fetchUsageActivity(DataTypeId documentId, Integer offset, Integer limit);
}