package com.provapoo.model;

public class SensorVibracao extends Sensor {

    public static final double VALOR_ESPERADO = 60.0;
    public static final double TOLERANCIA = 0.0001;

    public SensorVibracao(String codigo, String nomeEquipamento) {
        super(codigo, nomeEquipamento, "Vibracao");
    }

    @Override
    public boolean verificarAlerta(Medicao medicao) {
        // ALERTA se diferente de 60.0 Hz (com pequena tolerância)
        return Math.abs(medicao.getValor() - VALOR_ESPERADO) > TOLERANCIA;
    }

}