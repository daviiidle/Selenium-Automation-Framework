package com.demowebshop.automation.enums;

/**
 * Enumeration for supported browser types
 */
public enum BrowserType {
    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("edge"),
    SAFARI("safari");

    private final String browserName;

    BrowserType(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserName() {
        return browserName;
    }

    /**
     * Get BrowserType from string value
     * @param browserName String representation of browser
     * @return BrowserType enum
     */
    public static BrowserType fromString(String browserName) {
        for (BrowserType type : BrowserType.values()) {
            if (type.browserName.equalsIgnoreCase(browserName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown browser type: " + browserName);
    }

    @Override
    public String toString() {
        return browserName;
    }
}