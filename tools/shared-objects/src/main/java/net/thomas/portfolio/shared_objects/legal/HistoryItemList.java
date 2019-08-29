package net.thomas.portfolio.shared_objects.legal;

import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class HistoryItemList extends LinkedList<HistoryItem> {
	private static final long serialVersionUID = 1L;
}