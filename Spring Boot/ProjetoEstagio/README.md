#  Backend de Gestão de Cursos - Java Spring Boot

Este é o microserviço principal da aplicação, desenvolvido em **Java + Spring Boot**. Ele atua como o ponto central (BFF - *Backend For Frontend*) para o sistema, gerenciando usuários, cursos, matrículas, e interceptando a autenticação delegada ao servidor em C# (`AuthServer`).

---

##  Fluxo de Comunicação e Segurança

O sistema adota o fluxo de comunicação integrada: **Angular -> Java -> C# -> Java -> Angular**.

[ Angular (Frontend) ]
│
▼ (1. Envia Requisição / Login)
[ Java (AuthController / Filtros de Segurança) ]
│
▼ (2. Valida / Delega Autenticação)
[ C# (AuthServer) ]
│
▼ (3. Retorna Token JWT / Status)
[ Java (Valida Token e Processa Regra de Negócio) ]
│
▼ (4. Envia Resposta Final)
[ Angular (Frontend) ]


---

##  Tecnologias Utilizadas

*   **Linguagem:** Java 21 (LTS)
*   **Framework Principal:** Spring Boot 3.x
*   **Segurança:** Spring Security + JWT (JSON Web Tokens)
*   **Persistência de Dados:** Spring Data JPA / Hibernate
*   **Banco de Dados:** PostgreSQL
*   **Gerenciador de Dependências:** Maven

---

##  Estrutura de Pastas do Projeto

Abaixo está o mapeamento da estrutura de pacotes do seu projeto (`projeto.springframework.projetoestagio`):

```text
projeto.springframework.projetoestagio
│
├── 🎮 controller/                 # Camada de exposição dos Endpoints REST
│   ├── dto/                       # Objetos de Transferência de Dados (Requests/Responses)
│   ├── AuthController.java        # Gerencia o fluxo de login integrado com o C#
│   ├── CursoController.java       # Endpoints para gerenciamento de cursos
│   ├── UserController.java        # Endpoints para gerenciamento de usuários
│   └── UserCursoController.java   # Endpoints de associação (Matrícula de Usuários em Cursos)
│
├── 💾 domain/                     # Entidades ricas do banco de dados (JPA Entities)
│
├── ⚠️ exception/                  # Tratamento global de erros e exceções customizadas
│
├── 🗄️ repository/                 # Interfaces que estendem JpaRepository (Acesso ao Banco)
│
├── 🔒 security/                   # Módulo completo de segurança da aplicação
│   ├── config/
│   │   └── SecurityConfig.java    # Configurações de CORS, Rotas Públicas/Privadas e Filtros
│   ├── jwt/
│   │   ├── JwtAuthenticationFilter.java  # Interceptor que valida o Token JWT em cada requisição
│   │   └── JwtService.java        # Serviço responsável por decodificar e validar os Tokens
│   ├── model/
│   │   └── UserDetailsImpl.java   # Implementação do UserDetails do Spring Security
│   └── service/                   # Serviços auxiliares de carga de dados do usuário logado
│
└── ⚙️ service/                    # Camada de Regras de Negócio (Cursos, Usuários e Matrículas)
```
## Configurações Básicas (application.properties ou application.yml)
Certifique-se de configurar as variáveis de conexão com o banco de dados e a comunicação com o serviço em C#:
```text
# Configuração do Banco de Dados PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_seu_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Comunicação com o Servidor de Autenticação (C#)
auth.server.url=http://localhost:5000
```

## Detalhes dos Controllers
**1. AuthController** 
Responsável por receber as credenciais enviadas pelo Angular, repassá-las ao AuthServer em C#, obter o Token JWT e devolvê-lo com sucesso ao cliente.

**2. CursoController**
CRUD completo e consultas aos cursos ofertados no sistema.

**3. UserController** 
Gerenciamento cadastral de usuários (alunos/professores/administradores).

**4. UserCursoController** 
A lógica de relacionamento de muitos para muitos (N:M). Controla as matrículas, ou seja, quais usuários estão vinculados a quais cursos.

## Como Executar o Projeto
Certifique-se de ter o **Java 21** e o **Maven** instalados.
Certifique-se de que o banco PostgreSQL está ativo e com a base de dados criada.
No terminal, navegue até a pasta raiz do projeto Java:
```bash
mvn clean install
````
Execute o projeto com o comando:
```bash
mvn spring-boot:run
```
O servidor iniciará por padrão em: http://localhost:8080
