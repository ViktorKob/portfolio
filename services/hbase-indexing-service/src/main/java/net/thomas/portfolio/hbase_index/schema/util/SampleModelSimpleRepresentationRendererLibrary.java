package net.thomas.portfolio.hbase_index.schema.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Renderer;

public class SampleModelSimpleRepresentationRendererLibrary implements Renderer<String> {
	private final SampleModelSimpleRepresentationRendererLibrary library;
	private final Map<String, Renderer<String>> renderers;

	public SampleModelSimpleRepresentationRendererLibrary() {
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
	public String render(DataType element) {
		if (renderers.containsKey(element.getType())) {
			return renderers.get(element.getType()).render(element);
		} else {
			return "<Unable to render element of type " + element.getType() + ">";
		}
	}

	private class SimpleFieldRenderer implements Renderer<String> {
		private final String field;

		public SimpleFieldRenderer(String field) {
			this.field = field;
		}

		@Override
		public String render(DataType element) {
			return String.valueOf(element.get(field));
		}
	}

	private class DomainRenderer implements Renderer<String> {
		@Override
		public String render(DataType element) {
			final String domainPart = String.valueOf(element.get("domainPart"));
			if (element.get("domain") != null) {
				return domainPart + "." + render((DataType) element.get("domain"));
			} else {
				return domainPart;
			}
		}
	}

	private class EmailAddressRenderer implements Renderer<String> {
		@Override
		public String render(DataType element) {
			return library.render((DataType) element.get("localname")) + "@" + library.render((DataType) element.get("domain"));
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
