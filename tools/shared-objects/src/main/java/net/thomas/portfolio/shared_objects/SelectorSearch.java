package net.thomas.portfolio.shared_objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SelectorSearch {
	public final Selector selector;
	public final Integer offset;
	public final Integer limit;
	public final Long after;
	public final Long before;

	public SelectorSearch(Selector selector, Integer offset, Integer limit, Long after, Long before) {
		this.selector = selector;
		this.offset = offset;
		this.limit = limit;
		this.after = after;
		this.before = before;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
