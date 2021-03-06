package net.thomas.portfolio.nexus.service;

import static net.thomas.portfolio.services.ServiceGlobals.NEXUS_SERVICE_PATH;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;

import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.HeadersBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@RestController
public class OptionsController {
	@RequestMapping(path = NEXUS_SERVICE_PATH + "/graphql", method = OPTIONS)
	public ResponseEntity<String> renderAsSimpleRepresentation(DataTypeId id) {
		final HeadersBuilder<?> response = noContent();
		response.header("Access-Control-Allow-Origin", "*");
		response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.header("Access-Control-Allow-Headers",
				"DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range");
		response.header("Access-Control-Max-Age", "1728000");
		return response.build();
	}
}