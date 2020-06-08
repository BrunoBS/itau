**Requisitos:**

- JDK (Java Development Kit) 1.8 ou superior.
- Apache Maven 3.2.3


**Para baixar as dependencias da aplicação**

- Abre o  terminal do seu sistema operacional
- Navegue até a pasta raiz do projeto e digite o seguinte comando:

```
   mvn clean install
   
```


**Como Executar os testes da aplicação**

```
   mvn test
   
```

**Para executar a a aplicação executar o comando abaixo** 

```
  mvn spring-boot:run
  
```

**Para subir o projeto no container docker executar os comandos abaixo** 

Por default foi utilizado a porta 9090, configurada no dockerfile [DockerFie](/src/main/docker/dockerfile)

```
mvn clean package docker:build
docker run -t -p 9090:9090 --name api-transferencia-itau brunobsgt/api-transferencia-itau
  
```
**Observações**

1. Após executar a  aplicação acessar o link: http://{host}9090/swagger-ui.html  para conhecer a documentação da API.
2. Na raiz do projeto tem um  [arquivo](/api_transferencia_itau_v2_1.postman_collection) para realizar testes no POSTMAN.



######  Case Itaú - Api de transferência ###### 
Desenvolva um projeto que exponha APIs no padrão REST (JSON) e atenda as
seguintes funcionalidades:
1. Endpoint para cadastrar um cliente, com as seguintes informações: id (único),
nome, número da conta (único) e saldo em conta;
2. Endpoint para listar todos os clientes cadastrados;
3. Endpoint para buscar um cliente pelo número da conta;
4. Endpoint para realizar transferência entre 2 contas. A conta origem precisa ter
saldo o suficiente para a realização da transferência e a transferência deve ser
de no máximo R$ 1000,00 reais;
5. Endpoint para buscar as transferências relacionadas à uma conta, por ordem
de data decrescente. Lembre-se que transferências sem sucesso também
devem armazenadas.

###### Requisitos ######  
1. Solução desenvolvida em Java 8 ou superior;
2. Maven ou Gradle como gerenciador de dependências;
3. Banco de dados in memory;
4. Controle de concorrência na operação de transferência;
5. Utilize corretamente os padrões de HTTP response code para as APIs;
6. Controle de versão das APIs;
7. Testes unitários;
8. Testes integrados;
9. Documentação no código;
10. readme.md com a documentação de como utilizar a aplicação.


Qualquer dúvida fico a disposição.[Clique aqui](https://www.linkedin.com/in/bruno-barbosa-6a550a29/)


