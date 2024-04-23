
# Search Engine with MongoDB Integration


**Description:**
This project is a search engine developed using Spring MVC, Thymeleaf, and MongoDB. It enables users to search for specific words within a dataset stored in MongoDB, as well as perform additional operations such as character count analysis and identifying the top/bottom N most occurring words.

**Features:**
- **Data Extraction:** Files are downloaded from a website and stored within the project's codebase.
- **Data Migration:** Upon running the program, the data from files is moved to MongoDB for efficient storage and retrieval.
- **Search Functionality:** Users can search for specific words within the dataset stored in MongoDB.
- **Character Count:** The application provides functionality to calculate the character count for a given word or phrase.
- **Word Frequency Analysis:** Users can find the top/bottom N most occurring words within the dataset.

**Technologies Used:**
- Spring MVC: Framework for building web applications using the Model-View-Controller architecture.
- Thymeleaf: Template engine for server-side Java applications, facilitating dynamic content generation.
- MongoDB: NoSQL database for storing and managing unstructured data efficiently.

**Installation Instructions:**
1. Clone the repository to your local machine.
2. Ensure that MongoDB is installed and running on your system.
3. Import the project into your preferred IDE.
4. Run the application to populate MongoDB with data and start the server.
5. Access the search engine interface through a web browser.(http://localhost:8080/a5/search)

**Changes to make in Code for running it:**
https://www.mongodb.com/products/platform/cloud

go to the above url and signup and host your MongoDB and update it in spring application, in application.properties
```
spring.data.mongodb.uri=mongodb+srv://root:root@cluster0.crkygpz.mongodb.net/test
```

**Usage:**
1. Enter a word or phrase into the search bar to find occurrences within the dataset.
2. View the character count for a specific word or phrase.
3. Explore the top/bottom N most occurring words in the dataset.


**Repository Title:**
spring-mongodb-search-engine

Contributing:
Contributions to the project are welcome! Feel free to submit bug reports, feature requests, or pull requests to help improve the search engine.
