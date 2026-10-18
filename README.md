# Auto Escola 3ESR — Checkpoint 5

API REST desenvolvida em Java com Spring Boot para gerenciamento de uma auto-escola, incluindo instrutores, alunos, usuários autenticados e agendamento/cancelamento de instruções.

## Integrantes

- Mohamed Afif — RM: 554445
- Lucca Cardinale — RM: 556668



## Tecnologias

- Java 21 (Java 17 ou superior; o projeto foi configurado para Java 21)
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring Security
- JWT (java-jwt)
- BCrypt para criptografia de senhas
- MySQL
- Flyway
- Bean Validation
- Maven
- Springdoc OpenAPI / Swagger UI
- ViaCEP (consulta externa de CEP)
- JUnit 5, AssertJ e Spring Boot Test (testes automatizados)

## Funcionalidades implementadas

### Autenticação

- Login com usuário e senha.
- Geração de token JWT com validade de 30 minutos.
- Endpoints protegidos exigem `Authorization: Bearer <TOKEN>`.
- Senhas são armazenadas usando BCrypt.

### Usuários

Somente administradores podem:

- cadastrar usuários;
- listar usuários;
- consultar usuário por ID;
- atualizar login/perfil;
- excluir usuários.

Um usuário autenticado pode alterar a própria senha informando a senha atual e a nova senha.

Perfis disponíveis:

- `ADMIN`
- `USER`

### Instrutores

- Cadastro com nome, e-mail, telefone, CNH, especialidade e endereço.
- Listagem de instrutores ativos, ordenada por nome e paginada em 10 registros.
- Atualização somente de nome, telefone e endereço.
- E-mail, CNH e especialidade não podem ser alterados.
- Exclusão lógica: o instrutor fica `ativo = false` e não é apagado fisicamente.

Especialidades:

- `MOTOS`
- `CARROS`
- `VANS`
- `CAMINHOES`

### Alunos

- Cadastro com nome, e-mail, telefone, CPF e endereço.
- Listagem de alunos ativos, ordenada por nome e paginada em 10 registros.
- Atualização somente de nome, telefone e endereço.
- E-mail e CPF não podem ser alterados.
- Exclusão lógica: o aluno fica `ativo = false`.

### Agendamento de instruções

- Segunda a sábado.
- Horários de início entre 06:00 e 20:00, considerando a duração fixa de 1 hora.
- Antecedência mínima de 30 minutos.
- Aluno precisa estar ativo.
- Instrutor informado precisa estar ativo.
- Máximo de 2 instruções por aluno no mesmo dia.
- Um instrutor não pode possuir duas instruções no mesmo horário.
- Quando o instrutor não é informado, o sistema seleciona aleatoriamente um instrutor ativo e disponível da especialidade informada.
- Instruções canceladas não ocupam o horário do instrutor e não entram no limite diário do aluno.

### Cancelamento

- Motivo obrigatório entre:
  - `ALUNO_DESISTIU`
  - `INSTRUTOR_CANCELOU`
  - `OUTROS`
- Cancelamento permitido somente com antecedência mínima de 24 horas.
- O cancelamento é registrado sem apagar a instrução original.


### Consulta de endereço por CEP (API externa ViaCEP)

O projeto integra com o serviço público [ViaCEP](https://viacep.com.br/), usando `RestClient` do Spring. A consulta é feita pela API da própria aplicação e não exige token JWT.

- `GET /enderecos/cep/{cep}` — consulta o CEP e retorna logradouro, complemento, bairro, cidade e UF.
- O CEP pode ser enviado com ou sem hífen, por exemplo `01001-000` ou `01001000`.
- CEP com formato inválido retorna `400 Bad Request`; CEP inexistente retorna `404 Not Found`; falha no serviço externo retorna `502 Bad Gateway`.

Exemplo:

```http
GET http://localhost:8081/enderecos/cep/01001-000
```

Resposta esperada (os campos podem variar conforme o CEP):

```json
{
  "cep": "01001-000",
  "logradouro": "Praça da Sé",
  "complemento": "lado ímpar",
  "bairro": "Sé",
  "cidade": "São Paulo",
  "uf": "SP",
  "erro": null
}
```

## Configuração do banco

Crie o banco MySQL:

```sql
CREATE DATABASE autoescola3esr;
```

As migrations do Flyway criam e atualizam as tabelas automaticamente ao iniciar a aplicação.

Por padrão, a aplicação usa:

```text
URL: jdbc:mysql://localhost:3306/autoescola3esr
Usuário: root
Senha: fiap
Porta da API: 8081
```

Se necessário, altere pelas variáveis de ambiente `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `JWT_SECRET`.

## Usuário inicial

Em uma instalação limpa, a migration `V9__insert-admin-user.sql` cria automaticamente:

```text
Login: admin@autoescola.com
Senha: password
Perfil: ADMIN
```

A senha é armazenada no banco somente em formato BCrypt.

Depois do primeiro acesso, recomenda-se alterar a senha pelo endpoint `/usuarios/minha-senha`.

## Como executar

### IntelliJ IDEA

1. Abra a pasta do projeto.
2. Verifique se o Java configurado é 25 ou outra versão igual/superior a 17 compatível com o projeto.
3. Certifique-se de que o MySQL esteja em execução.
4. Crie o banco `autoescola3esr`.
5. Aguarde o Maven baixar as dependências.
6. Execute `AutoEscola3EsrApplication`.
7. A API ficará disponível em `http://localhost:8081`.

### Maven

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

## Swagger / OpenAPI

Com a aplicação em execução, acesse:

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- Documento OpenAPI em JSON: `http://localhost:8081/v3/api-docs`
- Documento OpenAPI em YAML: `http://localhost:8081/v3/api-docs.yaml`

No Swagger, use **Authorize** e informe `Bearer <TOKEN>` para testar os endpoints protegidos. O endpoint de consulta de CEP é público e pode ser executado sem autenticação.

## Testes automatizados

Os testes automatizados ficam em `src/test/java` e incluem testes unitários das entidades `Aluno`, `Instrutor`, `Instrucao` e `Usuario`, além da validação de formato do CEP no serviço ViaCEP. Execute com `mvn test` (Windows: `mvnw.cmd test`; Linux/macOS: `./mvnw test`). Os testes de repositório e de integração existentes precisam de um MySQL de teste disponível conforme `src/main/resources/application-test.properties`.

## Testes manuais da API

Os testes abaixo podem ser realizados pelo Swagger, Postman, Insomnia ou outra ferramenta HTTP. Sempre faça o login primeiro e copie o JWT para o header `Authorization`.

### 1. Login

`POST /login`

```json
{
  "login": "admin@autoescola.com",
  "senha": "password"
}
```

Resposta esperada: `200 OK` com um token JWT.

### 2. Cadastro de usuário comum

Com o token de ADMIN:

`POST /usuarios`

```json
{
  "login": "usuario@autoescola.com",
  "senha": "123456",
  "perfil": "USER"
}
```

Esperado: `201 Created`.

### 3. Listagem de usuários

Com ADMIN:

`GET /usuarios`

Esperado: `200 OK`, sem retornar a senha.

### 4. Testar bloqueio de usuário comum

Faça login com `usuario@autoescola.com` e a senha cadastrada.

Tente:

`GET /usuarios`

Esperado: `403 Forbidden`.

Isso comprova a regra de que somente ADMIN pode administrar usuários.

### 5. Alteração da própria senha

Com o token do usuário autenticado:

`PUT /usuarios/minha-senha`

```json
{
  "senhaAtual": "123456",
  "novaSenha": "novaSenha123"
}
```

Esperado: `204 No Content`.

Depois faça login novamente usando a nova senha.

### 6. Cadastro de aluno

Com ADMIN:

`POST /alunos`

```json
{
  "nome": "Aluno Teste",
  "email": "aluno.teste@email.com",
  "telefone": "11999999999",
  "cpf": "12345678901",
  "endereco": {
    "logradouro": "Rua Teste",
    "numero": "100",
    "complemento": "Apto 10",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01000-000"
  }
}
```

Esperado: `201 Created`.

### 7. Listagem de alunos

`GET /alunos`

Esperado: somente alunos ativos, ordenados por nome e com paginação de 10 registros.

### 8. Atualização de aluno

`PUT /alunos`

```json
{
  "id": 1,
  "nome": "Aluno Teste Atualizado",
  "telefone": "11988888888",
  "endereco": {
    "logradouro": "Rua Nova",
    "numero": "200",
    "complemento": "",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01000-001"
  }
}
```

Não envie `email` ou `cpf`: esses dados não fazem parte do DTO de atualização e, portanto, não podem ser alterados por esse endpoint.

### 9. Exclusão lógica de aluno

`DELETE /alunos/1`

Esperado: `204 No Content`.

Depois:

`GET /alunos`

O aluno não deve mais aparecer na listagem de ativos. O registro continua no banco com `ativo = false`.

### 10. Cadastro de instrutor

`POST /instrutores`

```json
{
  "nome": "Instrutor Teste",
  "email": "instrutor.teste@email.com",
  "telefone": "11977777777",
  "cnh": "12345678901",
  "especialidade": "CARROS",
  "endereco": {
    "logradouro": "Rua Teste",
    "numero": "50",
    "complemento": "",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01000-000"
  }
}
```

Esperado: `201 Created`.

### 11. Listagem de instrutores

`GET /instrutores`

Esperado: nome, e-mail, CNH/especialidade conforme o DTO utilizado pela implementação, somente ativos, ordenados por nome e paginados em 10.

### 12. Testar proteção da atualização do instrutor

`PUT /instrutores`

```json
{
  "id": 1,
  "nome": "Instrutor Atualizado",
  "telefone": "11966666666"
}
```

Esperado: atualização de nome/telefone/endereço.

Não é possível alterar e-mail, CNH ou especialidade porque esses campos não fazem parte do DTO de atualização.

### 13. Exclusão lógica de instrutor

`DELETE /instrutores/1`

Esperado: `204 No Content`.

O registro permanece no banco, mas com `ativo = false`.

### 14. Agendamento com instrutor informado

`POST /instrucoes`

Use uma data futura, de segunda a sábado, com horário inteiro entre 06:00 e 20:00 e pelo menos 30 minutos de antecedência.

```json
{
  "id_aluno": 1,
  "id_instrutor": 2,
  "data_hora": "15/09/2026 - 10:00"
}
```

Esperado: `200 OK`.

### 15. Agendamento com instrutor automático

Não informe `id_instrutor`, mas informe a especialidade:

```json
{
  "id_aluno": 1,
  "especialidade": "CARROS",
  "data_hora": "15/09/2026 - 11:00"
}
```

Esperado: o sistema escolherá aleatoriamente um instrutor ativo e disponível de `CARROS`.

### 16. Testar limite de duas instruções por aluno

Agende duas instruções para o mesmo aluno no mesmo dia, em horários diferentes.

A terceira tentativa no mesmo dia deve retornar erro de regra de negócio.

### 17. Testar conflito de instrutor

Agende uma instrução para um instrutor em `10:00` e tente agendar outra para o mesmo instrutor em `10:00`.

Esperado: erro informando que o instrutor está indisponível.

### 18. Testar aluno inativo

Exclua logicamente um aluno e tente agendar uma instrução para ele.

Esperado: erro de regra de negócio.

### 19. Testar instrutor inativo

Exclua logicamente um instrutor e tente agendar uma instrução usando o ID dele.

Esperado: erro de regra de negócio.

### 20. Consultar instrução

`GET /instrucoes/{id}`

Esperado: `200 OK`, com dados do aluno, instrutor, data/hora e situação de cancelamento.

### 21. Cancelamento com menos de 24 horas

Tente cancelar uma instrução que ocorrerá em menos de 24 horas.

`POST /instrucoes/{id}/cancelamento`

```json
{
  "motivo": "ALUNO_DESISTIU"
}
```

Esperado: erro de regra de negócio informando a antecedência mínima de 24 horas.

### 22. Cancelamento válido

Use uma instrução que ocorrerá daqui a mais de 24 horas:

`POST /instrucoes/{id}/cancelamento`

```json
{
  "motivo": "ALUNO_DESISTIU"
}
```

Esperado: `200 OK` e a instrução deve aparecer com `cancelada: true` e o motivo informado.

### 23. Testar motivos inválidos

Envie, por exemplo:

```json
{
  "motivo": "NAO_EXISTE"
}
```

Esperado: erro de validação, pois o motivo deve ser uma das opções definidas no enunciado.

## Endpoints principais

| Método | Endpoint | Acesso |
|---|---|---|
| POST | `/login` | Público |
| POST | `/usuarios` | ADMIN |
| GET | `/usuarios` | ADMIN |
| GET | `/usuarios/{id}` | ADMIN |
| PUT | `/usuarios` | ADMIN |
| DELETE | `/usuarios/{id}` | ADMIN |
| PUT | `/usuarios/minha-senha` | Autenticado |
| POST | `/instrutores` | ADMIN |
| GET | `/instrutores` | ADMIN/USER |
| GET | `/instrutores/{id}` | ADMIN/USER |
| PUT | `/instrutores` | ADMIN |
| DELETE | `/instrutores/{id}` | ADMIN |
| POST | `/alunos` | ADMIN |
| GET | `/alunos` | ADMIN/USER |
| GET | `/alunos/{id}` | ADMIN/USER |
| PUT | `/alunos` | ADMIN |
| DELETE | `/alunos/{id}` | ADMIN |
| POST | `/instrucoes` | ADMIN/USER |
| GET | `/instrucoes/{id}` | ADMIN/USER |
| GET | `/enderecos/cep/{cep}` | Público (ViaCEP) |
| POST | `/instrucoes/{id}/cancelamento` | ADMIN/USER |

## Migrations Flyway

As migrations estão em `src/main/resources/db/migration` e devem ser executadas automaticamente na inicialização:

- `V1` — criação de instrutores
- `V2` — telefone de instrutores
- `V3` — ativo de instrutores
- `V4` — criação de usuários
- `V5` — perfil de usuários
- `V6` — criação de alunos
- `V7` — criação de instruções
- `V8` — ativo de alunos
- `V9` — usuário ADMIN inicial
- `V10` — campos de cancelamento de instruções


## Observação sobre o usuário inicial

O usuário `admin@autoescola.com` / `password` existe apenas para facilitar a primeira execução em uma instalação limpa. Após o primeiro login, altere a senha e, para um ambiente de produção, utilize um segredo JWT e credenciais de banco por variáveis de ambiente.
