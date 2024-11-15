package com.micro.demo.entities.enums;

public enum CreditsType {
    ELECTIVE("Electivo", 1),
    MANDATORY("Obligatorio", 2);

    private final String label;
    private final int value;

    CreditsType(String label, int value) {
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
