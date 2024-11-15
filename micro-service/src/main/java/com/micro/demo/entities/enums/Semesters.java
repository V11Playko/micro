package com.micro.demo.entities.enums;

public enum Semesters {
    SEMESTRE_1("Semestre 1", 1),
    SEMESTRE_2("Semestre 2", 2),
    SEMESTRE_3("Semestre 3", 3),
    SEMESTRE_4("Semestre 4", 4),
    SEMESTRE_5("Semestre 5", 5),
    SEMESTRE_6("Semestre 6", 6),
    SEMESTRE_7("Semestre 7", 7),
    SEMESTRE_8("Semestre 8", 8),
    SEMESTRE_9("Semestre 9", 9),
    SEMESTRE_10("Semestre 10", 10);

    private final String label;
    private final int value;

    Semesters(String label, int value) {
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

