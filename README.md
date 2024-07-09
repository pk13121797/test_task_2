# TaskManagementSystem

Just simple task management system. The system allows you to manage tasks, employees 
and projects(1 project - many tasks, many tasks - many employees).
The entire code is covered by unit and integration tests.

Technologies:

1) Servlets
2) JDBC
3) Postgres
4) Junit & Mockito & AssertJ
5) Testcontainers
6) Log4j
7) Docker

Instructions to get the application up and running:

1) Make sure docker is installed and running.
2) Clone this repository.
3) Build the application war (Run mvn install or use IntelliJ IDEA to build the war file).
4) Check that port 5432 is free
5) Run docker-compose up. Give the application a few seconds to come up. 
6) Access the application at http://localhost:8080
