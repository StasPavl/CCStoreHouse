# CC StoreHouse

A character storage and management web application built with Spring Boot that helps writers organize, track, and find their fictional characters.

## Features

- Create and manage character profiles
- Store detailed character information
- Powerful search functionality
- Secure user authentication
- Responsive design for all devices
- Premium subscription options

## Technology Stack

- **Frontend**: HTML5, CSS3, Bootstrap 5, Thymeleaf, Vanilla JavaScript
- **Backend**: Java, Spring Boot 3
- **Database**: PostgreSQL
- **Security**: Spring Security
- **Payment Processing**: Stripe

## Prerequisites

- Java 17+
- Maven or Gradle
- PostgreSQL
- (Optional) Stripe account for payment processing

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/StasPavl/CCStoreHouse.git
   cd CCStoreHouse
   ```

2. Configure the database in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ccstorehouse
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. Set up Stripe keys (for premium features):
   ```properties
   stripe.api.key=your_stripe_api_key
   stripe.webhook.secret=your_stripe_webhook_secret
   ```

4. Build the project:
   ```bash
   mvn clean install
   ```

5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

6. Access the application at `http://localhost:8080`

## Development

- Use Spring DevTools for hot reloading
- Implement tests using JUnit and Spring Test
- Follow the existing code style and architectural patterns

## License

This project is licensed under the MIT License - see the LICENSE file for details. 