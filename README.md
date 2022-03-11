# Task Tracker Application (EY coding assessment)

## Description

This application has been built to support keeping of tasks (to-do notes) for users. 
The application does not include a user interface. Instead, the app can be accessed using the swagger page on the app is started up.
The swagger page will be available at http://localhost:8080/swagger-ui.html

The following features are supported by the app.
1. Create a new user  (using HTTP POST endpoint /user/{userId})
2. Fetch details of an existing user  (using HTTP GET endpoint /user)
3. Create a task associated with any of the existing users  (using HTTP POST endpoint /task)
4. Retrieve the tasks associated with a given user  (using HTTP GET endpoint /task)
5. Update an existing task  (using HTTP PUT endpoint /task)
6. Delete an existing task  (using HTTP DELETE endpoint /task)


## Build and execution

The application can be built using maven. To build, run the following command:
mvn clean package

The application can be executed using the following command:
java -jar target/task-tracker-0.0.1-SNAPSHOT.jar

The app spins up an in-memory database on startup. The database can be accessed on the console at http://localhost:8080/h2-console
Connection details:
Connection url - jdbc:h2:mem:mydb
Username - tasktrackeradmin
Password - amdin

## Design decisions

1. All the data processing logic has been implemented using Spring data JPA. Having these implementations in Java code makes the application agnostic to the underlying data storage. As a result, it will be possible to shift the underlying data store without much impact to the developed and tested app functionality
2. The validation performed in the service layer prior have been implemented using simple methods. This can be enhanced to use the Strategy and Chain of Responsibility design patterns for better code maintainability in case more complex validations are needed. (The design pattern was not implemented for want of time)
3. DB credentials have been hardcoded in the application.yml file. This will not generally be practiced in real world applications. In real-world applications, these credentials would ideally be fetched from external password vaults (or relevant mechanisms)