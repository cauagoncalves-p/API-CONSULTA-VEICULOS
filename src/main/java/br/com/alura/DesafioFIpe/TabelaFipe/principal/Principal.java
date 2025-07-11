package br.com.alura.DesafioFIpe.TabelaFipe.principal;

import br.com.alura.DesafioFIpe.TabelaFipe.model.Dados;
import br.com.alura.DesafioFIpe.TabelaFipe.model.Modelos;
import br.com.alura.DesafioFIpe.TabelaFipe.model.Veiculo;
import br.com.alura.DesafioFIpe.TabelaFipe.service.ConsumoApi;
import br.com.alura.DesafioFIpe.TabelaFipe.service.ConvertDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumoApi consumo = new ConsumoApi();
    private ConvertDados conversor = new ConvertDados();

    public void exibeMenu() {
        var menu = """
                *** OPÇÕES ***
                Carro
                Moto
                Caminhão
                
                Digite uma das opções para consulta: 
                """;

        System.out.println(menu);
        var opcao = leitura.nextLine();
        String endereco;

        if (opcao.toLowerCase().contains("carr")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        var marcas = conversor.obterLista(json, Dados.class);

        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .limit(10)
                .forEach(System.out::println);

        System.out.println("\nInforme o código da marca para consulta: ");
        var codigoMarca = leitura.nextLine();

        endereco += "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca:");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .limit(10)
                .forEach(System.out::println);

        System.out.println("\nDigite o trecho do nome do veículo a ser buscado:");
        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados:");
        modelosFiltrados
                .stream().limit(10)
                .forEach(System.out::println);

        System.out.println("\nDigite o código do modelo para buscar os valores de avaliações:");
        var codigoModelo = leitura.nextLine();

        endereco += "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);

        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (var ano : anos) {
            var enderecoAnos = endereco + "/" + ano.codigo();
            json = consumo.obterDados(enderecoAnos);
            var veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaliações por ano:");
        veiculos.forEach(System.out::println);
    }
}
