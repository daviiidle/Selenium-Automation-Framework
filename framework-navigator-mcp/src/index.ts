#!/usr/bin/env node

/**
 * Framework Navigator MCP Server
 * Provides intelligent framework navigation and indexing with minimal token usage
 */

import { Server } from '@modelcontextprotocol/sdk/server/index.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from '@modelcontextprotocol/sdk/types.js';
import * as dotenv from 'dotenv';
import path from 'path';
import { fileURLToPath } from 'url';

// Import tools
import { registerNavigationTools } from './tools/navigation-tools.js';
import { registerSearchTools } from './tools/search-tools.js';
import { registerRelationshipTools } from './tools/relationship-tools.js';

// Setup
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
dotenv.config({ path: path.join(__dirname, '..', '.env') });

// Server configuration
const SERVER_NAME = 'framework-navigator-mcp';
const SERVER_VERSION = '1.0.0';

// Framework root detection
const FRAMEWORK_ROOT = process.env.FRAMEWORK_ROOT || path.join(__dirname, '..', '..');

class FrameworkNavigatorServer {
  private server: Server;
  private tools: Map<string, any>;

  constructor() {
    this.server = new Server(
      {
        name: SERVER_NAME,
        version: SERVER_VERSION,
      },
      {
        capabilities: {
          tools: {},
        },
      }
    );

    this.tools = new Map();

    this.setupHandlers();
    this.registerTools();
  }

  private setupHandlers() {
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
      } catch (error) {
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
  }

  private registerTools() {
    // Register navigation tools (always enabled - core functionality)
    if (process.env.ENABLE_NAVIGATION_TOOLS !== 'false') {
      const navigationTools = registerNavigationTools(FRAMEWORK_ROOT);
      navigationTools.forEach(tool => this.tools.set(tool.name, tool));
      console.error(`✓ Registered ${navigationTools.length} navigation tools`);
    }

    // Register search tools (basic or full mode)
    const searchMode = process.env.ENABLE_SEARCH_TOOLS || 'full';
    if (searchMode !== 'false') {
      const searchTools = registerSearchTools(FRAMEWORK_ROOT, searchMode);
      searchTools.forEach(tool => this.tools.set(tool.name, tool));
      console.error(`✓ Registered ${searchTools.length} search tools (${searchMode} mode)`);
    }

    // Register relationship tools (optional - token-heavy)
    if (process.env.ENABLE_RELATIONSHIP_TOOLS === 'true') {
      const relationshipTools = registerRelationshipTools(FRAMEWORK_ROOT);
      relationshipTools.forEach(tool => this.tools.set(tool.name, tool));
      console.error(`✓ Registered ${relationshipTools.length} relationship tools`);
    } else {
      console.error(`✗ Relationship tools disabled (saves ~5k tokens)`);
    }

    console.error(`Total: ${this.tools.size} tools registered`);
  }

  async run() {
    const transport = new StdioServerTransport();
    await this.server.connect(transport);
    console.error('Framework Navigator MCP Server running on stdio');
    console.error(`Framework root: ${FRAMEWORK_ROOT}`);
  }
}

// Start server
const server = new FrameworkNavigatorServer();
server.run().catch((error) => {
  console.error('Fatal error:', error);
  process.exit(1);
});
