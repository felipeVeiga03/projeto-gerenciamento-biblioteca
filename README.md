# 📚 Sistema de Gerenciamento de Biblioteca (API REST)

![Java CI with Maven](https://github.com/SEU_USUARIO/SEU_REPOSITORIO/actions/workflows/ci.yml/badge.svg)

Uma API RESTful completa e robusta para o gerenciamento operacional de uma biblioteca, desenvolvida com o ecossistema Spring. O sistema implementa regras complexas de negócios para empréstimos, devoluções com multas acumuladas, controle de filas de reserva em tempo de execução, auditoria de dados e uma malha de segurança baseada em perfis de acesso.

---

## 🛠️ Tecnologias e Pré-requisitos

Para rodar e testar este projeto localmente, você precisará de:

- **Java 17** ou superior (recomenda-se Eclipse Temurin)
- **Maven 3.8+**
- **IDE** de sua preferência (IntelliJ IDEA, VS Code, Eclipse)

---

## ⚙️ Arquitetura e Configurações (Profiles)

O projeto está isolado e estruturado em dois ambientes distintos através de **Spring Profiles**:

### 📦 Desenvolvimento (`dev`)
Utiliza o banco de dados **H2 Database** configurado para persistência de dados em arquivo local. Os dados não são perdidos ao reiniciar a aplicação.
- **Arquivo local:** `./data/bibliotecadb`
- **H2 Console:** Disponível em `http://localhost:8080/h2-console`
- **Versionamento:** Banco estruturado e evoluído via scripts automatizados do **Flyway** (`src/main/resources/db/migration`).

### 🧪 Testes (`test`)
Utiliza o **H2 Database** puramente em memória (`jdbc:h2:mem:testdb`). O banco de dados é zerado a cada execução para isolar os testes de integração.

---

## 🚀 Como Executar o Projeto

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git](https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git)
   cd SEU_REPOSITORIO
