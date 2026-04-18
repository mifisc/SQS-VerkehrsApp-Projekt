package de.th_ro.sqs_verkehrsapp.incidents;

public enum IncidentCategory {
    WARNING("warning", "warning", "Gefahrenmeldung", 18),
    ROADWORK("roadworks", "roadworks", "Baustelle", 12),
    CLOSURE("closure", "closure", "Sperrung", 30);

    private final String apiPath;
    private final String responseField;
    private final String label;
    private final int baseRisk;

    IncidentCategory(String apiPath, String responseField, String label, int baseRisk) {
        this.apiPath = apiPath;
        this.responseField = responseField;
        this.label = label;
        this.baseRisk = baseRisk;
    }

    public String apiPath() {
        return apiPath;
    }

    public String responseField() {
        return responseField;
    }

    public String label() {
        return label;
    }

    public int baseRisk() {
        return baseRisk;
    }
}
