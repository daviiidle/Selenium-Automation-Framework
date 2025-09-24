# DemoWebShop Selenium Automation Framework

An enterprise-grade Selenium automation framework for testing [DemoWebShop](https://demowebshop.tricentis.com/) built with Java, Maven, and TestNG.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Reporting](#reporting)
- [Best Practices](#best-practices)
- [Contributing](#contributing)

## Overview

This framework provides a robust foundation for automated testing of the DemoWebShop e-commerce application. It follows industry best practices and design patterns to ensure maintainable, scalable, and reliable test automation.

## Features

- **Page Object Model (POM)** design pattern implementation
- **Cross-browser testing** support (Chrome, Firefox, Edge)
- **Parallel test execution** with TestNG
- **Automatic driver management** with WebDriverManager
- **Comprehensive reporting** with ExtentReports and Allure
- **Dynamic test data generation** with Java Faker
- **Structured logging** with Log4j2
- **Environment-specific configuration** support
- **Data-driven testing** capabilities with CSV and JSON
- **Screenshot capture** on test failures
- **CI/CD ready** with Maven profiles

## Prerequisites

- **Java 11** or higher
- **Maven 3.6.0** or higher
- **Git** for version control
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)

## Project Structure

```
selenium-automation-framework/
├── src/
│   ├── main/java/com/demowebshop/automation/
│   │   ├── config/          # Configuration management
│   │   ├── pages/           # Page Object Model classes
│   │   ├── utils/           # Utility classes and helpers
│   │   ├── factories/       # Factory patterns (WebDriver, etc.)
│   │   └── drivers/         # WebDriver management
│   └── test/
│       ├── java/com/demowebshop/automation/
│       │   ├── tests/       # Test classes
│       │   ├── base/        # Base test classes
│       │   └── listeners/   # TestNG listeners
│       └── resources/
│           ├── testdata/    # Test data files (CSV, JSON)
│           └── config/      # Configuration files
├── target/                  # Build artifacts
├── pom.xml                  # Maven configuration
├── .gitignore              # Git ignore rules
└── README.md               # Project documentation
```

## Installation

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd "Java Selenium framework"
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install
   ```

3. **Verify installation:**
   ```bash
   mvn compile
   ```

## Configuration

### Environment Variables

Create a `.env` file in the project root for environment-specific configurations:

```properties
# Browser Configuration
BROWSER=chrome
HEADLESS=false
IMPLICIT_WAIT=10
EXPLICIT_WAIT=20

# Application URLs
BASE_URL=https://demowebshop.tricentis.com/
API_BASE_URL=https://demowebshop.tricentis.com/api

# Test Data
TEST_EMAIL=test@example.com
TEST_PASSWORD=password123

# Reporting
EXTENT_REPORT_PATH=target/extent-reports/
ALLURE_RESULTS_PATH=target/allure-results/
```

### TestNG Configuration

The framework includes a TestNG configuration file at `src/test/resources/config/testng.xml` for organizing test execution.

## Running Tests

### Command Line Execution

1. **Run all tests:**
   ```bash
   mvn test
   ```

2. **Run tests with specific browser:**
   ```bash
   mvn test -Pchrome
   mvn test -Pfirefox
   mvn test -Pedge
   ```

3. **Run tests in headless mode:**
   ```bash
   mvn test -Pheadless
   ```

4. **Run specific test suite:**
   ```bash
   mvn test -DsuiteXmlFile=src/test/resources/config/smoke-tests.xml
   ```

5. **Run tests with parallel execution:**
   ```bash
   mvn test -Dparallel=methods -DthreadCount=3
   ```

### IDE Execution

- Right-click on test classes or methods in your IDE
- Use TestNG runner for more control over test execution
- Configure run configurations with different browsers and environments

## Reporting

### ExtentReports

HTML reports are generated automatically after test execution:
- Location: `target/extent-reports/`
- Open `extent-report.html` in a browser to view detailed results

### Allure Reports

Generate and view Allure reports:

```bash
# Generate Allure report
mvn allure:report

# Serve Allure report (opens in browser)
mvn allure:serve
```

### Screenshots

- Screenshots are captured automatically on test failures
- Stored in: `target/screenshots/`
- Embedded in both ExtentReports and Allure reports

## Best Practices

### Code Organization

1. **Follow Page Object Model pattern**
2. **Use meaningful class and method names**
3. **Keep tests independent and atomic**
4. **Implement proper wait strategies**
5. **Use data providers for data-driven tests**

### Test Data Management

1. **Store test data in external files (CSV, JSON)**
2. **Use Java Faker for dynamic data generation**
3. **Avoid hardcoded values in test methods**
4. **Implement test data cleanup strategies**

### Error Handling

1. **Implement proper exception handling**
2. **Use assertions effectively**
3. **Capture screenshots on failures**
4. **Log meaningful error messages**

## Development Commands

### Useful Maven Commands

```bash
# Clean and compile
mvn clean compile

# Run tests with specific tag
mvn test -Dgroups=smoke

# Run tests and generate reports
mvn clean test allure:report

# Check code style
mvn checkstyle:check

# Update dependencies
mvn versions:display-dependency-updates

# Create project reports
mvn site
```

### Code Quality

The project includes Checkstyle configuration for maintaining code quality:

```bash
# Check code style
mvn checkstyle:check

# Generate checkstyle report
mvn checkstyle:checkstyle
```

## Browser Support

- **Chrome** (default)
- **Firefox**
- **Microsoft Edge**
- **Headless mode** for all browsers

WebDriverManager automatically downloads and manages browser drivers.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests to ensure they pass
5. Submit a pull request

## Troubleshooting

### Common Issues

1. **Driver not found:**
   - Ensure WebDriverManager is properly configured
   - Check internet connectivity for driver downloads

2. **Tests failing intermittently:**
   - Review wait strategies
   - Check for race conditions
   - Verify element locators

3. **Maven build failures:**
   - Verify Java version compatibility
   - Check dependency conflicts
   - Clear Maven cache: `mvn dependency:purge-local-repository`

### Support

For issues and questions:
1. Check the troubleshooting section
2. Review existing GitHub issues
3. Create a new issue with detailed information

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Authors

- Framework Architect Agent - Initial framework setup
- SDET Team - Ongoing development and maintenance

---

**Happy Testing!** 🚀