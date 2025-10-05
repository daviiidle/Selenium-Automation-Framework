# Quarantined Tests

Tests that are temporarily excluded from CI execution due to flakiness or environment-specific issues.

## Current Quarantined Tests

### ProductSearchTests.testValidProductSearch

**Status:** ⚠️ Quarantined
**Date Quarantined:** 2025-10-06
**Reason:** Search functionality not returning results in CI headless Chrome environment
**Error:** `Search should return results for term: computer expected [true] but found [false]`
**Location:** `src/test/java/tests/products/ProductSearchTests.java`
**Excluded From:**
- `testng-ci.xml` (CI configuration)

**Investigation Notes:**
- Test passes locally but fails in GitHub Actions CI
- Likely timing issue with search results loading in headless mode
- May need explicit wait or selector update

**Action Items:**
- [ ] Test with increased wait times for search results
- [ ] Validate search input selector in headless Chrome
- [ ] Check if search results container loads properly
- [ ] Use MCP selenium-debug tools to find working selector

**To Re-enable:**
1. Fix the underlying issue
2. Verify test passes locally with headless Chrome
3. Remove exclusion from `testng-ci.xml`
4. Run CI to confirm
5. Update this document

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
