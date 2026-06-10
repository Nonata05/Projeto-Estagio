#  Servidor de Autenticação - AuthServer (.NET 10)

Este é o microserviço responsável pela **segurança, cadastro de usuários, autenticação e emissão de tokens JWT (JSON Web Tokens)** do Sistema de Gestão de Cursos. Ele garante que apenas usuários autenticados e com as permissões corretas possam acessar e manipular os dados de cursos e matrículas.

---

## 🛠️ Tecnologias Utilizadas

Este serviço foi desenvolvido utilizando o ecossistema mais recente da Microsoft:

*   **Runtime:** [.NET 10.0](https://dotnet.microsoft.com/pt-br/download/dotnet/10.0) (A versão mais recente e performática do .NET).
*   **Framework:** ASP.NET Core Web API.
*   **Banco de Dados:** PostgreSQL (Persistência de usuários e credenciais).
*   **Segurança:** JWT Bearer Authentication (com tokens criptografados).
*   **Recursos C#:**
    *   `<Nullable>enable</Nullable>`: Proteção contra erros de referência nula (*NullPointer*).
    *   `<ImplicitUsings>enable</ImplicitUsings>`: Código limpo e redução de imports redundantes.

---

##  Estrutura do Projeto

A arquitetura está organizada da seguinte forma:

*   `Controllers/`: Endpoints expostos para registro, login e validação.
*   `Models/`: Classes de mapeamento do banco e objetos de transferência de dados (DTOs).
*   `Services/`: Lógica de negócio (geração do token JWT, hashing de senhas).
*   `Program.cs`: Configuração de pipelines de requisição, injeção de dependências, banco de dados e autenticação.

---

##  Configurações (`appsettings.json`)

O projeto está configurado para se conectar ao banco PostgreSQL do Docker e utilizar as credenciais de JWT abaixo:

```json
{
  "Logging": {
    "LogLevel": {
      "Default": "Information",
      "Microsoft.AspNetCore": "Warning"
    }
  },
  "ConnectionStrings": {
    "DefaultConnection": "Host=postgres-db;Port=5432;Database=acompanhamento_curso;Username=postgres;Password=admin"
  },
  "Jwt": {
    "Key": "MINHA_CHAVE_SUPER_SECRETA_123456789123456789",
    "Issuer": "MeuAuthServer",
    "Audience": "MeuSistema"
  },
  "AllowedHosts": "*"
}
```
Nota de Desenvolvimento: O host postgres-db na connection string é utilizado quando a aplicação roda dentro do ambiente Docker. 
Se você for rodar o .NET localmente via terminal (dotnet watch run) e o PostgreSQL estiver no Docker da sua máquina física, altere
temporariamente Host=postgres-db para Host=localhost.

## Opção 1: Via Linha de Comando (.NET CLI)
Abra o terminal na pasta deste projeto:
```bash
cd "Dot Net/AuthServer"
````

Restaure as dependências NuGet:
```bash
dotnet restore
````
Execute o projeto:
```bash
**dotnet run
````
(A API iniciará por padrão na porta http://localhost:5000)

## Opção 2: Via Docker
Este projeto possui um Dockerfile configurado para rodar junto com o banco de dados PostgreSQL.

Garanta que o banco de dados PostgreSQL do Docker Compose esteja rodando.

Compile a imagem Docker do servidor de autenticação:
```bash 
docker build -t authserver-api .
```

Execute o container na mesma rede do seu banco de dados:
```bash
docker run -d -p 5000:5000 --name authserver authserver-api
```

## Endpoints Principais da API
1. Criar Nova Conta (Registro)
Rota: POST /api/auth/register
JSON de Entrada:
{
  "username": "usuario_exemplo",
  "email": "usuario@email.com",
  "password": "SenhaSegura@123"
}
2. Autenticar (Login)
Rota: POST /api/auth/login
JSON de Entrada:
{
  "email": "usuario@email.com",
  "password": "SenhaSegura@123"
}
JSON de Retorno (Sucesso):
{
  "authenticated": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiration": "2026-12-31T23:59:59Z"
}
##  Integração com o Backend (Java / Spring Boot)
A assinatura do Token utiliza o emissor (MeuAuthServer) e o público-alvo (MeuSistema). Certifique-se 
de que a API Java esteja configurada com essas mesmas propriedades para decodificar e validar o cabeçalho 
Authorization: Bearer <TOKEN> enviado pelo Frontend.
