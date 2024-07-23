package com.micro.demo.entities.enums;

public enum Semestre {
    SEMESTRE_1(1),
    SEMESTRE_2(2),
    SEMESTRE_3(3),
    SEMESTRE_5(5),
    SEMESTRE_6(6),
    SEMESTRE_7(7),
    SEMESTRE_8(8),
    SEMESTRE_9(9),
    SEMESTRE_10(10);

    private final int valor;

    Semestre(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

}
