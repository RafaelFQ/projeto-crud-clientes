# 🚀 CRUD de Clientes - API REST

API REST desenvolvida com Spring Boot para gerenciamento de clientes, permitindo realizar operações completas de CRUD (Create, Read, Update, Delete) com validação de dados e tratamento de exceções.

---

## 🧩 Modelo de Domínio
<img width="214" height="205" alt="Image" src="https://github.com/user-attachments/assets/c01c3824-b253-4196-9757-6e5e7b2d1fa5" />

---

## 📌 Funcionalidades

A API permite realizar as seguintes operações:

✔️ Buscar cliente por ID <br>
✔️ Listar clientes com paginação <br>
✔️ Inserir novo cliente <br>
✔️ Atualizar cliente existente <br>
✔️ Deletar cliente

Além disso, a aplicação possui:

* Validação de dados
* Tratamento de exceções
* Retorno de erros customizados
* Paginação de resultados

---

## 🛠 Tecnologias utilizadas
* Java 21
* Spring Boot 4.0.3
* Spring Data JPA / Hibernate
* Banco de dados H2
* Maven
* Postman

---

## 🧪 Testes da API

Os testes podem ser realizados utilizando o Postman.<br>
Exemplo de endpoint local:<br>
http://localhost:8080/clients

✔️  **Busca de cliente por id**<br>
**GET /clients/1**

✔️  **Busca paginada de clientes**<br>
**GET /clients?page=0&size=6&sort=name**

✔️  **Inserção de novo cliente**<br>

**POST /clients**
```json
{
  "name": "Maria Silva",
  "cpf": "12345678901",
  "income": 6500.0,
  "birthDate": "1994-07-20",
  "children": 2
}
```
✔️  **Atualização de cliente**<br>
**PUT /clients/1**
```json
{
"name": "Maria Silvaaa",
"cpf": "12345678901",
"income": 6500.0,
"birthDate": "1994-07-20",
"children": 2
}
```
✔️  **Deleção de cliente**<br>
**DELETE /clients/1**

---

## 👤 Autor

**Rafael Artur F Quaresma**

📌 Projeto desenvolvido como **desafio prático** do curso **Java Spring Professional**, da DevSuperior.
