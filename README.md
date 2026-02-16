# Student Management System

A comprehensive web-based Student Management System built with Spring Boot, featuring role-based access control, course enrollment management, and a modern responsive UI 

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [Security](#security)
- [Getting Started](#getting-started)
- [Docker Deployment](#docker-deployment)
- [API Endpoints](#api-endpoints)
- [User Roles](#user-roles)
- [Screenshots](#screenshots)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Overview

The Student Management System is a full-stack web application designed to manage students, teachers, courses, and departments in an educational institution. It provides role-based access control with separate interfaces and permissions for students and teachers.

## Features

### Core Functionality

- **User Authentication & Authorization**
  - Secure login/logout with BCrypt password encryption
  - Role-based access control (Student/Teacher)
  - Session management with Spring Security

- **Department Management**
  - Create, read, update, and delete departments
  - View department statistics (teachers, students, courses)
  - Teacher-only access for modifications

- **Course Management**
  - Full CRUD operations for courses
  - Department-based course organization
  - View enrolled students
  - Teacher-controlled enrollment management

- **Student Management**
  - Student profile creation and editing
  - View enrolled courses
  - Self-enrollment in available courses
  - Drop courses functionality
  - Students can edit their own profiles

- **Teacher Management**
  - View teacher profiles and department assignments
  - Teachers can manage all student enrollments
  - Full administrative control over the system

### Course Enrollment System

- **For Students:**
  - View all available courses
  - Enroll in courses with one click
  - Drop enrolled courses
  - View course details and enrolled peers

- **For Teachers:**
  - Enroll students from student profile view
  - Enroll students from course view
  - Drop students from courses
  - View all enrolled students per course
  - Manage enrollments across the system

## Technology Stack

### Backend

- **Java 17** - Programming language
- **Spring Boot 3.x** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database ORM
- **Hibernate** - JPA implementation
- **PostgreSQL** - Relational database
- **Lombok** - Code generation
- **Maven** - Dependency management

### Frontend

- **Thymeleaf** - Server-side template engine
- **Tailwind CSS** - Utility-first CSS framework
- **Font Awesome** - Icon library
- **Inter Font** - Modern typography

### DevOps

- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Eclipse Temurin JDK** - Java runtime

## Architecture

### MVC Pattern

```
┌─────────────┐
│   Browser   │
└──────┬──────┘
       │
┌──────▼──────────────────────────────────────┐
│          Spring Security Filter             │
│     (Authentication & Authorization)        │
└──────┬──────────────────────────────────────┘
       │
┌──────▼──────────────────────────────────────┐
│            Controllers Layer                │
│  (Handle HTTP requests, return views)       │
└──────┬──────────────────────────────────────┘
       │
┌──────▼──────────────────────────────────────┐
│            Service Layer                    │
│  (Business logic, authorization checks)     │
└──────┬──────────────────────────────────────┘
       │
┌──────▼──────────────────────────────────────┐
│         Repository Layer                    │
│  (Database operations via JPA)              │
└──────┬──────────────────────────────────────┘
       │
┌──────▼──────────────────────────────────────┐
│         PostgreSQL Database                 │
└─────────────────────────────────────────────┘
```

### Request Flow Example

1. User accesses `/students/123`
2. Spring Security validates session and loads user details
3. `StudentController.viewStudent()` method invoked
4. Controller calls `StudentService.getStudentById(123)`
5. Service fetches data from `StudentRepository`
6. Entity converted to DTO and returned to controller
7. Controller adds DTO to Model
8. Thymeleaf renders `student/view.html` template
9. HTML response sent to browser

## Database Schema

### Entity Relationships

```
Department 1───────N Student
    │                  │
    │                  │
    │                  N
    │                  │
    │                  │
    │              Course
    │                  │
    └──────────────────N
    │
    └──────────────N Teacher
```

### Tables

1. **departments** - Stores department information
2. **students** - Student records with foreign key to departments
3. **teachers** - Teacher records with foreign key to departments
4. **courses** - Course records with foreign key to departments
5. **student_course** - Many-to-many join table for enrollments

### Key Relationships

- Department → Students (One-to-Many)
- Department → Teachers (One-to-Many)
- Department → Courses (One-to-Many)
- Students ↔ Courses (Many-to-Many)

## Security

### Authentication

- **Custom UserDetailsService** implementation
- Loads user from either Student or Teacher repository
- BCrypt password hashing (strength: 10)
- Session-based authentication with HTTP-only cookies

### Authorization Layers

1. **URL-Level Security** - SecurityFilterChain configuration
   ```java
   /login, /register → Public access
   /departments/new → Teachers only
   /courses/*/edit → Teachers only
   /students/*/edit → Authenticated (self-edit check in controller)
   ```

2. **Method-Level Security** - `@PreAuthorize` annotations
   ```java
   @PreAuthorize("hasRole('TEACHER')")
   public String createDepartment() { ... }
   ```

3. **Service-Level Checks** - Programmatic authorization
   ```java
   if (!isTeacher && !student.getId().equals(currentUserId)) {
       throw new UnauthorizedAccessException();
   }
   ```

4. **View-Level Security** - Thymeleaf security expressions
   ```html
   <div sec:authorize="hasRole('TEACHER')">
       <!-- Teacher-only content -->
   </div>
   ```

### CSRF Protection

- CSRF tokens automatically generated for all forms
- Token validation on all POST, PUT, DELETE requests
- Session invalidation on logout

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 16+ (or use Docker)
- Docker & Docker Compose (optional, for containerized deployment)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/student-management-system.git
   cd student-management-system
   ```

2. **Configure Database**

   Create PostgreSQL database:
   ```sql
   CREATE DATABASE studentmanagementsystem;
   ```

   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/studentmanagementsystem
   spring.datasource.username=postgres
   spring.datasource.password=yourpassword
   ```

3. **Build the project**
   ```bash
   ./mvnw clean package
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application**
   ```
   http://localhost:8080
   ```

### Demo Credentials

The application seeds sample data on first run:

**Teacher Account:**
- Email: `teacher@example.com`
- Password: `password123`

**Student Account:**
- Email: `student@example.com`
- Password: `password123`

## Docker Deployment

### Using Docker Compose (Recommended)

1. **Build and start containers**
   ```bash
   docker-compose up -d
   ```

2. **View logs**
   ```bash
   docker-compose logs -f app
   ```

3. **Stop containers**
   ```bash
   docker-compose down
   ```

4. **Remove volumes (reset database)**
   ```bash
   docker-compose down -v
   ```

### Docker Architecture

```
┌─────────────────────────────────────────┐
│   Docker Network (bridge)               │
│                                         │
│  ┌───────────────┐   ┌──────────────┐  │
│  │   Postgres    │   │     App      │  │
│  │   Container   │   │  Container   │  │
│  │               │   │              │  │
│  │  Port: 5432   │◄──┤  Port: 8080  │  │
│  │               │   │              │  │
│  │  Volume:      │   │              │  │
│  │  postgres_data│   │              │  │
│  └───────────────┘   └──────────────┘  │
│         │                    │          │
└─────────┼────────────────────┼──────────┘
          │                    │
          ▼                    ▼
    Host:5432            Host:8080
```

### Docker Compose Services

**Postgres Service:**
- Image: `postgres:16`
- Database: `studentmanagementsystem`
- Persistent volume for data
- Health check to ensure database readiness

**App Service:**
- Built from Dockerfile
- Depends on Postgres (waits for health check)
- Auto-connects using service name resolution
- Spring Boot with embedded Tomcat

## Docker Integration

This project includes first-class Docker integration to make local development and CI runs reproducible.

What is included
- `Dockerfile` — builds the Spring Boot application into a container image.
- `compose.yaml` (or `docker-compose.yml`) — convenient local orchestration of the `app` service and a PostgreSQL service used by the application during development and CI.
- Health checks and service dependency wiring so the app waits for the database before starting.

Quick commands (PowerShell)

```powershell
# Build and start app + database (rebuild image)
docker-compose up -d --build

# Follow logs for the app service
docker-compose logs -f app

# Stop and remove containers
docker-compose down

# Reset DB data (remove volumes)
docker-compose down -v
```

Environment variables
- See `application.properties` and the compose file for environment variables. You can override them with a `.env` file or with environment variables in your shell/CI.

Notes
- Use the dockerized Postgres during integration tests to ensure a close-to-production environment if you are not using Testcontainers.

## API Endpoints

### Authentication

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/login` | Login page | Public |
| POST | `/login` | Process login | Public |
| GET | `/register` | Registration page | Public |
| POST | `/register` | Process registration | Public |
| POST | `/logout` | Logout user | Authenticated |

### Departments

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/departments` | List all departments | Authenticated |
| GET | `/departments/{id}` | View department details | Authenticated |
| GET | `/departments/new` | Department creation form | Teacher |
| POST | `/departments/new` | Create department | Teacher |
| GET | `/departments/{id}/edit` | Department edit form | Teacher |
| POST | `/departments/{id}/edit` | Update department | Teacher |
| POST | `/departments/{id}/delete` | Delete department | Teacher |

### Courses

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/courses` | List all courses | Authenticated |
| GET | `/courses/{id}` | View course details | Authenticated |
| GET | `/courses/new` | Course creation form | Teacher |
| POST | `/courses/new` | Create course | Teacher |
| GET | `/courses/{id}/edit` | Course edit form | Teacher |
| POST | `/courses/{id}/edit` | Update course | Teacher |
| POST | `/courses/{id}/delete` | Delete course | Teacher |
| POST | `/courses/{courseId}/students/{studentId}/enroll` | Enroll student | Teacher |
| POST | `/courses/{courseId}/students/{studentId}/drop` | Drop student | Teacher |

### Students

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/students` | List all students | Authenticated |
| GET | `/students/{id}` | View student profile | Authenticated |
| GET | `/students/new` | Student creation form | Teacher |
| POST | `/students/new` | Create student | Teacher |
| GET | `/students/{id}/edit` | Student edit form | Self or Teacher |
| POST | `/students/{id}/edit` | Update student | Self or Teacher |
| POST | `/students/{id}/delete` | Delete student | Teacher |
| POST | `/students/{studentId}/courses/{courseId}/enroll` | Enroll in course | Self or Teacher |
| POST | `/students/{studentId}/courses/{courseId}/drop` | Drop course | Self or Teacher |

### Teachers

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/teachers` | List all teachers | Authenticated |
| GET | `/teachers/{id}` | View teacher profile | Authenticated |

## User Roles

### Student Role

**Permissions:**
- View all departments, courses, students, and teachers
- View and edit own profile
- Enroll in available courses
- Drop enrolled courses
- Cannot create, edit, or delete other entities

**Use Cases:**
- Browse course catalog
- Manage own course enrollments
- View classmates and teachers
- Update personal information

### Teacher Role

**Permissions:**
- Full CRUD operations on departments
- Full CRUD operations on courses
- Full CRUD operations on students
- Enroll/drop any student in/from any course
- View all teacher profiles
- All student permissions

**Use Cases:**
- Administrative management of the system
- Create and organize courses
- Manage student enrollments
- View system statistics

## Project Structure

```
src/
├── main/
│   ├── java/com/sarwad/sms/studentmanagementsystem/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java          # Spring Security configuration
│   │   │   └── DataInitializer.java         # Sample data seeder
│   │   ├── controller/
│   │   │   ├── AuthController.java          # Login/registration
│   │   │   ├── DepartmentController.java    # Department CRUD
│   │   │   ├── CourseController.java        # Course CRUD + enrollment
│   │   │   ├── StudentController.java       # Student CRUD + enrollment
│   │   │   ├── TeacherController.java       # Teacher viewing
│   │   │   └── HomeController.java          # Dashboard
│   │   ├── dto/
│   │   │   ├── StudentDto.java              # Student data transfer
│   │   │   ├── TeacherDto.java              # Teacher data transfer
│   │   │   ├── CourseDto.java               # Course data transfer
│   │   │   ├── DepartmentDto.java           # Department data transfer
│   │   │   └── RegistrationDto.java         # User registration
│   │   ├── entity/
│   │   │   ├── Student.java                 # Student JPA entity
│   │   │   ├── Teacher.java                 # Teacher JPA entity
│   │   │   ├── Course.java                  # Course JPA entity
│   │   │   ├── Department.java              # Department JPA entity
│   │   │   └── Role.java                    # Enum for user roles
│   │   ├── exception/
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── DuplicateResourceException.java
│   │   │   ├── UnauthorizedAccessException.java
│   │   │   └── GlobalExceptionHandler.java  # Centralized error handling
│   │   ├── repository/
│   │   │   ├── StudentRepository.java       # Student database access
│   │   │   ├── TeacherRepository.java       # Teacher database access
│   │   │   ├── CourseRepository.java        # Course database access
│   │   │   └── DepartmentRepository.java    # Department database access
│   │   ├── security/
│   │   │   ├── CustomUserDetails.java       # User details wrapper
│   │   │   └── CustomUserDetailsService.java # Loads users for auth
│   │   ├── service/
│   │   │   ├── StudentService.java          # Student business logic
│   │   │   ├── TeacherService.java          # Teacher business logic
│   │   │   ├── CourseService.java           # Course business logic
│   │   │   └── DepartmentService.java       # Department business logic
│   │   └── StudentManagementSystemApplication.java  # Main class
│   └── resources/
│       ├── application.properties           # Application configuration
│       ├── templates/                       # Thymeleaf templates
│       │   ├── fragments/
│       │   │   └── layout.html              # Reusable layout components
│       │   ├── auth/
│       │   │   ├── login.html               # Login page
│       │   │   └── register.html            # Registration page
│       │   ├── dashboard.html               # Main dashboard
│       │   ├── department/
│       │   │   ├── list.html                # Department list
│       │   │   ├── view.html                # Department details
│       │   │   └── form.html                # Department create/edit
│       │   ├── course/
│       │   │   ├── list.html                # Course list
│       │   │   ├── view.html                # Course details + enrollment
│       │   │   └── form.html                # Course create/edit
│       │   ├── student/
│       │   │   ├── list.html                # Student list
│       │   │   ├── view.html                # Student profile + enrollment
│       │   │   └── form.html                # Student create/edit
│       │   ├── teacher/
│       │   │   ├── list.html                # Teacher list
│       │   │   └── view.html                # Teacher profile
│       │   └── error/
│       │       └── error.html               # Error page
│       └── static/                          # Static resources (if any)
└── test/
    └── java/                                # Unit and integration tests
```

## Configuration

### Application Properties

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/studentmanagementsystem
spring.datasource.username=postgres
spring.datasource.password=yourpassword

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Thymeleaf Configuration
spring.thymeleaf.cache=false

# Logging
logging.level.org.springframework.security=DEBUG
```

### Environment Variables (Docker)

```yaml
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/studentmanagementsystem
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=3106
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

## Development

### Building the Project

```bash
# Clean and package
./mvnw clean package

# Skip tests
./mvnw clean package -DskipTests

# Run tests only
./mvnw test
```

### Running in Development Mode

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Using IDE
Run StudentManagementSystemApplication.java main method
```

### Database Management

**View tables:**
```sql
\dt
```

**Check enrollments:**
```sql
SELECT s.name, c.name 
FROM students s 
JOIN student_course sc ON s.id = sc.student_id
JOIN courses c ON c.id = sc.course_id;
```

**Reset database:**
```bash
docker-compose down -v
docker-compose up -d
```

## Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   ```bash
   # Change port in application.properties
   server.port=8081
   ```

2. **Database connection refused**
   ```bash
   # Ensure PostgreSQL is running
   docker-compose logs postgres
   
   # Check connection settings
   spring.datasource.url=jdbc:postgresql://localhost:5432/studentmanagementsystem
   ```

3. **Templates not found**
   ```bash
   # Rebuild the project
   ./mvnw clean package
   
   # Check templates are in src/main/resources/templates/
   ```

4. **Authentication fails**
   ```bash
   # Check logs for BCrypt errors
   docker-compose logs app | grep -i "auth"
   
   # Verify demo credentials in DataInitializer.java
   ```

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Write unit tests for new features
- Ensure all tests pass before submitting PR

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Spring Boot framework and documentation
- Tailwind CSS for the beautiful UI components
- PostgreSQL for robust database management
- Docker for simplified deployment
- Thymeleaf for server-side templating

## Contact

For questions or support, please open an issue on GitHub.

---

**Built with Spring Boot, PostgreSQL, and Thymeleaf**
