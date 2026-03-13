![SE333 CI](https://github.com/Kyle-Eberhart/SE333-A5/actions/workflows/SE333_CI.yml/badge.svg)

## Project Overview

This project implements automated testing and continuous integration for a simplified Amazon-style shopping cart system.

The application calculates order totals using a set of pricing rules including:
- Regular item pricing
- Delivery cost calculation
- Additional cost for electronic items

The goal of the assignment is to demonstrate modern software testing practices including:

- Unit testing with **JUnit 5**
- Integration testing with a **database-backed shopping cart**
- **Mocking with Mockito**
- Code coverage analysis using **JaCoCo**
- Static code analysis using **Checkstyle**
- Continuous integration using **GitHub Actions**

## Testing

Two categories of tests were implemented:

### Unit Tests
`AmazonUnitTest.java`

These tests isolate the `Amazon` class and mock dependencies such as the shopping cart and pricing rules. They verify correct price aggregation logic.

### Integration Tests
`AmazonIntegrationTest.java`

These tests verify end-to-end functionality including:
- Database interaction
- Item persistence
- Delivery pricing logic
- Electronic item surcharge

## Continuous Integration

GitHub Actions automatically runs on every push to the `main` branch and performs:

- Checkstyle static analysis
- JUnit test execution
- JaCoCo code coverage generation

The workflow uploads the following artifacts:
- `checkstyle-result.xml`
- `jacoco.xml`

All workflow runs for this project are currently passing.

## Technologies Used

- Java 21
- Maven
- JUnit 5
- Mockito
- AssertJ
- JaCoCo
- Checkstyle
- GitHub Actions
