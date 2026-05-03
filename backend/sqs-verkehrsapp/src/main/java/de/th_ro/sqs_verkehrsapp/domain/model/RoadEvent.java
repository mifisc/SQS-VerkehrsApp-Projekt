package de.th_ro.sqs_verkehrsapp.domain.model;

public record RoadEvent(String id, String roadId, String title, String subtitle, String description, RoadEventType type, Coordinate coordinate, RiskLevel riskLevel) {
}
