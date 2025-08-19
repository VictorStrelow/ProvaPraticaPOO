package com.provapoo.view;

import com.provapoo.model.*;
import com.provapoo.service.SensorService;
import com.provapoo.service.SensorService.RegistrarResultado;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class InterfaceUsuario {
    private SensorService service;
    private Scanner sc;
    private DateTimeFormatter formatter;

    public InterfaceUsuario() {
        this.service = new SensorService();
        this.sc = new Scanner(System.in);
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            String opc = sc.nextLine().trim();
            try {
                switch (opc) {
                    case "1":
                        cadastrarSensor();
                        break;
                    case "2":
                        listarSensores();
                        break;
                    case "3":
                        registrarMedicao();
                        break;
                    case "4":
                        exibirHistorico();
                        break;
                    case "5":
                        verificarAlertas();
                        break;
                    case "6":
                        listarSensoresCriticos();
                        break;
                    case "0":
                        System.out.println("\nEncerrando sistema... Obrigado por usar o Monitoramento WEG!");
                        rodando = false;
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (Exception e) {
                // Tratamento geral caso algo inesperado aconteça
                System.out.println("Ocorreu um erro: " + e.getMessage());
            }
        }
        sc.close();
    }

    private void exibirMenu() {
        System.out.println("\n===========================================");
        System.out.println(" Sistema de Monitoramento WEG - Versão 1.0");
        System.out.println("===========================================\n");
        System.out.println("1 - Cadastrar Sensor");
        System.out.println("2 - Listar Sensores");
        System.out.println("3 - Registrar Medição");
        System.out.println("4 - Exibir Histórico de Medições");
        System.out.println("5 - Verificar Alertas");
        System.out.println("6 - Listar Sensores Críticos");
        System.out.println("0 - Sair\n");
        System.out.print("Digite a opção: ");
    }

    private void cadastrarSensor() {
        System.out.print("\nDigite o código do sensor: ");
        String codigo = sc.nextLine().trim();
        System.out.print("Digite o nome do equipamento: ");
        String nome = sc.nextLine().trim();

        System.out.println("Escolha o tipo de sensor:");
        System.out.println("1 - Temperatura");
        System.out.println("2 - Vibração");
        System.out.print("Opção: ");
        String op = sc.nextLine().trim();

        Sensor novo = null;
        if (op.equals("1")) {
            novo = new SensorTemperatura(codigo, nome);
        } else if (op.equals("2")) {
            novo = new SensorVibracao(codigo, nome);
        } else {
            System.out.println("Opção de tipo inválida. Cadastro cancelado.");
            return;
        }

        boolean ok = service.cadastrarSensor(novo);
        if (!ok) {
            System.out.println("Código já cadastrado. Tente outro código.");
            return;
        }

        System.out.println("\nSensor cadastrado com sucesso!");
        if (novo instanceof SensorTemperatura) {
            System.out.printf("Tipo: Temperatura | Limite de alerta: %.1f °C%n", SensorTemperatura.LIMITE_TEMPERATURA);
        } else {
            System.out.printf("Tipo: Vibração | Valor técnico esperado: %.1f Hz%n", SensorVibracao.VALOR_ESPERADO);
        }
    }

    private void listarSensores() {
        List<Sensor> lista = service.listarSensores();
        System.out.println("\nSensores Cadastrados:\n");
        if (lista.isEmpty()) {
            System.out.println("Nenhum sensor cadastrado.");
            return;
        }
        for (Sensor s : lista) {
            System.out.println(s.toString());
        }
    }

    private void registrarMedicao() {
        System.out.print("\nDigite o código do sensor: ");
        String codigo = sc.nextLine().trim();
        Sensor s = service.buscarSensorPorCodigo(codigo);
        if (s == null) {
            System.out.println("Sensor não encontrado.");
            return;
        }

        try {
            System.out.print("Digite o valor da medição: ");
            String valorStr = sc.nextLine().trim();
            double valor = Double.parseDouble(valorStr);

            System.out.print("Digite a data e hora (formato dd/MM/yyyy HH:mm): ");
            String dataHoraStr = sc.nextLine().trim();

            // valida formato
            try {
                LocalDateTime.parse(dataHoraStr, formatter);
            } catch (DateTimeParseException dtpe) {
                System.out.println("Formato de data/hora inválido. Uso: dd/MM/yyyy HH:mm");
                return;
            }

            Medicao m = new Medicao(valor, dataHoraStr);
            RegistrarResultado res = service.registrarMedicao(codigo, m);

            System.out.println("\n" + res.getMensagem());
            if (res.isGerouAlerta()) {
                if (s instanceof SensorTemperatura) {
                    System.out.printf("%nALERTA: Medição fora do limite técnico! (%.3f > %.1f)%n",
                            valor, SensorTemperatura.LIMITE_TEMPERATURA);
                } else if (s instanceof SensorVibracao) {
                    System.out.printf("%nALERTA: Medição fora do valor técnico esperado! (%.3f != %.1f)%n",
                            valor, SensorVibracao.VALOR_ESPERADO);
                } else {
                    System.out.println("ALERTA detectado!");
                }
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Valor inválido. Digite um número válido para a medição.");
        }
    }

    private void exibirHistorico() {
        System.out.print("\nDigite o código do sensor: ");
        String codigo = sc.nextLine().trim();
        Sensor s = service.buscarSensorPorCodigo(codigo);
        if (s == null) {
            System.out.println("Sensor não encontrado.");
            return;
        }
        List<Medicao> lista = service.listarMedicoesDoSensor(codigo);
        System.out.printf("%nHistórico de Medições do Sensor %s:%n%n", codigo);
        if (lista == null || lista.isEmpty()) {
            System.out.println("Nenhuma medição registrada.");
            return;
        }

        int idx = 1;
        for (Medicao m : lista) {
            boolean alerta = s.verificarAlerta(m);
            String suffix = alerta ? " ALERTA" : "";
            System.out.printf("%d. Valor: %.3f | Data: %s%s%n", idx++, m.getValor(), m.getDataHora(), suffix);
        }
    }

    private void verificarAlertas() {
        System.out.println("\nVerificando sensores...\n");
        Map<Sensor, Integer> mapa = service.contarAlertasPorSensor();
        if (mapa.isEmpty()) {
            System.out.println("Nenhum sensor cadastrado.");
            return;
        }
        for (Map.Entry<Sensor, Integer> e : mapa.entrySet()) {
            Sensor s = e.getKey();
            int qtd = e.getValue();
            System.out.printf("Sensor %s (%s) - %s:%n", s.getCodigo(), s.getTipo(), s.getNomeEquipamento());
            if (qtd > 0) {
                System.out.printf("%d alerta(s) detectado(s)%n%n", qtd);
            } else {
                System.out.println("Nenhum alerta\n");
            }
        }
    }

    private void listarSensoresCriticos() {
        int limiar = 3; // mais de 3 alertas é crítico
        Map<Sensor, Integer> criticos = service.identificarSensoresCriticos(limiar);
        System.out.println("\nSensores com mais de 3 alertas:\n");
        if (criticos.isEmpty()) {
            System.out.println("Nenhum sensor crítico identificado.");
            return;
        }
        for (Map.Entry<Sensor, Integer> e : criticos.entrySet()) {
            Sensor s = e.getKey();
            int qtd = e.getValue();
            System.out.printf("Código: %s | Tipo: %s | Equipamento: %s | Alertas: %d%n",
                    s.getCodigo(), s.getTipo(), s.getNomeEquipamento(), qtd);
        }
        System.out.println("\nATENÇÃO: Inspeção imediata recomendada!");
    }
}