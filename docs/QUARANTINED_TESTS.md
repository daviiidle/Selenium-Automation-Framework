# Quarantined Tests

Tests that are temporarily excluded from CI execution due to flakiness or environment-specific issues.

## Current Quarantined Tests

### 1. ProductSearchTests.testValidProductSearch

**Status:** ⚠️ Quarantined
**Date Quarantined:** 2025-10-06
**Reason:** Search functionality not returning results in CI headless Chrome environment
**Error:** `Search should return results for term: computer expected [true] but found [false]`
**Location:** `src/test/java/tests/products/ProductSearchTests.java:67`
**Excluded From:**
- `testng-ci.xml` (CI sequential configuration)

**Investigation Notes:**
- Test passes locally but fails in GitHub Actions CI
- NOT related to parallel execution (still fails in sequential mode)
- Likely selector or timing issue specific to headless Chrome in CI
- Search input may not be triggering or results not loading

**Action Items:**
- [ ] Use MCP selenium-debug to validate search input selector on live site
- [ ] Check if search requires explicit wait for results container
- [ ] Test locally with headless Chrome: `mvn test -Pci -Dtest=ProductSearchTests#testValidProductSearch`
- [ ] Add debugging/screenshots to see what's happening in CI

**To Re-enable:**
1. Fix selector or add proper waits
2. Verify test passes locally with headless Chrome
3. Remove exclusion from `testng-ci.xml`
4. Run CI to confirm
5. Update this document

---

## Re-enabled Tests (Fixed)

### AccountManagementTests.testOrderHistoryManagement
- **Fixed:** 2025-10-06 - Resolved by switching CI to sequential execution
- **Root Cause:** Parallel execution causing Thread.sleep() interruptions

### ShoppingCartTests.testUpdateCartItems
- **Fixed:** 2025-10-06 - Resolved by switching CI to sequential execution
- **Root Cause:** Parallel execution causing Thread.sleep() interruptions

---

## Quarantine Guidelines

### When to Quarantine a Test
- Test is flaky (fails intermittently without code changes)
- Test fails in CI but passes locally
- Test blocks CI pipeline and needs investigation
- External dependency issue (website changes, API down)

### Quarantine Process
1. Document the failure in this file
2. Exclude test from CI TestNG configuration
3. Create GitHub issue to track fix
4. Investigate root cause
5. Fix and re-enable test
6. Remove from quarantine list

### Maximum Quarantine Period
- **7 days:** Review progress and update ticket
- **14 days:** Escalate or delete test if not fixable
- **30 days:** Test must be fixed or permanently removed

## Re-enabled Tests (History)

_No tests have been re-enabled yet_

---

**Note:** Quarantined tests are still included in local test runs (`testng-complete.xml`). They are only excluded from CI execution to maintain pipeline stability.
