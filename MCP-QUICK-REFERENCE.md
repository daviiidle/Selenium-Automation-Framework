# MCP Quick Reference - Optimized Setup

## Active Tools (Always Available)

### Framework Navigator (12 tools)
```
Navigation (8 tools):
  - list-page-objects          # Find all Page Objects
  - list-test-classes          # Find all test classes
  - find-class                 # Search by class name
  - find-method                # Search by method name
  - get-class-info             # Get class details
  - get-framework-stats        # Framework overview
  - list-all-classes           # All classes by category
  - refresh-index              # Reindex framework

Search (4 tools):
  - search-selectors           # Find selectors by name
  - search-annotations         # Find @Test, @DataProvider, etc.
  - list-selector-jsons        # List selector files
  - get-selector-json          # Read selector file
```

### Claude Codex MCP (13 tools)
```
Repository (3 tools):
  - repo_read                  # Read multiple files (parallel)
  - repo_write                 # Write with secret scanning
  - repo_search                # Ripgrep-based code search

Git (4 tools):
  - git_status                 # Current status
  - git_diff                   # View changes
  - git_commit                 # Create commits
  - git_log                    # Commit history

Quality (2 tools):
  - quality_lint               # Run linters
  - quality_test               # Run tests

Codex (4 tools):
  - codex_exec                 # Delegate complex tasks
  - codex_get_diff             # View Codex changes
  - artifact_download          # Download CI artifacts
  - artifact_parse_junit       # Parse test reports
```

### Memory Keeper (12 tools)
```
Core (5 tools):
  - context_save               # Save context item
  - context_get                # Retrieve by key/filter
  - context_search             # Search within session
  - context_search_all         # Search across sessions
  - context_semantic_search    # Natural language search

Sessions (2 tools):
  - context_session_start      # Start new session
  - context_session_list       # List recent sessions

Checkpoints (2 tools):
  - context_checkpoint         # Create checkpoint
  - context_restore_checkpoint # Restore from checkpoint

Backup (2 tools):
  - context_export             # Export session data
  - context_import             # Import session data

Status (1 tool):
  - context_status             # Current status
```

---

## Disabled Tools (Enable When Needed)

### Selenium Debug (Commented Out)
**When to enable:** Debugging selectors, validating waits, live testing

**How to enable:**
1. Edit `.mcp.json`
2. Move `selenium-debug` from `disabledServers` to `mcpServers`
3. Restart Claude Code

**What you get:**
- validate-selector-live (test selectors on live site)
- find-alternative-selectors (find better selectors)
- find-working-selector (replace failing selector)
- update-selector-file (auto-update JSON)
- update-page-object-selector (auto-update Java)
- retry-single-test (rerun after fixes)
- add-to-retry-queue (queue failed tests)

### Maven Test Runner (Disabled)
**When to enable:** Need structured test execution and analysis

**Alternative:** Use `bash` commands:
```bash
mvn clean test -Dtest=ClassName
mvn test -Dtest=ClassName#methodName
```

---

## Tool Usage Patterns

### 1. Navigate Codebase
```
1. get-framework-stats          # Overview
2. list-page-objects            # Find Page Objects
3. get-class-info HomePage      # Get details
4. search-selectors emailInput  # Find selector
```

### 2. Search Code
```
1. repo_search "pattern"        # Search entire repo
2. find-method "methodName"     # Find method
3. search-annotations Test      # Find @Test methods
```

### 3. Read/Write Files
```
1. repo_read ["file1", "file2"] # Read files (parallel)
2. repo_write "path" "content"  # Write (auto security scan)
3. git_status                   # Check status
4. quality_lint ["*.java"]      # Lint before commit
5. git_commit "message"         # Commit changes
```

### 4. Track Context
```
1. context_save key="decision" value="..." category="decision"
2. context_get category="task" priorities=["high"]
3. context_search query="selector bug"
4. context_checkpoint name="before-refactor"
```

### 5. Debug Selectors (Enable selenium-debug first)
```
1. validate-selector-live css="#email"
2. find-alternative-selectors css="#email"
3. update-selector-file "login-selectors.json" "emailInput" "css" "#new-email"
```

---

## Token Budget

| Component | Tools | Approx Tokens |
|-----------|-------|---------------|
| Framework Navigator | 12 | ~6,000 |
| Claude Codex MCP | 13 | ~8,000 |
| Memory Keeper | 12 | ~7,000 |
| **Total Active** | **37** | **~21,000** |
| | | |
| Selenium Debug (disabled) | 30 | ~15,000 |
| Maven Test Runner (disabled) | 7 | ~7,000 |
| Relationship Tools (disabled) | 6 | ~8,000 |
| Advanced Search (disabled) | 3 | ~3,000 |
| **Total Disabled** | **46** | **~33,000** |

**Available for Reasoning:** ~179k tokens (from 200k total)

---

## Configuration Tweaks

### Enable Full Search Tools
Edit `.mcp.json`:
```json
"ENABLE_SEARCH_TOOLS": "full"  // Adds search-imports, find-data-providers
```
**Cost:** +3k tokens

### Enable Relationship Tools
```json
"ENABLE_RELATIONSHIP_TOOLS": "true"  // Adds 6 relationship mapping tools
```
**Cost:** +8k tokens

### Enable Memory Keeper Batch Ops
```json
"ENABLE_BATCH_OPS": "true"  // Adds batch_save, batch_delete, batch_update
```
**Cost:** +4k tokens

---

## Common Tasks

### Find and Read a Class
```
1. find-class "HomePage"
2. repo_read ["src/main/java/.../HomePage.java"]
```

### Fix a Selector
```
1. search-selectors "emailInput"
2. repo_read ["src/main/resources/selectors/login-selectors.json"]
3. repo_write "path/to/file" "updated content"
```

### Track a Decision
```
context_save key="refactor-decision" value="Decided to use Page Factory pattern for better maintainability" category="decision" priority="high"
```

### Search Past Work
```
context_search_all query="selector timeout" sessions=[] limit=10
```

### Create Checkpoint Before Major Change
```
1. context_checkpoint name="before-login-refactor"
2. [make changes]
3. context_restore_checkpoint name="before-login-refactor"  # If needed
```

---

## Performance Tips

1. **Use repo_read for multiple files** - It's parallelized and faster than Read
2. **Use repo_search instead of Grep** - It's ripgrep-powered and respects .gitignore
3. **Save important context early** - Don't wait until the end
4. **Create checkpoints before refactors** - Easy rollback
5. **Enable selenium-debug only when needed** - Saves 15k tokens

---

## Troubleshooting

### Tool not found
- Check if it's in `disabledServers` section
- Move to `mcpServers` and restart Claude Code

### Too many tools (context overflow)
- Disable more features in `.mcp.json`
- Set `ENABLE_SEARCH_TOOLS: "false"`
- Remove Memory Keeper entirely

### Need more analysis tools
- Enable relationship tools temporarily
- Use `repo_search` as alternative

---

## Summary

**Before Optimization:** 83 tools, ~54k tokens used
**After Optimization:** 37 tools, ~21k tokens used
**Savings:** 46 tools, ~33k tokens (61% reduction)

**You still have:**
- ✅ Full framework navigation
- ✅ Complete repo/git/quality operations
- ✅ Cross-session memory
- ✅ Essential search capabilities

**You don't have (but can enable):**
- ❌ Relationship mapping (use repo_search instead)
- ❌ Advanced import analysis
- ❌ Live selector debugging (enable selenium-debug when needed)
- ❌ Structured test execution (use bash mvn instead)
