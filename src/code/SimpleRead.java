package code;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class SimpleRead {
    private String caminho;
    private File arquivo;
    
    public SimpleRead(String caminho){
        // caminho do arquivo processado 
        this.caminho = caminho;
        // objeto arquivo
        this.arquivo = new File(this.caminho);
    }


    public String[] loadText() throws FileNotFoundException {
        Scanner scanner = new Scanner(arquivo, "UTF-8");
        List<String> palavras = new ArrayList<>();
        
        // Lê o arquivo linha por linha
        while (scanner.hasNextLine()) {
            String linha = scanner.nextLine().trim();  // Remove espaços em branco no início e fim da linha
            
            // Ignora a linha se estiver vazia após a remoção dos espaços
            if (linha.isEmpty()) {
                continue;  // Pula para a próxima linha
            }
            
            // Remove caracteres especiais diretamente na linha inteira
            String linhaLimpa = linha.replaceAll("[^\\p{L}\\p{N}\\s]", ""); // Remove tudo que não for letra, número ou espaço
            
            // Divide a linha em palavras e adiciona ao conjunto de palavras
            String[] palavrasLinha = linhaLimpa.split("\\s+");
            Collections.addAll(palavras, palavrasLinha);
        }
        
        scanner.close();  // Fecha o scanner após terminar a leitura
        return palavras.toArray(new String[0]); // Retorna o texto em minúsculas
    }
    
}