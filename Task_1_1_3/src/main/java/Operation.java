import java.util.Map;
import java.util.function.BiFunction;

public abstract class Operation extends Expression {
    protected final Expression left;
    protected final Expression right;

    public Operation(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    protected abstract String operator();

    private static final Map<Character, BiFunction<Expression, Expression, Operation>> OPS =
        Map.of(
            '+', Add::new,
            '-', Sub::new,
            '*', Mul::new,
            '/', Div::new
        );

    public static boolean isOperator(char c) {
        return OPS.containsKey(c);
    }

    public static Operation fromChar(char c, Expression l, Expression r) {
        BiFunction<Expression, Expression, Operation> ctor = OPS.get(c);
        if (ctor == null) {
            throw new IllegalArgumentException("Unknown operator: " + c);
        }
        return ctor.apply(l, r);
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof Expression otherExpr))
            return false;
        return otherExpr instanceof Operation o && left.equals(o.getLeft()) && right.equals(o.getRight());
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + operator() + right + ")";
    }
}