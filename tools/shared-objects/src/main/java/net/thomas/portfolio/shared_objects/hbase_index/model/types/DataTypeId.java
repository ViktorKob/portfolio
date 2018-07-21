package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.PreSerializedParameter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataTypeId implements ParameterGroup {
	public static final DataTypeId NULL_ID = new NullId();

	@JsonIgnore
	public String type;
	@JsonIgnore
	public String uid;

	public DataTypeId() {
	}

	public DataTypeId(String type, String uid) {
		setDti_type(type);
		setDti_uid(uid);
	}

	public DataTypeId(DataTypeId source) {
		type = source.type;
		uid = source.uid;
	}

	public void setDti_type(String type) {
		this.type = type;
	}

	public void setDti_uid(String uid) {
		if (uid != null) {
			this.uid = uid.toUpperCase();
		} else {
			uid = null;
		}
	}

	public String getDti_type() {
		return type;
	}

	public String getDti_uid() {
		return uid;
	}

	@Override
	@JsonIgnore
	public Parameter[] getParameters() {
		return new Parameter[] { new PreSerializedParameter("dti_type", type), new PreSerializedParameter("dti_uid", uid) };
	}

	@Override
	public int hashCode() {
		int hash = type.hashCode();
		hash = 37 * hash + uid.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataTypeId) {
			final DataTypeId other = (DataTypeId) obj;
			return type.equals(other.type) && uid != null && uid.equals(other.uid);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return type + "-" + uid;
	}

	private static final class NullId extends DataTypeId {
	}
}