/**
 * Represents addition of two expressions.
 */
public class Add extends Operation {

    /**
     * Creates an addition operation.
     *
     * @param left left operand
     * @param right right operand
     */
    public Add(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression diff(String var) {
        return new Add(left.diff(var), right.diff(var));
    }

    @Override
    public Expression simplify() {
        Expression newLeft = left.simplify();
        Expression newRight = right.simplify();

        if (newLeft instanceof Number l && l.getNumber() == 0) {
            return newRight;
        }

        if (newRight instanceof Number r && r.getNumber() == 0) {
            return newLeft;
        }

        if (newLeft instanceof Number l && newRight instanceof Number r) {
            return new Number(l.getNumber() + r.getNumber());
        }

        return new Add(newLeft, newRight);
    }

    @Override
    protected Expression substitute(String var, Expression substituting) {
        return new Add(
                left.substitute(var, substituting),
                right.substitute(var, substituting)
        );
    }

    @Override
    protected String operator() {
        return "+";
    }
}
