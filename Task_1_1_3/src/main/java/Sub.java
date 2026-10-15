/**
 * Represents subtraction of two expressions.
 */
public class Sub extends Operation {

    /**
     * Creates a subtraction operation.
     *
     * @param left left operand
     * @param right right operand
     */
    public Sub(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression diff(String var) {
        return new Sub(left.diff(var), right.diff(var));
    }

    @Override
    public Expression simplify() {
        Expression newLeft = left.simplify();
        Expression newRight = right.simplify();

        if (left.equals(right)) {
            return new Number(0);
        }

        if (newLeft instanceof Number l && newRight instanceof Number r) {
            return new Number(l.getNumber() - r.getNumber());
        }

        return new Sub(newLeft, newRight);
    }

    @Override
    protected Expression substitute(String var, Expression substituting) {
        return new Sub(
                left.substitute(var, substituting),
                right.substitute(var, substituting)
        );
    }

    @Override
    protected String operator() {
        return "-";
    }
}
