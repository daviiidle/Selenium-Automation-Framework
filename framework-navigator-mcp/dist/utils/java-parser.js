/**
 * Java Parser Utilities
 * Simple regex-based Java parsing for extracting class/method information
 */
export class JavaParser {
    /**
     * Parse Java file content and extract class information
     */
    static parseJavaFile(content, filePath) {
        const lines = content.split('\n');
        // Extract package
        const packageMatch = content.match(/package\s+([\w.]+)\s*;/);
        const packageName = packageMatch ? packageMatch[1] : '';
        // Extract class name
        const classMatch = content.match(/(?:public\s+)?(?:abstract\s+)?class\s+(\w+)/);
        if (!classMatch) {
            return null; // Not a class file (might be interface)
        }
        const className = classMatch[1];
        // Extract extends
        const extendsMatch = content.match(/class\s+\w+\s+extends\s+([\w<>]+)/);
        const extendsClass = extendsMatch ? extendsMatch[1] : undefined;
        // Extract implements
        const implementsMatch = content.match(/implements\s+([\w\s,<>]+)/);
        const implementsInterfaces = implementsMatch
            ? implementsMatch[1].split(',').map(i => i.trim())
            : [];
        // Extract imports
        const imports = this.extractImports(content);
        // Extract class annotations
        const classAnnotations = this.extractClassAnnotations(lines);
        // Extract methods
        const methods = this.extractMethods(lines);
        // Extract fields
        const fields = this.extractFields(lines);
        // Check if abstract
        const isAbstract = /abstract\s+class/.test(content);
        return {
            className,
            packageName,
            extendsClass,
            implementsInterfaces,
            imports,
            methods,
            fields,
            annotations: classAnnotations,
            isAbstract,
            lineCount: lines.length,
        };
    }
    /**
     * Extract imports from Java content
     */
    static extractImports(content) {
        const imports = [];
        const importRegex = /import\s+([\w.]+)\s*;/g;
        let match;
        while ((match = importRegex.exec(content)) !== null) {
            imports.push(match[1]);
        }
        return imports;
    }
    /**
     * Extract class-level annotations
     */
    static extractClassAnnotations(lines) {
        const annotations = [];
        for (let i = 0; i < lines.length; i++) {
            const line = lines[i].trim();
            if (line.startsWith('@') && !line.startsWith('@Override')) {
                const annotationMatch = line.match(/@(\w+)/);
                if (annotationMatch) {
                    annotations.push(annotationMatch[1]);
                }
            }
            if (line.includes('class ')) {
                break; // Stop at class definition
            }
        }
        return annotations;
    }
    /**
     * Extract methods from Java file
     */
    static extractMethods(lines) {
        const methods = [];
        for (let i = 0; i < lines.length; i++) {
            const line = lines[i].trim();
            // Match method definition
            const methodMatch = line.match(/(public|private|protected)?\s*(static)?\s*([\w<>]+)\s+(\w+)\s*\(([^)]*)\)/);
            if (methodMatch && !line.includes('class ') && !line.includes('new ')) {
                const [, visibility, staticMod, returnType, methodName, params] = methodMatch;
                // Extract annotations for this method
                const annotations = [];
                for (let j = i - 1; j >= Math.max(0, i - 5); j--) {
                    const prevLine = lines[j].trim();
                    if (prevLine.startsWith('@')) {
                        const annotMatch = prevLine.match(/@(\w+)/);
                        if (annotMatch) {
                            annotations.unshift(annotMatch[1]);
                        }
                    }
                    else if (prevLine && !prevLine.startsWith('//')) {
                        break;
                    }
                }
                methods.push({
                    name: methodName,
                    returnType: returnType || 'void',
                    parameters: params || '',
                    lineNumber: i + 1,
                    isPublic: visibility === 'public' || !visibility,
                    isStatic: staticMod === 'static',
                    annotations,
                });
            }
        }
        return methods;
    }
    /**
     * Extract fields from Java file
     */
    static extractFields(lines) {
        const fields = [];
        for (let i = 0; i < lines.length; i++) {
            const line = lines[i].trim();
            // Match field definition (avoid methods)
            const fieldMatch = line.match(/(private|protected|public)?\s*(static)?\s*(final)?\s*([\w<>]+)\s+(\w+)\s*[=;]/);
            if (fieldMatch && !line.includes('(') && !line.includes('return')) {
                const [, visibility, staticMod, , type, fieldName] = fieldMatch;
                // Extract value if present
                const valueMatch = line.match(/=\s*(.+?);/);
                fields.push({
                    name: fieldName,
                    type,
                    lineNumber: i + 1,
                    isPrivate: visibility === 'private',
                    isStatic: staticMod === 'static',
                    value: valueMatch ? valueMatch[1].trim() : undefined,
                });
            }
        }
        return fields;
    }
    /**
     * Extract selector definitions from Page Object
     */
    static extractSelectors(content) {
        const selectors = [];
        const lines = content.split('\n');
        for (let i = 0; i < lines.length; i++) {
            const line = lines[i];
            // Match By.cssSelector, By.id, By.xpath, etc.
            const patterns = [
                { regex: /By\.cssSelector\s*\(\s*"([^"]+)"\s*\)/, type: 'css' },
                { regex: /By\.xpath\s*\(\s*"([^"]+)"\s*\)/, type: 'xpath' },
                { regex: /By\.id\s*\(\s*"([^"]+)"\s*\)/, type: 'id' },
                { regex: /By\.name\s*\(\s*"([^"]+)"\s*\)/, type: 'name' },
                { regex: /By\.className\s*\(\s*"([^"]+)"\s*\)/, type: 'className' },
            ];
            for (const { regex, type } of patterns) {
                const match = line.match(regex);
                if (match) {
                    const varMatch = line.match(/(\w+)\s*=/);
                    selectors.push({
                        name: varMatch ? varMatch[1] : 'unknown',
                        selector: match[1],
                        type,
                        lineNumber: i + 1,
                    });
                }
            }
        }
        return selectors;
    }
    /**
     * Check if file is a Page Object
     */
    static isPageObject(classInfo) {
        return classInfo.extendsClass?.includes('BasePage') ||
            classInfo.className.endsWith('Page');
    }
    /**
     * Check if file is a Test class
     */
    static isTestClass(classInfo) {
        return classInfo.extendsClass?.includes('BaseTest') ||
            classInfo.className.endsWith('Tests') ||
            classInfo.methods.some(m => m.annotations.includes('Test'));
    }
}
//# sourceMappingURL=java-parser.js.map