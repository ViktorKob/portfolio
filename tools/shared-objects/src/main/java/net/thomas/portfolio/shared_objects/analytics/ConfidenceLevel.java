package net.thomas.portfolio.shared_objects.analytics;

import io.swagger.annotations.ApiModel;

@ApiModel("Level of confidence in the information returned")
public enum ConfidenceLevel {
	UNLIKELY,
	POSSIBLY,
	CERTAIN
}