package com.micro.demo.entities.enums;

public enum TipoCompetencia {
    GENERICA("1", "Genérica"),
    DISCIPLINARIA("2", "Disciplinaria");

    private final String value;
    private final String label;

    TipoCompetencia(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static TipoCompetencia fromValue(String value) {
        for (TipoCompetencia tipo : TipoCompetencia.values()) {
            if (tipo.value.equals(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

    @Override
    public String toString() {
        return label;
    }
}
