package com.micro.demo.entities.enums;

public enum Semesters {
    SEMESTER_1("Semestre 1", 1),
    SEMESTER_2("Semestre 2", 2),
    SEMESTER_3("Semestre 3", 3),
    SEMESTER_4("Semestre 4", 4),
    SEMESTER_5("Semestre 5", 5),
    SEMESTER_6("Semestre 6", 6),
    SEMESTER_7("Semestre 7", 7),
    SEMESTER_8("Semestre 8", 8),
    SEMESTER_9("Semestre 9", 9),
    SEMESTER_10("Semestre 10", 10);

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

