package net.thomas.portfolio.shared_objects.legal;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
@ToString
public class HistoryItem extends ResourceSupport {
	@ApiModelProperty(value = "Should be ignored", example = "0")
	@NonNull
	private int itemId;
	@ApiModelProperty(value = "Should be ignored", example = "")
	@NonNull
	private LegalQueryType type;
	@ApiModelProperty(value = "Should be ignored", example = "0")
	@NonNull
	private long timeOfLogging;
	@NonNull
	private DataTypeId selectorId;
	@NonNull
	private LegalInformation legalInfo;

	@ApiModelProperty("Id of the log item as an integer >= 0")
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	@ApiModelProperty("The type of query logged")
	public LegalQueryType getType() {
		return type;
	}

	public void setType(LegalQueryType type) {
		this.type = type;
	}

	@ApiModelProperty("The time of logging as a Unix timestamp * 1000")
	public long getTimeOfLogging() {
		return timeOfLogging;
	}

	public void setTimeOfLogging(long timeOfLogging) {
		this.timeOfLogging = timeOfLogging;
	}

	public DataTypeId getSelectorId() {
		return selectorId;
	}

	public void setSelectorId(DataTypeId selectorId) {
		this.selectorId = selectorId;
	}

	public LegalInformation getLegalInfo() {
		return legalInfo;
	}

	public void setLegalInfo(LegalInformation legalInfo) {
		this.legalInfo = legalInfo;
	}
}