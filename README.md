# Spring Boot Project

This is a Spring Boot project that requires a `secrets.properties` file to be placed inside the `src/main/resources` directory. This file contains essential configuration properties for the application to run.

## Project Setup

### Prerequisites

1. Java 11 or later
2. Maven 3.6 or later
3. MySQL Server
4. Node.js and npm (for frontend development)

### Database Configuration

Ensure you have a MySQL database set up and running. The project uses the following default configurations for the database:

- URL: `jdbc:mysql://localhost:3306/devinsight_dev`
- Username: `devinsight_dev`
- Password: `devinsight_dev`

### Creating `secrets.properties` File

Create a file named `secrets.properties` in the `src/main/resources` directory with the following content:

JDBC_DATABASE_URL=jdbc:mysql://localhost:3306/devinsight_dev
JDBC_DATABASE_USERNAME=devinsight_dev
JDBC_DATABASE_PASSWORD=devinsight_dev
JDBC_DATABASE_DRIVER=com.mysql.cj.jdbc.Driver
JDBC_DATABASE_DIALECT=org.hibernate.dialect.MySQLDialect
JPA_HIBERNATE_DDL=update
EMAIL_SMTP_HOST=smtp.gmail.com
EMAIL_SMTP_PORT=587
EMAIL_USERNAME=devinsightapp@gmail.com
EMAIL_PASSWORD=<EMAIL PASSWORD GOES HERE>
SPRING_BASE_PATH=/dev/api/v1
JWT_ACCESS_KEY=<JWT_ACCESS_KEY_GOES_HERE>
JWT_REFRESH_KEY=<JWT_REFRESH_KEY_GOES_HERE>
JWT_ACCESS_VALID_MINUTES=1080
JWT_REFRESH_VALID_MINUTES=14400
FRONTEND_URL=<FRONTEND_URL_GOES_HERE>
GITHUB_TOKEN=<GITHUB_TOKEN_GOES_HERE>

Replace `<GITHUB_TOKEN_GOES_HERE>` with your actual GitHub token.

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/tolgaozgun/DevinsightBackend.git
   cd DevinsightBackend
    ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
    mvn spring-boot:run
    ```

The application will be accessible at `http://localhost:8080/dev/api/v1`.

# Frontend Development
If you run the frontend application, ensure it is running on http://localhost:3000 as specified in the secrets.properties file.

# Environment Variables
For security purposes, do not hardcode sensitive information directly into the codebase. Use environment variables or configuration management tools to manage your application's secrets.

# Additional Notes
Ensure your SMTP email settings are correctly configured for sending emails.
Update the JWT access and refresh keys periodically for security purposes.

# License
This project is licensed under the MIT License - see the LICENSE.md file for details.

# Contact
For any questions or support, please contact devinsightapp@gmail.com.
