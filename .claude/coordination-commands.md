# Agent Coordination Commands

## Available Agents
All agents have been created and are ready for deployment. Use these commands to activate specific agents:

### Framework Foundation
```bash
/agent framework-architect
```
**Mission**: Create Maven project structure and enterprise-grade build configuration

### Core Components
```bash
/agent page-object-creator
```
**Mission**: Implement Page Object Model classes for DemoWebShop application

```bash
/agent test-data-manager
```
**Mission**: Create dynamic data factories and test data management system

```bash
/agent configuration-manager
```
**Mission**: Set up environment configuration and WebDriver management

### Test Implementation
```bash
/agent test-creator
```
**Mission**: Implement comprehensive end-to-end test scenarios

### Reporting & Analytics
```bash
/agent reporting-specialist
```
**Mission**: Set up ExtentReports, Allure, and logging framework

## Deployment Sequence
1. **Start with**: `/agent framework-architect` (Foundation)
2. **Parallel deployment**: `/agent configuration-manager`
3. **After foundation**: `/agent page-object-creator` + `/agent test-data-manager`
4. **Integration phase**: `/agent test-creator`
5. **Final integration**: `/agent reporting-specialist`

## Coordination Protocol
- Each agent reports completion status to main brain
- Agents coordinate through .claude/CLAUDE.md updates
- Quality gates must pass before next agent activation
- Integration testing between agent deliverables