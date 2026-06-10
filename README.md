# 🎓 Sistema de Gestão de Cursos - Projeto de Estágio

Este é o repositório principal (Monorepo) da solução completa para o **Sistema de Gestão de Cursos**. A aplicação foi projetada seguindo padrões modernos de arquitetura de software, utilizando uma abordagem de microsserviços/APIs independentes, banco de dados containerizado e um frontend de última geração.

---

## 🚀 Destaque Tecnológico (Bleeding Edge Stack)

Este projeto foi desenvolvido utilizando as versões estáveis mais modernas do mercado, demonstrando o uso de recursos avançados de performance, tipagem e segurança:

*   **Frontend:** Angular `21.2.12` sobre Node.js `v22.22.0` (utilizando Vitest para testes de alta performance).
*   **API Principal (Regras de Negócio):** Spring Boot com **Java 21 (LTS)**.
*   **Serviço de Autenticação (OAuth2/JWT):** ASP.NET Core com **.NET 10** e *Nullable Reference Types* habilitado por padrão.
*   **Infraestrutura:** Docker e Docker Compose para orquestração de ambientes e bancos de dados.

---

## 🏗️ Estrutura do Repositório

A organização das pastas do projeto reflete a separação de responsabilidades (SoC):

```bash
Projeto-Estagio/
├── docker-compose.yml       # Orquestração de bancos de dados e serviços auxiliares
├── README.md                # Este documento (Visão Geral da Solução)
│
├── Sistema-Cursos/         # 💻 Frontend em Angular 21 (Interface do Usuário)
│   └── README.md            # Como rodar e testar o Frontend
│
├── Spring Boot/             # ☕ Backend Principal (API de Cursos e Matrículas)
│   └── ProjetoEstagio/
│       └── README.md        # Detalhes técnicos e inicialização do Java 21
│
└── Dot Net/                 # ⚡ Microserviço de Segurança e Identity (.NET 10)
    └── AuthServer/
        └── README.md        # Como inicializar o Servidor de Autenticação
```
## 🌐 Arquitetura de Comunicação e Portas

Os serviços comunicam-se entre si de forma desacoplada através de chamadas HTTP/REST. 
Abaixo está o mapeamento padrão de portas do ambiente de desenvolvimento:

| Serviço | Tecnologia | Endereço Local | Função Principal |
| :--- | :--- | :--- | :--- |
| **Frontend Web** | Angular 21 | `http://localhost:4200` | Interface do usuário e consumo das APIs |
| **Auth Server** | .NET 10 | `http://localhost:5000` | Autenticação, Cadastro de Usuários e emissão de JWT |
| **Business API** | Java 21 | `http://localhost:8080` | CRUD de Cursos, Matrículas e Regras de Negócio |
| **Database** | Docker (DB) | *Configurado no Compose* | Persistência relacional de dados |

---

## 🛠️ Pré-requisitos para Rodar a Aplicação

Para compilar e executar todos os projetos na sua máquina (Windows/Linux/macOS), você precisará instalar:

*   **Git** (para versionamento de código)
*   **Node.js** `v22.22.0` ou superior (com **NPM** `10.9.4` ou superior)
*   **Java JDK 21** (ou superior)
*   **.NET SDK 10.0** (ou superior)
*   **Docker Desktop** (para subir os bancos de dados rapidamente)

---

## 🚀 Como Executar o Projeto (Guia Rápido)

### Passo 1: Clonar o Repositório
Abra o seu terminal e clone o projeto:

```bash
git clone https://github.com/Nonata05/Projeto-Estagio.git
cd Projeto-Estagio
````
### Passo 2: Subir a Infraestrutura (Banco de Dados)
Antes de rodar as aplicações, suba os serviços de banco de dados declarados no Docker:

docker-compose up -d
(Certifique-se de que o aplicativo Docker Desktop está aberto e rodando no seu computador)

##  Estrutura do Repositório

A organização das pastas do projeto reflete a separação de responsabilidades. Clique nos links abaixo para acessar a documentação detalhada de cada módulo:

*    **[Frontend (Angular 21)](./Sistema-Cursos/README.md)**: Interface do usuário, componentes e instruções de inicialização do frontend.
*    **[Backend Principal (Java 21 / Spring Boot)](./Spring%20Boot/ProjetoEstagio/README.md)**: Regras de negócio, mapeamento de banco de dados e controle de cursos/matrículas.
*    **[Auth Server (C# / .NET 10)](./Dot%20Net/AuthServer/README.md)**: Microsserviço responsável pela segurança, geração de tokens JWT e controle de acessos.
*    **[Configuração Docker (docker-compose.yml)](./docker-compose.yml)**: Arquivo de orquestração do banco de dados PostgreSQL.

---

##  Autor

Desenvolvido com dedicação por:
*   **[Raimunda Nonata]** — [GitHub (Nonata05)](https://github.com/Nonata05)

---

##  Declaração de Uso de Inteligência Artificial

Esta documentação foi estruturada, refinada e organizada com o auxílio de ferramentas de **Inteligência Artificial (IA)**. 

O uso da tecnologia de IA limitou-se à formatação estética em Markdown, correção gramatical, tradução técnica de termos e organização visual da arquitetura de pastas. Toda a lógica de negócio, codificação dos microsserviços (.NET, Java e Angular) e decisões arquiteturais de infraestrutura foram concebidas e implementadas integralmente pelo autor do projeto.
