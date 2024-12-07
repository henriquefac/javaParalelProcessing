__kernel void countWords(
    __global const char* texto,            // Texto de entrada
    __global const char* palavra,          // Palavras a serem procuradas
    __global int* ocorrencias,             // Contador de ocorrências
    const int textoLength                  // Tamanho do texto
) {
    // Obtém o índice global do thread
    int index = get_global_id(0);
    
    // Só executa se o índice estiver dentro dos limites do texto
    if (index < textoLength) {
        // Inicializa uma variável para comparar a palavra com o texto
        int i = 0;
        
        // Percorre o texto até encontrar a palavra
        while (texto[index + i] == palavra[i] && palavra[i] != '\0') {
            i++;
        }

        // Se a palavra for encontrada, incrementa o contador
        if (palavra[i] == '\0') {
            atomic_add(&ocorrencias[index], 1);
        }
    }
}
