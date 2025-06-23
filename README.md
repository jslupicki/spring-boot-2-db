## Project Description

This project researches how to use **Spring Data JDBC** to work with two different databases simultaneously within a single application. It showcases managing two independent database connections (in this example: two H2 in-memory databases), each with its own repository and data source configuration.

## Technologies

- **Spring Boot**
- **Spring Data JDBC**
- **Kotlin 2.1**
- **Java 24**
- **H2 Database** (in-memory databases for testing)
- **Flyway** – for database migrations - migrations are performed programmatically

## Key Features

- Multiple data source support using Spring Data JDBC (each database has its own configuration)
- Separate repositories for each database
- Use-case example: saving and reading data independently for each database
- Two data sources configured in `application.yaml`

## Requirements

- Java 24+
- Maven
- IntelliJ IDEA (recommended)
- No external database requirements (everything runs in-memory)

## Quick Start

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd spring-boot-2-db
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   or directly:
   ```bash
   java -jar target/spring-boot-2-db-0.0.1-SNAPSHOT.jar
   ```

## Configuration

The default `application.yaml` contains the full configuration for both H2 databases (`db1`, `db2`), including connection pool settings (HikariCP). Each database has its own set of parameters.

Spring Boot managed Flyway migrations are disabled as they are performed programmatically (FlywayMigrate).
