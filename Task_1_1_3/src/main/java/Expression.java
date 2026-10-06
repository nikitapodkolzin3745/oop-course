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

    protected abstract Expression substitute(
            String var,
            Expression substituting
    );

    /**
     * Evaluates the expression after applying substitutions.
     *
     * @param substituting substitutions in the form "x = 1; y = 2"
     * @return evaluated integer value
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

        throw new IllegalArgumentException("Not enough variables");
    }

    /**
     * Parses an expression.
     *
     * @param term expression text
     * @return parsed expression tree
     */
    public static Expression parse(String term) {
        return new Parser(term.replace(" ", "")).parse();
    }

    /**
     * Recursive-descent parser with operator priorities.
     */
    private static class Parser {
        private final String expression;
        private int pos = 0;

        Parser(String expression) {
            this.expression = expression;
        }

        Expression parse() {
            Expression result = parseExpression(1);

            if (pos != expression.length()) {
                throw new IllegalArgumentException(
                        "Unexpected character: " + expression.charAt(pos)
                );
            }

            return result;
        }

        private Expression parseExpression(int minPriority) {
            Expression left = parseFactor();

            while (pos < expression.length()) {
                char op = expression.charAt(pos);

                if (!Operation.isOperator(op)) {
                    break;
                }

                int priority = Operation.priority(op);

                if (priority < minPriority) {
                    break;
                }

                pos++;

                Expression right = parseExpression(priority + 1);
                left = Operation.fromChar(op, left, right);
            }

            return left;
        }

        private Expression parseFactor() {
            if (pos >= expression.length()) {
                throw new IllegalArgumentException("Unexpected end");
            }

            if (expression.charAt(pos) == '(') {
                pos++;

                Expression inside = parseExpression(1);

                if (pos >= expression.length()
                        || expression.charAt(pos) != ')') {
                    throw new IllegalArgumentException("Missing ')'");
                }

                pos++;
                return inside;
            }

            int begin = pos;

            while (pos < expression.length()
                    && !Operation.isOperator(expression.charAt(pos))
                    && expression.charAt(pos) != '('
                    && expression.charAt(pos) != ')') {
                pos++;
            }

            if (isNumber(expression, begin, pos)) {
                return new Number(
                        Integer.parseInt(expression, begin, pos, 10)
                );
            }

            if (Variable.isVariable(expression, begin, pos)) {
                return new Variable(expression.substring(begin, pos));
            }

            throw new IllegalArgumentException("Invalid token");
        }

        private boolean isNumber(String value, int begin, int end) {
            if (begin == end) {
                return false;
            }

            for (int i = begin; i < end; i++) {
                char c = value.charAt(i);

                if (c < '0' || c > '9') {
                    return false;
                }
            }

            return true;
        }
    }
}