## Project Description

This project researches how to use **Spring Data JDBC** to work with two different databases simultaneously within a
single application. It showcases managing two independent database connections (in this example: two H2 in-memory
databases), each with its own repository and data source configuration.

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

The default `application.yaml` contains the full configuration for both H2 databases (`db1`, `db2`), including
connection pool settings (HikariCP). Each database has its own set of parameters.

Spring Boot managed Flyway migrations are disabled as they are performed
programmatically ([FlywayMigrate](src/main/kotlin/com/slupicki/springboot2db/config/FlywayMigrate.kt)).

## Technical Details

I didn't find a way to use pure `@EnableJdbcRepositories` annotations to enable repositories for multiple data sources.
The final obstacle was that the `@EnableJdbcRepositories` annotation does not have `mappingContextRef` to provide custom `mappingContext`.
I tried multiple approaches, but I always ended up with the same error that there are 2 `JdbcMappingContext` beans and
`Spring Data JDBC` does not know which one to use.

**Solution**: The solution which I found is to create repository beans manually in the configuration classes.
Example [Db1Config](src/main/kotlin/com/slupicki/springboot2db/config/Db1Config.kt) and method `db1SomeEntityRepository`
which use `JdbcRepositoryFactory` to create the repository bean. In this way we will have full control over the
repository creation process and we can specify which `JdbcMappingContext` to use for each repository.
Also repositories are the same as created by Spring Data JDBC, so they have all the features like queries
based on method names, etc.


- **Data Sources**: Two data sources are defined in [`application.yaml`](src/main/resources/application.yaml):
  - `db1`: Represents the first H2 database.
  - `db2`: Represents the second H2 database.
- **Repositories**: Each database has its own repository interface:
  - [`Db1SomeEntityRepository`](src/main/kotlin/com/slupicki/springboot2db/repo/db1/Db1SomeEntityRepository.kt): For operations on the first database.
  - [`Db2SomeEntityRepository`](src/main/kotlin/com/slupicki/springboot2db/repo/db2/Db2SomeEntityRepository.kt): For operations on the second database.
- **DB Configurations**: Each database has its own configuration class:
  - [`Db1Config`](src/main/kotlin/com/slupicki/springboot2db/config/Db1Config.kt): Configuration for the first database.
  - [`Db2Config`](src/main/kotlin/com/slupicki/springboot2db/config/Db2Config.kt): Configuration for the second database.
