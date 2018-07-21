package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static java.util.Collections.emptyList;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentInfos {
	private List<DocumentInfo> infos;

	public DocumentInfos() {
		infos = emptyList();
	}

	public DocumentInfos(List<DocumentInfo> infos) {
		this.infos = infos;
	}

	public List<DocumentInfo> getInfos() {
		return infos;
	}

	public void setInfos(List<DocumentInfo> infos) {
		this.infos = infos;
	}

	@Override
	public int hashCode() {
		return infos.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return infos.equals(obj);
	}

	@Override
	public String toString() {
		return infos.toString();
	}
}