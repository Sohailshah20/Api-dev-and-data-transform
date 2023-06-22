## Requirements
This application requires Java 17 to run

## Steps to run the application
1. Clone the project on your computer and run it
2. Open postman and first upload the csv file using http://localhost:8080/api/upload
3. Now you can access all the other end points

## Database 
This project uses H2 in memory database.
- After running the applicaiton visit http://localhost:8080/h2-console to access the database
- Make sure the JDBC url is set to jdbc:h2:mem:testdb
- Click connect to see the database

