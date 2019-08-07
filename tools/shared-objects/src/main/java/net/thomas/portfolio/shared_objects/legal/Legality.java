package net.thomas.portfolio.shared_objects.legal;

import io.swagger.annotations.ApiModel;

@ApiModel("Legal status of the query in question")
public enum Legality {
	LEGAL,
	ILLEGAL
}