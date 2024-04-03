# Aluguel de Carros - Sistema Cliente-Servidor com Sockets Multicast em Java

Grupo: Tomás Manzi, João Victor Almeida, Gustavo Henrique, João Ricardo Barros

Projeto referente à disciplina de Fundamentos Computação Concorrente Paralela e Distribuída (FCCPD241). Este é um sistema simples de aluguel de carros implementado em Java utilizando sockets multicast para comunicação entre cliente e servidor, e comunicando através de datagramas convertidos em JSON, a execução da instâncias de cada cliente é feita utilizando Threads.

## Requisitos

- Java Development Kit (JDK) instalado (JDK-17)
- Eclipse IDE (ou qualquer outra IDE de sua preferência)
- Biblioteca para Manipulação de Objetos JSON (org.json) : https://github.com/stleary/JSON-java
* É necessário que você adicione o JAR da biblioteca (org.json), via sua IDE, E.G (Eclipse): Project > Properties > Java Build Bath > Libraries > Classpath > Add External JAR
* Em seguida pressione apply e verifique os imports.

## Instruções de Execução

1. Faça o download do projeto ou extraia o arquivo .zip ou .7z contendo o código fonte.
2. Abra a pasta do projeto em sua IDE.
3. Execute primeiro o servidor e depois o cliente.

### Executando o Servidor

1. Abra o projeto do servidor em sua IDE.
2. Execute a classe `ServerMulticast` para iniciar o servidor.
3. O servidor estará pronto para receber conexões dos clientes.

### Executando o Cliente

1. Abra o projeto do cliente em sua IDE.
2. Execute a classe `ClientMulticast` para iniciar o cliente.
3. Siga as instruções no console para interagir com o sistema de aluguel de carros.

## Funcionalidades :

### Comandos:

* Cliente:   

1 - Anuncios | 2 - Alugar carro | 3 - Devolver carro | 4 - Encerrar

* Servidor:

1 - Enviar Anuncios | 2 - Registrar Veículos | 3 - Realizar Empréstimo | 4 - Proceder Devolução | 5 - Encerrar Serviço

## Descrição;

- Anúncio de novidades sobre os carros disponíveis emitidos pelo gerente (servidor).
- Registro de novos modelos de carros pelo servidor pelo gerente (servidor).
- Escolha e aluguel de carros pelos clientes.
- Devolução de carros alugados pelo cliente, gerenciador pelo lado do servidor.
