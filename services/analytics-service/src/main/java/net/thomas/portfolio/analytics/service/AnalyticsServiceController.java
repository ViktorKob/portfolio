package net.thomas.portfolio.analytics.service;

import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_KNOWLEDGE_PATH;
import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_KNOWLEDGE_ROOT_PATH;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.analytics.system.AnalyticsControl;
import net.thomas.portfolio.common.services.parameters.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.HttpRestClientInitializable;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@Controller
public class AnalyticsServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final AnalyticsServiceConfiguration config;
	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;
	private AnalyticsControl analyticsSystem;

	public AnalyticsServiceController(AnalyticsServiceConfiguration config) {
		this.config = config;
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean(name = "HbaseIndexModelAdaptor")
	public HbaseIndexModelAdaptor getHbaseIndexModelAdaptor() {
		return new HbaseIndexModelAdaptorImpl();
	}

	@PostConstruct
	public void initialize() {
		new Thread(() -> {
			((HttpRestClientInitializable) hbaseAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getHbaseIndexing()));
			TYPE.setValidStrings(hbaseAdaptor.getDataTypes());
		}).start();
		analyticsSystem = new AnalyticsControl();
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = LOOKUP_KNOWLEDGE_ROOT_PATH + "/{dti_type}/{dti_uid}" + LOOKUP_KNOWLEDGE_PATH, method = GET)
	public ResponseEntity<?> lookupPriorKnowledge(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			return ok(analyticsSystem.getPriorKnowledge(id));
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}
}