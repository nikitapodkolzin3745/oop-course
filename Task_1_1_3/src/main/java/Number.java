/**
 * Represents an integer constant.
 */
public class Number extends Expression {
    private final int number;

    /**
     * Creates a constant expression.
     *
     * @param number constant value
     */
    public Number(int number) {
        this.number = number;
    }

    @Override
    public Expression diff(String var) {
        return new Number(0);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Number otherExpr)) {
            return false;
        }

        return number == otherExpr.number;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    @Override
    protected Expression substitute(String var, Expression substituting) {
        return this;
    }

    /**
     * Returns the stored integer value.
     *
     * @return constant value
     */
    public int getNumber() {
        return number;
    }
}
