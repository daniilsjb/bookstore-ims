# Bookstore IMS

A bookstore inventory management system implemented as a Software Engineering course project.

## Features

- Management of the bookstore's product catalogue. 📕
- Creation of stock movements with automatic updates to stock levels. 📈
- Registration of users by managers, authentication, and basic access control. 👥

## Technologies

- [Kotlin][kotlin], a modern and type-safe programming language.
- [PostgreSQL][postgres], an object-relational database management system.
- [Spring JPA][spring-jpa], a Hibernate-based data access layer implementation.
- [Spring Boot][spring-boot], a highly popular Java-based web framework.
- [Vaadin Flow][vaadin], a library for development of user interfaces.
- [Docker][docker], a containerization tool used in development.

## Building

To run this project locally, you will need to start up the database followed by the application itself.

### Prerequisites

There are several options for building this project, but ultimately you will need the following:

- [Docker][docker] or a local installation of [PostgreSQL][postgres]
- Java 17 SDK

You may perform all the steps using a terminal or use a modern IDE such as [IntelliJ IDEA][intellij]. If you don't have
Docker installed, you may wish to consider using [Docker Desktop][docker-desktop], which is bundled with all necessary
software along with a graphical user interface.

### Starting the Database

This project uses PostgreSQL database management system. To run the application, you can either start a database server
using Docker or by manually running the DBMS on your machine.

#### Using Docker (Recommended)

The simplest way to start the database container is via the provided [`docker-compose.yml`](/docker-compose.yml)
configuration file. Assuming you are in the root of the repository, simply run the following:

```sh
docker-compose up
```

This will take care of pulling the necessary image and setting up the environment variables, exposed ports, etc.
Alternatively, you may explicitly run the equivalent Docker command yourself:

```sh
docker run -d \
  --name bookstore_db \
  -e POSTGRES_DB=bookstore \
  -e POSTGRES_USER=bookstore_user \
  -e POSTGRES_PASSWORD=bookstore_user \
  -p 5432:5432 \
  postgres
```

#### Manually

Create a new database server using the PostgreSQL package and configure it with the following settings (applicable to
local environments only):

| Setting       | Value            |
|---------------|------------------|
| Database Name | `bookstore`      |
| Username      | `bookstore_user` |
| Password      | `bookstore_user` |
| Host          | `localhost`      |
| Port          | `5432`           |

### Starting the Application

Before running the server, ensure that the database has started successfully.

> [!Note]
> To access the system you need to be authenticated as one of its users. By default, the system always starts with the
> following test users when executed in a local development environment:
>
> | Username | Password   | Role     |
> |----------|------------|----------|
> | John Doe | `password` | Manager  |
> | Robb Doe | `password` | Employee |
> | POS #001 | `password` | Employee |

#### Using IntelliJ IDEA (Recommended)

Simply open the project in the IDE and run it - IntelliJ will automatically take care of dependencies, etc.  If you do
not have a suitable Java 17 SDK, you may [download one directly from the IDE][intellij-jdk] (such as the Eclipse Temurin
version of OpenJDK).

To verify that the application has started correctly, simply open the URL <http://localhost:8080> in any web browser of
your choice. You may also try running HTTP request samples from the [`http`](/http) folder using [IntelliJ's built-in
HTTP client][intellij-http] after selecting the `local` environment profile.

#### Using Terminal

If you prefer building from the command-line, you may use the provided `mvnw` script to build and run the project.
Ensure that the path to your Java 17 SDK is specified in the `JAVA_HOME` environment variable.

If you are on Windows, execute the following commands:

```sh
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

If you are on Linux or macOS, execute the following commands:

```sh
./mvnw clean install
./mvnw spring-boot:run
```

[kotlin]: https://kotlinlang.org/
[postgres]: https://www.postgresql.org/

[spring-boot]: https://spring.io/projects/spring-boot
[spring-jpa]: https://spring.io/projects/spring-data-jpa
[vaadin]: https://vaadin.com/

[docker]: https://www.docker.com/
[docker-desktop]: https://www.docker.com/products/docker-desktop/

[intellij]: https://www.jetbrains.com/idea/
[intellij-jdk]: https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk
[intellij-http]: https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html
