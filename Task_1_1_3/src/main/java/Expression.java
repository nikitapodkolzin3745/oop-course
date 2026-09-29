/**
 * Represents an expression tree.
 */
public abstract class Expression {

    @Override
    public abstract String toString();

    public abstract Expression diff(String var);

    @Override
    public abstract boolean equals(Object other);

    public abstract Expression simplify();

    protected abstract Expression substitute(String var, Expression substituting);

    /**
     * Evaluates the expression after applying substitutions.
     *
     * @param substituting substitutions in the form "x = 1; y = 2"
     * @return evaluated integer value
     * @throws IllegalStateException if some variables remain unsubstituted
     */
    public int eval(String substituting) {
        Expression current = this;

        for (String assignment : substituting.split("; ")) {
            String[] parts = assignment.split(" = ", 2);
            current = current.substitute(
                    parts[0],
                    new Number(Integer.parseInt(parts[1]))
            );
        }

        Expression result = current.simplify();

        if (result instanceof Number n) {
            return n.getNumber();
        }

        throw new IllegalStateException("Not enough variables");
    }

    private static Expression parse(String term, int begin, int end) {
        if (Number.isNumber(term, begin, end)) {
            return new Number(Integer.parseInt(term, begin, end, 10));
        }

        if (Variable.isVariable(term, begin, end)) {
            return new Variable(term.substring(begin, end));
        }

        int depth = 0;

        for (int i = begin + 1; i < end - 1; i++) {
            char c = term.charAt(i);

            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (depth == 0 && Operation.isOperator(c)) {
                Expression left = parse(term, begin + 1, i);
                Expression right = parse(term, i + 1, end - 1);
                return Operation.fromChar(c, left, right);
            }
        }

        throw new IllegalArgumentException("Invalid expression");
    }

    /**
     * Parses a fully parenthesized expression.
     *
     * @param term expression text
     * @return parsed expression tree
     */
    public static Expression parse(String term) {
        return parse(term, 0, term.length());
    }
}
