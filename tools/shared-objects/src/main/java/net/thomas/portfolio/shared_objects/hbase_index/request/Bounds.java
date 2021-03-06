package net.thomas.portfolio.shared_objects.hbase_index.request;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.SingleParameter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bounds implements ParameterGroup {
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", hidden = true)
	public Integer offset;
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", hidden = true)
	public Integer limit;
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", hidden = true)
	public Long after;
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", hidden = true)
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

	public void replaceMissing(Integer offset, Integer limit, Long after, Long before) {
		if (this.offset == null) {
			this.offset = offset;
		}
		if (this.limit == null) {
			this.limit = limit;
		}
		if (this.after == null || after != null && this.after < after) {
			this.after = after;
		}
		if (this.before == null || before != null && this.before > before) {
			this.before = before;
		}
	}

	@ApiModelProperty(value = "Number of elements to skip", example = "0")
	public Integer getB_offset() {
		return offset;
	}

	public void setB_offset(Integer offset) {
		this.offset = offset;
	}

	@ApiModelProperty(value = "Maximimum number of elements to return", example = "20")
	public Integer getB_limit() {
		return limit;
	}

	public void setB_limit(Integer limit) {
		this.limit = limit;
	}

	@ApiModelProperty(value = "Oldest relevant date as a Unix timestamp in milliseconds", example = "1215463675")
	public Long getB_after() {
		return after;
	}

	public void setB_after(Long after) {
		this.after = after;
	}

	@ApiModelProperty(value = "Newest relevant date as a Unix timestamp in milliseconds", example = "1415463675")
	public Long getB_before() {
		return before;
	}

	public void setB_before(Long before) {
		this.before = before;
	}

	@Override
	@JsonIgnore
	@ApiModelProperty(hidden = true)
	public Parameter[] getParameters() {
		return new Parameter[] { new SingleParameter("b_offset", offset), new SingleParameter("b_limit", limit),
				new SingleParameter("b_before", before), new SingleParameter("b_after", after) };
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (after == null ? 0 : after.hashCode());
		result = prime * result + (before == null ? 0 : before.hashCode());
		result = prime * result + (limit == null ? 0 : limit.hashCode());
		result = prime * result + (offset == null ? 0 : offset.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Bounds)) {
			return false;
		}
		final Bounds other = (Bounds) obj;
		if (after == null) {
			if (other.after != null) {
				return false;
			}
		} else if (!after.equals(other.after)) {
			return false;
		}
		if (before == null) {
			if (other.before != null) {
				return false;
			}
		} else if (!before.equals(other.before)) {
			return false;
		}
		if (limit == null) {
			if (other.limit != null) {
				return false;
			}
		} else if (!limit.equals(other.limit)) {
			return false;
		}
		if (offset == null) {
			if (other.offset != null) {
				return false;
			}
		} else if (!offset.equals(other.offset)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}
