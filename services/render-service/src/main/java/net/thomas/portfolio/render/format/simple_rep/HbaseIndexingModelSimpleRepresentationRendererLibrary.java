package net.thomas.portfolio.render.format.simple_rep;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.render.common.Renderer;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContext;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public class HbaseIndexingModelSimpleRepresentationRendererLibrary implements Renderer<String, SimpleRepresentationRenderContext> {
	private final HbaseIndexingModelSimpleRepresentationRendererLibrary library;
	private final Map<String, Renderer<String, SimpleRepresentationRenderContext>> renderers;

	public HbaseIndexingModelSimpleRepresentationRendererLibrary() {
		library = this;
		renderers = new HashMap<>();
		renderers.put("Localname", new SimpleFieldRenderer("name"));
		renderers.put("DisplayedName", new SimpleFieldRenderer("name"));
		renderers.put("PublicId", new SimpleFieldRenderer("number"));
		renderers.put("PrivateId", new SimpleFieldRenderer("number"));
		renderers.put("Domain", new DomainRenderer());
		renderers.put("EmailAddress", new EmailAddressRenderer());
	}

	@Override
	public String render(DataType element, SimpleRepresentationRenderContext context) {
		if (renderers.containsKey(element.getId().type)) {
			return renderers.get(element.getId().type)
				.render(element, context);
		} else {
			return "<Unable to render element of type " + element.getId().type + ">";
		}
	}

	private class SimpleFieldRenderer implements Renderer<String, SimpleRepresentationRenderContext> {
		private final String field;

		public SimpleFieldRenderer(String field) {
			this.field = field;
		}

		@Override
		public String render(DataType element, SimpleRepresentationRenderContext context) {
			return element.get(field)
				.toString();
		}
	}

	private class DomainRenderer implements Renderer<String, SimpleRepresentationRenderContext> {
		@Override
		public String render(DataType element, SimpleRepresentationRenderContext context) {
			final String domainPart = element.get("domainPart");
			if (element.get("domain") != null) {
				return domainPart + "." + render(element.get("domain"), context);
			} else {
				return domainPart;
			}
		}
	}

	private class EmailAddressRenderer implements Renderer<String, SimpleRepresentationRenderContext> {
		@Override
		public String render(DataType element, SimpleRepresentationRenderContext context) {
			return library.render(element.get("localname"), context) + "@" + library.render(element.get("domain"), context);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
