# GitHub Actions CI Setup Guide

## Overview
This framework is configured for GitHub Actions with optimized settings for 2-core runners, Java 21, and headless Chrome testing.

## CI Configuration Files

### 1. GitHub Actions Workflow
**Location:** `.github/workflows/selenium-tests.yml`

**Features:**
- Java 21 with Temurin distribution
- Chrome browser (headless mode)
- Three execution modes:
  - **Pull Requests:** Smoke tests only
  - **Push to main:** Full test suite
  - **Scheduled (daily 2 AM UTC):** Full suite + performance tests
- Security scanning with Trivy
- Test result artifacts and Allure reports

### 2. TestNG Configurations

| Configuration | File | Purpose | Thread Count |
|--------------|------|---------|--------------|
| **CI (All Tests)** | `src/test/resources/config/testng-ci.xml` | Complete test suite for CI | 2 |
| **Smoke Tests** | `src/test/resources/config/testng-smoke.xml` | Critical path tests only | 2 |
| **Performance** | `src/test/resources/config/testng-performance.xml` | Performance benchmarks | 2 |
| **Local (Complete)** | `src/test/resources/config/testng-complete.xml` | Full suite for local dev | 6 |

### 3. Maven CI Profile
**Location:** `pom.xml` (lines 314-362)

**Settings:**
- **Memory:** 4GB max, 1GB initial (vs 8GB/2GB for local)
- **Threads:** 2 (vs 12 for local)
- **GC:** G1GC with 200ms pause target
- **Retry:** 1 automatic retry for flaky tests

## Running Tests Locally (CI Mode)

### Smoke Tests (Fast - ~2-5 minutes)
```bash
mvn clean test -Pci -DsuiteXmlFile=src/test/resources/config/testng-smoke.xml -Dbrowser=chrome -Dheadless=true
```

### Full Test Suite (CI Settings)
```bash
mvn clean test -Pci -Dbrowser=chrome -Dheadless=true
```

### Performance Tests Only
```bash
mvn clean test -Pci -DsuiteXmlFile=src/test/resources/config/testng-performance.xml -Dbrowser=chrome -Dheadless=true
```

## CI Execution Flow

### On Pull Request
1. Checkout code
2. Setup Java 21
3. Cache Maven dependencies
4. Install Chrome
5. Resolve dependencies
6. **Run smoke tests** (testng-smoke.xml)
7. Generate Allure report
8. Upload test results

### On Push to Main
1-5. (Same as PR)
6. **Run all tests** (testng-ci.xml)
7-8. (Same as PR)

### Scheduled (Daily)
- Runs both **test** and **performance-test** jobs
- Uses production-like staging environment
- Extended timeout (600s vs 300s)

## Key Optimizations for CI

### 1. Resource Management
- **4GB RAM** limit (GitHub runners have ~7GB total)
- **2 threads** for parallel execution (2-core runners)
- **Compressed class pointers** and **string deduplication**

### 2. Browser Configuration
- **Headless Chrome** (no GUI overhead)
- **Reduced logging** (Chrome verbose logging disabled)
- **IPv4 stack** preference for faster networking

### 3. Test Execution
- **Method-level parallelism** (not class-level)
- **No unlimited threads** (explicit 2-thread limit)
- **Retry mechanism** (1 automatic retry for flaky tests)

## Test Groups

Tests are organized by groups for selective execution:

| Group | Purpose | Example Tests |
|-------|---------|---------------|
| `smoke` | Critical path validation | Login, homepage load, add to cart |
| `functional` | Core business logic | Checkout, registration, search |
| `negative` | Error handling | Invalid inputs, edge cases |
| `performance` | Load and response times | Page load, search speed |
| `ui` | Visual elements | Element visibility, layout |
| `security` | Auth and data protection | Session management, password fields |

## Troubleshooting CI Failures

### Common Issues

#### 1. Out of Memory Errors
- Check `MAVEN_OPTS` in workflow (should be `-Xmx4g -Xms1g`)
- Verify CI profile is active (`-Pci` flag)

#### 2. WebDriver Initialization Failures
- Ensure Chrome setup step ran successfully
- Check for CDP version warnings (non-critical)

#### 3. Null Pointer in Tests
- Verify TestNG XML uses `<classes>` not `<packages>` with groups
- Ensure `@BeforeMethod` is executing

#### 4. Timeout Failures
- Increase `time-out` in TestNG XML (default: 300000ms)
- Check network connectivity to demowebshop.tricentis.com

### Debug Commands

```bash
# Run with debug logging
mvn clean test -Pci -X -Dbrowser=chrome -Dheadless=true

# Run single test class
mvn clean test -Pci -Dtest=LoginTests -Dbrowser=chrome -Dheadless=true

# Check Java version
java -version

# Verify Chrome installation
google-chrome --version
```

## Artifacts and Reports

### Generated Artifacts
- **Surefire Reports:** `target/surefire-reports/`
- **Allure Results:** `target/allure-results/`
- **Screenshots:** `target/screenshots/`

### Accessing in GitHub Actions
1. Go to workflow run
2. Scroll to "Artifacts" section
3. Download `test-results-chrome-java21`
4. Extract and view reports

## Environment Variables

| Variable | Default | Purpose |
|----------|---------|---------|
| `BROWSER` | chrome | Browser type |
| `HEADLESS` | true | Headless mode |
| `ENVIRONMENT` | dev | Test environment |
| `MAVEN_OPTS` | -Xmx4g -Xms1g | JVM memory settings |

## Next Steps

1. **Push changes** to trigger first CI run
2. **Monitor workflow** execution in Actions tab
3. **Review test results** and artifacts
4. **Tune thread count** if needed (adjust in testng-ci.xml)
5. **Add more smoke tests** for critical user journeys

## CI Badge

Add this to your README.md:

```markdown
![Selenium Tests](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/selenium-tests.yml/badge.svg)
```

## Support

For CI issues:
1. Check workflow logs in Actions tab
2. Review this guide's troubleshooting section
3. Run tests locally with `-Pci` profile to reproduce
4. Check TestNG XML configuration for group/class setup
