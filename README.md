# Bookstore App

This web application, built using Spring Boot, offers role-based authorization, authentication, 
registration and supports four basic CRUD operations for efficient interaction with a relational database. 
In the capacity of an administrator, you can access the following features:

 - Create book / category
 - Update book / category / order by id
 - Delete book / category by id

The currently logged-in user has the capability to:

 - Retrieve all books / books by category id/ categories / cart items / orders / order items by order id
 - Get book / item from specific order / category  by id
 - Add / remove cart item from shopping cart
 - Update quantity of cart item by id
 - Create order
    
# Structure

The project is structured with three-tier architecture:

1. The presentation tier (controllers)
2. The application logic tier (services)
3. The data tier (repositories)

# Technologies

 - Spring Boot 
 - Spring Security 
 - Spring Data JPA
 - Spring Web
 - Hibernate
 - JWT
 - Docker
 - Apache Maven 
 - MySQL 
 - Liquibase 
 - Lombok
 - Swagger
 
# Launch

1. Clone or download repository
2. Install and run the Docker engine 
3. Add your database configuration to the corresponding fields in .env file 
4. You can inject custom data in init/DataInitializer class
5. Build the project using 'mvn clean package' command
6. Build the Docker images using 'docker-compose build' command
7. Run the Docker container using 'docker-compose up' command
