# Bookkeeper

A financial management web application built with Spring Boot that helps users track their finances, manage budgets, and gain insights into their spending habits.

## Features

- Track income and expenses
- Set and manage budgets
- Secure user authentication
- Responsive design for all devices
- Premium subscription options
- Financial insights and reports

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
   git clone https://github.com/yourusername/bookkeeper.git
   cd bookkeeper
   ```

2. Configure the database in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/bookkeeper
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
   ./gradlew build
   ```

5. Run the application:
   ```bash
   ./gradlew bootRun
   ```

6. Access the application at `http://localhost:8080`

## Development

- Use Spring DevTools for hot reloading
- Implement tests using JUnit and Spring Test
- Follow the existing code style and architectural patterns

## License

This project is licensed under the MIT License - see the LICENSE file for details. 