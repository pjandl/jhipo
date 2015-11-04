# JHIPO
Java implementation of (simple) hypothetical computer to help the study of Computer Organization and Architecture.
<hr/>
<p>HIPO é um computador hipotético simples que possibilita o estudo da organização de computadores e também dos fundamentos da programação assembly.</p>

<h2>Organização</h2>

<p>A organização interna do HIPO, que pode ser vista na Figura 1, exibe as seguintes características:</p>

<ul>
<li>Palavra de dados de 08 bits (um byte), ou seja, seu barramento de dados possui 08 bits (um byte) de largura.
<ul>
<li>Seu barramento de endereços também possui 08 bits de largura, ou seja, permite o endereçamento de 2^8 = 256 posições de um byte, de maneira que:</li>
<li>Sua memória é organizada como 256 x 01 byte (256 posições com um byte de largura ou, simplesmente, 256 por um); e</li>
<li>Podem existir até 256 dispositivos de entrada e saída (E/S ou I/O) distintos, cujas operações de leitura (entrada) e escrita (saída) envolvem a transferência de um byte a cada operação.</li>
</ul>
<li>Possui os seguintes registradores internos:
<ul>
<li>ACC (Accumulator ou Acumulador) com 08 bits de largura, destinado a conter o valor de operandos e de resultados de outras operações;</li>
<li>PC (Program Counter) com 08 bits de largura, que contém o endereço da posição de memória correspondente à execução de um programa;</li>
<li>SP (Stack Pointer) com 08 bits de largura, que indica o endereço da memória onde se armazena o próximo endereço de retorno;</li>
<li>RI (Registrador de Instruções) com 08 bits de largura, que contém o código da instrução corrente;</li>
<li>RDM (Registrador de Dados de Memória ou E/S) com 08 bits de largura, destinado a conter os dados lidos ou escritos na memória ou dispositivos de I/O;</li>
<li>REM (Registrador de Endereços de Memória ou E/S) com 08 bits de largura, destinado a conter os endereços de memória ou dispositivos de I/O necessários as operações de leitura e escrita;</li>
<li>Além de duas flags de estado (ou códigos de condição), com 01 bit cada uma, para sinalizar a presença de valores N (negativo) e Z (zero) no registrador ACC (acumulador).</li>
</ul>
<li>Decodificador capaz de interpretar seu conjunto de instruções.</li>
<li>Unidade de controle que gera sinais de controle para operar tanto os componentes internos existentes por meio de micro-operações padronizadas, como também a memória e os dispositivos de E/S conectados aos seus barramentos de dados, endereços e controle.</li>
<li>Unidade Lógica e Aritmética (ULA) responsável pela execução das operações aritméticas e lógicas sobre os dados do processador HIPO, armazenando seus resultados no registrador ACC (acumulador).</li>
</ul>
	
<p>O HIPO possui um único modo de endereçamento, denominado modo direto (ou absoluto), onde qualquer operando que corresponda a um endereço é tratado como um endereço físico de memória ou de dispositivo, ou seja, o valor deste operando é empregado diretamente como um endereço de memória/dispositivo, sem qualquer tradução, modificação ou tratamento.</p>

<p>Desta maneira o processador HIPO deve ser minimamente conectado a um banco de memória por meio de seus barramentos de endereço (08 bits de largura), de dados (08 bits de largura) e de controle (operações de read e write). O banco de memória pode ter tamanho máximo de 256 bytes. Também é possível sua interligação com até 256 dispositivos de E/S diferentes, os quais podem prover valores de entrada, comandos ou oferecer sinalização de saída ou acionamento de outros elementos.</p>
