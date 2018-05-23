package net.thomas.portfolio.hbase_index.schema.util;

import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.RecognitionLevel.KNOWN;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.RecognitionLevel.UNKNOWN;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Renderer;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter.SimpleDateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class SampleModelHeadlineRendererLibrary implements Renderer<String> {
	private final SampleModelHeadlineRendererLibrary library;
	private final SimpleDateConverter converter;
	private final Map<String, Renderer<String>> renderers;
	private final HbaseModelAdaptor adaptor;

	public SampleModelHeadlineRendererLibrary(HbaseModelAdaptor adaptor) {
		this.adaptor = adaptor;
		library = this;
		converter = new DateConverter.SimpleDateConverter();
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
	public String render(Datatype element) {
		if (renderers.containsKey(element.getType())) {
			return renderers.get(element.getType())
				.render(element);
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
		public String render(Datatype element) {
			String headline = String.valueOf(element.get(field));
			if (adaptor.isSelector(element.getType()) && requiresJustification((Selector) element)) {
				headline += "!";
			}
			return headline;
		}
	}

	private class DomainRenderer implements Renderer<String> {
		@Override
		public String render(Datatype element) {
			final String domainPart = String.valueOf(element.get("domainPart"));
			if (element.get("domain") != null) {
				return domainPart + "." + render((Datatype) element.get("domain"));
			} else {
				return domainPart;
			}
		}
	}

	private class EmailAddressRenderer implements Renderer<String> {
		@Override
		public String render(Datatype element) {
			String headline = library.render((Datatype) element.get("localname")) + "@" + library.render((Datatype) element.get("domain"));
			if (requiresJustification((Selector) element)) {
				headline += "!";
			}
			return headline;
		}
	}

	private class EmailEndpointRenderer implements Renderer<String> {
		@Override
		public String render(Datatype element) {
			if (element.containsKey("displayedName")) {
				return library.render((Datatype) element.get("displayedName")) + " <( " + library.render((Datatype) element.get("address")) + " )>";
			} else {
				return library.render((Datatype) element.get("address"));
			}
		}
	}

	private class PstnEndpointRenderer implements Renderer<String> {
		@Override
		public String render(Datatype element) {
			String rendering = "";
			if (element.containsKey("pstn")) {
				rendering += "Pstn: " + library.render((Datatype) element.get("pstn"));
			}
			if (element.containsKey("imsi")) {
				if (rendering.length() > 0) {
					rendering += ", ";
				}
				rendering += "Imsi: " + library.render((Datatype) element.get("imsi"));
			}
			if (element.containsKey("imei")) {
				if (rendering.length() > 0) {
					rendering += ", ";
				}
				rendering += "Imei: " + library.render((Datatype) element.get("imei"));
			}
			return rendering;
		}
	}

	private class EmailRenderer implements Renderer<String> {
		@Override
		public String render(Datatype element) {
			final Document document = (Document) element;
			final String headline = library.render((Datatype) element.get("from")) + " - " + converter.formatTimestamp(document.getTimeOfEvent()) + ": "
					+ element.get("subject");
			if (headline.length() > 250) {
				return headline.substring(0, 250);
			} else {
				return headline;
			}
		}
	}

	private class SmsRenderer implements Renderer<String> {
		@Override
		public String render(Datatype element) {
			final Document document = (Document) element;
			final String headline = library.render((Datatype) element.get("sender")) + " - " + converter.formatTimestamp(document.getTimeOfEvent()) + ": "
					+ element.get("message");
			if (headline.length() > 250) {
				return headline.substring(0, 250);
			} else {
				return headline;
			}
		}
	}

	private class VoiceRenderer implements Renderer<String> {
		@Override
		public String render(Datatype element) {
			final Document document = (Document) element;
			final int duration = (Integer) element.get("durationIsSeconds");
			final String headline = library.render((Datatype) element.get("caller")) + " - " + converter.formatTimestamp(document.getTimeOfEvent())
					+ ": call final duration was " + duration / 60 + "m " + duration % 60 + "s";
			if (headline.length() > 250) {
				return headline.substring(0, 250);
			} else {
				return headline;
			}
		}
	}

	private boolean requiresJustification(Selector selector) {
		// final PreviousKnowledge knowledge = adaptor.getPreviousKnowledgeFor(selector);
		final PreviousKnowledge knowledge = new PreviousKnowledge(UNKNOWN, UNKNOWN);
		return knowledge.isDanish == KNOWN;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
