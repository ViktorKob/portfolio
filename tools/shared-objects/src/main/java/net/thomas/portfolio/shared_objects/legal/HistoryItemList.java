package net.thomas.portfolio.shared_objects.legal;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class HistoryItemList extends ResourceSupport implements List<HistoryItem> {
	private final List<HistoryItem> items;

	public HistoryItemList() {
		items = new LinkedList<>();
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return items.contains(o);
	}

	@Override
	public Iterator<HistoryItem> iterator() {
		return items.iterator();
	}

	@Override
	public Object[] toArray() {
		return items.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return items.toArray(a);
	}

	@Override
	public boolean add(HistoryItem e) {
		return items.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return items.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return items.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends HistoryItem> c) {
		return items.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends HistoryItem> c) {
		return items.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return items.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return items.retainAll(c);
	}

	@Override
	public void clear() {
		items.clear();
	}

	@Override
	public HistoryItem get(int index) {
		return items.get(index);
	}

	@Override
	public HistoryItem set(int index, HistoryItem element) {
		return items.set(index, element);
	}

	@Override
	public void add(int index, HistoryItem element) {
		items.add(index, element);
	}

	@Override
	public HistoryItem remove(int index) {
		return items.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return items.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return items.lastIndexOf(o);
	}

	@Override
	public ListIterator<HistoryItem> listIterator() {
		return items.listIterator();
	}

	@Override
	public ListIterator<HistoryItem> listIterator(int index) {
		return items.listIterator(index);
	}

	@Override
	public List<HistoryItem> subList(int fromIndex, int toIndex) {
		return items.subList(fromIndex, toIndex);
	}
}