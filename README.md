# Mini-Mundo: Sistema de Gerenciamento de Projetos e Tarefas

Interface responsiva para controle de escopo de projetos, prazos e dependências de tarefas. O projeto foi estruturado utilizando arquitetura desacoplada (API REST no Back-end e interface estática no Front-end).

---

## 🚀 Tecnologias Utilizadas

- **Back-end:** Java 17, Spring Boot 3.x, Spring Security (JWT), JPA / Hibernate
- **Front-end:** HTML5, CSS3, JavaScript (Vanilla ES6)
- **Banco de Dados:** PostgreSQL 

---

# ⚙️ Requisitos para Desenvolvimento

Seguindo as diretrizes de boas práticas, o ambiente foi projetado para que **não seja necessário instalar nenhuma ferramenta local de desenvolvimento** (como Java, Maven ou PostgreSQL). 

A única ferramenta obrigatória que você precisa instalada na sua máquina é:
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (com suporte a Docker Compose)

---

## 🌐 Imagem Oficial no Docker Hub
A imagem deste sistema está registrada publicamente e pronta para execução isolada diretamente do registro oficial do Docker Hub:

### 🔗 Link do Repositório: jcsfilho1/mini-mundo no Docker Hub

https://hub.docker.com/r/jcsfilho1/mini-mundo

---
## 🐳 Como Executar com Docker (Ambiente de Desenvolvimento)

Para rodar a aplicação completa (Back-end + Banco de Dados) sem precisar instalar Java ou PostgreSQL localmente, basta ter o Docker instalado e rodar o comando abaixo na raiz do projeto:

```bash
docker-compose up --build
```


### Como Rodar a Imagem do Docker Hub Customizando o Ambiente:

Se você precisar rodar a imagem homologada apontando para outro banco de dados ou alterando a porta de acesso, você pode injetar Variáveis de Ambiente no comando de inicialização sem precisar alterar o código-fonte:

```bash
docker run -d \
  -p 9090:9090 \
  -e PORT=9090 \
  -e DB_URL=jdbc:postgresql://seu-servidor-banco:5432/nome_banco \
  -e DB_USER=admin \
  -e DB_PASS=admin \
  --name mini-mundo-app \
  jcsfilho1/mini-mundo:latest

```

# 🛠️ Modo Alternativo (Execução Tradicional sem Docker)

## Pré-requisitos

- Java 17 instalado
- Maven instalado 3.x instalado.
- Banco PostgreS Instalado 
- Serviço do PostgreSQL rodando localmente.
---

## 🛠️Configuração do Banco de Dados (PostgreSQL)

Antes de rodar o Back-end, você precisa ter o PostgreSQL instalado e criar um banco de dados vazio chamado `minimundo`.

Abra o arquivo `src/main/resources/application.properties` e ajuste as credenciais conforme a sua máquina:

properties
spring.datasource.url=jdbc:postgresql://localhost:5432/minimundo
spring.datasource.username=admin
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update

---

## 🛠️ Passo 1: Iniciar o Back-end

```bash
./mvnw clean spring-boot:run
```

---
## 💻 Como Iniciar o Front-end
Com o Back-end rodando (seja via Docker ou Localmente na porta 8080), você deve abrir a interface do usuário:

Navegue até a pasta frontend/.

Abra o arquivo login.html no seu navegador de preferência (Recomendado utilizar a extensão Live Server do VS Code).

Caso seja o primeiro acesso, clique em "Cadastrar" para criar um usuário no banco de dados para efetuar o login.
---


# Obrigado pela atenção!