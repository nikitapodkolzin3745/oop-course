public class Number extends Expression {
    private final int number;

    public Number(int number) {
        this.number = number;
    }

    @Override
    public Expression diff(String var) {
        return new Number(0);
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof Expression otherExpr))
            return false;
        return otherExpr instanceof Number o && number == o.getNumber();
    }

    @Override
    public Expression simplify() {
        return this;
    }

    public static boolean isNumber(String x, int begin, int end) {
        if (begin == end) return false;

        for (int i = begin; i < end; i++) {
            char c = x.charAt(i);
            if (c < '0' || c > '9')
                return false;
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

    public int getNumber() {
        return number;
    }
}