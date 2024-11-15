package com.micro.demo.entities.enums;

public enum ElectivaSociohumanistica {
    PROBLEMAS_SOCIALES_EN_LA_FRONTERA("1150511", "PROBLEMAS SOCIALES EN LA FRONTERA"),
    NUEVAS_TECNOLOGIAS_Y_SOCIEDAD("1150521", "NUEVAS TECNOLOGIAS Y SOCIEDAD"),
    PSICOLOGIA("1150513", "PSICOLOGIA"),
    RELACIONES_HUMANAS("1150514", "RELACIONES HUMANAS"),
    PSICOLOGIA_INDUSTRIAL("1150515", "PSICOLOGIA INDUSTRIAL"),
    PROYECTO_SOCIAL_ING_DE_SISTEMAS("1150519", "PROYECTO SOCIAL ING DE SISTEMAS");

    private final String codigo;
    private final String nombre;

    ElectivaSociohumanistica(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
}
