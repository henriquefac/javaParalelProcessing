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

    // Paralelização no CPU
    public static HashMap<String, Integer> parallelCPU(String[] texto) throws InterruptedException, ExecutionException{
        int numTrheads = Runtime.getRuntime().availableProcessors(); // número de threads disponíveis
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
        
        // Contador final
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

    // Paralelização com GPU (OpenCL)
    public static HashMap<String, Integer> parallelGPU(String filePath) {
        CL.setExceptionsEnabled(true);
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return null;
        }

        // Prepara os dados para OpenCL
        byte[] textBytes = text.getBytes();
        String[] words = text.split("\\s+");
        byte[][] wordBytesArray = new byte[words.length][];
        for (int i = 0; i < words.length; i++) {
            wordBytesArray[i] = words[i].getBytes();
        }
        
        // Inicializa o OpenCL
        cl_platform_id[] platforms = new cl_platform_id[1];
        clGetPlatformIDs(platforms.length, platforms, null);

        cl_device_id[] devices = new cl_device_id[1];
        clGetDeviceIDs(platforms[0], CL_DEVICE_TYPE_GPU, devices.length, devices, null);

        cl_context context = clCreateContext(null, 1, devices, null, null, null);
        cl_command_queue commandQueue = clCreateCommandQueueWithProperties(context, devices[0], null, null);

        // Cria o programa OpenCL
        String kernelSource;
        try {
            kernelSource = new String(Files.readAllBytes(Paths.get("resources/kernel.cl")));
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo do kernel: " + e.getMessage());
            return null;
        }

        cl_program program = clCreateProgramWithSource(context, 1, new String[]{kernelSource}, null, null);
        clBuildProgram(program, 0, null, null, null, null);
        cl_kernel kernel = clCreateKernel(program, "countWords", null);

        // Aloca buffers na memória da GPU
        cl_mem textBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_char * textBytes.length, Pointer.to(textBytes), null);
        cl_mem wordBuffers[] = new cl_mem[words.length];
        for (int i = 0; i < words.length; i++) {
            wordBuffers[i] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_char * wordBytesArray[i].length, Pointer.to(wordBytesArray[i]), null);
        }
        cl_mem occurrencesBuffer = clCreateBuffer(context, CL_MEM_WRITE_ONLY, Sizeof.cl_int * words.length, null, null);

        // Define os argumentos do kernel
        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(textBuffer));
        for (int i = 0; i < words.length; i++) {
            clSetKernelArg(kernel, i + 1, Sizeof.cl_mem, Pointer.to(wordBuffers[i]));
        }
        clSetKernelArg(kernel, words.length + 1, Sizeof.cl_mem, Pointer.to(occurrencesBuffer));
        clSetKernelArg(kernel, words.length + 2, Sizeof.cl_int, Pointer.to(new int[]{textBytes.length}));

        // Executa o kernel
        long[] globalWorkSize = new long[]{textBytes.length};
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, globalWorkSize, null, 0, null, null);
        
        // Lê os resultados de volta
        int[] occurrences = new int[words.length];
        clEnqueueReadBuffer(commandQueue, occurrencesBuffer, CL_TRUE, 0, Sizeof.cl_int * words.length, Pointer.to(occurrences), 0, null, null);

        // Fecha recursos
        clReleaseMemObject(textBuffer);
        for (cl_mem buffer : wordBuffers) {
            clReleaseMemObject(buffer);
        }
        clReleaseMemObject(occurrencesBuffer);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);

        // Cria e retorna o contador com as palavras e suas ocorrências
        HashMap<String, Integer> contadorFinal = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            contadorFinal.put(words[i], occurrences[i]);
        }

        return contadorFinal;
    }
}
