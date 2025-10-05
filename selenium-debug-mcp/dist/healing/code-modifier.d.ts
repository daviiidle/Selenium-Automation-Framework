/**
 * Code Modifier
 * Safely modifies framework files (Java, JSON, XML) with backup capabilities
 */
export interface ModificationResult {
    success: boolean;
    filePath: string;
    backupPath?: string;
    changes: string;
    error?: string;
}
export declare class CodeModifier {
    private frameworkRoot;
    private backupDir;
    constructor(frameworkRoot: string);
    /**
     * Update selector in JSON file
     */
    updateSelectorJson(selectorFile: string, selectorName: string, newSelector: {
        type: string;
        value: string;
    }): Promise<ModificationResult>;
    /**
     * Update Page Object selector in Java file
     */
    updatePageObjectSelector(pageClassName: string, selectorVariable: string, newSelectorType: string, newSelectorValue: string): Promise<ModificationResult>;
    /**
     * Update wait time in Page Object or BaseTest
     */
    updateWaitTime(javaFile: string, currentSeconds: number, newSeconds: number): Promise<ModificationResult>;
    /**
     * Update TestNG configuration
     */
    updateTestNGConfig(configFile: string, setting: string, newValue: string | number): Promise<ModificationResult>;
    /**
     * Backup a file before modification
     */
    private backupFile;
    /**
     * Restore file from backup
     */
    restoreFromBackup(backupPath: string, targetPath: string): Promise<boolean>;
    /**
     * List all backups
     */
    listBackups(): Promise<string[]>;
    /**
     * Clear old backups (keep last N)
     */
    clearOldBackups(keepLast?: number): Promise<number>;
}
//# sourceMappingURL=code-modifier.d.ts.map