package org.example;

/**
 * Domain is an enum — a type that can only be one of a fixed set of values.
 * Think of it like a category label. Every prompt we analyze will be assigned
 * exactly one of these three domains.
 *
 * Why use an enum instead of plain strings like "CODE" or "WRITING"?
 *   - Typo-safe: "WRITTING" won't compile, but a misspelled string would run silently.
 *   - Easy to use in switch/if statements.
 *   - You can attach extra data to each constant (see the 'label' field below).
 */
public enum Domain {

    // Each constant calls the constructor below with a human-readable label.
    CODE("Code / Programming"),
    WRITING("Creative / Professional Writing"),
    GENERAL("General Purpose");

    // A display label stored inside each enum value.
    private final String label;

    // Enum constructor — called automatically when each constant is created.
    Domain(String label) {
        this.label = label;
    }

    // Getter so other classes can read the label.
    public String getLabel() {
        return label;
    }
}
