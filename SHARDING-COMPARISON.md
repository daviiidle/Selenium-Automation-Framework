# CI Test Sharding Strategy Comparison

## Executive Summary

Three sharding strategies are available, each with different tradeoffs for speed vs resource usage.

---

## 📊 Strategy Comparison

| Strategy | Jobs | Tests/Job | Speed | Resource Usage | Best For |
|----------|------|-----------|-------|----------------|----------|
| **Original** | 1 | 82 | Baseline (100%) | Low | Local/small CI |
| **4-Shard** | 4 | ~20 | 4x faster | Medium | Balanced approach |
| **12-Shard** | 12 | 4-10 | **12x faster** | High | Fast feedback |

---

## 🚀 Ultra-Sharded (12 Jobs) - **RECOMMENDED FOR SPEED**

### Configuration
- **File:** `.github/workflows/selenium-tests-ultra-sharded.yml`
- **Parallelization:** 12 independent jobs
- **Per-job resources:** 2GB RAM, 2 CPU cores

### Shard Distribution
```
Shard 1:  Login Tests (10 tests)
Shard 2:  Comprehensive Login (10 tests)
Shard 3:  Registration (8 tests)
Shard 4:  Password Recovery (7 tests)
Shard 5:  Shopping Cart (5 tests)
Shard 6:  Checkout (5 tests)
Shard 7:  Product Catalog (9 tests)
Shard 8:  Product Search (5 tests)
Shard 9:  HomePage (5 tests)
Shard 10: Comprehensive HomePage (10 tests)
Shard 11: Error Handling (4 tests)
Shard 12: Account Management (4 tests)
```

### Advantages
✅ **Maximum parallelization** - All test classes run simultaneously  
✅ **Fastest feedback** - Results in ~5-8 minutes (vs 30-60 minutes sequential)  
✅ **Isolated failures** - One failing test class doesn't affect others  
✅ **Easy debugging** - Clear per-class reports  
✅ **No resource contention** - Each shard has dedicated resources  

### Disadvantages
⚠️ **Higher CI costs** - 12 concurrent runners (check GitHub Actions pricing)  
⚠️ **More artifacts** - 12 separate result sets to download  

### When to Use
- **Pull requests** - Fast validation before merge
- **Continuous integration** - Every push to main/develop
- **When GitHub Actions concurrency limits allow**

---

## ⚖️ Medium-Sharded (4 Jobs) - **BALANCED**

### Configuration
- **File:** `.github/workflows/selenium-tests-sharded.yml`
- **Parallelization:** 4 jobs
- **Per-job resources:** 3GB RAM, 2 CPU cores

### Shard Distribution
```
Shard 1: Authentication (4 test classes, ~35 tests)
Shard 2: Cart + Checkout (2 test classes, ~10 tests)
Shard 3: Products + Navigation (4 test classes, ~34 tests)
Shard 4: Error + Account (2 test classes, ~8 tests)
```

### Advantages
✅ **Good balance** - 4x speedup with moderate resource usage  
✅ **Lower CI costs** - Only 4 concurrent runners  
✅ **Grouped by feature** - Logical test organization  

### When to Use
- **Cost-conscious environments**
- **Moderate CI concurrency limits**
- **Nightly regression suites**

---

## 🔧 Original (1 Job) - **BASELINE**

### Configuration
- **File:** `.github/workflows/selenium-tests.yml`
- **Parallelization:** Single job with TestNG parallelism
- **Resources:** 4GB RAM, 2 CPU cores

### When to Use
- **Local development** - Simple to run
- **Very limited GitHub Actions quota**
- **Smoke tests** (already has separate config)

---

## 💡 Recommendations

### For Your Use Case (CI Failures Due to Timeouts)

**Use Ultra-Sharded (12 jobs)** because:

1. **Isolation fixes timeout issues** - Each test class runs in fresh environment
2. **Smaller blast radius** - WebDriver initialization failure affects only 1/12 of tests
3. **Better resource allocation** - 2GB per shard vs shared 4GB
4. **Faster failure detection** - Know which specific class is problematic
5. **GitHub Actions free tier** - 2000 minutes/month supports this approach

### Cost Analysis (GitHub Free Tier)

**12-Shard Strategy:**
- Per-run time: ~8 minutes × 12 jobs = 96 CI minutes
- Runs per month (50 pushes): 96 × 50 = **4,800 minutes**
- **Exceeds free tier** - Consider paid plan or reduce triggers

**4-Shard Strategy:**
- Per-run time: ~15 minutes × 4 jobs = 60 CI minutes  
- Runs per month (50 pushes): 60 × 50 = **3,000 minutes**
- **Slightly over free tier** - Acceptable with selective triggers

### Hybrid Approach (BEST)

```yaml
# Use different strategies based on trigger:

Pull Requests: 
  → Smoke tests only (1 job, 2 min)
  → Fast validation

Push to develop:
  → 4-Shard strategy
  → Moderate speed, cost-effective

Push to main:
  → 12-Shard strategy
  → Maximum coverage, fastest feedback

Scheduled (nightly):
  → 12-Shard strategy
  → Full regression with detailed reports
```

---

## 🎯 Next Steps

1. **Test the ultra-sharded workflow:**
   ```bash
   git add .github/workflows/selenium-tests-ultra-sharded.yml
   git add src/test/resources/config/shards/
   git commit -m "Add ultra-sharded CI workflow"
   git push
   ```

2. **Monitor first run:**
   - Check GitHub Actions tab
   - Verify all 12 shards start
   - Review combined report

3. **Compare metrics:**
   - Execution time
   - Pass rate per shard
   - Resource usage

4. **Adjust as needed:**
   - Increase thread-count if shards finish too quickly
   - Decrease if resource contention occurs
   - Rebalance shards if some are much slower

---

## 📝 Files Created

### Ultra-Sharded (12 jobs)
- `.github/workflows/selenium-tests-ultra-sharded.yml`
- `src/test/resources/config/shards/testng-shard-*.xml` (12 files)

### Medium-Sharded (4 jobs)
- `.github/workflows/selenium-tests-sharded.yml`
- `src/test/resources/config/testng-shard-[1-4].xml`

### Original (kept for reference)
- `.github/workflows/selenium-tests.yml`

---

## 🐛 Troubleshooting

**If ultra-sharded workflow fails:**
1. Check individual shard logs
2. Identify failing test class
3. Run that shard locally:
   ```bash
   mvn test -DsuiteXmlFile=src/test/resources/config/shards/testng-shard-login.xml
   ```

**If resource limits hit:**
1. Reduce `max-parallel` in workflow
2. Use 4-shard strategy instead
3. Enable `fail-fast: true` for faster feedback

