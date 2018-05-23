package net.thomas.portfolio.render.format.simple_rep;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.render.common.Renderer;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContext;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class HbaseIndexingModelSimpleRepresentationRendererLibrary implements Renderer<String, SimpleRepresentationRenderContext> {
	private final HbaseIndexingModelSimpleRepresentationRendererLibrary library;
	private final Map<String, Renderer<String, SimpleRepresentationRenderContext>> renderers;

	public HbaseIndexingModelSimpleRepresentationRendererLibrary() {
		library = this;
		renderers = new HashMap<>();
		renderers.put("Localname", new SimpleFieldRenderer("name"));
		renderers.put("DisplayedName", new SimpleFieldRenderer("name"));
		renderers.put("Pstn", new SimpleFieldRenderer("number"));
		renderers.put("Imsi", new SimpleFieldRenderer("number"));
		renderers.put("Imei", new SimpleFieldRenderer("number"));
		renderers.put("Domain", new DomainRenderer());
		renderers.put("EmailAddress", new EmailAddressRenderer());
	}

	@Override
	public String render(DataType element, SimpleRepresentationRenderContext context) {
		if (renderers.containsKey(element.getType())) {
			return renderers.get(element.getType())
				.render(element, context);
		} else {
			return "<Unable to render element of type " + element.getType() + ">";
		}
	}

	private class SimpleFieldRenderer implements Renderer<String, SimpleRepresentationRenderContext> {
		private final String field;

		public SimpleFieldRenderer(String field) {
			this.field = field;
		}

		@Override
		public String render(DataType element, SimpleRepresentationRenderContext context) {
			return String.valueOf(element.get(field));
		}
	}

	private class DomainRenderer implements Renderer<String, SimpleRepresentationRenderContext> {
		@Override
		public String render(DataType element, SimpleRepresentationRenderContext context) {
			final String domainPart = String.valueOf(element.get("domainPart"));
			if (element.get("domain") != null) {
				return domainPart + "." + render(DataType.from(element.get("domain")), context);
			} else {
				return domainPart;
			}
		}
	}

	private class EmailAddressRenderer implements Renderer<String, SimpleRepresentationRenderContext> {
		@Override
		public String render(DataType element, SimpleRepresentationRenderContext context) {
			return library.render(DataType.from(element.get("localname")), context) + "@" + library.render(DataType.from(element.get("domain")), context);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
