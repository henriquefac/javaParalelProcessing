import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import code.SimpleRead;
import code.WordCounter;

public class App {
    public static void main(String[] args) throws Exception {
        // Instancia o leitor de arquivo
        SimpleRead read = new SimpleRead("C:\\Users\\henri\\Documents\\projectJava\\trabalho_comp_paralela\\ultimo\\Dracula-165307.txt");
        
        // Lê o texto e organiza em uma única string dividida por espaço
        String[] texto = read.loadText();
        
        // Medir tempo para o método serialCPU
        long startSerial = System.nanoTime();
        HashMap<String, Integer> resultadoSerial = WordCounter.serialCPU(texto);
        long endSerial = System.nanoTime();
        System.out.println("Tempo para serialCPU: " + (endSerial - startSerial) / 1_000_000.0 + " ms");
        
        // Medir tempo para o método parallelCPU
        long startParallel = System.nanoTime();
        HashMap<String, Integer> resultadoParallel = WordCounter.parallelCPU(texto);
        long endParallel = System.nanoTime();
        System.out.println("Tempo para parallelCPU: " + (endParallel - startParallel) / 1_000_000.0 + " ms");
        
        // Opcional: Validar se os resultados são iguais
        if (resultadoSerial.equals(resultadoParallel)) {
            System.out.println("Os resultados da contagem são iguais!");
        } else {
            System.out.println("Os resultados da contagem são diferentes!");
        }
    }
}
