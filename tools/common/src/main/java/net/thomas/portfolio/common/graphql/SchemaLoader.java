package net.thomas.portfolio.common.graphql;

import java.util.Map.Entry;

import org.springframework.web.client.RestTemplate;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

public class SchemaLoader {

	public static void main(String[] args) {
		final RestTemplate rest = new RestTemplate();
		final String schemaAsJson = rest.getForObject("http://localhost:8110/schema.json", String.class);
		System.out.println("Schema:");
		System.out.println(schemaAsJson);
		try {
			final Object schema = Jsoner.deserialize(schemaAsJson);
			if (schema instanceof JsonObject) {
				printNode((JsonObject) schema);
			}
		} catch (final JsonException e) {
			e.printStackTrace();
		}
	}

	private static void printNode(JsonObject node) {
		for (final Entry<String, Object> entry : node.entrySet()) {
			if (entry.getValue() instanceof JsonObject) {
				System.out.println(entry.getKey() + ":");
				printNode((JsonObject) entry.getValue());
			} else if (entry.getValue() instanceof JsonArray) {
				printNode((JsonArray) entry.getValue());
			} else {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
		}
	}

	private static void printNode(JsonArray array) {
		for (final Object subNode : array) {
			System.out.println("[");
			if (subNode instanceof JsonObject) {
				printNode((JsonObject) subNode);
			}
			System.out.println("]");
		}
	}
}