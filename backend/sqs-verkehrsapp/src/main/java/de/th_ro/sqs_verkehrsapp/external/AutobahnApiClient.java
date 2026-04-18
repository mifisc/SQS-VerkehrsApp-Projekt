package de.th_ro.sqs_verkehrsapp.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.th_ro.sqs_verkehrsapp.incidents.Incident;
import de.th_ro.sqs_verkehrsapp.incidents.IncidentCategory;
import de.th_ro.sqs_verkehrsapp.shared.RoadNormalizer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AutobahnApiClient {
    private static final List<DateTimeFormatter> AUTOBANH_TIMESTAMPS = List.of(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
    );

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AutobahnApiProperties properties;

    public AutobahnApiClient(HttpClient httpClient, ObjectMapper objectMapper, AutobahnApiProperties properties) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public List<Incident> fetchIncidents(String roadId, IncidentCategory category) {
        RuntimeException lastException = null;

        for (int attempt = 0; attempt <= properties.maxRetries(); attempt++) {
            try {
                return doFetch(roadId, category);
            } catch (IOException | InterruptedException exception) {
                if (exception instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                lastException = new RuntimeException("Autobahn API ist aktuell nicht erreichbar.", exception);
            }
        }

        throw lastException == null ? new RuntimeException("Autobahn API ist aktuell nicht erreichbar.") : lastException;
    }

    public List<String> fetchRoads() {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(properties.baseUrl() + "/"))
                    .GET()
                    .timeout(properties.requestTimeout())
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException("Unexpected status code: " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode roadsNode = root.path("roads");
            if (!roadsNode.isArray()) {
                return List.of();
            }

            List<String> roads = new ArrayList<>();
            for (JsonNode roadNode : roadsNode) {
                roads.add(roadNode.asText(""));
            }
            return RoadNormalizer.normalize(roads);
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Autobahn-Liste konnte nicht geladen werden.", exception);
        }
    }

    private List<Incident> doFetch(String roadId, IncidentCategory category) throws IOException, InterruptedException {
        String encodedRoad = URLEncoder.encode(roadId, StandardCharsets.UTF_8);
        String endpoint = properties.baseUrl() + "/" + encodedRoad + "/services/" + category.apiPath();

        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .GET()
                .timeout(properties.requestTimeout())
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 204 || response.statusCode() == 404) {
            return List.of();
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Unexpected status code: " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode itemsNode = root.path(category.responseField());
        if (!itemsNode.isArray()) {
            return List.of();
        }

        List<Incident> incidents = new ArrayList<>();
        for (JsonNode item : itemsNode) {
            JsonNode coordinate = item.path("coordinate");
            String latitude = coordinate.path("lat").asText("");
            String longitude = coordinate.path("long").asText("");

            if (latitude.isBlank() || longitude.isBlank()) {
                continue;
            }

            incidents.add(new Incident(
                    item.path("identifier").asText(),
                    roadId,
                    category,
                    item.path("title").asText("Ohne Titel"),
                    item.path("subtitle").asText(""),
                    toDescription(item.path("description")),
                    Double.parseDouble(latitude),
                    Double.parseDouble(longitude),
                    Boolean.parseBoolean(item.path("isBlocked").asText("false")),
                    item.path("future").asBoolean(false),
                    parseTimestamp(item.path("startTimestamp").asText("")),
                    DataSourceType.LIVE
            ));
        }

        return incidents;
    }

    private List<String> toDescription(JsonNode descriptionNode) {
        if (!descriptionNode.isArray()) {
            return List.of();
        }

        List<String> description = new ArrayList<>();
        for (JsonNode line : descriptionNode) {
            String value = line.asText("").trim();
            if (!value.isBlank()) {
                description.add(value);
            }
        }
        return description;
    }

    private Instant parseTimestamp(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        for (DateTimeFormatter formatter : AUTOBANH_TIMESTAMPS) {
            try {
                return OffsetDateTime.parse(value, formatter).toInstant();
            } catch (DateTimeParseException ignored) {
                // Try the next known Autobahn API timestamp format.
            }
        }

        throw new RuntimeException("Zeitstempel der Autobahn API konnte nicht gelesen werden: " + value);
    }
}
