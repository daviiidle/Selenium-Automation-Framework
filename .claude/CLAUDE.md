# MCP Framework Coordination — System Directives

## Core Mission
MCP-first automation agent. Delegate all filesystem, execution, and inspection tasks to MCP servers. Generate code only when MCP cannot handle the operation.

## Available MCP Tools

**Maven Test Runner** - Test execution and analysis
**Selenium Debug** - Selector validation, page inspection, test analysis, diagnostics, and code updates
**Framework Navigator** - Navigation, search, and relationship mapping (80-97% token savings)
**Memory Keeper** - Context persistence, session management, knowledge graphs, and cross-session state tracking
**Claude Codex MCP** - Repository operations, git management, quality checks, and Codex integration

## Execution Principles

**MCP-First Rule:** Can this be done via MCP? If yes → use MCP. If no → provide minimal code.

**CRITICAL: Tool Usage Hierarchy (MANDATORY)**
1. **NEVER use Read/Write/Edit/Grep tools** - These are BANNED for this project
2. **ALWAYS use Codex MCP tools** for file operations:
   - `mcp__claude-codex-mcp__repo_read` replaces Read
   - `mcp__claude-codex-mcp__repo_write` replaces Write/Edit
   - `mcp__claude-codex-mcp__repo_search` replaces Grep/Search
3. **ALWAYS use Git MCP tools** instead of Bash git commands
4. **ALWAYS run quality checks** before committing changes

**Priorities:**
1. Inspect before modifying (use MCP tools)
2. Execute through MCP (never bypass)
3. Write with `repo_write` (automatic security scanning)
4. Report concisely

## Standard Workflow

**Before:** Use `mcp__claude-codex-mcp__repo_read` (NOT Read tool), verify existence with `repo_search`, confirm no conflicts
**During:** Apply fixes with `mcp__claude-codex-mcp__repo_write` (NOT Edit/Write tools), validate incrementally
**After:** Run `mcp__claude-codex-mcp__quality_lint` and `mcp__claude-codex-mcp__quality_test`, report in 2-3 sentences max

## Safety Protocols

- Never recreate pom.xml or classes without request
- Verify file existence before creation (use `repo_search` from Codex MCP)
- One change per file diff
- Fetch context via MCP, don't infer
- Maintain Java/TestNG conventions
- Use `repo_write` for all file modifications (automatic secret scanning and path validation)
- Leverage `git_commit` for version control operations instead of manual git commands

## Auto-Recovery Workflow

**Goal:** 100% test pass rate through iterative isolation and repair

1. **Analysis** - Parse reports, extract failures
2. **Isolation** - Rerun failing tests individually, apply minimal fixes
3. **Verification** - Track resolved vs unresolved, continue until all pass
4. **Full Suite** - Execute complete suite
5. **Termination** - Stop at 100% pass rate, deliver summary

**Constraints:** Modify failing methods only, suppress verbose output, reuse build artifacts

## Token Optimization

- Delegate I/O to MCP (use `repo_read` for parallel multi-file reads)
- Use `repo_search` instead of grep for code searches (faster, regex-powered)
- Return diffs, not full files
- Batch related operations
- Fetch data instead of guessing
- Summaries over full outputs

## Claude Codex MCP Tool Reference

**Repository Operations:**
- `mcp__claude-codex-mcp__repo_read` - Read multiple files in parallel (more efficient than standard Read)
- `mcp__claude-codex-mcp__repo_search` - Fast ripgrep-based code search with regex patterns
- `mcp__claude-codex-mcp__repo_write` - Write files with automatic secret scanning and path validation

**Git Operations:**
- `mcp__claude-codex-mcp__git_status` - Get current git status (branch, staged, modified files)
- `mcp__claude-codex-mcp__git_diff` - View changes (supports staged/unstaged)
- `mcp__claude-codex-mcp__git_commit` - Create commits with auto-staging
- `mcp__claude-codex-mcp__git_log` - View commit history

**Quality Gates:**
- `mcp__claude-codex-mcp__quality_lint` - Run linters (auto-detects or specify: eslint, ruff, pylint, flake8)
- `mcp__claude-codex-mcp__quality_test` - Run tests (auto-detects or specify: pytest, jest, mocha, cargo)

**Codex Integration (Optional):**
- `mcp__claude-codex-mcp__codex_exec` - Delegate complex code generation to Codex CLI
- `mcp__claude-codex-mcp__codex_get_diff` - Review changes made by Codex

**When to Use Codex MCP:**
- Multi-file reads → Use `repo_read` instead of multiple Read calls
- Code search → Use `repo_search` instead of Grep
- File writes → Use `repo_write` for automatic security scanning
- Git operations → Use git tools instead of Bash commands
- Before commits → Run `quality_lint` and `quality_test`
- Large refactoring → Consider `codex_exec` for complex transformations

## Default Mode
MCP-Optimized Debug Mode - continuous debugging via MCP as primary execution layer