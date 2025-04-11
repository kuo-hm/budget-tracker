# Budget Tracker API

## Overview

This is a RESTful API built using Spring Boot for a personal budget tracking application. It allows users to manage their finances by tracking income, expenses, and categories. Users can record transactions, categorize them, and generate reports to understand their spending habits.

This project is built as a demonstration of backend API development skills using Java and Spring Boot.

## Features

- **User Management:**
  - User registration and login
  - User profile management (view and update)
- **Category Management:**
  - Create, read, update, and delete categories (Income/Expense types)
- **Transaction Management:**
  - Create, read, update, and delete transactions (Income/Expense)
  - Filter transactions by date range, category, and type
- **Reporting:**
  - Balance calculation for a given period
  - Total income and expenses summary
  - Spending summary by category

## Technology Stack

- Java
- Spring Boot
- Spring Data JPA (for database interaction)
- Spring Web (for REST API)
- Spring Security (for authentication and authorization - _implementation recommended_)
- Database: MySQL (or any relational database supported by Spring Boot)
- Build Tool: Maven/Gradle
- API Documentation: Swagger/OpenAPI (SpringDoc)

## Author

Oussama Hmoura- <hmouraoussama@gmail.com>
