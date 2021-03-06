package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "A collection of document infos")
public class DocumentInfos {
	private List<DocumentInfo> infos;

	public DocumentInfos() {
		infos = emptyList();
	}

	public DocumentInfos(List<DocumentInfo> infos) {
		this.infos = infos;
	}

	// Only here because Resources from spring hateoas chooses to return a collection instead of a list
	public DocumentInfos(Collection<DocumentInfo> infos) {
		this.infos = new ArrayList<>(infos);
	}

	public List<DocumentInfo> getInfos() {
		return infos;
	}

	public void setInfos(List<DocumentInfo> infos) {
		this.infos = infos;
	}

	public boolean hasData() {
		return !infos.isEmpty();
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