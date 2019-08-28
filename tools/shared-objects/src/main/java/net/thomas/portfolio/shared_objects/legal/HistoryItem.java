package net.thomas.portfolio.shared_objects.legal;

import static lombok.AccessLevel.PRIVATE;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@RequiredArgsConstructor(access = PRIVATE)
@Builder
@Getter
@Setter
@ToString
public class HistoryItem extends ResourceSupport {
	@ApiModelProperty(value = "Internal id of logged item as integer", example = "1234")
	@NonNull
	private int itemId;
	@ApiModelProperty(value = "Type of query that was logged")
	@NonNull
	private LegalQueryType type;
	@ApiModelProperty(value = "Time of logging for item as Unix timestamp with milliseconds", example = "1415463675")
	@NonNull
	@Builder.Default
	private long timeOfLogging = System.currentTimeMillis();
	@NonNull
	private DataTypeId selectorId;
	@NonNull
	private LegalInformation legalInfo;

	@ApiModelProperty("Id of the log item as an integer >= 0")
	public int getHI_ItemId() {
		return itemId;
	}

	public void setHI_ItemId(int itemId) {
		this.itemId = itemId;
	}

	@ApiModelProperty("The type of query logged")
	public LegalQueryType getHI_Type() {
		return type;
	}

	public void setHI_Type(LegalQueryType type) {
		this.type = type;
	}

	@ApiModelProperty("The time of logging as a Unix timestamp * 1000")
	public long getHI_TimeOfLogging() {
		return timeOfLogging;
	}

	public void setHI_TimeOfLogging(long timeOfLogging) {
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