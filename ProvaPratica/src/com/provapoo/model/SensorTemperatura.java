package com.provapoo.model;

public class SensorTemperatura extends Sensor {

    public static final double LIMITE_TEMPERATURA = 80.0;

    public SensorTemperatura(String codigo, String nomeEquipamento) {
        super(codigo, nomeEquipamento, "Temperatura");
    }

    @Override
    public boolean verificarAlerta(Medicao medicao) {
        return medicao.getValor() > LIMITE_TEMPERATURA;
    }

}