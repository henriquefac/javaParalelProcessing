import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import code.WordCounter;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws Exception {
        // Caminhos dos arquivos de entrada
        String[] arquivos = {
            "recursos\\DonQuixote-388208.txt",
            "recursos\\Dracula-165307.txt",
            "recursos\\MobyDick-217452.txt"
        };

        // Gravação no CSV (inicia com cabeçalho se necessário)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados.csv", true))) {
            writer.write("Arquivo,Tipo,Tempo de Processamento (ns)\n"); // Cabeçalho do CSV
        } catch (IOException e) {
            System.err.println("Erro ao abrir o arquivo CSV: " + e.getMessage());
            return;
        }

        // Processar cada arquivo
        for (String caminhoArquivoEntrada : arquivos) {
            processarArquivo(caminhoArquivoEntrada);
        }
    }

    private static void processarArquivo(String caminhoArquivoEntrada) throws Exception {
        // Medir tempo para o método serialCPU
        long startSerial = System.nanoTime();
        HashMap<String, Integer> resultadoSerial = WordCounter.serialCPU(loadText(caminhoArquivoEntrada));
        long endSerial = System.nanoTime();
        long tempoSerial = endSerial - startSerial; // em nanosegundos

        // Medir tempo para o método parallelCPU
        long startParallelCPU = System.nanoTime();
        HashMap<String, Integer> resultadoParallelCPU = WordCounter.parallelCPU(loadText(caminhoArquivoEntrada));
        long endParallelCPU = System.nanoTime();
        long tempoParallelCPU = endParallelCPU - startParallelCPU; // em nanosegundos

        // Medir tempo para o método parallelGPU
        long startParallelGPU = System.nanoTime();
        HashMap<String, Integer> resultadoParallelGPU = WordCounter.parallelGPU(caminhoArquivoEntrada);
        long endParallelGPU = System.nanoTime();
        long tempoParallelGPU = endParallelGPU - startParallelGPU; // em nanosegundos

        // Exibir resultados no console
        System.out.println("Processando arquivo: " + caminhoArquivoEntrada);
        System.out.println("Tempo para serialCPU: " + tempoSerial + " ns");
        System.out.println("Tempo para parallelCPU: " + tempoParallelCPU + " ns");
        System.out.println("Tempo para parallelGPU: " + tempoParallelGPU + " ns");

        // Opcional: Validar se os resultados são iguais
        if (resultadoSerial.equals(resultadoParallelCPU) && resultadoSerial.equals(resultadoParallelGPU)) {
            System.out.println("Os resultados da contagem são iguais!");
        } else {
            System.out.println("Os resultados da contagem são diferentes!");
        }

        // Gravar os resultados em um arquivo CSV
        gravarCSV(caminhoArquivoEntrada, tempoSerial, tempoParallelCPU, tempoParallelGPU);
    }

    private static String[] loadText(String caminhoArquivoEntrada) throws IOException {
        // Lê o conteúdo do arquivo e retorna o texto como um array de palavras
        String content = new String(Files.readAllBytes(Paths.get(caminhoArquivoEntrada)));
        return content.split("\\s+");
    }

    private static void gravarCSV(String caminhoArquivoEntrada, long tempoSerial, long tempoParallelCPU, long tempoParallelGPU) {
        // Caminho do arquivo CSV
        String caminhoArquivoCSV = "resultados.csv";
    
        // Tenta abrir o arquivo CSV em modo de escrita
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivoCSV, true))) {
            // Extraindo apenas o nome do arquivo (sem o caminho completo)
            String nomeArquivo = caminhoArquivoEntrada.split("\\\\")[caminhoArquivoEntrada.split("\\\\").length - 1];
    
            // Escrever os tempos para serial, parallelCPU e parallelGPU para cada arquivo
            writer.write(nomeArquivo + ",serialCPU," + tempoSerial + "\n");
            writer.write(nomeArquivo + ",parallelCPU," + tempoParallelCPU + "\n");
            writer.write(nomeArquivo + ",parallelGPU," + tempoParallelGPU + "\n");
    
            System.out.println("Resultados gravados em: " + caminhoArquivoCSV);
        } catch (IOException e) {
            System.err.println("Erro ao gravar o arquivo CSV: " + e.getMessage());
        }
    }
}
