# Relatório de Análise de Algoritmos de Contagem de Palavras

## Resumo

Este trabalho visa avaliar o desempenho de três abordagens diferentes para contagem de palavras em arquivos de texto: uma implementação serial na CPU, uma implementação paralela utilizando a CPU e uma implementação paralela utilizando a GPU. O foco principal é comparar os tempos de execução das abordagens em diferentes arquivos de texto. A análise inclui a execução dos algoritmos em três arquivos de texto distintos e a comparação dos resultados de tempo de execução utilizando uma análise estatística. Todos os resultados são registrados em um arquivo CSV para facilitar a visualização e análise.

## Introdução

Neste trabalho, foram escolhidos três métodos distintos para contagem de palavras em arquivos de texto:

1. **Método Serial (serialCPU)**: A implementação serial processa o arquivo palavra por palavra de forma sequencial na CPU. Esta abordagem é simples, mas tende a ser mais lenta para arquivos maiores, pois não aproveita as vantagens do processamento paralelo.
   
2. **Método Paralelo CPU (parallelCPU)**: Nesta abordagem, o processamento das palavras é distribuído entre múltiplos núcleos de CPU, utilizando a paralelização para reduzir o tempo de execução. O número de threads é ajustado automaticamente para aproveitar ao máximo os recursos disponíveis da CPU.
   
3. **Método Paralelo GPU (parallelGPU)**: Este método faz uso da GPU para a contagem de palavras. Como a GPU é otimizada para tarefas paralelizadas em grande escala, espera-se que essa implementação tenha um desempenho superior, especialmente para arquivos muito grandes.

A abordagem geral para o trabalho envolve a leitura de três arquivos de texto diferentes, o processamento utilizando os três métodos e a comparação dos tempos de execução. Os resultados são gravados em um arquivo CSV, e gráficos serão gerados para análise visual do desempenho.

## Metodologia

A análise dos resultados obtidos envolve o uso de ferramentas de medição de tempo para capturar o tempo de execução de cada uma das três abordagens em diferentes arquivos de texto. Os arquivos de entrada são:

- `DonQuixote-388208.txt`
- `Dracula-165307.txt`
- `MobyDick-217452.txt`

Cada um desses arquivos é processado utilizando os três métodos (serialCPU, parallelCPU e parallelGPU), e o tempo de execução de cada um é registrado. A análise estatística envolve:

1. **Cálculo do tempo de execução**: O tempo de execução de cada método é registrado em nanosegundos.
2. **Comparação dos métodos**: A comparação entre os métodos é feita através da diferença no tempo de execução, levando em consideração o tamanho dos arquivos e a eficiência de cada implementação.
3. **Geração de gráficos**: Gráficos de barras são gerados para visualização comparativa dos tempos de execução dos três métodos.

Para cada execução, os resultados são gravados em um arquivo CSV, que contém informações sobre o nome do arquivo de entrada, o tipo de método utilizado e o tempo de execução correspondente.

## Resultados e Discussão

Os resultados obtidos mostram como o tempo de execução de cada abordagem varia conforme o tipo de arquivo de entrada. Abaixo estão os tempos registrados para cada método:

- **DonQuixote-388208.txt**:
  - Tempo serialCPU: 3.436711 x 10^8 ns
  - Tempo parallelCPU: 2.934445 x 10^8  ns
  - Tempo parallelGPU: 4.194066 x 10^8 ns
  
- **Dracula-165307.txt**:
  - Tempo serialCPU: 7.290631 x 10^7 ns
  - Tempo parallelCPU: 4.478740 x 10^7 ns
  - Tempo parallelGPU: 3.377730 x 10^7 ns
  
- **MobyDick-217452.txt**:
  - Tempo serialCPU: 4.970681 x 10^7 ns
  - Tempo parallelCPU: 1.231431 x 10^8 ns
  - Tempo parallelGPU: 5.113680 x 10^7 ns

Gráficos comparando os tempos de execução das três abordagens para cada arquivo:

```plaintext
| Arquivo               | serialCPU (ns)        | parallelCPU (ns)      | parallelGPU (ns)      |
|-----------------------|-----------------------|-----------------------|-----------------------|
| DonQuixote-388208.txt | 3.436711 x 10^8       | 2.934445 x 10^8       | 4.194066 x 10^8       |
| Dracula-165307.txt   | 7.290631 x 10^7       | 4.478740 x 10^7       | 3.377730 x 10^7       |
| MobyDick-217452.txt  | 4.970681 x 10^7       | 1.231431 x 10^8       | 5.113680 x 10^7       |
