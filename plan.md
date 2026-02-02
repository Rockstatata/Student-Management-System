Below is a **step by step, implementation-first plan** designed so you can **build the project cleanly from zero to production-ready**, and also **feed each step directly to Copilot** without confusion.

This is written like an engineering execution plan, not theory.

---

# Step by Step Implementation Plan

## Student Management System (Spring Boot + Thymeleaf)

---

## PHASE 0: Project & Environment Setup

### Step 0.1 Create Project Skeleton

* Create Spring Boot project
* Packaging: `com.example.webapp`
* Dependencies:

    * Spring Web
    * Spring Data JPA
    * Spring Security
    * Thymeleaf
    * PostgreSQL Driver
    * Lombok (optional but recommended)
    * Validation

Directory structure:

```
src/main/java/com/example/webapp
├── config
├── controller
├── service
├── repository
├── entity
├── dto
└── security
```

---

### Step 0.2 Docker Setup

Create:

* `Dockerfile`
* `docker-compose.yml`

docker-compose services:

* `postgres`
* `app`

Verify:

```
docker-compose up -d
```

---

### Step 0.3 Application Configuration

Create `application.yml`:

* PostgreSQL datasource
* Hibernate DDL auto = `update`
* Show SQL (dev only)
* Thymeleaf cache disabled (dev)

---

## PHASE 1: Domain Modeling (JPA Entities)

### Step 1.1 Create Role Enum

```java
public enum Role {
    STUDENT,
    TEACHER
}
```

---

### Step 1.2 Create Department Entity

* Fields: id, name
* Constraints: unique name
* Relationships:

    * OneToMany teachers
    * OneToMany courses

Test:

* Application starts
* Table auto-created

---

### Step 1.3 Create Teacher Entity

* Fields: id, name, email, role
* Role default: TEACHER
* Relationships:

    * ManyToOne department
    * ManyToMany students

Important:

* Teacher owns teacher_student join table

---

### Step 1.4 Create Course Entity

* Fields: id, name, description
* Relationships:

    * ManyToOne department
    * ManyToMany students (mappedBy)

---

### Step 1.5 Create Student Entity

* Fields: id, name, roll, email, role
* Constraints: unique roll
* Role default: STUDENT
* Relationships:

    * ManyToMany teachers (mappedBy)
    * ManyToMany courses (own join table)

---

### Step 1.6 Verify Schema

* Start application
* Confirm tables:

    * students
    * teachers
    * courses
    * departments
    * teacher_student
    * student_course

---

## PHASE 2: Repository Layer

### Step 2.1 Create Repositories

For each entity:

* Extend `JpaRepository<Entity, Long>`

Example:

```java
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRoll(String roll);
}
```

---

### Step 2.2 Add Query Helpers

* Student: findByRoll
* Teacher: findByEmail
* Department: findByName

---

## PHASE 3: Security & Authentication

### Step 3.1 Security Configuration

Create `SecurityConfig`:

* Disable CSRF (for simplicity)
* Use form login
* Session-based auth
* Role-based access rules

Rules:

```
/login/** -> permitAll
/student/** -> authenticated
/teacher/** -> hasRole(TEACHER)
/admin/** -> hasRole(TEACHER)
```

---

### Step 3.2 UserDetails Implementation

Decision:

* Use **Teacher and Student as security users**

Create:

* Custom `UserDetailsService`
* Map:

    * Student → ROLE_STUDENT
    * Teacher → ROLE_TEACHER

---

### Step 3.3 Auth Controller

Create:

* `/login`
* `/login/student`
* `/login/teacher`

Logic:

* Authenticate using predefined credentials
* Store principal in session

---

### Step 3.4 Seed Initial Users

Create `DataInitializer`:

* One teacher
* One student
* One department
* One course

This avoids manual DB work.

---

## PHASE 4: Service Layer (Business Logic)

### Step 4.1 Student Service

Responsibilities:

* Create student
* Update student
* Delete student
* Self-update logic

Critical logic:

```java
if (currentUser.isStudent() && !currentUser.id.equals(studentId)) {
    throw new AccessDeniedException();
}
```

Also:

* Never allow role updates in student self-update

---

### Step 4.2 Teacher Service

Responsibilities:

* CRUD teachers
* Assign students
* Assign department

---

### Step 4.3 Course Service

Responsibilities:

* CRUD courses
* Assign students
* Assign department

---

### Step 4.4 Department Service

Responsibilities:

* CRUD departments

---

## PHASE 5: DTOs & Validation

### Step 5.1 Create DTOs

Mandatory DTOs:

* StudentCreateDto
* StudentUpdateDto (no role field)
* TeacherDto
* CourseDto
* DepartmentDto

Add:

* `@NotBlank`
* `@Email`
* `@Size`

---

### Step 5.2 DTO to Entity Mapping

* Manual mapping or mapper class
* No direct entity binding in controllers

---

## PHASE 6: Controllers (MVC)

### Step 6.1 Student Controller

Endpoints:

* `GET /students`
* `GET /students/{id}`
* `GET /students/{id}/edit`
* `POST /students/{id}/edit`

Authorization:

* Teacher → always allowed
* Student → only if self

---

### Step 6.2 Teacher Controller

Teacher-only:

* CRUD teachers
* Assign students

---

### Step 6.3 Course Controller

Teacher-only:

* CRUD courses
* Assign students

---

### Step 6.4 Department Controller

Teacher-only:

* CRUD departments

---

## PHASE 7: Thymeleaf UI

### Step 7.1 Base Layout

Create:

* `layout.html`
* Role-based navbar
* Logout button

---

### Step 7.2 List Views

Create list pages for:

* Students
* Teachers
* Courses
* Departments

---

### Step 7.3 Forms

Create forms for:

* Create
* Edit

Rules:

* Role field hidden for students
* Role field disabled for student edit

---

### Step 7.4 Authorization in Views

Use:

```html
sec:authorize="hasRole('TEACHER')"
```

---

## PHASE 8: Relationship Management

### Step 8.1 Student ↔ Course Assignment

* Multi-select UI
* Persist join table updates

---

### Step 8.2 Teacher ↔ Student Assignment

* Multi-select UI
* Persist join table updates

---

## PHASE 9: Error Handling & Hardening

### Step 9.1 Exception Handling

Create:

* GlobalExceptionHandler
* Handle:

    * AccessDeniedException
    * EntityNotFoundException

---

### Step 9.2 Prevent URL Manipulation

* Validate entity ownership in service layer
* Never trust path variables alone

---

## PHASE 10: Final Validation

### Step 10.1 Security Tests

Manually verify:

* Student cannot edit others
* Student cannot change role
* Teacher can do everything

---

### Step 10.2 Docker Validation

Test:

```
docker-compose down
docker-compose up --build
```

Confirm:

* App reachable
* Data persists

---

## PHASE 11: Cleanup & Documentation

### Step 11.1 Code Cleanup

* Remove unused imports
* Add comments where logic is non-obvious

---

### Step 11.2 README Finalization

Include:

* Run instructions
* Login credentials
* Feature list

---

## Recommended Copilot Usage Pattern

Give Copilot **one step at a time**, for example:

> “Implement Student entity with JPA mappings exactly as described in PRD Phase 1 Step 1.5”

This avoids hallucination and keeps structure clean.

---
