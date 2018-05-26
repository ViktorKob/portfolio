package net.thomas.portfolio.shared_objects.hbase_index.request;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvertedIndexLookupRequest {
	@JsonIgnore
	public DataTypeId selectorId;
	@JsonIgnore
	public LegalInformation legalInfo;
	@JsonIgnore
	public Integer offset;
	@JsonIgnore
	public Integer limit;
	@JsonIgnore
	public Long after;
	@JsonIgnore
	public Long before;

	public InvertedIndexLookupRequest() {
	}

	public InvertedIndexLookupRequest(DataTypeId selectorId, Integer offset, Integer limit, Long after, Long before) {
		this.selectorId = selectorId;
		this.offset = offset;
		this.limit = limit;
		this.after = after;
		this.before = before;
	}

	public DataTypeId getIil_Id() {
		return selectorId;
	}

	public void setIil_Id(DataTypeId selectorId) {
		this.selectorId = selectorId;
	}

	public Integer getIil_offset() {
		return offset;
	}

	public void setIil_offset(Integer offset) {
		this.offset = offset;
	}

	public Integer getIil_limit() {
		return limit;
	}

	public void setIil_limit(Integer limit) {
		this.limit = limit;
	}

	public Long getIil_after() {
		return after;
	}

	public void setIil_after(Long after) {
		this.after = after;
	}

	public Long getIil_before() {
		return before;
	}

	public void setIil_before(Long before) {
		this.before = before;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InvertedIndexLookupRequest) {
			final InvertedIndexLookupRequest other = (InvertedIndexLookupRequest) obj;
			return selectorId.equals(other.selectorId) && offset == other.offset && limit == other.limit && after == other.after && before == other.before;
		}
		return false;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
