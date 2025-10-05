/**
 * Framework Index
 * In-memory cache of framework structure for fast lookups
 */
export class FrameworkIndex {
    classes = new Map();
    methods = new Map();
    selectors = new Map();
    selectorJsons = new Map();
    testDependencies = new Map();
    // Category maps for quick filtering
    pageObjects = new Set();
    testClasses = new Set();
    baseClasses = new Set();
    // Index status
    indexed = false;
    indexTime = null;
    /**
     * Add a class to the index
     */
    addClass(className, classInfo, filePath, category) {
        this.classes.set(className, { classInfo, filePath, category });
        // Update category sets
        if (category === 'page-object') {
            this.pageObjects.add(className);
        }
        else if (category === 'test') {
            this.testClasses.add(className);
        }
        else if (category === 'base') {
            this.baseClasses.add(className);
        }
        // Index methods
        classInfo.methods.forEach(method => {
            const methodName = method.name;
            if (!this.methods.has(methodName)) {
                this.methods.set(methodName, []);
            }
            this.methods.get(methodName).push({
                className,
                methodInfo: method,
                filePath,
            });
        });
    }
    /**
     * Add a selector to the index
     */
    addSelector(selector) {
        const selectorName = selector.name;
        if (!this.selectors.has(selectorName)) {
            this.selectors.set(selectorName, []);
        }
        this.selectors.get(selectorName).push(selector);
    }
    /**
     * Add selector JSON entries
     */
    addSelectorJson(fileName, entries) {
        this.selectorJsons.set(fileName, entries);
    }
    /**
     * Add test dependencies
     */
    addTestDependency(testClass, dependency) {
        this.testDependencies.set(testClass, dependency);
    }
    /**
     * Get class by name
     */
    getClass(className) {
        return this.classes.get(className);
    }
    /**
     * Find classes by pattern
     */
    findClasses(pattern) {
        const regex = new RegExp(pattern, 'i');
        return Array.from(this.classes.values()).filter(c => regex.test(c.classInfo.className));
    }
    /**
     * Get all Page Objects
     */
    getPageObjects() {
        return Array.from(this.pageObjects).map(name => this.classes.get(name));
    }
    /**
     * Get all Test classes
     */
    getTestClasses() {
        return Array.from(this.testClasses).map(name => this.classes.get(name));
    }
    /**
     * Get all classes
     */
    getAllClasses() {
        return Array.from(this.classes.values());
    }
    /**
     * Find methods by name
     */
    findMethods(methodName) {
        return this.methods.get(methodName) || [];
    }
    /**
     * Find methods by pattern
     */
    findMethodsByPattern(pattern) {
        const regex = new RegExp(pattern, 'i');
        const results = [];
        this.methods.forEach((methods) => {
            methods.forEach(method => {
                if (regex.test(method.methodInfo.name)) {
                    results.push(method);
                }
            });
        });
        return results;
    }
    /**
     * Find selectors
     */
    findSelectors(selectorName) {
        return this.selectors.get(selectorName) || [];
    }
    /**
     * Get all selectors for a Page Object
     */
    getSelectorsForPageObject(pageObjectName) {
        const allSelectors = [];
        this.selectors.forEach(selectorList => {
            selectorList.forEach(selector => {
                if (selector.pageObject === pageObjectName) {
                    allSelectors.push(selector);
                }
            });
        });
        return allSelectors;
    }
    /**
     * Get selector JSON entries
     */
    getSelectorJson(fileName) {
        return this.selectorJsons.get(fileName);
    }
    /**
     * Get all selector JSON files
     */
    getAllSelectorJsons() {
        return Array.from(this.selectorJsons.keys());
    }
    /**
     * Get test dependencies
     */
    getTestDependencies(testClass) {
        return this.testDependencies.get(testClass);
    }
    /**
     * Get framework statistics
     */
    getStats() {
        return {
            totalClasses: this.classes.size,
            pageObjects: this.pageObjects.size,
            testClasses: this.testClasses.size,
            baseClasses: this.baseClasses.size,
            totalMethods: Array.from(this.methods.values()).reduce((sum, m) => sum + m.length, 0),
            totalSelectors: Array.from(this.selectors.values()).reduce((sum, s) => sum + s.length, 0),
            selectorJsonFiles: this.selectorJsons.size,
            indexed: this.indexed,
            indexTime: this.indexTime?.toISOString(),
        };
    }
    /**
     * Mark index as complete
     */
    markIndexed() {
        this.indexed = true;
        this.indexTime = new Date();
    }
    /**
     * Check if indexed
     */
    isIndexed() {
        return this.indexed;
    }
    /**
     * Clear index
     */
    clear() {
        this.classes.clear();
        this.methods.clear();
        this.selectors.clear();
        this.selectorJsons.clear();
        this.testDependencies.clear();
        this.pageObjects.clear();
        this.testClasses.clear();
        this.baseClasses.clear();
        this.indexed = false;
        this.indexTime = null;
    }
}
//# sourceMappingURL=FrameworkIndex.js.map