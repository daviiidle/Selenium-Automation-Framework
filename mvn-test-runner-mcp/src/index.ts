#!/usr/bin/env node

/**
 * Maven Test Runner MCP Server
 * Executes Maven tests and extracts critical failure details for automated fixing
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
import { registerMavenTestTools } from './tools/maven-test-tools.js';

// Setup
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
dotenv.config({ path: path.join(__dirname, '..', '.env') });

// Server configuration
const SERVER_NAME = 'mvn-test-runner';
const SERVER_VERSION = '1.0.0';

// Framework root detection
const FRAMEWORK_ROOT = process.env.FRAMEWORK_ROOT || path.join(__dirname, '..', '..');

class MavenTestRunnerServer {
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
    // Register Maven test tools
    const mavenTestTools = registerMavenTestTools(FRAMEWORK_ROOT);
    mavenTestTools.forEach(tool => this.tools.set(tool.name, tool));

    console.error(`Registered ${this.tools.size} Maven test tools`);
  }

  async run() {
    const transport = new StdioServerTransport();
    await this.server.connect(transport);
    console.error('Maven Test Runner MCP Server running on stdio');
    console.error(`Framework root: ${FRAMEWORK_ROOT}`);
  }
}

// Start server
const server = new MavenTestRunnerServer();
server.run().catch((error) => {
  console.error('Fatal error:', error);
  process.exit(1);
});
