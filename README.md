# Birthday-Keeper REST Application

## Description
**Birthday-Keeper** is a **Spring Boot** RESTful application that enables users 
to manage their friends' birthdays effectively. With this app, users can:

- Add friends and their dates of birth. 
- Calculate the number of days remaining until the next birthday. 
- Securely manage user accounts with authentication and authorization.

The app integrates with a MySQL database for persistent data storage 
and provides Swagger-based API documentation for easy testing and exploration.

## Docker instructions

**Instructions for Using Docker**

Instead of setting up a local environment (as described in the next section), you can use Docker to run the application:

1. **Ensure prerequisites**: Install Docker and `docker-compose` on your system.
2. **Clone the repository**:
   ```shell
    git clone https://github.com/mkoutra/birthday-keeper
    cd birthday-keeper
    ```
3. **Build the JAR**:
- For **Linux/macOS**:
    ```bash
    ./gradlew clean build
    ```
- For **Windows**:
  ```shell
    gradlew.bat clean build
  ```
4. **Start the database service**:
   ```bash
    docker-compose up mysql-db
   ```
   **Wait** for the database to fully initialize (~1 minute).
5. **Start the Spring REST app** in a (different terminal):
   ```bash
    docker-compose up spring-backend
   ```

**Shut Down the Services**

   To stop and remove the containers, close each terminal running the services,
   then run the following command in your project directory:
   ```bash
    docker-compose down 
   ```
   This will cleanly stop and remove all the containers created by docker-compose.

## Requirements

To run this application, **without Docker**, ensure the following are installed:

- **Java 17+**: Required to run the Spring Boot application.
- **MySQL**: Used as the database for storing user and birthday information.

## Setup and Run

### Step 1: Clone the Repository

```shell
git clone https://github.com/mkoutra/birthday-keeper
cd birthday-keeper
```

### Step 2: Configure the Database

  1. Create a MySQL database for the application.
  2. Open the configuration file `src/main/resources/application-test.properties`
     and update the database credentials if needed:
      ```.properties
        spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DB:birthdaykeeperdb}?serverTimezone=UTC
        spring.datasource.username=${MYSQL_USER:springuser}
        spring.datasource.password=${MYSQL_PASSWORD:12345}
      ```
      The current version uses a MySQL with the following specs:
      - Database name: `birthdaykeeperdb`
      - username: `springuser`
      - password: `12345`
      - host: `localhost`
      - port: `3306`
  3. Ensure your MySQL server is running.

### Step 3: Run the Application

- For **Linux/macOS**:
    ```bash
    ./gradlew bootRun
    ```
- For **Windows**:
  ```shell
    gradlew.bat bootRun
  ```
The application will start and run at http://localhost:8080.

### Step 4: Explore the API

- **Swagger UI**:

  Open http://localhost:8080/swagger-ui.html for a graphical interface to 
  view and test the API endpoints.

- **JSON API Docs**:

  Access raw API documentation at http://localhost:8080/v3/api-docs.