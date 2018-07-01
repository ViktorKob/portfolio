package net.thomas.portfolio.analytics.service;

import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_KNOWLEDGE_PATH;
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

import net.thomas.portfolio.analytics.system.FakeAnalyticsSystem;
import net.thomas.portfolio.common.services.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@Controller
public class AnalyticsServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final AnalyticsServiceConfiguration config;
	private final FakeAnalyticsSystem analyticsSystem;
	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;

	public AnalyticsServiceController(AnalyticsServiceConfiguration config) {
		this.config = config;
		analyticsSystem = new FakeAnalyticsSystem();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public HbaseIndexModelAdaptor getHbaseIndexModelAdaptor() {
		return new HbaseIndexModelAdaptorImpl();
	}

	@PostConstruct
	public void initialize() {
		((HbaseIndexModelAdaptorImpl) hbaseAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getHbaseIndexing()));
		new Thread(() -> {
			TYPE.setValidStrings(hbaseAdaptor.getDataTypes());
		}).run();
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = LOOKUP_KNOWLEDGE_PATH + "/{dti_type}/{dti_uid}", method = GET)
	public ResponseEntity<?> lookupPriorKnowledge(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			return ok(analyticsSystem.getPriorKnowledge(id));
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}
}