# QUESTIONS (3.1) 

## a) Identify a couple of examples that use AssertJ expressive methods chaining.

* In A_EmployeeRepoTest.java
```java
   assertThat(found).isEqualTo(alex);
   assertThat(allEmployees).hasSize(3).extracting(Employee::getName).containsOnly(alex.getName(), ron.getName(), bob.getName());
```

## b) Take note of transitive annotations included in @DataJpaTest.
```java
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Inherited
@BootstrapWith(org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTestContextBootstrapper.class)
@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension.class)
@OverrideAutoConfiguration(enabled=false)
@TypeExcludeFilters(DataJpaTypeExcludeFilter.class)
@Transactional
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@ImportAutoConfiguration

Using this annotation only enables auto-configuration that is relevant to Data JPA tests. Similarly, component scanning is limited to JPA repositories and entities (@Entity).

By default, tests annotated with @DataJpaTest are transactional and roll back at the end of each test. 
```
## c) Identify an example in which you mock the behavior of the repository (and avoid involving adatabase).

* B_EmployeeService_UnitTest.java
```java
   @BeforeEach
    public void setUp() {

        //these expectations provide an alternative to the use of the repository
        Employee john = new Employee("john", "john@deti.com");
        john.setId(111L);

        Employee bob = new Employee("bob", "bob@deti.com");
        Employee alex = new Employee("alex", "alex@deti.com");

        List<Employee> allEmployees = Arrays.asList(john, bob, alex);

        Mockito.when(employeeRepository.findByName(john.getName())).thenReturn(john);
        Mockito.when(employeeRepository.findByName(alex.getName())).thenReturn(alex);
        Mockito.when(employeeRepository.findByName("wrong_name")).thenReturn(null);
        Mockito.when(employeeRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito.when(employeeRepository.findAll()).thenReturn(allEmployees);
        Mockito.when(employeeRepository.findById(-99L)).thenReturn(Optional.empty());
    }

    @Test
     void whenSearchValidName_thenEmployeeShouldBeFound() {
        String name = "alex";
        Employee found = employeeService.getEmployeeByName(name);

        assertThat(found.getName()).isEqualTo(name);
    }
```

## d) What is the difference between standard @Mock and @MockBean?
@Mock (Mockito) é usado em testes unitários para criar mocks isolados, geralmente com @ExtendWith(MockitoExtension.class).

@MockBean (Spring Boot) é usado em testes de integração para substituir beans no contexto do Spring, garantindo que a aplicação use o mock ao invés do bean real.

## e) What is the role of the file “application-integrationtest.properties”? In which conditions will it be used?

```java
## note changed port 3306 --> 33060
spring.datasource.url=jdbc:mysql://localhost:33060/tqsdemo
spring.jpa.hibernate.ddl-auto=create-drop
spring.datasource.username=demo
spring.datasource.password=demo


## db
## docker run --name mysql5tqs -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=tqsdemo -e MYSQL_USER=demo -e MYSQL_PASSWORD=demo -p 33060:3306 -d mysql/mysql-server:5.7

```

O ficheiro application-integrationtest.properties define configurações específicas para testes de integração no Spring Boot, , garantindo um ambiente controlado.

Ele permite:
✅ Personalizar a base de dados para testes (ex.: URL, credenciais).
✅ Alterar o comportamento da aplicação apenas durante os testes.
✅ Garantir um ambiente de teste isolado, sem afetar a configuração padrão.

## f) the sample project demonstrates three test strategies to assess an API (C, D and E) developed with SpringBoot. Which are the main/key differences?

As três estratégias de teste (C, D e E) no projeto avaliam a API Spring Boot de formas diferentes:


### **C - Testes Unitários**

**Objetivo:**  
Testar componentes isolados da aplicação, como serviços ou repositórios, sem envolver a interação com o banco de dados ou outras dependências externas. O foco está em garantir que cada unidade de código funcione corretamente de forma independente.

**Ferramentas:**
- **JUnit:** Framework principal para execução de testes.
- **Mockito:** Usado para simular o comportamento de dependências externas (mocks) e isolar a lógica do componente em teste.
- **@Mock:** Para criar objetos mock de dependências que o componente em teste utiliza.
- **@InjectMocks:** Para injetar as dependências mockadas no objeto que está sendo testado.

**Mocking:**  
- Usa **@Mock** para criar mocks das dependências do componente, como serviços ou repositórios, permitindo que o teste se concentre apenas na lógica do componente em questão.  
- **Não há interação** com a base de dados real ou com outros componentes do sistema.

---

### **D - Testes de Integração (com MockMvc)**

**Objetivo:**  
Testar a integração entre a camada web (controladores) e a camada de persistência (repositórios). Aqui, a aplicação é executada em um ambiente controlado para verificar como os componentes interagem, sem envolver o sistema em produção.

**Ferramentas:**
- **@SpringBootTest:** Carrega o contexto completo da aplicação, permitindo que você teste a integração entre as camadas.
- **@AutoConfigureMockMvc:** Configura e injeta o objeto **MockMvc**, que simula requisições HTTP e interage com os controladores da aplicação, permitindo testar endpoints sem precisar rodar um servidor real.
- **@MockBean:** Permite substituir beans reais no contexto Spring com mocks, para simular o comportamento de serviços ou repositórios.
- **@AutoConfigureTestDatabase:** Usado para configurar um banco de dados em memória, para testar a interação com o banco sem afetar o banco real.

**Mocking:**  
- Pode substituir beans reais por mocks usando **@MockBean**, garantindo que a lógica de negócios (serviços, repositórios) não seja chamada diretamente.
- A base de dados real é substituída por um banco de dados em memória, como **H2**, para não afetar os dados reais e garantir um ambiente isolado.

---

### **E - Testes de Aceitação/End-to-End (E2E)**

**Objetivo:**  
Testar a aplicação como um todo, simulando interações reais de cliente e servidor. Aqui, o objetivo é validar se a API está funcionando corretamente em um ambiente mais realista, com a comunicação entre o front-end e o back-end, incluindo a base de dados real (ou uma versão de teste).

**Ferramentas:**
- **@SpringBootTest com WebEnvironment.RANDOM_PORT:** Inicia a aplicação em uma porta aleatória, permitindo testar o servidor como se fosse um cliente real.
- **TestRestTemplate:** Um cliente HTTP para realizar requisições reais à API e verificar as respostas, simulando como um cliente externo usaria a API.
- **RestAssured (opcional):** Uma ferramenta de testes de APIs para facilitar a realização de requisições HTTP e validação de respostas.
- **TestContainers (opcional):** Pode ser usado para inicializar containers Docker com bases de dados reais, garantindo um ambiente de teste mais próximo do ambiente de produção.
- **@AutoConfigureTestDatabase:** Para configurar a base de dados em memória, mas também pode ser configurada para usar um banco real, dependendo do caso.

**Mocking:**  
- **Nenhum.** Todos os componentes, incluindo banco de dados, serviços e repositórios, são reais. A aplicação é testada como um todo, em um cenário mais próximo da produção.
- O foco está em validar o comportamento completo da aplicação, como se fosse um usuário final interagindo com a API.

---