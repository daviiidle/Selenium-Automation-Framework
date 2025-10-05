/**
 * Code Modifier
 * Safely modifies framework files (Java, JSON, XML) with backup capabilities
 */
import { promises as fs } from 'fs';
import path from 'path';
export class CodeModifier {
    frameworkRoot;
    backupDir;
    constructor(frameworkRoot) {
        this.frameworkRoot = frameworkRoot;
        this.backupDir = path.join(frameworkRoot, '.mcp-backups');
    }
    /**
     * Update selector in JSON file
     */
    async updateSelectorJson(selectorFile, selectorName, newSelector) {
        const filePath = path.join(this.frameworkRoot, 'src/main/resources/selectors', selectorFile);
        try {
            // Backup first
            const backupPath = await this.backupFile(filePath);
            // Read current content
            const content = await fs.readFile(filePath, 'utf-8');
            const selectors = JSON.parse(content);
            // Update selector
            if (!selectors[selectorName]) {
                return {
                    success: false,
                    filePath,
                    changes: '',
                    error: `Selector '${selectorName}' not found in file`,
                };
            }
            const oldSelector = selectors[selectorName];
            selectors[selectorName] = {
                type: newSelector.type,
                selector: newSelector.value,
            };
            // Write back with formatting
            await fs.writeFile(filePath, JSON.stringify(selectors, null, 2), 'utf-8');
            return {
                success: true,
                filePath,
                backupPath,
                changes: `Updated ${selectorName}: ${JSON.stringify(oldSelector)} → ${JSON.stringify(newSelector)}`,
            };
        }
        catch (error) {
            return {
                success: false,
                filePath,
                changes: '',
                error: error instanceof Error ? error.message : String(error),
            };
        }
    }
    /**
     * Update Page Object selector in Java file
     */
    async updatePageObjectSelector(pageClassName, selectorVariable, newSelectorType, newSelectorValue) {
        const filePath = path.join(this.frameworkRoot, 'src/main/java/com/demowebshop/automation/pages', `${pageClassName}.java`);
        try {
            const backupPath = await this.backupFile(filePath);
            let content = await fs.readFile(filePath, 'utf-8');
            // Find and replace the selector
            const patterns = [
                {
                    regex: new RegExp(`(${selectorVariable}\\s*=\\s*By\\.)\\w+\\("([^"]+)"\\)`, 'g'),
                    replacement: `$1${newSelectorType}("${newSelectorValue}")`,
                },
                {
                    regex: new RegExp(`(private\\s+By\\s+${selectorVariable}\\s*=\\s*By\\.)\\w+\\("([^"]+)"\\)`, 'g'),
                    replacement: `$1${newSelectorType}("${newSelectorValue}")`,
                },
            ];
            let modified = false;
            let oldValue = '';
            for (const pattern of patterns) {
                if (pattern.regex.test(content)) {
                    // Capture old value
                    const match = content.match(pattern.regex);
                    if (match) {
                        oldValue = match[0];
                    }
                    content = content.replace(pattern.regex, pattern.replacement);
                    modified = true;
                    break;
                }
            }
            if (!modified) {
                return {
                    success: false,
                    filePath,
                    changes: '',
                    error: `Selector variable '${selectorVariable}' not found in ${pageClassName}`,
                };
            }
            await fs.writeFile(filePath, content, 'utf-8');
            return {
                success: true,
                filePath,
                backupPath,
                changes: `Updated ${selectorVariable} in ${pageClassName}: ${oldValue} → By.${newSelectorType}("${newSelectorValue}")`,
            };
        }
        catch (error) {
            return {
                success: false,
                filePath,
                changes: '',
                error: error instanceof Error ? error.message : String(error),
            };
        }
    }
    /**
     * Update wait time in Page Object or BaseTest
     */
    async updateWaitTime(javaFile, currentSeconds, newSeconds) {
        const possiblePaths = [
            path.join(this.frameworkRoot, 'src/test/java/base', `${javaFile}.java`),
            path.join(this.frameworkRoot, 'src/main/java/com/demowebshop/automation/pages', `${javaFile}.java`),
        ];
        for (const filePath of possiblePaths) {
            try {
                await fs.access(filePath);
                const backupPath = await this.backupFile(filePath);
                let content = await fs.readFile(filePath, 'utf-8');
                // Replace Duration.ofSeconds patterns
                const pattern = new RegExp(`Duration\\.ofSeconds\\(${currentSeconds}\\)`, 'g');
                if (!pattern.test(content)) {
                    continue;
                }
                content = content.replace(pattern, `Duration.ofSeconds(${newSeconds})`);
                await fs.writeFile(filePath, content, 'utf-8');
                return {
                    success: true,
                    filePath,
                    backupPath,
                    changes: `Updated wait time in ${javaFile}: ${currentSeconds}s → ${newSeconds}s`,
                };
            }
            catch {
                continue;
            }
        }
        return {
            success: false,
            filePath: '',
            changes: '',
            error: `Could not find or update ${javaFile}`,
        };
    }
    /**
     * Update TestNG configuration
     */
    async updateTestNGConfig(configFile, setting, newValue) {
        const filePath = path.join(this.frameworkRoot, 'src/test/resources/config', configFile);
        try {
            const backupPath = await this.backupFile(filePath);
            let content = await fs.readFile(filePath, 'utf-8');
            // Update XML attribute
            const pattern = new RegExp(`${setting}="[^"]*"`, 'g');
            if (!pattern.test(content)) {
                return {
                    success: false,
                    filePath,
                    changes: '',
                    error: `Setting '${setting}' not found in ${configFile}`,
                };
            }
            const oldMatch = content.match(pattern);
            const oldValue = oldMatch ? oldMatch[0] : '';
            content = content.replace(pattern, `${setting}="${newValue}"`);
            await fs.writeFile(filePath, content, 'utf-8');
            return {
                success: true,
                filePath,
                backupPath,
                changes: `Updated ${setting} in ${configFile}: ${oldValue} → ${setting}="${newValue}"`,
            };
        }
        catch (error) {
            return {
                success: false,
                filePath,
                changes: '',
                error: error instanceof Error ? error.message : String(error),
            };
        }
    }
    /**
     * Backup a file before modification
     */
    async backupFile(filePath) {
        await fs.mkdir(this.backupDir, { recursive: true });
        const fileName = path.basename(filePath);
        const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
        const backupPath = path.join(this.backupDir, `${fileName}.${timestamp}.backup`);
        const content = await fs.readFile(filePath);
        await fs.writeFile(backupPath, content);
        return backupPath;
    }
    /**
     * Restore file from backup
     */
    async restoreFromBackup(backupPath, targetPath) {
        try {
            const content = await fs.readFile(backupPath);
            await fs.writeFile(targetPath, content);
            return true;
        }
        catch {
            return false;
        }
    }
    /**
     * List all backups
     */
    async listBackups() {
        try {
            const files = await fs.readdir(this.backupDir);
            return files.filter(f => f.endsWith('.backup'));
        }
        catch {
            return [];
        }
    }
    /**
     * Clear old backups (keep last N)
     */
    async clearOldBackups(keepLast = 10) {
        try {
            const backups = await this.listBackups();
            backups.sort().reverse(); // Most recent first
            const toDelete = backups.slice(keepLast);
            let deleted = 0;
            for (const backup of toDelete) {
                const filePath = path.join(this.backupDir, backup);
                await fs.unlink(filePath);
                deleted++;
            }
            return deleted;
        }
        catch {
            return 0;
        }
    }
}
//# sourceMappingURL=code-modifier.js.map