package net.thomas.portfolio.hbase_index.fake;

import static java.lang.Math.random;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.EntityId;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.processing.Entity2DataTypeConverter;
import net.thomas.portfolio.hbase_index.schema.processing.data.InvertedIndex;
import net.thomas.portfolio.hbase_index.schema.processing.data.SelectorStatistics;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.EntityVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.BlankVisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitorBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public class FakeHbaseIndex implements HbaseIndex {
	private final Map<String, Map<String, Entity>> storage;
	private InvertedIndex invertedIndex;
	private SelectorStatistics selectorStatistics;
	private Map<String, References> sourceReferences;
	private final Entity2DataTypeConverter entity2DataTypeConverter;
	private EntityVisitor<BlankVisitingContext> entityExtractor;

	public FakeHbaseIndex() {
		storage = new HashMap<>();
		entityExtractor = new StrictEntityHierarchyVisitorBuilder<BlankVisitingContext>()
				.setEntityPostActionFactory(createActionFactory()).build();
		entity2DataTypeConverter = new Entity2DataTypeConverter();
	}

	public FakeHbaseIndex(FakeHbaseIndexSerializable serializable) {
		storage = serializable.storage;
		selectorStatistics = serializable.selectorStatistics;
		invertedIndex = serializable.invertedIndex;
		sourceReferences = serializable.sourceReferences;
		entity2DataTypeConverter = new Entity2DataTypeConverter();
	}

	public FakeHbaseIndexSerializable getSerializable() {
		return new FakeHbaseIndexSerializable(this);
	}

	public void addEntitiesAndChildren(Collection<Event> entities) {
		for (final Event entity : entities) {
			entityExtractor.visit(entity, new BlankVisitingContext());
		}
	}

	private VisitorEntityPostActionFactory<BlankVisitingContext> createActionFactory() {
		final VisitorEntityPostActionFactory<BlankVisitingContext> actionFactory = new VisitorEntityPostActionFactory<BlankVisitingContext>() {
			@Override
			public <T extends Entity> VisitorEntityPostAction<T, BlankVisitingContext> getEntityPostAction(
					Class<T> entityClass) {
				return (entity, context) -> {
					addEntity(entity);
				};
			}
		};
		return actionFactory;
	}

	private void addEntity(Entity entity) {
		final String type = entity.getClass().getSimpleName();
		if (!storage.containsKey(type)) {
			storage.put(type, new HashMap<>());
		}
		storage.get(type).put(entity.uid, entity);
	}

	public void setInvertedIndex(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	public void setSelectorStatistics(SelectorStatistics selectorStatistics) {
		this.selectorStatistics = selectorStatistics;
	}

	public void setReferences(Map<String, References> sourceReferences) {
		this.sourceReferences = sourceReferences;
	}

	@Override
	public DataType getDataType(DataTypeId id) {
		if (storage.containsKey(id.type)) {
			final Map<String, Entity> typeStorage = storage.get(id.type);
			if (typeStorage.containsKey(id.uid)) {
				return convert(typeStorage.get(id.uid));
			}
		}
		return null;
	}

	private DataType convert(Entity entity) {
		return entity2DataTypeConverter.convert(entity);
	}

	@Override
	public DocumentInfos invertedIndexLookup(DataTypeId selectorId, Indexable indexable) {
		final List<EntityId> eventIds = invertedIndex.getEventUids(selectorId.uid, indexable.path);
		return new DocumentInfos(
				eventIds.stream().map(eventId -> (Event) storage.get(eventId.type.getSimpleName()).get(eventId.uid))
						.map(entity -> extractInfo(entity)).collect(toList()));
	}

	private DocumentInfo extractInfo(Event event) {
		return new DocumentInfo(new DataTypeId(event.getClass().getSimpleName(), event.uid), event.timeOfEvent,
				event.timeOfInterception);
	}

	@Override
	public Statistics getStatistics(DataTypeId selectorId) {
		return new Statistics(selectorStatistics.get(selectorId.uid));
	}

	@Override
	public References getReferences(DataTypeId documentId) {
		if (sourceReferences.containsKey(documentId.uid)) {
			return sourceReferences.get(documentId.uid);
		} else {
			return new References();
		}
	}

	@Override
	public Entities getSamples(String type, int amount) {
		if (storage.containsKey(type)) {
			if (amount >= storage.get(type).size()) {
				return convert(storage.get(type).values());
			} else {
				final List<Entity> instances = new ArrayList<>(storage.get(type).values());
				final Set<Entity> samples = new HashSet<>();
				while (samples.size() < amount) {
					samples.add(getRandomInstance(instances));
				}
				return convert(samples);
			}
		} else {
			return new Entities();
		}
	}

	private Entities convert(Collection<Entity> values) {
		return new Entities(values.stream().map(entity -> convert(entity)).collect(toList()));
	}

	private <T> T getRandomInstance(final List<T> instances) {
		return instances.get((int) (random() * instances.size()));
	}

	public void printSamples(int amount) {
		for (final String type : storage.keySet()) {
			for (final DataType sample : getSamples(type, amount).getEntities()) {
				System.out.println(sample);
			}
		}
	}

	public static class FakeHbaseIndexSerializable {
		private Map<String, Map<String, Entity>> storage;
		private InvertedIndex invertedIndex;
		private SelectorStatistics selectorStatistics;
		private Map<String, References> sourceReferences;

		public FakeHbaseIndexSerializable() {
		}

		public FakeHbaseIndexSerializable(FakeHbaseIndex index) {
			storage = index.storage;
			selectorStatistics = index.selectorStatistics;
			invertedIndex = index.invertedIndex;
			sourceReferences = index.sourceReferences;
		}

		public Map<String, Map<String, Entity>> getStorage() {
			return storage;
		}

		public void setStorage(Map<String, Map<String, Entity>> storage) {
			this.storage = storage;
		}

		public InvertedIndex getInvertedIndex() {
			return invertedIndex;
		}

		public void setInvertedIndex(InvertedIndex invertedIndex) {
			this.invertedIndex = invertedIndex;
		}

		public SelectorStatistics getSelectorStatistics() {
			return selectorStatistics;
		}

		public void setSelectorStatistics(SelectorStatistics selectorStatistics) {
			this.selectorStatistics = selectorStatistics;
		}

		public Map<String, References> getSourceReferences() {
			return sourceReferences;
		}

		public void setSourceReferences(Map<String, References> sourceReferences) {
			this.sourceReferences = sourceReferences;
		}
	}
}