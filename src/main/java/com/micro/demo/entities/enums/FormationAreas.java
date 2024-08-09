package com.micro.demo.entities.enums;

public enum FormationAreas {
    SOCIO_HUMANISTIC("Socio humanística", 1),
    PROFESSIONAL_SPECIFIC("Profesional específica", 2),
    BASIC_SCIENCES("Ciencias básicas", 3),
    APPLIED_BASIC_SCIENCES("Ciencias básicas aplicadas", 4);

    private final String label;
    private final int value;

    FormationAreas(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
}
