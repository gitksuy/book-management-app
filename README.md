# Book Management API

A Spring Boot RESTful API for managing books with Google Books API integration. This project showcases best practices in building modern Java microservices.

## Features

- **Complete Book Management**: Create, read, update, and delete operations for books
- **Pagination and Sorting**: Efficient data retrieval with 10 books per page
- **Advanced Search**: Filter books by title, author, or category
- **Google Books Integration**: Enhance book details with data from Google Books API
- **Comprehensive Logging**: Detailed request/response logging for troubleshooting
- **Error Handling**: Global exception handling with meaningful error messages
- **API Documentation**: Interactive Swagger UI for testing and exploration
- **Unit Tests**: Thorough test coverage of service layer

## Technology Stack

- **Java 17**: Modern language features for cleaner, more concise code
- **Spring Boot 3.2.3**: Latest Spring Boot framework with Spring WebMVC
- **Spring Data JPA**: Object-relational mapping and repository abstraction
- **H2 Database**: In-memory database for development and testing
- **WebFlux**: Non-blocking API calls to external services
- **Swagger/OpenAPI 3**: API documentation and testing interface
- **Lombok**: Reduce boilerplate code with annotations
- **JUnit 5 & Mockito**: Comprehensive unit testing

## Project Structure

```
book-management-api/
├── src/
│   ├── main/
│   │   ├── java/com/interview/bookapi/
│   │   │   ├── BookApiApplication.java       # Application entry point
│   │   │   ├── config/                       # Application configuration
│   │   │   ├── controller/                   # REST API endpoints
│   │   │   ├── dto/                          # Data Transfer Objects
│   │   │   ├── entity/                       # JPA Entity models
│   │   │   ├── exception/                    # Exception handling
│   │   │   ├── repository/                   # Data access layer
│   │   │   ├── service/                      # Business logic
│   │   │   └── util/                         # Utility classes
│   │   └── resources/
│   │       └── application.properties        # Application configuration
│   └── test/
│       └── java/com/interview/bookapi/       # Test classes
├── postman/                                  # Postman collection for API testing
└── pom.xml                                   # Project dependencies
```

## API Endpoints

| HTTP Method | Endpoint | Description |
|-------------|----------|-------------|
| GET | `/api/books` | Get all books (paginated) |
| GET | `/api/books/{id}` | Get book by ID |
| GET | `/api/books/{id}/details` | Get book with Google Books details |
| GET | `/api/books/search` | Search books by title, author, or category |
| POST | `/api/books` | Create a new book |
| PUT | `/api/books/{id}` | Update an existing book |
| DELETE | `/api/books/{id}` | Delete a book |

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use the Maven wrapper included)

### Running the Application

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/book-management-app.git
   cd book-management-app
   ```

2. Set up your configuration:
   ```
   cp src/main/resources/application-template.properties src/main/resources/application.properties
   ```
   Then edit `application.properties` to add your Google Books API key and other sensitive information.

3. Build the project:
   ```
   mvn clean package
   ```

4. Run the application:
   ```
   java -jar target/book-api-0.0.1-SNAPSHOT.jar
   ```

5. The application will start on port 8080 by default. You can access:
   - API: http://localhost:8080/api/books
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb, Username: sa, Password: password)

### Secure Configuration Management

This project follows best practices for managing sensitive configuration:

1. **Template Configuration**: The repository includes `application-template.properties` with placeholders for sensitive data.

2. **Environment-Specific Configs**: Example files (`application-dev.properties.example`) show how to set up environment-specific configurations.

### Configuration

Key application settings in `application.properties`:

- Database configuration (H2 in-memory database by default)
- Google Books API key
- Logging settings
- Swagger/OpenAPI configuration

## Development

### Running Tests

```
mvn test
```

### API Documentation

Interactive API documentation is available via Swagger UI when the application is running:
http://localhost:8080/swagger-ui.html

### Postman Collection

For those who prefer testing with Postman, a ready-to-use collection is included in the `postman` directory. The collection includes requests for all API endpoints with example data.

To use the Postman collection:

1. Open Postman
2. Click "Import" > "File" > Browse to `postman/Book_Management_API.postman_collection.json`
3. The collection will be imported with all preconfigured requests
4. Make sure the application is running before sending requests

Sample requests included:
- Create new books
- Retrieve books with pagination
- Search for books by various criteria
- Update and delete books
- Retrieve book details with Google Books integration

## License

This project is licensed under the MIT License - see the LICENSE file for details.