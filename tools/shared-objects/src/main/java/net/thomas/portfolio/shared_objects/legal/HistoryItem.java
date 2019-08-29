package net.thomas.portfolio.shared_objects.legal;

import static lombok.AccessLevel.PRIVATE;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
@ToString
public class HistoryItem {
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
}