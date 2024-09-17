# An app created with a microsservices archtechture using Kafka
## An app with multiple services that run separately but work together as a group. 

Project created along a extra curricular course create by Alura in partnership with software enginners from Nubank.

It's a food app created using a microsservice archtecture, with the goal of having better scalibity and growth possibilities within each service.


Each service has its own function within the app, having asynchronous communication where needed allows for rapid responses for the users.


Some messages are only sent to certain services after they have been verified by another. Creating a safer environment that way

## Deploy

1. Download the project
2. Change environmental variables as deem necessary in applications.properties of each service
3. Be sure to have Zookeeper and Kafka running so that the services may connect to the server

## Function

* Receive messages to the corresponding services
* Analyse for frauds(when the amount is >= 4500) when a user has ordered a order
* Log service that logs all of the messages
* A way to trace back the path of each message, from what services it went to with a unique ID
* Create a new user whenever a new email is sent
* Common libraries that are used across the services for database and kafka
* Ability to run multiple instances of any giving service with a simple command
