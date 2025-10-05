# Framework Navigator MCP Server

Intelligent framework indexing and navigation with minimal token usage.

## Overview

This MCP server indexes your entire Java Selenium framework and provides fast, token-efficient navigation tools. Instead of reading full files (500+ tokens), you can query specific information (10-20 tokens).

## Token Savings

**Before (without Navigator):**
- Read entire HomePage.java to find a method: **~500 tokens**
- Search through files manually: **~1000+ tokens**

**After (with Navigator):**
- `find-method clickLoginButton`: **~15 tokens**
- `get-class-info HomePage`: **~50 tokens**

**Average savings: 80-97% token reduction**

## Available Tools

### Navigation Tools (8 tools)

1. **list-page-objects** - List all Page Object classes
2. **list-test-classes** - List all test classes by category
3. **find-class** - Find class by name/pattern
4. **find-method** - Find method across all files
5. **get-class-info** - Get class details without reading file
6. **get-framework-stats** - Framework statistics
7. **list-all-classes** - All classes by category
8. **refresh-index** - Refresh the index after code changes

### Search Tools (7 tools)

9. **search-selectors** - Find selectors by name
10. **search-annotations** - Find @Test, @DataProvider, etc.
11. **list-selector-jsons** - List selector JSON files
12. **get-selector-json** - Get selectors from JSON file
13. **search-imports** - Find classes importing specific packages
14. **find-data-providers** - Find all DataProvider methods
15. **get-page-object-selectors** - Get selectors for a Page Object

### Relationship Tools (7 tools)

16. **get-test-dependencies** - Page Objects used by test
17. **get-selector-usage** - Which Page Object uses selector
18. **map-test-to-selectors** - Full test → page → selector chain
19. **find-page-object-usage** - Which tests use a Page Object
20. **get-class-hierarchy** - Class inheritance chain
21. **find-subclasses** - All classes extending base class
22. **get-package-classes** - All classes in a package

## How It Works

1. **On First Use**: Indexes all `.java` files and selector JSONs (~2-5 seconds)
2. **Cached**: Index stored in memory for instant lookups
3. **Manual Refresh**: Use `refresh-index` tool after code changes

## Usage Examples

```javascript
// Find a method across entire framework
mcp__framework-navigator__find-method({ methodName: "clickLoginButton" })
// Returns: { class: "HomePage", path: "src/.../HomePage.java", line: 45 }

// Get class info without reading full file
mcp__framework-navigator__get-class-info({ className: "LoginPage" })
// Returns: methods, fields, extends, implements - all metadata

// Map test to all its dependencies
mcp__framework-navigator__map-test-to-selectors({ testClass: "LoginTests" })
// Returns: all Page Objects and their selectors used by test
```

## Auto-Refresh Strategy

**Current:** Manual refresh via `refresh-index` tool

**Future Options:**
1. Time-based: Auto-refresh every N minutes
2. File-watching: Monitor src/ directories for changes
3. On-demand: Refresh when tool detects stale data

For now, run `refresh-index` after making significant code changes.

## Build & Run

```bash
npm install
npm run build
```

Server automatically registered in `.mcp.json` as `framework-navigator`.

## Architecture

```
src/
├── index.ts              # MCP server bootstrap
├── indexer/
│   ├── JavaIndexer.ts    # Parse Java files
│   ├── SelectorIndexer.ts # Parse JSON selectors
│   └── FrameworkIndex.ts  # In-memory cache
├── tools/
│   ├── navigation-tools.ts    # Navigation tools
│   ├── search-tools.ts        # Search tools
│   └── relationship-tools.ts  # Relationship mapping
└── utils/
    └── java-parser.ts    # Java parsing utilities
```

## Performance

- **Index Time**: ~2-5 seconds for 60 Java files
- **Query Time**: <10ms per lookup
- **Memory**: ~5-10MB for typical framework
- **Token Savings**: 80-97% average reduction
