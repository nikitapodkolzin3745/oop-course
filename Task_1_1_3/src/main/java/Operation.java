import java.util.Map;
import java.util.function.BiFunction;

/**
 * Base class for binary operations.
 */
public abstract class Operation extends Expression {
    protected final Expression left;
    protected final Expression right;

    /**
     * Creates a binary operation.
     *
     * @param left left operand
     * @param right right operand
     */
    public Operation(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    protected abstract String operator();

    private static final Map<Character,
            BiFunction<Expression, Expression, Operation>> OPS =
            Map.of(
                    '+', Add::new,
                    '-', Sub::new,
                    '*', Mul::new,
                    '/', Div::new
            );

    /**
     * Checks whether a character is a supported operator.
     *
     * @param c character to check
     * @return true if the character is an operator
     */
    public static boolean isOperator(char c) {
        return OPS.containsKey(c);
    }

    private static final Map<Character, Integer> PRIORITY = Map.of(
        '+', 1,
        '-', 1,
        '*', 2,
        '/', 2
    );

    /**
     * Returns a operation priority.
     *
     * @param c operation symbol
     */
    public static int priority(char c) {
        Integer p = PRIORITY.get(c);
        if (p == null) {
            throw new IllegalArgumentException("Unknown operator");
        }
        return p;
    }

    /**
     * Creates an operation corresponding to the given operator.
     *
     * @param c operator character
     * @param l left operand
     * @param r right operand
     * @return created operation
     */
    public static Operation fromChar(char c, Expression l, Expression r) {
        BiFunction<Expression, Expression, Operation> ctor = OPS.get(c);

        if (ctor == null) {
            throw new IllegalArgumentException("Unknown operator: " + c);
        }

        return ctor.apply(l, r);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Operation otherExpr)) {
            return false;
        }

        return getClass() == otherExpr.getClass()
                && left.equals(otherExpr.left)
                && right.equals(otherExpr.right);
    }

    /**
     * Returns the left operand.
     *
     * @return left operand
     */
    public Expression getLeft() {
        return left;
    }

    /**
     * Returns the right operand.
     *
     * @return right operand
     */
    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + operator() + right + ")";
    }
}
