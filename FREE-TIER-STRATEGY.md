# 🆓 GitHub Actions Free Tier Optimization Strategy

## Free Tier Limits

**GitHub Free Account:**
- 2,000 CI minutes per month
- Unlimited public repos
- 500 MB artifact storage

---

## 💰 Smart Execution Strategy

Your workflow automatically selects the right approach based on the trigger:

```
Pull Request      → Smoke Tests (1 job, 3 min)      = 3 minutes
Push to develop   → 4 Shards (2 at a time)          = 30 minutes
Push to main      → 12 Shards (3 at a time)         = 32 minutes
Nightly (2am UTC) → 12 Shards (3 at a time)         = 32 minutes
```

---

## 📊 Monthly Budget Analysis

### Conservative Estimate (30 days):

| Activity | Frequency | Minutes/Run | Total Minutes |
|----------|-----------|-------------|---------------|
| **PRs** | 20 PRs | 3 min | 60 min |
| **Develop pushes** | 30 pushes | 30 min | 900 min |
| **Main pushes** | 8 pushes | 32 min | 256 min |
| **Nightly runs** | 30 nights | 32 min | 960 min |
| **Total** | | | **2,176 min** |

**Result:** Slightly over 2,000 minutes by ~176 minutes (9%)

---

## 🎯 Optimization Options

### Option 1: Reduce Develop Pushes (EASIEST)
**Action:** Batch commits before pushing to develop
- Reduce 30 → 25 pushes = Save 150 minutes
- **New total: 2,026 minutes** ✅ Just fits!

### Option 2: Skip Some Nightly Runs
**Action:** Run nightly only on weekdays
- 30 nights → 22 nights = Save 256 minutes
- **New total: 1,920 minutes** ✅ Comfortable margin

### Option 3: Reduce Shard Parallelism on Develop
**Action:** Use smoke tests for develop, full shards only for main
- 30 develop pushes × 27 min saved = Save 810 minutes
- **New total: 1,366 minutes** ✅ Lots of headroom

### Option 4: Hybrid (RECOMMENDED)
```yaml
PRs              → Smoke tests (3 min)
Develop pushes   → Smoke tests (3 min)  # Changed!
Main pushes      → 12 shards (32 min)
Nightly (Mon-Fri)→ 12 shards (32 min)
```

**Monthly cost:**
- PRs: 20 × 3 = 60 min
- Develop: 30 × 3 = 90 min (saved 810!)
- Main: 8 × 32 = 256 min
- Nightly: 22 × 32 = 704 min
- **Total: 1,110 minutes** ✅ Almost 50% buffer!

---

## 🚀 The Free Tier Workflow

**File:** `.github/workflows/selenium-tests-free-tier.yml`

### Key Features:

1. **Smart Job Selection:**
   - Automatically picks the right test suite
   - No manual intervention needed

2. **Parallelism Controls:**
   - PRs: 1 job (instant start)
   - Develop: max 2 parallel jobs
   - Main/Nightly: max 3 parallel jobs

3. **Resource Optimization:**
   - Reduced memory per shard (1.5GB vs 2GB)
   - Shorter artifact retention (7 days vs 30)
   - Maven cache reuse

4. **Manual Override:**
   - Can trigger any suite manually via GitHub Actions UI
   - Good for debugging specific issues

---

## 📈 Performance Comparison

| Strategy | Time | CI Minutes | Free Tier? |
|----------|------|------------|------------|
| Original (1 job) | 45 min | 45 min | ✅ |
| Smoke tests | 3 min | 3 min | ✅ |
| 4 shards (parallel=2) | 30 min | 60 min | ✅ |
| 12 shards (parallel=3) | 32 min | 128 min | ✅ |
| 12 shards (parallel=12) | 10 min | 120 min | ⚠️ |

**Key Insight:** 
- `max-parallel=3` gives 80% of the speed benefit
- Uses only 7% more CI minutes than `max-parallel=12`
- Stays comfortably within free tier limits

---

## 🔧 Configuration Tweaks

### To Further Reduce Usage:

**1. Skip CI on Documentation Changes:**
```yaml
on:
  push:
    paths-ignore:
      - '**.md'
      - 'docs/**'
      - '*.txt'
```

**2. Use Concurrency to Cancel Outdated Runs:**
```yaml
concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true  # Cancel old runs when new push arrives
```

**3. Reduce Nightly Frequency:**
```yaml
schedule:
  - cron: '0 2 * * 1,3,5'  # Mon, Wed, Fri only
```

---

## 📝 Implementation Steps

1. **Replace your current workflow:**
   ```bash
   # Backup old workflow
   mv .github/workflows/selenium-tests.yml .github/workflows/selenium-tests.yml.backup
   
   # Use the free-tier optimized one
   git add .github/workflows/selenium-tests-free-tier.yml
   git commit -m "Switch to free tier optimized CI workflow"
   git push
   ```

2. **Monitor usage:**
   - Go to Settings → Billing → Usage this month
   - Track minutes consumed
   - Adjust strategy if needed

3. **Test manually first:**
   - Go to Actions → Selenium Tests (Free Tier Optimized)
   - Click "Run workflow"
   - Select "smoke" to test
   - Verify it works

---

## 💡 Pro Tips

1. **Use Branch Protection Wisely:**
   - Only require smoke tests to pass on PRs
   - Let full regression run async after merge

2. **Batch Your Commits:**
   - Work on feature branch locally
   - Push once when ready (not after every commit)

3. **Use `[skip ci]` in Commit Messages:**
   ```bash
   git commit -m "Update README [skip ci]"
   ```

4. **Cache Aggressively:**
   - Maven dependencies cached (already done)
   - Consider caching browser binaries if needed

---

## 🎯 Expected Free Tier Usage (Hybrid Strategy)

```
Daily Breakdown:
- 1 PR × 3 min = 3 min
- 1 develop push × 3 min = 3 min
- 1 nightly run × 32 min = 32 min (weekdays only)
- Occasional main push × 32 min = ~8 min/day average

Average: ~46 minutes/day
Monthly: ~1,380 minutes

Free tier: 2,000 minutes
Buffer: 620 minutes (31%)
```

✅ **Comfortably fits with room for spikes!**

---

## 🔄 Rollback Plan

If you need to go back to the original workflow:

```bash
mv .github/workflows/selenium-tests.yml.backup .github/workflows/selenium-tests.yml
rm .github/workflows/selenium-tests-free-tier.yml
git commit -m "Rollback to original workflow"
git push
```

---

## 📞 Questions?

- **Too slow?** Increase `max-parallel` values
- **Using too many minutes?** Enable `[skip ci]` more aggressively
- **Tests timing out?** The timeout fixes are already applied
- **Need full regression?** Manually trigger with "sharded-12" option

