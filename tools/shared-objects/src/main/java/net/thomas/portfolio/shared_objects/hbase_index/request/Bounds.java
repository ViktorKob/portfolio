package net.thomas.portfolio.shared_objects.hbase_index.request;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.common.services.Parameter;
import net.thomas.portfolio.common.services.ParameterGroup;
import net.thomas.portfolio.common.services.PreSerializedParameter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bounds implements ParameterGroup {
	@JsonIgnore
	public Integer offset;
	@JsonIgnore
	public Integer limit;
	@JsonIgnore
	public Long after;
	@JsonIgnore
	public Long before;

	public Bounds() {
	}

	public Bounds(Integer offset, Integer limit, Long after, Long before) {
		this.offset = offset;
		this.limit = limit;
		this.after = after;
		this.before = before;
	}

	public Bounds(Bounds source) {
		offset = source.offset;
		limit = source.limit;
		after = source.after;
		before = source.before;
	}

	public void update(Bounds bounds) {
		if (bounds.offset != null) {
			offset = bounds.offset;
		}
		if (bounds.limit != null) {
			limit = bounds.limit;
		}
		if (bounds.after != null) {
			after = bounds.after;
		}
		if (bounds.before != null) {
			before = bounds.before;
		}
	}

	public Integer getB_offset() {
		return offset;
	}

	public void setB_offset(Integer offset) {
		this.offset = offset;
	}

	public Integer getB_limit() {
		return limit;
	}

	public void setB_limit(Integer limit) {
		this.limit = limit;
	}

	public Long getB_after() {
		return after;
	}

	public void setB_after(Long after) {
		this.after = after;
	}

	public Long getB_before() {
		return before;
	}

	public void setB_before(Long before) {
		this.before = before;
	}

	@Override
	@JsonIgnore
	public Parameter[] getParameters() {
		return new Parameter[] { new PreSerializedParameter("b_offset", offset), new PreSerializedParameter("b_limit", limit),
				new PreSerializedParameter("b_before", before), new PreSerializedParameter("b_after", after) };
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Bounds) {
			final Bounds other = (Bounds) obj;
			return offset == other.offset && limit == other.limit && after == other.after && before == other.before;
		}
		return false;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}