package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class GeoLocationSerializer extends StdSerializer<GeoLocation> {
	private static final long serialVersionUID = 1L;

	public GeoLocationSerializer() {
		this(null);
	}

	public GeoLocationSerializer(Class<GeoLocation> type) {
		super(type);
	}

	@Override
	public void serialize(GeoLocation entity, JsonGenerator generator, SerializerProvider serializers) throws IOException {
		generator.writeStartObject();
		generator.writeFieldName("dataType");
		generator.writeObject("GeoLocation");
		generator.writeFieldName("longitude");
		generator.writeNumber(entity.getLongitude());
		generator.writeFieldName("latitude");
		generator.writeNumber(entity.getLatitude());
		generator.writeEndObject();
	}
}