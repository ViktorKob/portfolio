package net.thomas.portfolio.render.format.text;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.render.common.Renderer;
import net.thomas.portfolio.render.common.context.TextRenderContext;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter.Iec8601DateConverter;

public class HbaseIndexingModelTextRendererLibrary implements Renderer<String, TextRenderContext> {
	private final HbaseIndexingModelTextRendererLibrary library;
	private final Iec8601DateConverter converter;
	private final Map<String, Renderer<String, TextRenderContext>> renderers;

	public HbaseIndexingModelTextRendererLibrary() {
		library = this;
		converter = new DateConverter.Iec8601DateConverter();
		renderers = new HashMap<>();
		renderers.put("Localname", new SimpleFieldRenderer("name"));
		renderers.put("DisplayedName", new SimpleFieldRenderer("name"));
		renderers.put("Pstn", new SimpleFieldRenderer("number"));
		renderers.put("Imsi", new SimpleFieldRenderer("number"));
		renderers.put("Imei", new SimpleFieldRenderer("number"));
		renderers.put("Domain", new DomainRenderer());
		renderers.put("EmailAddress", new EmailAddressRenderer());
		renderers.put("EmailEndpoint", new EmailEndpointRenderer());
		renderers.put("PstnEndpoint", new PstnEndpointRenderer());
		renderers.put("Email", new EmailRenderer());
		renderers.put("Sms", new SmsRenderer());
		renderers.put("Voice", new VoiceRenderer());
	}

	@Override
	public String render(DataType element, TextRenderContext context) {
		final String type = element.getId().type;
		if (renderers.containsKey(type)) {
			return renderers.get(type)
				.render(element, context);
		} else {
			return "<Unable to render element of type " + element.getId().type + ">";
		}
	}

	private class SimpleFieldRenderer implements Renderer<String, TextRenderContext> {
		private final String field;

		public SimpleFieldRenderer(String field) {
			this.field = field;
		}

		@Override
		public String render(DataType element, TextRenderContext context) {
			return element.get(field)
				.toString();
		}
	}

	private class DomainRenderer implements Renderer<String, TextRenderContext> {
		@Override
		public String render(DataType element, TextRenderContext context) {
			final String domainPart = element.get("domainPart");
			if (element.get("domain") != null) {
				return domainPart + "." + render(element.get("domain"), context);
			} else {
				return domainPart;
			}
		}
	}

	private class EmailAddressRenderer implements Renderer<String, TextRenderContext> {
		@Override
		public String render(DataType element, TextRenderContext context) {
			final String headline = library.render(element.get("localname"), context) + "@" + library.render(element.get("domain"), context);
			return headline;
		}
	}

	private class EmailEndpointRenderer implements Renderer<String, TextRenderContext> {
		@Override
		public String render(DataType element, TextRenderContext context) {
			if (element.containsKey("displayedName")) {
				return library.render(element.get("displayedName"), context) + " <( " + library.render(element.get("address"), context) + " )>";
			} else {
				return library.render(element.get("address"), context);
			}
		}
	}

	private class PstnEndpointRenderer implements Renderer<String, TextRenderContext> {
		@Override
		public String render(DataType element, TextRenderContext context) {
			String rendering = "";
			if (element.containsKey("pstn")) {
				rendering += "Pstn: " + library.render(element.get("pstn"), context);
			}
			if (element.containsKey("imsi")) {
				if (rendering.length() > 0) {
					rendering += ", ";
				}
				rendering += "Imsi: " + library.render(element.get("imsi"), context);
			}
			if (element.containsKey("imei")) {
				if (rendering.length() > 0) {
					rendering += ", ";
				}
				rendering += "Imei: " + library.render(element.get("imei"), context);
			}
			return rendering;
		}
	}

	private class EmailRenderer implements Renderer<String, TextRenderContext> {
		@Override
		public String render(DataType element, TextRenderContext context) {
			final Document document = (Document) element;
			final String headline = library.render(element.get("from"), context) + " - " + converter.formatTimestamp(document.getTimeOfEvent()) + ": "
					+ element.get("subject");
			if (headline.length() > 250) {
				return headline.substring(0, 250);
			} else {
				return headline;
			}
		}
	}

	private class SmsRenderer implements Renderer<String, TextRenderContext> {
		@Override
		public String render(DataType element, TextRenderContext context) {
			final Document document = (Document) element;
			final String headline = library.render(element.get("sender"), context) + " - " + converter.formatTimestamp(document.getTimeOfEvent()) + ": "
					+ element.get("message");
			if (headline.length() > 250) {
				return headline.substring(0, 250);
			} else {
				return headline;
			}
		}
	}

	private class VoiceRenderer implements Renderer<String, TextRenderContext> {
		@Override
		public String render(DataType element, TextRenderContext context) {
			final Document document = (Document) element;
			final int duration = element.get("durationIsSeconds");
			final String headline = library.render(element.get("caller"), context) + " - " + converter.formatTimestamp(document.getTimeOfEvent())
					+ ": call final duration was " + duration / 60 + "m " + duration % 60 + "s";
			if (headline.length() > 250) {
				return headline.substring(0, 250);
			} else {
				return headline;
			}
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
