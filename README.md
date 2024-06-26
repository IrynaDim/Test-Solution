# Overview
This project implements a RESTful API for managing user resources based on a Spring Boot web application. The API adheres to best practices outlined in RESTful API design principles and includes error handling, unit testing with Spring, and JSON formatted responses.

## Features Implemented
1. User Resource Fields:
- Email (required, validated against email pattern)
- First name (required)
- Last name (required)
- Birth date (required, must be earlier than the current date)
- Address (optional)
- Phone number (optional, validated for non-null and non-empty input)
2. Functionality:
- Create user: Allows registration of users aged over 18 (age threshold configurable via properties file)
- Update user fields: Supports updating one/some or all user fields
- Delete user
- Search users by birth date range: Validates that the "From" date is less than the "To" date and returns a list of matching users
3. Code Organization:
- Utilizes a repository layer for simplified interaction and testing
- Includes unit tests for comprehensive code coverage using Spring and Mockito. The tests were written considering
a balance between time spent and coverage achieved. While it's possible to write more tests, for example, for mappers and other components, the aim was to avoid over-inflating the project as it is primarily intended for testing purposes.
- Implements logging via aspects for enhanced traceability and debugging. A simple logging strategy was chosen to 
demonstrate proficiency in working with aspects. While more advanced logging strategies could be implemented, the focus was on illustrating fundamental logging principles.
- Codebase is well-commented for clarity and understanding
4. Additional Notes:
- Security measures were not implemented as they were not specified in the requirements
- Email and birth date validation are implemented as per the task description; phone number validation is limited to non-null and non-empty input due to absence of specific requirements
- Utilizes Swagger for documentation. Available by http://localhost:8080/swagger-ui/index.html url
- Efforts were made to maximize code coverage with tests, although additional scenarios could be explored

## Data Population
As the database layer was not mandatory for this project, Flyway or Liquibase were not utilized. However, if needed, 
the following SQL queries can be used to populate the database with sample data:
```sql
INSERT INTO users (first_name, last_name, email, birth_date, address, phone_number)
VALUES ('User1', 'Lastname1', 'user1@example.com', '2000-01-01', 'address 1', '1111111111');
INSERT INTO users (first_name, last_name, email, birth_date, address, phone_number)
VALUES ('User2', 'Lastname2', 'user2@example.com', '2001-02-02', 'address 2', '2222222222');
INSERT INTO users (first_name, last_name, email, birth_date, address, phone_number)
VALUES ('User3', 'Lastname3', 'user3@example.com', '2002-03-03', 'address 3', '3333333333');
INSERT INTO users (first_name, last_name, email, birth_date, address, phone_number)
VALUES ('User4', 'Lastname4', 'user4@example.com', '2003-04-04', 'address 4', '4444444444');
INSERT INTO users (first_name, last_name, email, birth_date, address, phone_number)
VALUES ('User5', 'Lastname5', 'user5@example.com', '2004-05-05', 'address 5', '5555555555');
INSERT INTO users (first_name, last_name, email, birth_date, address, phone_number)
VALUES ('User6', 'Lastname6', 'user6@example.com', '2005-06-06', 'address 6', '6666666666');
INSERT INTO users (first_name, last_name, email, birth_date, address, phone_number)
VALUES ('User7', 'Lastname7', 'user7@example.com', '2006-07-07', 'address 7', '7777777777');
INSERT INTO users (first_name, last_name, email, birth_date, address, phone_number)
VALUES ('User8', 'Lastname8', 'user8@example.com', '2007-08-08', 'address 8', '8888888888');
INSERT INTO users (first_name, last_name, email, birth_date, address, phone_number)
VALUES ('User9', 'Lastname9', 'user9@example.com', '2008-09-09', 'address 9', '9999999999');
INSERT INTO users (first_name, last_name, email, birth_date, address, phone_number)
VALUES ('User10', 'Lastname10', 'user10@example.com', '2009-10-10', 'address 10', '1010101010');
```
