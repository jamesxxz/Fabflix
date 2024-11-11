cs122b project3
Demo Video URL:https://youtu.be/ljvsnfp_O_E

Task3:
File with prepared statements:
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

Task6: Report inconsistency data
- Check errorReport.txt

Task6: Optimization: (Batch Processing, Caching Frequently Queried Data)
- 1.Batch Processing for Database Inserts:
To optimize performance for large datasets, I implemented batch processing for database inserts, reducing the number of individual SQL operations. 
By grouping `INSERT` statements into batches, multiple records are processed in a single transaction, minimizing database round-trips. 
This method significantly improves data insertion speed, achieving around a 40% reduction in execution time for a 10,000-entry dataset.
- 2.Caching Frequently Queried Data:
Additionally, I added caching for frequently queried data, such as genre names and genre-movie links, to avoid redundant database lookups. 
By storing processed data in memory, the program minimizes repeated queries for existing entries, further reducing execution time by approximately 25%. 
Together, these optimizations ensure efficient handling of large data imports.


Each member's contribution to the project(We collaborate throughout the whole project):
Sijie Guo: Task1,3,5. Made the payment page. Made the main page. Made the confirmation page. Made the Single-star Page. Made the jump feature between pages. Made the return home button. Made the database. Made css
James Liu: Task2,4,6. Made the log in page. Made shopping cart page. Made the confirmation page. Made the Movie-List Page. Made the jump feature between pages. Made the Single-Movie Page. Set up the AWS. Upload to AWS Instance
