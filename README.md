# SaasOOH — Backend

API REST de uma plataforma SaaS voltada para empresas de mídia Out-of-Home (OOH). Permite gerenciar o inventário de painéis publicitários, campanhas, clientes e equipe comercial, com controle de acesso por perfil e plano de assinatura.

> 🖥️ Frontend: [saasooh-frontend](https://github.com/leonardondornelles/saasooh-frontend)

---

## Tecnologias

- **Java 21** + **Spring Boot 3.4**
- **Spring Security** + **JWT** (autenticação stateless)
- **Spring Data JPA** + **PostgreSQL**
- **Lombok** · **Bean Validation**
- **Maven**

---

## Funcionalidades

### Autenticação e Multitenancy
- Registro de tenant (empresa + usuário admin) em uma única operação
- Login com geração de token JWT
- Filtro JWT aplicado a todas as rotas protegidas
- Controle de acesso por `RoleUser`: `ADMIN`, `EXECUTIVO`, `FINANCEIRO`

### Gestão de Painéis e Faces
- Cadastro de painéis com geolocalização (latitude/longitude), tipo, iluminação e cidade
- Tipos suportados com limite de faces por tipo:

| Tipo | Faces |
|---|---|
| OUTDOOR | 2 |
| FRONT_LIGHT | 2 |
| TRIEDRO | 3 |
| LED | 5 |
| EMPENA | 1 |
| RODOVIÁRIO | 2 |

- Cada face possui status independente (`FaceStatus`)

### Campanhas
- Associação entre cliente, face, executivo responsável e empresa
- Datas de início/fim, valor mensal e valor total
- Status do ciclo de vida: `RESERVED`, `ACTIVE`, `FINISHED`, `CANCELLED`

### Clientes e Equipe
- CRUD de clientes e colaboradores
- DTO de performance de executivo (`ExecutivePerformanceDTO`)

### Planos SaaS
Cada empresa opera em um plano que define limites de funcionalidades:

| Plano | Painéis | Alertas | Portal do Cliente | Propostas PDF |
|---|---|---|---|---|
| BASIC | Até 50 | ❌ | ❌ | ❌ |
| PRO | Até 300 | ✅ | ✅ | ✅ |
| ENTERPRISE | Ilimitado | ✅ | White-label | ✅ |

---

## Estrutura do Projeto

```
src/main/java/com/neuralFlux/Saas_OOH_demo/
├── controllers/        # Endpoints REST
├── services/           # Regras de negócio
├── repositories/       # Interfaces JPA
├── models/             # Entidades JPA
├── dtos/               # Objetos de transferência de dados
├── enums/              # PanelType, FaceStatus, RoleUser, SaasPlan, StatusCampaign
├── security/           # JwtFilter, SecurityConfig, UserDetailsImpl
└── exceptions/         # GlobalExceptionHandler, ResourceNotFoundException
```

---

## Como Rodar Localmente

### Pré-requisitos
- Java 21+
- Maven 3.9+
- PostgreSQL rodando localmente

### 1. Clone o repositório
```bash
git clone https://github.com/leonardondornelles/saasooh-backend.git
cd saasooh-backend
```

### 2. Configure o banco de dados

Crie um banco no PostgreSQL e edite `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/saasooh
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update

jwt.secret=sua_chave_secreta
jwt.expiration=86400000
```

### 3. Execute

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## Principais Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/register` | Registro de novo tenant |
| POST | `/api/auth/login` | Login e geração de JWT |
| GET | `/api/users/me` | Dados do usuário autenticado |
| GET/POST | `/api/panels` | Listar / criar painéis |
| GET | `/api/panels/{id}` | Detalhes do painel com faces |
| GET/POST | `/api/campaigns` | Listar / criar campanhas |
| GET/POST | `/api/customers` | Listar / criar clientes |
| GET/POST | `/api/faces` | Listar / criar faces |

Todas as rotas (exceto `/api/auth/**`) exigem o header:
```
Authorization: Bearer <token>
```

---

## Autor

**Leonardo Noronha Dornelles**  
Estudante de Ciência da Computação  
[GitHub](https://github.com/leonardondornelles)
