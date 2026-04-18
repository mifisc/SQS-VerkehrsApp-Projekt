package de.th_ro.sqs_verkehrsapp.shared;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class RoadNormalizer {
    private static final Pattern ROAD_PATTERN = Pattern.compile("^[A-Z][A-Z0-9/]{0,8}$");

    private RoadNormalizer() {
    }

    public static List<String> normalize(Collection<String> roads) {
        if (roads == null) {
            return List.of();
        }

        return roads.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> value.toUpperCase(Locale.ROOT))
                .filter(value -> ROAD_PATTERN.matcher(value).matches())
                .distinct()
                .toList();
    }
}
