# Mini-Mundo: Sistema de Gerenciamento de Projetos e Tarefas

Interface responsiva para controle de escopo de projetos, prazos e dependências de tarefas.

## 🚀 Tecnologias Utilizadas

- **Back-end:** Java 17, Spring Boot 3.x, Spring Security (JWT), JPA / Hibernate
- **Front-end:** HTML5, CSS3, JavaScript (Vanilla ES6)
- **Banco de Dados:** PostgreSQL 

## 🛠️ Como Rodar o Projeto Localmente

### Pré-requisitos

- Java 17 instalado
- Maven instalado
- Banco PostgreS Instalado 

#### 🛠️Configuração do Banco de Dados (PostgreSQL)

Antes de rodar o Back-end, você precisa ter o PostgreSQL instalado e criar um banco de dados vazio chamado `minimundo`.

Abra o arquivo `src/main/resources/application.properties` e ajuste as credenciais conforme a sua máquina:

properties
spring.datasource.url=jdbc:postgresql://localhost:5432/minimundo
spring.datasource.username=admin
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update

##### 🛠️ Passo 1: Iniciar o Back-end

bash
./mvnw clean spring-boot:run

##### 🐳 Como Executar com Docker (Ambiente de Desenvolvimento)

Para rodar a aplicação completa (Back-end + Banco de Dados) sem precisar instalar Java ou PostgreSQL localmente, basta ter o Docker instalado e rodar o comando abaixo na raiz do projeto:

```bash
docker-compose up --build