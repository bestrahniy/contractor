# Contractor Project

The contractor service is a microservice for managing counterparties within a fintech application. It is implemented using Spring Boot without ORM, relying on Spring Data (CrudRepository) and JdbcTemplate, and handles core entities such as contractors, countries, industries, and organisational forms. The service supports creation, updating, deletion, data filtering via REST API, and paginated output using standard SQL-based pagination.


to run:  
- mvn -B clean package -DskipTests &&  docker-compose down -v --remove-orphans && docker-compose build --no-cache app && docker-compose up

to test ptoject you need open test file and run tests 
