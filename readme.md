# Budget Tracker API

## Overview

This is a RESTful API built using Spring Boot for a personal budget tracking application. It allows users to manage their finances by tracking income, expenses, and categories. Users can record transactions, categorize them, and generate reports to understand their spending habits.

This project is built as a demonstration of backend API development skills using Java and Spring Boot.

## Features

*   **User Management:**
    *   User registration and login
    *   User profile management (view and update)
*   **Category Management:**
    *   Create, read, update, and delete categories (Income/Expense types)
*   **Transaction Management:**
    *   Create, read, update, and delete transactions (Income/Expense)
    *   Filter transactions by date range, category, and type
*   **Reporting:**
    *   Balance calculation for a given period
    *   Total income and expenses summary
    *   Spending summary by category

## Technology Stack

*   Java
*   Spring Boot
*   Spring Data JPA (for database interaction)
*   Spring Web (for REST API)
*   Spring Security (for authentication and authorization - *implementation recommended*)
*   Database: MySQL (or any relational database supported by Spring Boot)
*   Build Tool: Maven/Gradle
*   API Documentation: Swagger/OpenAPI (SpringDoc)
*   Testing: JUnit, Spring Test, Mockito (*recommended*)

## Setup Instructions

1.  **Prerequisites:**
    *   Java Development Kit (JDK) 17 or higher
    *   Maven or Gradle
    *   MySQL or another relational database

2.  **Clone the repository:**
    ```bash
    git clone [YOUR_REPOSITORY_URL]
    cd [YOUR_PROJECT_DIRECTORY]
    ```

3.  **Database Configuration:**
    *   Create a database named `budget_tracker_db` (or whatever you prefer).
    *   Update the database connection properties in `src/main/resources/application.properties` or `application.yml` to match your database setup (username, password, URL).  Example in `application.properties`:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/budget_tracker_db?useSSL=false&serverTimezone=UTC
        spring.datasource.username=your_db_username
        spring.datasource.password=your_db_password
        spring.jpa.hibernate.ddl-auto=update # Use 'create-drop' for development, 'update' for ensuring schema is up-to-date
        spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect # Or your specific database dialect
        ```

4.  **Build the application:**
    ```bash
    ./mvnw clean install  # For Maven
    ./gradlew clean build # For Gradle
    ```

5.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run # For Maven
    ./gradlew bootRun      # For Gradle
    ```
    The API will be accessible at `http://localhost:8080/api`.

6.  **API Documentation:**
    *   Access the Swagger UI for interactive API documentation at `http://localhost:8080/swagger-ui/index.html` after the application is running. This documentation provides detailed information about each endpoint, request bodies, response schemas, and authentication requirements.

## API Endpoints

All API endpoints are prefixed with `/api`. Authentication (using Bearer Token - JWT recommended) is required for most endpoints, unless explicitly stated as public.

### 1. User Endpoints (`/api/users`)

*   **`POST /api/users/register`**: **Register a new user (Public Endpoint)**
    *   **Description:**  Allows new users to register an account.
    *   **Request Body:** `application/json`
        ```json
        {
          "username": "newUsername",
          "email": "[email address removed]",
          "password": "securePassword"
        }
        ```
    *   **Response Codes:**
        *   `201 Created`: User registered successfully.
        *   `400 Bad Request`: Invalid input data (e.g., missing fields, invalid email format).
        *   `500 Internal Server Error`: Server-side error.

*   **`POST /api/users/login`**: **Login user (Public Endpoint)**
    *   **Description:**  Authenticates an existing user and returns an authentication token (e.g., JWT).
    *   **Request Body:** `application/json`
        ```json
        {
          "username": "existingUsername",
          "password": "userPassword"
        }
        ```
    *   **Response Codes:**
        *   `200 OK`: User logged in successfully. Response body may include an authentication token (JWT).
        *   `401 Unauthorized`: Authentication failed (invalid credentials).
        *   `400 Bad Request`: Invalid request.
        *   `500 Internal Server Error`: Server-side error.

*   **`POST /api/users/logout`**: **Logout user (Authenticated)**
    *   **Description:** Invalidates the user's current session or authentication token.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Response Codes:**
        *   `200 OK`: User logged out successfully.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `500 Internal Server Error`: Server-side error.

*   **`GET /api/users/me`**: **Get current user's profile (Authenticated)**
    *   **Description:** Retrieves the profile information for the currently logged-in user.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Response Codes:**
        *   `200 OK`: User profile details in JSON format.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `500 Internal Server Error`: Server-side error.
    *   **Response Body Example:**
        ```json
        {
          "userId": 1,
          "username": "existingUsername",
          "email": "[email address removed]",
          "createdAt": "2024-01-01T10:00:00Z",
          "updatedAt": "2024-01-01T10:00:00Z"
        }
        ```

*   **`PUT /api/users/me`**: **Update current user's profile (Authenticated)**
    *   **Description:** Updates the profile information for the currently logged-in user.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Request Body:** `application/json` (Include fields you want to update, e.g., email)
        ```json
        {
          "email": "[email address removed]"
        }
        ```
    *   **Response Codes:**
        *   `200 OK`: User profile updated successfully. Response body contains updated user profile.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `400 Bad Request`: Invalid input data.
        *   `500 Internal Server Error`: Server-side error.

### 2. Category Endpoints (`/api/categories`)

*   **`GET /api/categories`**: **Get all categories (Authenticated)**
    *   **Description:** Retrieves all categories belonging to the logged-in user.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Response Codes:**
        *   `200 OK`: List of categories in JSON format.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `500 Internal Server Error`: Server-side error.
    *   **Response Body Example:**
        ```json
        [
          {
            "categoryId": 1,
            "name": "Food",
            "type": "EXPENSE",
            "createdAt": "2024-01-01T10:00:00Z",
            "updatedAt": "2024-01-01T10:00:00Z"
          },
          {
            "categoryId": 2,
            "name": "Salary",
            "type": "INCOME",
            "createdAt": "2024-01-01T10:00:00Z",
            "updatedAt": "2024-01-01T10:00:00Z"
          }
        ]
        ```

*   **`POST /api/categories`**: **Create a new category (Authenticated)**
    *   **Description:** Creates a new income or expense category for the logged-in user.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Request Body:** `application/json`
        ```json
        {
          "name": "Entertainment",
          "type": "EXPENSE"
        }
        ```
    *   **Response Codes:**
        *   `201 Created`: Category created successfully. Response body contains the created category details.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `400 Bad Request`: Invalid input data (e.g., missing fields, invalid type).
        *   `500 Internal Server Error`: Server-side error.

*   **`GET /api/categories/{categoryId}`**: **Get category by ID (Authenticated)**
    *   **Description:** Retrieves a specific category by its ID.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Path Parameter:** `categoryId` (integer, ID of the category).
    *   **Response Codes:**
        *   `200 OK`: Category details in JSON format.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `404 Not Found`: Category with the given ID not found.
        *   `500 Internal Server Error`: Server-side error.

*   **`PUT /api/categories/{categoryId}`**: **Update category (Authenticated)**
    *   **Description:** Updates an existing category.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Path Parameter:** `categoryId` (integer, ID of the category to update).
    *   **Request Body:** `application/json` (Include fields to update, e.g., name, type)
        ```json
        {
          "name": "Movies & Fun",
          "type": "EXPENSE"
        }
        ```
    *   **Response Codes:**
        *   `200 OK`: Category updated successfully. Response body contains the updated category details.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `400 Bad Request`: Invalid input data.
        *   `404 Not Found`: Category with the given ID not found.
        *   `500 Internal Server Error`: Server-side error.

*   **`DELETE /api/categories/{categoryId}`**: **Delete category (Authenticated)**
    *   **Description:** Deletes a category.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Path Parameter:** `categoryId` (integer, ID of the category to delete).
    *   **Response Codes:**
        *   `204 No Content`: Category deleted successfully.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `404 Not Found`: Category with the given ID not found.
        *   `500 Internal Server Error`: Server-side error.

### 3. Transaction Endpoints (`/api/transactions`)

*   **`GET /api/transactions`**: **Get all transactions (Authenticated, with optional filters)**
    *   **Description:** Retrieves all transactions for the logged-in user, with optional filtering by date range, category, and transaction type.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Query Parameters (optional):**
        *   `startDate`: Date string (YYYY-MM-DD) - Filter transactions starting from this date.
        *   `endDate`: Date string (YYYY-MM-DD) - Filter transactions up to this date.
        *   `categoryId`: Integer - Filter transactions by category ID.
        *   `transactionType`: String (`INCOME` or `EXPENSE`) - Filter by transaction type.
    *   **Response Codes:**
        *   `200 OK`: List of transactions in JSON format.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `500 Internal Server Error`: Server-side error.
    *   **Example Request (with filters):** `/api/transactions?startDate=2023-01-01&endDate=2023-01-31&categoryId=1&transactionType=EXPENSE`

*   **`POST /api/transactions`**: **Create a new transaction (Authenticated)**
    *   **Description:** Creates a new income or expense transaction for the logged-in user.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Request Body:** `application/json`
        ```json
        {
          "date": "2024-02-15",
          "amount": 50.00,
          "categoryId": 1,
          "description": "Lunch with colleagues",
          "transactionType": "EXPENSE"
        }
        ```
    *   **Response Codes:**
        *   `201 Created`: Transaction created successfully. Response body contains the created transaction details.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `400 Bad Request`: Invalid input data (e.g., missing fields, invalid date format, category ID does not exist).
        *   `500 Internal Server Error`: Server-side error.

*   **`GET /api/transactions/{transactionId}`**: **Get transaction by ID (Authenticated)**
    *   **Description:** Retrieves a specific transaction by its ID.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Path Parameter:** `transactionId` (integer, ID of the transaction).
    *   **Response Codes:**
        *   `200 OK`: Transaction details in JSON format.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `404 Not Found`: Transaction with the given ID not found.
        *   `500 Internal Server Error`: Server-side error.

*   **`PUT /api/transactions/{transactionId}`**: **Update transaction (Authenticated)**
    *   **Description:** Updates an existing transaction.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Path Parameter:** `transactionId` (integer, ID of the transaction to update).
    *   **Request Body:** `application/json` (Include fields to update, e.g., amount, description)
        ```json
        {
          "amount": 55.00,
          "description": "Lunch with colleagues - updated amount"
        }
        ```
    *   **Response Codes:**
        *   `200 OK`: Transaction updated successfully. Response body contains the updated transaction details.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `400 Bad Request`: Invalid input data.
        *   `404 Not Found`: Transaction with the given ID not found.
        *   `500 Internal Server Error`: Server-side error.

*   **`DELETE /api/transactions/{transactionId}`**: **Delete transaction (Authenticated)**
    *   **Description:** Deletes a transaction.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Path Parameter:** `transactionId` (integer, ID of the transaction to delete).
    *   **Response Codes:**
        *   `204 No Content`: Transaction deleted successfully.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `404 Not Found`: Transaction with the given ID not found.
        *   `500 Internal Server Error`: Server-side error.

### 4. Reporting Endpoints (`/api/reports`)

*   **`GET /api/reports/balance`**: **Get balance report (Authenticated)**
    *   **Description:** Calculates and returns the balance (total income - total expenses) for a specified date range.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Query Parameters (required):**
        *   `startDate`: Date string (YYYY-MM-DD) - Start date for the report.
        *   `endDate`: Date string (YYYY-MM-DD) - End date for the report.
    *   **Response Codes:**
        *   `200 OK`: Balance report in JSON format.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `400 Bad Request`: Missing or invalid date parameters.
        *   `500 Internal Server Error`: Server-side error.
    *   **Response Body Example:**
        ```json
        {
          "balance": 1500.50
        }
        ```

*   **`GET /api/reports/summary`**: **Get income and expenses summary (Authenticated)**
    *   **Description:** Returns a summary of total income and total expenses for a specified date range.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Query Parameters (required):**
        *   `startDate`: Date string (YYYY-MM-DD) - Start date for the report.
        *   `endDate`: Date string (YYYY-MM-DD) - End date for the report.
    *   **Response Codes:**
        *   `200 OK`: Summary report in JSON format.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `400 Bad Request`: Missing or invalid date parameters.
        *   `500 Internal Server Error`: Server-side error.
    *   **Response Body Example:**
        ```json
        {
          "totalIncome": 3500.00,
          "totalExpenses": 2000.50
        }
        ```

*   **`GET /api/reports/category-spending`**: **Get category spending summary (Authenticated)**
    *   **Description:** Returns a summary of spending per category for a specified date range.
    *   **Authentication:** Bearer Token (JWT) required in `Authorization` header.
    *   **Query Parameters (required):**
        *   `startDate`: Date string (YYYY-MM-DD) - Start date for the report.
        *   `endDate`: Date string (YYYY-MM-DD) - End date for the report.
    *   **Response Codes:**
        *   `200 OK`: Category spending report in JSON format.
        *   `401 Unauthorized`: Authentication required or invalid token.
        *   `400 Bad Request`: Missing or invalid date parameters.
        *   `500 Internal Server Error`: Server-side error.
    *   **Response Body Example:**
        ```json
        [
          {
            "categoryName": "Food",
            "totalSpent": 850.00
          },
          {
            "categoryName": "Entertainment",
            "totalSpent": 320.00
          }
        ]
        ```

## Future Enhancements

*   Implement user authentication and authorization using Spring Security and JWT for enhanced security.
*   Add budget setting features per category, allowing users to set budget limits and track against actual spending.
*   Implement more advanced reporting and data visualization, such as charts and graphs.
*   Add data validation on API requests for better error handling and user feedback.
*   Implement comprehensive unit and integration tests to ensure API reliability and robustness.
*   Consider adding features like recurring transactions and financial goal setting.
*   Explore adding support for different currencies.

## Author

[Your Name] - [Your Email/LinkedIn Profile (Optional)]