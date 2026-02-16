# Testing Documentation

## Overview

This document provides comprehensive information about the testing strategy, setup, and best practices for the Student Management System project.

## Test Suite Summary

- **Total Tests**: 83
- **Unit Tests**: 32 (Services)
- **Repository Tests**: 23 (Data Access Layer)
- **Entity Tests**: 27 (Domain Objects)
- **Integration Tests**: 1 (Application Context)

## Test Structure

```
src/test/
├── java/
│   └── com/sarwad/sms/studentmanagementsystem/
│       ├── service/
│       │   ├── StudentServiceTest.java       (18 tests)
│       │   └── CourseServiceTest.java        (14 tests)
│       ├── repository/
│       │   ├── StudentRepositoryTest.java    (13 tests)
│       │   └── CourseRepositoryTest.java     (10 tests)
│       ├── entity/
│       │   ├── StudentTest.java              (15 tests)
│       │   └── CourseTest.java               (12 tests)
│       └── StudentManagementSystemApplicationTests.java  (1 test)
└── resources/
    └── application-test.yml
```

## Testing Technologies

### Frameworks & Libraries

- **JUnit 5** (Jupiter): Test framework
- **Mockito**: Mocking framework for unit tests
- **AssertJ**: Fluent assertion library
- **Spring Boot Test**: Integration testing support
- **H2 Database**: In-memory database for testing

### Dependencies

```xml
<!-- Testing Dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

## Test Categories

### 1. Unit Tests (Services)

Unit tests focus on testing individual service methods in isolation using mocks for dependencies.

**Key Features:**
- Use `@ExtendWith(MockitoExtension.class)`
- Mock all dependencies with `@Mock`
- Inject mocks with `@InjectMocks`
- Follow AAA pattern (Arrange-Act-Assert)

**Example:**
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {
    
    @Mock
    private StudentRepository studentRepository;
    
    @InjectMocks
    private StudentService studentService;
    
    @Test
    @DisplayName("Should return all students successfully")
    void getAllStudents_Success() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(students);
        
        // Act
        List<StudentDto> result = studentService.getAllStudents();
        
        // Assert
        assertThat(result).isNotNull().hasSize(1);
        verify(studentRepository, times(1)).findAll();
    }
}
```

### 2. Repository Tests

Repository tests verify data access layer functionality using a real database (H2 in-memory).

**Key Features:**
- Use `@SpringBootTest` with `@ActiveProfiles("test")`
- Use `@Transactional` for automatic rollback
- Test CRUD operations and custom queries
- Verify transactional behavior

**Example:**
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("StudentRepository Tests")
class StudentRepositoryTest {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Test
    @DisplayName("Should save student successfully")
    void save_Success() {
        // Arrange & Act
        Student savedStudent = studentRepository.save(testStudent);
        
        // Assert
        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getId()).isNotNull();
    }
}
```

### 3. Entity Tests

Entity tests verify domain object functionality including getters, setters, builders, and validation.

**Key Features:**
- Test constructor behavior
- Verify builder pattern
- Test getters and setters
- Verify default values
- Test equals and hashCode (when implemented)

**Example:**
```java
@DisplayName("Student Entity Tests")
class StudentTest {
    
    @Test
    @DisplayName("Should create student using builder")
    void builder_Success() {
        // Act
        Student student = Student.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();
        
        // Assert
        assertThat(student).isNotNull();
        assertThat(student.getName()).isEqualTo("John Doe");
    }
}
```

## Test Configuration

### application-test.yml

```yaml
spring:
  application:
    name: Student-Management-System-Test

  # H2 In-Memory Database
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: sa
    password: 

  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: false

  docker:
    compose:
      enabled: false
```

## Running Tests

### Command Line

```bash
# Run all tests
mvn clean test

# Run tests with specific profile
mvn test -Dspring.profiles.active=test

# Run specific test class
mvn test -Dtest=StudentServiceTest

# Run specific test method
mvn test -Dtest=StudentServiceTest#getAllStudents_Success

# Run tests with coverage
mvn clean test jacoco:report
```

### IDE (IntelliJ IDEA / Eclipse)

1. Right-click on test class or method
2. Select "Run Test" or "Debug Test"
3. View results in Test Runner panel

## Test Coverage Goals

- **Service Layer**: >80% line coverage
- **Repository Layer**: >70% line coverage
- **Entity Layer**: >60% line coverage
- **Overall**: >70% line coverage

## Best Practices

### 1. Test Naming Conventions

- Test classes: `{ClassUnderTest}Test`
- Test methods: `{methodName}_{scenario}_{expectedBehavior}`
- Use `@DisplayName` for human-readable test descriptions

### 2. AAA Pattern

Always structure tests using Arrange-Act-Assert:

```java
@Test
void testMethod() {
    // Arrange - Set up test data and mocks
    when(mock.method()).thenReturn(value);
    
    // Act - Execute the method under test
    var result = service.method();
    
    // Assert - Verify the outcome
    assertThat(result).isNotNull();
    verify(mock).method();
}
```

### 3. Test Isolation

- Each test should be independent
- Use `@BeforeEach` for common setup
- Avoid shared mutable state
- Use unique test data (timestamps/UUIDs) when needed

### 4. Assertion Best Practices

- Use AssertJ for fluent assertions
- Test one concept per test method
- Verify both positive and negative scenarios
- Include edge cases and boundary conditions

### 5. Mock Management

- Only mock external dependencies
- Don't mock the class under test
- Verify important interactions
- Use `lenient()` for optional stubs

## CI/CD Integration

### GitHub Actions Workflow

Tests run automatically on:
- Push to `main` or `master` branch
- Pull requests targeting `main` or `master`

**Workflow Configuration**: `.github/workflows/test.yml`

```yaml
name: Test Suite

on:
  push:
    branches: [main, master]
  pull_request:
    branches: [main, master]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
      - run: mvn clean test
```

### Branch Protection Rules

To enforce testing before merging:

1. Go to repository Settings
2. Navigate to Branches → Branch protection rules
3. Add rule for `main` branch:
   - ✅ Require pull request before merging
   - ✅ Require approvals (minimum 1)
   - ✅ Require status checks to pass
   - ✅ Require branches to be up to date
   - ✅ Include administrators

## Troubleshooting

### Common Issues

**Issue**: Tests pass locally but fail in CI
- **Solution**: Ensure test profile is active, check environment-specific configurations

**Issue**: H2 database errors
- **Solution**: Verify H2 dialect is configured correctly, check entity annotations

**Issue**: Unique constraint violations
- **Solution**: Use unique test data with timestamps or UUIDs

**Issue**: Mockito stubbing issues
- **Solution**: Only stub methods that will be called, use `lenient()` for optional stubs

## Future Enhancements

- [ ] Add integration tests for controllers
- [ ] Implement mutation testing (PIT)
- [ ] Add performance/load tests
- [ ] Set up SonarQube integration
- [ ] Add contract testing (Pact)
- [ ] Implement BDD with Cucumber

## References

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/reference/testing/index.html)
- [Test-Driven Development](https://martinfowler.com/bliki/TestDrivenDevelopment.html)

## Contact

For questions or issues related to testing, please contact the development team or open an issue on GitHub.
