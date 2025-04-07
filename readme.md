# AndreExamSpring2025 Project Overview

## Introduction
AndreExamSpring2025 is a RESTful API application for managing ski lessons and instructors. It provides endpoints for creating, reading, updating, and deleting ski lessons and instructors, as well as user authentication and authorization.

## Project Structure
The project follows a standard MVC architecture with the following components:

### Main Packages
- **config**: Configuration classes for the application, including Hibernate and application configuration
- **controllers**: Controllers for handling HTTP requests
- **dao**: Data Access Objects for database operations
- **dto**: Data Transfer Objects for transferring data between processes
- **entities**: Entity classes representing database tables
- **enums**: Enumeration types
- **exceptions**: Custom exception classes
- **routes**: Route definitions for the API
- **utils**: Utility classes

### Domain Model
The application manages the following entities:

1. **SkiLesson**:
   - Properties: id, name, price, level, startTime, endTime, location
   - Relationships: Many-to-one with Instructor

2. **Instructor**:
   - Properties: id, firstName, lastName, email, phone, yearsOfExperience
   - Relationships: One-to-many with SkiLesson

3. **UserAccount**:
   - Properties: username, password
   - Relationships: Many-to-many with Roles

## API Endpoints
The application provides the following main endpoints:

- **SkiLessons**:
    - GET `/api/skilessons`: Get all ski lessons
    - GET `/api/skilessons/level/{level}`: Get ski lessons by level
    - GET `/api/skilessons/{id}`: Get a ski lesson by ID
    - POST `/api/skilessons`: Create a new ski lesson
    - PUT `/api/skilessons/{id}`: Update a ski lesson
    - DELETE `/api/skilessons/{id}`: Delete a ski lesson
    - PUT `/api/skilessons/{lessonId}/instructors/{instructorId}`: Add an instructor to a ski lesson
    - POST `/api/skilessons/populate`: Populate the database with ski lessons

- **Instructors**:
    - GET `/api/instructors/{id}/skilessons`: Get ski lessons by instructor

- **Security**:
    - GET `/api/auth/test`: Test endpoint
    - GET `/api/auth/healthcheck`: Health check endpoint
    - POST `/api/auth/login`: Authenticate a user
    - POST `/api/auth/register`: Register a new user
    - GET `/api/auth/verify`: Verify a token
    - GET `/api/auth/tokenlifespan`: Get token lifespan

## Testing
The project includes unit tests and integration tests using JUnit, RestAssured, and TestContainers. Tests can be run with Maven: `mvn test`

## Technologies Used
- **Java 17**: Programming language
- **Maven**: Build tool
- **Hibernate**: ORM for database operations
- **Javalin**: Web framework for building RESTful APIs
- **PostgreSQL**: Database
- **JBCrypt & TokenSecurity**: Security libraries for authentication and authorization
- **JUnit, RestAssured, TestContainers**: Testing frameworks
- **Lombok**: Reduces boilerplate code
- **Jackson**: JSON handling
- **SLF4J & Logback**: Logging

## Getting Started
1. Clone the repository
2. Create a `config.properties` file in the resources directory with the following properties:
```
DB_NAME=your_db_name
DB_USERNAME=postgres
DB_PASSWORD=your_password
SECRET_KEY=minimum32characterslong
ISSUER=your_issuer
TOKEN_EXPIRE_TIME=3600000
```
The DB_NAME, DB_USERNAME, DB_PASSWORD, ISSUER, and TOKEN_EXPIRE_TIME properties should be filled in with the appropriate values. The SECRET_KEY property should be a minimum of 32 characters long.
3. Build the project with Maven: `mvn clean package`
4. Run the application: `java -jar target/app.jar`


## Deployment
The application can be deployed as a Docker container using the provided Dockerfile.
