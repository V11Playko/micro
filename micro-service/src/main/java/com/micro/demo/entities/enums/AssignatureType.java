package com.micro.demo.entities.enums;

public enum AssignatureType {
    THEORETICAL("Teórico", 1),
    PRACTICAL("Práctico", 2);

    private final String label;
    private final int value;

    AssignatureType(String label, int value) {
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
