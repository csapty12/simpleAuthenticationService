# Simple Authentication Service

### Purpose
This is a simple authentication service to enable a user to register and login to an application. 

### How to Run Locally
to run the application locally, start the docker containers:

```docker-compose up```

This will start up a docker container for mysql with `port 3306` exposed to the client. Once it has started, a `users` database is created, with a `roles` table. 

The first time you run this application, there will be no user roles in the database, so run these two commands in the `users` database:

```
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```

This will create two roles in the table which are persisted in docker as volumes are enabled. 

It makes use of a MySQL database to store the user details. 



