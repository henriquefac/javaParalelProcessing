import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import code.SimpleRead;
import code.WordCounter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

public class App {
    public static void main(String[] args) throws Exception {
        // Caminhos dos arquivos de entrada
        String[] arquivos = {
            "C:\\Users\\henri\\Documents\\projectJava\\trabalho_comp_paralela\\ultimo\\trabalho_ex_3\\recursos\\DonQuixote-388208.txt",
            "C:\\Users\\henri\\Documents\\projectJava\\trabalho_comp_paralela\\ultimo\\trabalho_ex_3\\recursos\\Dracula-165307.txt",
            "C:\\Users\\henri\\Documents\\projectJava\\trabalho_comp_paralela\\ultimo\\trabalho_ex_3\\recursos\\MobyDick-217452.txt"
        };

        // Gravação no CSV (inicia com cabeçalho se necessário)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados.csv", true))) {
            writer.write("Arquivo,Tipo,Tempo de Execução (ms)\n"); // Cabeçalho do CSV
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
        // Instancia o leitor de arquivo
        SimpleRead read = new SimpleRead(caminhoArquivoEntrada);
        
        // Lê o texto e organiza em uma única string dividida por espaço
        String[] texto = read.loadText();

        // Medir tempo para o método serialCPU
        long startSerial = System.nanoTime();
        HashMap<String, Integer> resultadoSerial = WordCounter.serialCPU(texto);
        long endSerial = System.nanoTime();
        double tempoSerial = (endSerial - startSerial) / 1_000_000.0; // em milissegundos

        // Medir tempo para o método parallelCPU
        long startParallel = System.nanoTime();
        HashMap<String, Integer> resultadoParallel = WordCounter.parallelCPU(texto);
        long endParallel = System.nanoTime();
        double tempoParallel = (endParallel - startParallel) / 1_000_000.0; // em milissegundos

        // Exibir resultados no console
        System.out.println("Processando arquivo: " + caminhoArquivoEntrada);
        System.out.println("Tempo para serialCPU: " + tempoSerial + " ms");
        System.out.println("Tempo para parallelCPU: " + tempoParallel + " ms");

        // Opcional: Validar se os resultados são iguais
        if (resultadoSerial.equals(resultadoParallel)) {
            System.out.println("Os resultados da contagem são iguais!");
        } else {
            System.out.println("Os resultados da contagem são diferentes!");
        }

        // Gravar os resultados em um arquivo CSV
        gravarCSV(caminhoArquivoEntrada, tempoSerial, tempoParallel);
    }

    private static void gravarCSV(String caminhoArquivoEntrada, double tempoSerial, double tempoParallel) {
        // Caminho do arquivo CSV
        String caminhoArquivoCSV = "resultados.csv";

        // Tenta abrir o arquivo CSV em modo de escrita
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivoCSV, true))) {
            // Escrever os tempos para serial e paralelo para cada arquivo
            writer.write(caminhoArquivoEntrada + ",serialCPU," + tempoSerial + "\n");
            writer.write(caminhoArquivoEntrada + ",parallelCPU," + tempoParallel + "\n");

            System.out.println("Resultados gravados em: " + caminhoArquivoCSV);
        } catch (IOException e) {
            System.err.println("Erro ao gravar o arquivo CSV: " + e.getMessage());
        }
    }
}
