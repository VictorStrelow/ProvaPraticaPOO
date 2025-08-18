package com.provapoo.model;

public class Medicao {
    private double valor;
    private String dataHora; // formato dd/MM/yyyy HH:mm

    public Medicao(double valor, String dataHora) {
        this.valor = valor;
        this.dataHora = dataHora;
    }

    // Getters e Setters
    public double getValor() {
        return valor;
    }
    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDataHora() {
        return dataHora;
    }
    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return String.format("Valor: %.3f | Data: %s", valor, dataHora);
    }
}