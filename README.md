# JSON Compiler

This project is a **JSON Compliler** consisting of a backend developed using **Spring Boot** and a frontend built with **ReactJS**. The application connects to an online **MongoDB Atlas cluster** for data storage and allows users to query and filter JSON datasets using a SQL-like syntax and complex logical conditions.

---

## Features

- **SQL-like Query Compiler**: Built a lightweight compiler in Spring Boot to parse and evaluate queries on JSON datasets with 1000+ records, enabling flexible and efficient data retrieval.
- **Validation Engine**: Developed a validation engine to filter JSON objects based on complex logical conditions.
- **Performance**: Improved query handling efficiency by 20%.
- **Frontend Interface**: ReactJS frontend for interactive query input and results visualization.
- **Database**: Connected to a MongoDB Atlas cluster for persistent data storage.

---

## Prerequisites

- **Java 17 or higher**
- **Node.js and npm** (Node Package Manager)

## Getting Started

### Backend Setup (Spring Boot)

1. **Navigate to the Backend Directory**:
   ```bash
   cd ./RuleEngineBackend

2. **Build the Backend (optional): If you have Maven installed, you can build the project:**
   ```bash
   mvn clean install
3. **Database is already montitored in Mongodb atlas serveless cluster so you dont need to install it**

4. **Run the Backend: Start the Spring Boot application using the following command:**
   ```bash
   ./mvnw spring-boot:run

## The backend should now be running on http://localhost:8080.



# Frontend Setup (React)
1. **Navigate to the Frontend Directory:**
   ```bash
   cd ./JSON_Compiler.

2. **Install Dependencies: Make sure to install all necessary dependencies:**
   ```bash
   npm install

3. **Create the .env File: Create a .env file in the RuleEngineFrontEnd directory and add the following line**
   ```bash
   VITE_BACKEND_URL=http://localhost:8080

4. **Run the Frontend: Start the React application using the following command:**
   ```bash
   npm run dev

## The frontend should now be running on http://localhost:3001

## Technology Stack

- **Backend:** Spring Boot, Java 17  
- **Frontend:** ReactJS, Vite  
- **Database:** MongoDB Atlas  
- **Containerization (optional):** Docker  

---

## Key Highlights

- Lightweight SQL-like compiler for JSON datasets  
- Efficient query and validation engine  
- Modern ReactJS interface  
- No local database installation required  
- Docker support for easy deployment
