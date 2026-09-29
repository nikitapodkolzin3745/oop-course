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
        if (!(other instanceof Expression otherExpr)) {
            return false;
        }

        return otherExpr instanceof Number o && number == o.getNumber();
    }

    @Override
    public Expression simplify() {
        return this;
    }

    /**
     * Checks whether a substring consists only of decimal digits.
     *
     * @param x source string
     * @param begin inclusive start index
     * @param end exclusive end index
     * @return true if the substring is a decimal number
     */
    public static boolean isNumber(String x, int begin, int end) {
        if (begin == end) {
            return false;
        }

        for (int i = begin; i < end; i++) {
            char c = x.charAt(i);

            if (c < '0' || c > '9') {
                return false;
            }
        }

        return true;
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
