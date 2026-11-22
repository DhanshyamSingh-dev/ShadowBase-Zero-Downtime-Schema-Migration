# ShadowBase: Zero-Downtime Schema Migration Sandbox

## 1. Project Overview
ShadowBase is a Java-based database schema migration testing sandbox designed to test database schema changes in an isolated environment before applying them to production databases.

## 2. Project Objective
The objective is to build a safe and controlled environment for analyzing, validating, and testing database schema changes.

## 3. Technology Stack
- Java
- Spring Boot
- MySQL
- Docker
- Maven
- Git and GitHub
- Postman
- HTML, CSS and JavaScript

## 4. Implemented Features – Week 1
- Spring Boot backend setup
- Health Check REST API
- MySQL database connectivity
- Database schema reader
- Schema Reader REST API
- Schema comparison service
- Migration plan generation
- Migration plan validation
- Docker-based MySQL testing environment
- Docker profile configuration
- Git ignore configuration for local database credentials

## 5. API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Check application health |
| GET | `/api/schema/tables` | Read database tables |
| POST | `/api/schema/compare` | Compare schemas and generate a migration plan |

## 6. Current Project Status
Week 1 development tasks have been implemented and tested, including Docker MySQL integration and schema comparison API testing.

The migration planner currently generates SQL statements but does not execute migrations against the database.

## 7. Planned Features
- Advanced schema comparison
- SQL migration execution
- Migration history tracking
- Error detection and reporting
- Migration rollback strategy
- Frontend and backend integration
- Migration testing and validation improvements

## 8. Development Goals
To develop a reliable database migration testing platform that demonstrates safe schema evolution and zero-downtime migration concepts.

## 9. Project Information
Java Development Internship Project – Infotact Solutions.
## 10. Week 1 Test Results

### Test Case 1: Multiple Tables Testing
- Database: shadowbase_test
- Tables: docker_test, products, test_users
- Expected Result: API should return all tables with their column details.
- Actual Result: All three tables and their column details were returned successfully.
- Status: PASS

### Test Case 2: Empty Database Testing
- Database: shadowbase_empty_test
- Expected Result: API should return an empty JSON array.
- Actual Result: API returned an empty JSON array with HTTP 200 OK.
- Status: PASS

### Test Case 3: Invalid Schema Request
- Endpoint: POST /api/schema/compare
- Request Body: {}
- Expected Result: HTTP 500 Internal Server Error.
- Actual Result: API returned HTTP 500 Internal Server Error.
- Status: PASS

### Test Case 4: Original Database Verification
- Database: shadowbase_test
- Expected Result: API should read all three original tables after restoring the database configuration.
- Actual Result: All three tables were returned successfully.
- Status: PASS

