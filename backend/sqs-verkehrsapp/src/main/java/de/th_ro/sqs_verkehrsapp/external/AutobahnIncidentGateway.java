package de.th_ro.sqs_verkehrsapp.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.th_ro.sqs_verkehrsapp.incidents.Incident;
import de.th_ro.sqs_verkehrsapp.incidents.IncidentCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AutobahnIncidentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutobahnIncidentGateway.class);
    private static final TypeReference<List<Incident>> INCIDENT_LIST = new TypeReference<>() {
    };

    private final AutobahnApiClient autobahnApiClient;
    private final IncidentCacheEntryRepository cacheRepository;
    private final ObjectMapper objectMapper;
    private final AutobahnApiProperties properties;

    public AutobahnIncidentGateway(
            AutobahnApiClient autobahnApiClient,
            IncidentCacheEntryRepository cacheRepository,
            ObjectMapper objectMapper,
            AutobahnApiProperties properties
    ) {
        this.autobahnApiClient = autobahnApiClient;
        this.cacheRepository = cacheRepository;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public IncidentQueryResult loadIncidents(List<String> roads) {
        List<Incident> incidents = new ArrayList<>();
        boolean hasLive = false;
        boolean hasCache = false;

        for (String road : roads) {
            for (IncidentCategory category : IncidentCategory.values()) {
                Optional<IncidentCacheEntry> cachedEntry = cacheRepository.findByRoadIdAndCategory(road, category.name());

                try {
                    List<Incident> liveIncidents = autobahnApiClient.fetchIncidents(road, category)
                            .stream()
                            .map(incident -> incident.withSource(DataSourceType.LIVE))
                            .toList();
                    incidents.addAll(liveIncidents);
                    writeCache(road, category, liveIncidents, cachedEntry.orElseGet(IncidentCacheEntry::new));
                    hasLive = true;
                } catch (RuntimeException exception) {
                    if (cachedEntry.isPresent() && isFresh(cachedEntry.get())) {
                        incidents.addAll(readCache(cachedEntry.get(), DataSourceType.CACHE));
                        hasCache = true;
                    } else {
                        LOGGER.warn("No cache available for road {} and category {}.", road, category, exception);
                    }
                }
            }
        }

        return new IncidentQueryResult(incidents, DataSourceType.combine(hasLive, hasCache), Instant.now());
    }

    public List<String> loadAvailableRoads() {
        return autobahnApiClient.fetchRoads();
    }

    private boolean isFresh(IncidentCacheEntry entry) {
        return entry.getCachedAt().isAfter(Instant.now().minus(properties.cacheTtl()));
    }

    private List<Incident> readCache(IncidentCacheEntry entry, DataSourceType source) {
        try {
            return objectMapper.readValue(entry.getPayloadJson(), INCIDENT_LIST)
                    .stream()
                    .map(incident -> incident.withSource(source))
                    .toList();
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Cache konnte nicht gelesen werden.", exception);
        }
    }

    private void writeCache(String road, IncidentCategory category, List<Incident> incidents, IncidentCacheEntry entry) {
        try {
            entry.setRoadId(road);
            entry.setCategory(category.name());
            entry.setPayloadJson(objectMapper.writeValueAsString(incidents));
            cacheRepository.save(entry);
        } catch (JsonProcessingException exception) {
            LOGGER.warn("Cache payload for road {} and category {} could not be serialized.", road, category, exception);
        } catch (RuntimeException exception) {
            LOGGER.warn("Cache for road {} and category {} could not be updated.", road, category, exception);
        }
    }
}
