
### **Principais Conceitos**  
- **Uso de Mocks**: Criar objetos simulados para substituir dependências externas nos testes unitários.  
- **Injeção de Mocks**: Utilização da anotação `@Mock` para criar automaticamente instâncias simuladas.  
- **Definição de Comportamento**: Configuração de respostas esperadas para chamadas de métodos (`when(...).thenReturn(...)`).  
- **Verificação de Chamadas**: Confirmação do uso correto dos mocks através de `verify()`. 

- **Desenvolvimento Orientado a Testes (TDD)**: Escrever testes antes da implementação do código.  
- **Isolamento de Dependências**: Uso de mocks para simular APIs externas e evitar chamadas reais durante os testes.  
- **Conversão de JSON**: Transformação de respostas JSON em objetos Java para facilitar a manipulação dos dados.

- **Testes de Integração**: Avaliam a comunicação entre componentes reais do sistema, sem uso de mocks.  
- **Plugin Maven Failsafe**: Responsável por executar testes de integração sem interferir nos testes unitários.  
- **Padrões de Nomenclatura**: Classes de testes de integração devem terminar com "IT" para serem reconhecidas corretamente pelo Maven.  

### **Comandos Maven e Suas Funções**  
- `mvn test` → Executa apenas os testes unitários (arquivos que terminam com "Test").  
- `mvn package` → Executa testes unitários e empacota o projeto.  
- `mvn package -DskipTests=true` → Compila e empacota o projeto sem rodar os testes.  
- `mvn failsafe:integration-test` → Executa apenas os testes de integração (arquivos que terminam com "IT").  
- `mvn install` → Executa todos os testes (unitários e de integração) e instala o artefato no repositório local. 