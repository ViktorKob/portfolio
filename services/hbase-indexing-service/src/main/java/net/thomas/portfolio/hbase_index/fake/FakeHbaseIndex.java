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

import net.thomas.portfolio.hbase_index.fake.world.WorldAccess;
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
	private WorldAccess world;
	private final Entity2DataTypeConverter entity2DataTypeConverter;
	private final EntityVisitor<BlankVisitingContext> entityExtractor;

	public FakeHbaseIndex() {
		storage = new HashMap<>();
		entityExtractor = new StrictEntityHierarchyVisitorBuilder<BlankVisitingContext>().setEntityPostActionFactory(createActionFactory()).build();
		entity2DataTypeConverter = new Entity2DataTypeConverter();
	}

	public void setInvertedIndex(final InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	public void setSelectorStatistics(final SelectorStatistics selectorStatistics) {
		this.selectorStatistics = selectorStatistics;
	}

	public void setWorldAccess(final WorldAccess world) {
		this.world = world;
		addEntitiesAndChildren(world);
	}

	public void addEntitiesAndChildren(final Iterable<Event> entities) {
		for (final Event entity : entities) {
			entityExtractor.visit(entity, new BlankVisitingContext());
		}
	}

	private VisitorEntityPostActionFactory<BlankVisitingContext> createActionFactory() {
		final VisitorEntityPostActionFactory<BlankVisitingContext> actionFactory = new VisitorEntityPostActionFactory<BlankVisitingContext>() {
			@Override
			public <T extends Entity> VisitorEntityPostAction<T, BlankVisitingContext> getEntityPostAction(final Class<T> entityClass) {
				if (!Event.class.isAssignableFrom(entityClass)) {
					return (entity, context) -> {
						addEntity(entity);
					};
				} else {
					return (entity, context) -> {
					};
				}
			}
		};
		return actionFactory;
	}

	private void addEntity(final Entity entity) {
		final String type = entity.getClass().getSimpleName();
		if (!storage.containsKey(type)) {
			storage.put(type, new HashMap<>());
		}
		storage.get(type).put(entity.uid, entity);
	}

	@Override
	public DataType getDataType(final DataTypeId id) {
		return convert(getDataType(id.type, id.uid));
	}

	public Entity getEntity(final EntityId id) {
		return getDataType(id.type.getSimpleName(), id.uid);
	}

	public Entity getDataType(final String type, final String uid) {
		if (storage.containsKey(type)) {
			final Map<String, Entity> typeStorage = storage.get(type);
			if (typeStorage.containsKey(uid)) {
				return typeStorage.get(uid);
			}
		} else {
			final Event event = world.getEvent(uid);
			if (event != null) {
				return event;
			}
		}
		return null;
	}

	private DataType convert(final Entity entity) {
		return entity2DataTypeConverter.convert(entity);
	}

	@Override
	public DocumentInfos invertedIndexLookup(final DataTypeId selectorId, final Indexable indexable) {
		final List<EntityId> eventIds = invertedIndex.getEventUids(selectorId.uid, indexable.path);
		return new DocumentInfos(eventIds.stream().map(eventId -> (Event) getEntity(eventId)).map(entity -> extractInfo(entity)).collect(toList()));
	}

	private DocumentInfo extractInfo(final Event event) {
		return new DocumentInfo(new DataTypeId(event.getClass().getSimpleName(), event.uid), event.timeOfEvent, event.timeOfInterception);
	}

	@Override
	public Statistics getStatistics(final DataTypeId selectorId) {
		return new Statistics(selectorStatistics.get(selectorId.uid));
	}

	@Override
	public References getReferences(final DataTypeId documentId) {
		return world.getReferences(documentId.uid);
	}

	@Override
	public Entities getSamples(final String type, final int amount) {
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

	private Entities convert(final Collection<Entity> values) {
		return new Entities(values.stream().map(entity -> convert(entity)).collect(toList()));
	}

	private <T> T getRandomInstance(final List<T> instances) {
		return instances.get((int) (random() * instances.size()));
	}

	public void printSamples(final int amount) {
		for (final String type : storage.keySet()) {
			for (final DataType sample : getSamples(type, amount).getEntities()) {
				System.out.println(sample);
			}
		}
	}
}