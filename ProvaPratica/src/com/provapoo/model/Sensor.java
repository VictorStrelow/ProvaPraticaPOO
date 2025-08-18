package com.provapoo.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Sensor {

    private String codigo;
    private String nomeEquipamento;
    private String tipo;

    private List<Medicao> medicoes;

    public Sensor(String codigo, String nomeEquipamento, String tipo) {
        this.codigo = codigo;
        this.nomeEquipamento = nomeEquipamento;
        this.tipo = tipo;
        this.medicoes = new ArrayList<>();
    }

    // Getters e Setters
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNomeEquipamento() {
        return nomeEquipamento;
    }
    public void setNomeEquipamento(String nomeEquipamento) {
        this.nomeEquipamento = nomeEquipamento;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Medicao> getMedicoes() {
        return medicoes;
    }
    public void adicionarMedicao(Medicao medicao) {
        this.medicoes.add(medicao);
    }

    public abstract boolean verificarAlerta(Medicao medicao);

    @Override
    public String toString() {
        return String.format("Código: %s | Tipo: %s | Equipamento: %s", codigo, tipo, nomeEquipamento);
    }

}