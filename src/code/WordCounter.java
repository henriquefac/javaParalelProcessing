package code;

import java.util.*;
import java.util.concurrent.*;

import org.jocl.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.jocl.CL.*;


public class WordCounter {

    public static HashMap<String, Integer> serialCPU(String[] texto){
        HashMap<String, Integer> contador = new HashMap<>();
        for (String palavra : texto) {
            contador.put(palavra, contador.getOrDefault(palavra, 0) + 1);  // Conta as ocorrências da palavra
        }
        return contador;
    }

    //paralelização 
    public static HashMap<String, Integer> parallelCPU(String[] texto) throws  InterruptedException, ExecutionException{
        int numTrheads = Runtime.getRuntime().availableProcessors(); // números de threads disponíveis
        ExecutorService executor = Executors.newFixedThreadPool(numTrheads);
        List<Callable<HashMap<String, Integer>>> tasks = new ArrayList<>();

        // Dividir texto em partes e cria tarefas para cada parte
        int chunkSize = texto.length / numTrheads;
        for (int i = 0; i < numTrheads; i++){
            int start = i * chunkSize;
            int end = (i == numTrheads - 1) ? texto.length : (i + 1) * chunkSize;
            tasks.add(() -> serialCPU(Arrays.copyOfRange(texto, start, end)));
        }

        List<Future<HashMap<String, Integer>>> results = executor.invokeAll(tasks);
        
        //contador final
        HashMap<String, Integer> contadorFinal = new HashMap<>();
        for (Future<HashMap<String, Integer>> future : results){
            HashMap<String, Integer> resultadoParcial = future.get();
            for (Map.Entry<String, Integer> entry : resultadoParcial.entrySet()){
                contadorFinal.put(entry.getKey(), contadorFinal.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        executor.shutdown();
        return contadorFinal;
    }


}