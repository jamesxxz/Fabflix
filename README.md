- # General
    - #### Team#: 
    - CPDD

    - #### Names: 
    - Sijie Guo, James Liu

    - #### Project 4 Video Demo Link:

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution:
    - James implemented Task1,
    - Sijie Guo implemented Task2,


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
      - /Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/src/AddStarAndMovieServlet.java
      - /Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/src/ConfirmationServlet.java
      - /Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/src/DashboardServlet.java
      - /Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/src/EmployeeLoginServlet.java
      - /Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/src/IndexServlet.java
      - /Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/src/LoginServlet.java
      - /Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/src/MoviesServlet.java
      - /Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/src/PaymentServlet.java
      - /Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/src/SingleMovieServlet.java
      - /Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/src/SingleStarServlet.java
      
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
      Connection Pooling is implemented using Tomcat's JDBC Connection Pooling feature. Instead of creating a new database connection for each request, connections are retrieved from a pre-configured pool, improving performance and resource utilization.
    - #### Explain how Connection Pooling works with two backend SQL.

- # Files with Prepared Statement:
- AddStarAndMovieServlet
- ConfirmationServlet
- DashboardServlet
- EmployeeLoginServlet
- IndexServlet
- Inserter
- LoginServlet
- MoviesServlet
- PaymentServlet
- SingleMovieServlet
- SingleStarServlet

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?


- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |