# Railway_Management_System
In this project which i finished 2 years ago during the fall semester in university for Database class, i implemented the entities and endpoints for a server back-end Java application initialized with the Spring framework.

My aim was to design a Railway Management System. This system contains Stops, Routes, Trains and Schedules. Each of these components have unique value, purpose, dependencies and description:
1) Stops:
 
a) Each stop must have a unique name

2) Routes

a) Each route must have a unique name

b) Each route can contain arbitrary amounts of stops

c) The order of the stops is not important

3) Trains

a) Each train must have a unique name

4) Schedules

a) Schedules must contain a route, a main train and time. Ex: (Route: DT2, Train: Barbaros Train, Time: 13:30)

b) Schedules can also have an arbitrary amount of backup trains if something happens to the main train



Important Prerequisites: Docker ( https://www.docker.com/get-started ) and JDK 14 is needed for this project.

Here is a list of API endpoints that i implemented: 

Input - fields that will be sent to the endpoint. 

Output - expected output of the endpoint.

P.S in Report.pdf file which i uploaded i describe the sql tables i used and designed and decisions i made during creation of the database. Check it our to fully understand it. 

![11](https://user-images.githubusercontent.com/98253476/180263422-dd9fe07d-c2e5-43d1-aa65-ca3b3ec5d59d.jpg)
![12](https://user-images.githubusercontent.com/98253476/180263479-e39e12cd-5c07-42fc-982b-b42821c0c5d4.jpg)
![13](https://user-images.githubusercontent.com/98253476/180263492-d8cd917f-7525-45dd-989f-88cd878c59e3.jpg)
![14](https://user-images.githubusercontent.com/98253476/180263510-f0d878ef-ab76-4db2-9f47-3e58694bf34e.jpg)
