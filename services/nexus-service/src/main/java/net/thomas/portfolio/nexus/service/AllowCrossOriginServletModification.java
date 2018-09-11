package net.thomas.portfolio.nexus.service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.servlet.GraphQLServletListener;
import graphql.servlet.SimpleGraphQLHttpServlet;

@Component
public class AllowCrossOriginServletModification {
	@Autowired
	private SimpleGraphQLHttpServlet servlet;

	@PostConstruct
	public void modifyServlet() {
		servlet.addListener(new CustomListener());
	}

	class CustomListener implements GraphQLServletListener {
		@Override
		public RequestCallback onRequest(HttpServletRequest request, HttpServletResponse response) {
			response.addHeader("Access-Control-Allow-Origin", "*");
			return null;
		}
	}
}