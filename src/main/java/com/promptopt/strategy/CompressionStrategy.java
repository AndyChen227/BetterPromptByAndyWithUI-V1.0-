package com.promptopt.strategy;

/**
 * CompressionStrategy is a Java interface — it defines a CONTRACT.
 *
 * Any class that "implements" this interface MUST provide a compress() method.
 * This is the Strategy Pattern: we define one shared interface, then write
 * multiple swappable implementations. The caller (Main.java) doesn't need to
 * know WHICH strategy it's using — it just calls compress().
 *
 * Why use an interface here instead of a base class?
 *   Each strategy is completely independent — they share no logic, only a shape.
 *   An interface enforces the shape without forcing shared implementation.
 */
public interface CompressionStrategy {

    /**
     * Takes a prompt and returns a compressed (shorter, cleaner) version.
     *
     * @param prompt  the original user prompt
     * @return        the compressed prompt
     */
    String compress(String prompt);

    /**
     * Returns a short name for this strategy, used in the terminal menu.
     * Default methods in interfaces provide a body — implementing classes
     * can override this, but don't have to.
     */
    default String name() {
        // getSimpleName() returns "RemoveRedundancyStrategy", etc.
        return this.getClass().getSimpleName();
    }
}
