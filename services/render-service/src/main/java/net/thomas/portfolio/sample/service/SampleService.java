package net.thomas.portfolio.sample.service;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class SampleService {

	@Secured("ROLE_USER")
	public String secure() {
		return "Connection is \"secure\".";
	}

	@PreAuthorize("true")
	public String authorized() {
		return "Authorized user";
	}

	@PreAuthorize("false")
	public String unauthorized() {
		return "Access denied";
	}
}
