# Quick Start Guide

Get up and running with AI-driven test healing in 5 minutes!

## Step 1: Install Dependencies (2 minutes)

```bash
cd selenium-debug-mcp
npm install
npm run build
```

## Step 2: Configure Claude Code (2 minutes)

Create `.claude/mcp-config.json` in your framework root:

```json
{
  "mcpServers": {
    "selenium-debug": {
      "command": "node",
      "args": ["./selenium-debug-mcp/dist/index.js"]
    }
  }
}
```

## Step 3: Restart Claude Code (1 minute)

Close and reopen Claude Code to load the MCP server.

## Step 4: Test It! (1 minute)

Open Claude Code and try:

```
"Run my tests and fix any failures you find"
```

## That's It!

Claude will now:
- ✅ Monitor your test execution
- ✅ Detect failures in real-time
- ✅ Navigate to live pages to test selectors
- ✅ Apply fixes automatically with backups
- ✅ Retry fixed tests
- ✅ Report results

## Example Commands

### Basic Healing
```
"Fix my failing tests"
"Run mvn test and auto-heal any failures"
```

### Specific Test
```
"LoginTest is failing, please fix it"
"Debug and fix the RegisterTest.testValidRegistration test"
```

### Investigation Only
```
"Why is my login test failing?"
"Check if the email selector works on the login page"
```

### Batch Healing
```
"Run all authentication tests and fix any issues"
"Test the entire checkout flow and auto-heal failures"
```

## Verify Installation

Check that the MCP server is loaded:

1. Open Claude Code
2. Type: `"List your available MCP tools"`
3. You should see 30+ tools including:
   - `stream-test-execution`
   - `find-working-selector`
   - `update-selector-file`
   - etc.

## Troubleshooting

### "MCP server not found"
- Check `.claude/mcp-config.json` exists
- Verify path to `dist/index.js` is correct
- Restart Claude Code

### "npm install fails"
- Ensure Node.js 18+ is installed: `node --version`
- Try: `npm install --legacy-peer-deps`

### "Build fails"
- Check TypeScript is installed: `npx tsc --version`
- Try: `npm run clean && npm run build`

### "Tests won't run"
- Verify Maven is installed: `mvn --version`
- Check Java 21 is installed: `java --version`
- Ensure you're in the framework root directory

## What to Expect

When you ask Claude to fix your tests, you'll see:

1. **Initial Analysis**: Claude uses `stream-test-execution` to monitor tests
2. **Failure Detection**: Real-time detection as tests fail
3. **Live Investigation**: Claude navigates to pages with Chrome
4. **Fix Application**: Automatic code updates with backups
5. **Retry Execution**: Re-runs fixed tests
6. **Results Report**: Summary of all fixes applied

## Next Steps

- Read [SELF-HEALING-GUIDE.md](./SELF-HEALING-GUIDE.md) for detailed workflow
- Check [TOOLS-REFERENCE.md](./TOOLS-REFERENCE.md) for all available tools
- See [README.md](./README.md) for complete documentation

---

**Ready? Just say:**

```
"Run my tests and automatically fix any failures you find!"
```
