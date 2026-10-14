# Pet Adoption Portal — Backend

## About the Project
The Pet Adoption Portal backend is developed using Java and Spring Boot. It handles user authentication, pet listing management, adoption requests, and communication with a MySQL database.

## Technologies Used
- Java 21
- Spring Boot
- Maven
- MySQL
- Spring Data JPA
- Spring Security

## Features
- User registration and authentication
- Pet listing management
- Pet adoption requests
- Administrative management of pet listings and adoption requests
- Database integration using MySQL

## Prerequisites
- JDK 21
- MySQL Server
- Maven (or the included Maven Wrapper)

## Setup
1. Clone the repository.
2. Create a MySQL database named `pet_adoption`.
3. Configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` as environment variables.
4. Open a terminal in the project directory and run:

```powershell
.\mvnw.cmd spring-boot:run
```

The application is expected to run at `http://localhost:8080`.

## Security
Never commit database credentials, `.env` files, or other secrets. Configure authentication and authorization before deploying the application publicly.

## Author
Koushal Yadav
