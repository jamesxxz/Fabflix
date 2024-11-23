- # General
    - #### Team#:
    - CPDD

    - #### Names:
    - Sijie Guo, James Liu

    - #### Project 4 Video Demo Link:

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution:
    - James implemented Task1,Task3,Task4,readme
    - Sijie Guo implemented Task2,Task4,readme


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
        - Fabflix/src/AddStarAndMovieServlet.java
        - Fabflix/src/ConfirmationServlet.java
        - Fabflix/src/DashboardServlet.java
        - Fabflix/src/EmployeeLoginServlet.java
        - Fabflix/src/IndexServlet.java
        - Fabflix/src/LoginServlet.java
        - Fabflix/src/MoviesServlet.java
        - Fabflix/src/PaymentServlet.java
        - Fabflix/src/SingleMovieServlet.java
        - Fabflix/src/SingleStarServlet.java
        - Fabflix/src/MovieSuggestion.java

    - #### Explain how Connection Pooling is utilized in the Fabflix code.
      Connection Pooling is implemented using Tomcat's JDBC Connection Pooling feature.
      Instead of creating a new database connection for each request, connections are retrieved from a pre-configured pool, improving performance and resource utilization.
      Take AddStarAndMovieServlet as an example. The servlet uses a DataSource object to manage connections:dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
      Each request retrieves a connection from the pool:
      try (Connection conn = dataSource.getConnection()) {
      }

      This approach ensures efficient connection reuse and minimizes the overhead of creating and destroying connections.

      Apart from the servlet files, the context.xml is also revised.
      In the context.xml File:

      The JDBC connection pool is configured with:
      maxTotal=100: Limits the maximum number of active connections in the pool.
      maxIdle=30: Restricts the number of idle connections in the pool.
      maxWaitMillis=10000: Sets the maximum wait time (in milliseconds) for a connection to become available.
      cachePrepStmts=true: Enables prepared statement caching, which improves performance in a pooled environment.


- #### Explain how Connection Pooling works with two backend SQL.
  Connection Pooling in a system with two backend SQL databases involves managing connections to both a Master database and a Slave database.
  The Master database is used for write operations, and the Slave database is used for read operations. This setup optimizes performance and scalability by dividing the workload between the two databases.
  The context.xml file is configured with separate data sources for the Master and Slave databases. Each data source is defined with its own connection pool.
  The Master DataSource handles all the write operations such as INSERT, UPDATE, DELETE, and so on. The Slave DataSource handles all the read operations such as SELECT.

  In our Fabflix code, servlets dynamically retrieve connections from the appropriate pool based on the type of database operation.
  For write-intensive operations, such as adding a new movie or updating user information, a connection is typically retrieved from the jdbc/moviedb_master pool, as shown in the init method of one of my servlets AddStarAndMovieServlet: dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/masterdb");
  For read-intensive operations, such as fetching movie details or handling user queries, a connection is retrieved from the jdbc/slavedb pool, as shown in the init() method of my servlet:dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/slavedb");

  After this, code logic determines the appropriate data source based on the operation type(INSERT, SELECT, and so on)

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
    - MovieSuggestion

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
      Configuration file
        - Fabflix/WebContent/META-INF/context.xml
        - Fabflix/WebContent/WEB-INF/web.xml

      Servlets utilize read-only datasource:
        - Fabflix/src/ConfirmationServlet.java
        - Fabflix/src/DashboardServlet.java
        - Fabflix/src/EmployeeLoginServlet.java
        - Fabflix/src/IndexServlet.java
        - Fabflix/src/LoginServlet.java
        - Fabflix/src/MoviesServlet.java
        - Fabflix/src/SingleMovieServlet.java
        - Fabflix/src/SingleStarServlet.java
        - Fabflix/src/MovieSuggestion.java

      Servlets utilize read/write datasource:
        - Fabflix/src/PaymentServlet.java
        - Fabflix/src/AddStarAndMovieServlet.java


    - #### How read/write requests were routed to Master/Slave SQL?



-----------------------------------The following are contents for Project5------------------------------------------------------
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
