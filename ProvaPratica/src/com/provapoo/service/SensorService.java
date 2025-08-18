package com.provapoo.service;

import com.provapoo.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorService {
    private List<Sensor> sensores;

    public SensorService() {
        this.sensores = new ArrayList<>();
    }

    public boolean cadastrarSensor(Sensor sensor) {
        if (buscarSensorPorCodigo(sensor.getCodigo()) != null) {
            return false; // código já existe
        }
        sensores.add(sensor);
        return true;
    }

    public List<Sensor> listarSensores() {
        return sensores;
    }

    public Sensor buscarSensorPorCodigo(String codigo) {
        for (Sensor s : sensores) {
            if (s.getCodigo().equalsIgnoreCase(codigo)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Registra medição no sensor. Retorna uma mensagem com sucesso/erro e se gerou alerta.
     */
    public RegistrarResultado registrarMedicao(String codigoSensor, Medicao medicao) {
        Sensor s = buscarSensorPorCodigo(codigoSensor);
        if (s == null) {
            return new RegistrarResultado(false, "Sensor não encontrado.", false);
        }
        s.adicionarMedicao(medicao);
        boolean alerta = s.verificarAlerta(medicao);
        return new RegistrarResultado(true, "Medição registrada com sucesso.", alerta);
    }

    public List<Medicao> listarMedicoesDoSensor(String codigoSensor) {
        Sensor s = buscarSensorPorCodigo(codigoSensor);
        if (s == null) return null;
        return s.getMedicoes();
    }

    /**
     * Retorna mapa sensor -> quantidade de alertas ocorridos (por histórico)
     */
    public Map<Sensor, Integer> contarAlertasPorSensor() {
        Map<Sensor, Integer> mapa = new HashMap<>();
        for (Sensor s : sensores) {
            int count = 0;
            for (Medicao m : s.getMedicoes()) {
                if (s.verificarAlerta(m)) count++;
            }
            mapa.put(s, count);
        }
        return mapa;
    }

    public Map<Sensor, Integer> identificarSensoresCriticos(int limiar) {
        Map<Sensor, Integer> todos = contarAlertasPorSensor();
        Map<Sensor, Integer> criticos = new HashMap<>();
        for (Map.Entry<Sensor, Integer> e : todos.entrySet()) {
            if (e.getValue() > limiar) {
                criticos.put(e.getKey(), e.getValue());
            }
        }
        return criticos;
    }

    // Classe auxiliar para resultado de registro
    public static class RegistrarResultado {
        private boolean sucesso;
        private String mensagem;
        private boolean gerouAlerta;

        public RegistrarResultado(boolean sucesso, String mensagem, boolean gerouAlerta) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.gerouAlerta = gerouAlerta;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }

        public boolean isGerouAlerta() {
            return gerouAlerta;
        }
    }
}