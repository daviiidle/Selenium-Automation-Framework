#!/usr/bin/env node
/**
 * Selenium Debug MCP Server
 * Provides intelligent debugging and live test execution for Java Selenium framework
 */
import { Server } from '@modelcontextprotocol/sdk/server/index.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import { CallToolRequestSchema, ListToolsRequestSchema, ListResourcesRequestSchema, ReadResourceRequestSchema, } from '@modelcontextprotocol/sdk/types.js';
import * as dotenv from 'dotenv';
import path from 'path';
import { fileURLToPath } from 'url';
// Import tools
import { registerLiveTestingTools } from './tools/live-testing.js';
import { registerStaticAnalysisTools } from './tools/static-analysis.js';
import { registerFixSuggestionTools } from './tools/fix-suggestions.js';
import { registerHealingTools } from './tools/healing-tools.js';
// Import resources
import { registerFrameworkResources } from './resources/framework-resources.js';
// Setup
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
dotenv.config({ path: path.join(__dirname, '..', '.env') });
// Server configuration
const SERVER_NAME = 'selenium-debug-mcp';
const SERVER_VERSION = '1.0.0';
// Framework root detection
const FRAMEWORK_ROOT = process.env.FRAMEWORK_ROOT || path.join(__dirname, '..', '..');
class SeleniumDebugServer {
    server;
    tools;
    resources;
    constructor() {
        this.server = new Server({
            name: SERVER_NAME,
            version: SERVER_VERSION,
        }, {
            capabilities: {
                tools: {},
                resources: {},
            },
        });
        this.tools = new Map();
        this.resources = new Map();
        this.setupHandlers();
        this.registerTools();
        this.registerResources();
    }
    setupHandlers() {
        // List available tools
        this.server.setRequestHandler(ListToolsRequestSchema, async () => {
            return {
                tools: Array.from(this.tools.values()),
            };
        });
        // Execute tool
        this.server.setRequestHandler(CallToolRequestSchema, async (request) => {
            const toolName = request.params.name;
            const toolArgs = request.params.arguments || {};
            const tool = this.tools.get(toolName);
            if (!tool || !tool.handler) {
                throw new Error(`Unknown tool: ${toolName}`);
            }
            try {
                const result = await tool.handler(toolArgs);
                return {
                    content: [
                        {
                            type: 'text',
                            text: JSON.stringify(result, null, 2),
                        },
                    ],
                };
            }
            catch (error) {
                const errorMessage = error instanceof Error ? error.message : String(error);
                return {
                    content: [
                        {
                            type: 'text',
                            text: JSON.stringify({
                                error: errorMessage,
                                tool: toolName,
                                arguments: toolArgs,
                            }, null, 2),
                        },
                    ],
                    isError: true,
                };
            }
        });
        // List available resources
        this.server.setRequestHandler(ListResourcesRequestSchema, async () => {
            return {
                resources: Array.from(this.resources.values()).map(r => ({
                    uri: r.uri,
                    name: r.name,
                    description: r.description,
                    mimeType: r.mimeType,
                })),
            };
        });
        // Read resource
        this.server.setRequestHandler(ReadResourceRequestSchema, async (request) => {
            const resourceUri = request.params.uri;
            const resource = this.resources.get(resourceUri);
            if (!resource || !resource.handler) {
                throw new Error(`Unknown resource: ${resourceUri}`);
            }
            try {
                const content = await resource.handler();
                return {
                    contents: [
                        {
                            uri: resourceUri,
                            mimeType: resource.mimeType || 'text/plain',
                            text: content,
                        },
                    ],
                };
            }
            catch (error) {
                throw new Error(`Failed to read resource ${resourceUri}: ${error}`);
            }
        });
    }
    registerTools() {
        // Register live testing tools
        const liveTestingTools = registerLiveTestingTools(FRAMEWORK_ROOT);
        liveTestingTools.forEach(tool => this.tools.set(tool.name, tool));
        // Register static analysis tools
        const staticAnalysisTools = registerStaticAnalysisTools(FRAMEWORK_ROOT);
        staticAnalysisTools.forEach(tool => this.tools.set(tool.name, tool));
        // Register fix suggestion tools
        const fixSuggestionTools = registerFixSuggestionTools(FRAMEWORK_ROOT);
        fixSuggestionTools.forEach(tool => this.tools.set(tool.name, tool));
        // Register self-healing tools
        const healingTools = registerHealingTools(FRAMEWORK_ROOT);
        healingTools.forEach(tool => this.tools.set(tool.name, tool));
        console.error(`Registered ${this.tools.size} tools`);
    }
    registerResources() {
        // Register framework resources
        const frameworkResources = registerFrameworkResources(FRAMEWORK_ROOT);
        frameworkResources.forEach(resource => this.resources.set(resource.uri, resource));
        console.error(`Registered ${this.resources.size} resources`);
    }
    async run() {
        const transport = new StdioServerTransport();
        await this.server.connect(transport);
        console.error('Selenium Debug MCP Server running on stdio');
        console.error(`Framework root: ${FRAMEWORK_ROOT}`);
    }
}
// Start server
const server = new SeleniumDebugServer();
server.run().catch((error) => {
    console.error('Fatal error:', error);
    process.exit(1);
});
//# sourceMappingURL=index.js.map