# MCP-First Automation Framework

## Core Directive
**MCP-first execution.** Use MCP tools for all operations. Generate code only when MCP cannot handle the task.

## Tool Usage Rules (MANDATORY)

**File Operations:**
- `repo_read` - Read files (NEVER use Read tool)
- `repo_write` - Write/edit files with auto security scan (NEVER use Write/Edit tools)
- `repo_search` - Search code with regex (NEVER use Grep tool)

**Git Operations:**
- `git_status`, `git_diff`, `git_commit`, `git_log` (NEVER use Bash git commands)

**Quality Gates:**
- `quality_lint` - Run before commit
- `quality_test` - Run before commit

**Framework Navigation:**
- `list-page-objects`, `list-test-classes`, `find-class`, `find-method`
- `get-class-info`, `search-selectors`, `search-annotations`

## Standard Workflow

1. **Inspect** - Use `repo_search` to locate, `repo_read` to examine
2. **Modify** - Use `repo_write` to change files
3. **Validate** - Run `quality_lint` and `quality_test`
4. **Commit** - Use `git_commit` with descriptive message
5. **Report** - 2-3 sentences max

## Safety Protocols

- Verify file existence before creation (`repo_search`)
- One logical change per commit
- Never recreate pom.xml without explicit request
- Maintain Java/TestNG conventions
- Fetch context via MCP, never infer

## Test Recovery Workflow

**Goal:** 100% pass rate through isolation and repair

1. Parse failure reports
2. Rerun failing tests individually
3. Apply minimal targeted fixes
4. Verify fix resolves issue
5. Continue until all pass
6. Run full suite
7. Stop at 100% pass rate

## Token Optimization

- Parallel reads with `repo_read`
- Return diffs, not full files
- Batch related operations
- Summaries over verbose output

## Active MCP Servers

- **Framework Navigator** - 12 tools, navigation/search
- **Claude Codex MCP** - 13 tools, repo/git/quality
- **Memory Keeper** - 38 tools, context persistence

**Disabled (enable on-demand):**
- Selenium Debug - Selector debugging (15k tokens)
- Maven Test Runner - Use bash mvn instead (7k tokens)
