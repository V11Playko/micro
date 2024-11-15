package com.micro.demo.entities.enums;

public enum Programs {
    SYSTEMS_ENGINEERING("Ingeniería de sistemas", 1);

    private final String label;
    private final int value;

    Programs(String label, int value) {
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
