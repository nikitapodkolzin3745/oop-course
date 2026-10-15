/**
 * Represents a variable.
 */
public class Variable extends Expression {
    private final String name;

    /**
     * Creates a variable.
     *
     * @param name variable name
     */
    public Variable(String name) {
        this.name = name;
    }

    @Override
    public Expression diff(String var) {
        return new Number(name.equals(var) ? 1 : 0);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Variable otherExpr)) {
            return false;
        }

        return name.equals(otherExpr.name);
    }

    @Override
    public Expression simplify() {
        return this;
    }

    /**
     * Checks whether a substring is a valid Java identifier.
     *
     * @param x source string
     * @param begin inclusive start index
     * @param end exclusive end index
     * @return true if the substring is a valid variable name
     */
    public static boolean isVariable(String x, int begin, int end) {
        if (begin == end) {
            return false;
        }

        if (!Character.isJavaIdentifierStart(x.charAt(begin))) {
            return false;
        }

        for (int i = begin + 1; i < end; i++) {
            if (!Character.isJavaIdentifierPart(x.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected Expression substitute(String var, Expression substituting) {
        if (var.equals(name)) {
            return substituting;
        }

        return this;
    }

    /**
     * Returns the variable name.
     *
     * @return variable name
     */
    public String getName() {
        return name;
    }
}
