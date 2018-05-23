package net.thomas.portfolio.render.service;

import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_DATATYPE;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.enums.Service.HBASE_INDEXING_SERVICE;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_HTML_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_TEXT_PATH;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.PreSerializedParameter;
import net.thomas.portfolio.hbase_index.fake.HbaseIndexSchemaImpl;
import net.thomas.portfolio.render.context.HtmlRenderContextBuilder;
import net.thomas.portfolio.render.context.TextRenderContextBuilder;
import net.thomas.portfolio.render.html.HtmlRenderControl;
import net.thomas.portfolio.render.text.TextRenderControl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;

@Controller
public class RenderServiceController {

	private final RenderServiceConfiguration configuration;
	private final TextRenderControl textRenderControl;
	private final HtmlRenderControl htmlRenderControl;
	@Autowired
	private EurekaClient discoveryClient;
	private HttpRestClient hbaseIndexClient;
	private HbaseIndexSchemaImpl schema;

	public RenderServiceController(RenderServiceConfiguration configuration) {
		this.configuration = configuration;
		textRenderControl = new TextRenderControl();
		htmlRenderControl = new HtmlRenderControl();
	}

	@PostConstruct
	public void prepareForRendering() {
		hbaseIndexClient = new HttpRestClient(discoveryClient, getRestTemplate(), configuration.getHbaseIndexing());
		schema = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HbaseIndexSchemaImpl.class);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_TEXT_PATH)
	public ResponseEntity<String> renderAsText(String type, String uid) {
		final Datatype datatype = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATATYPE, Datatype.class,
				new PreSerializedParameter("type", type), new PreSerializedParameter("uid", uid));
		return ResponseEntity.ok(textRenderControl.render(datatype, new TextRenderContextBuilder().build()));
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_HTML_PATH)
	public ResponseEntity<String> renderAsHtml(String type, String uid) {
		final Datatype datatype = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATATYPE, Datatype.class,
				new PreSerializedParameter("type", type), new PreSerializedParameter("uid", uid));
		return ResponseEntity.ok(htmlRenderControl.render(datatype, new HtmlRenderContextBuilder().build()));
	}
}