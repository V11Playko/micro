package com.micro.demo.entities.enums;

public enum Faculties {
    ENGINEERING("Facultad de Ingeniería", 1),
    BASIC_SCIENCES("Facultad de Ciencias básicas", 2),
    AGRICULTURAL_SCIENCES("Facultad de Ciencias agrarias y del ambiente", 3),
    BUSINESS_SCIENCES("Facultad de Ciencias empresariales", 4),
    HEALTH_SCIENCES("Facultad de Ciencias de la salud", 5),
    SOCIAL_HUMANITIES("Facultad de Ciencias sociales y humanas", 6),
    EDUCATION_ARTS_HUMANITIES("Facultad de Educación, Artes y Humanidades", 7);

    private final String label;
    private final int value;

    Faculties(String label, int value) {
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
