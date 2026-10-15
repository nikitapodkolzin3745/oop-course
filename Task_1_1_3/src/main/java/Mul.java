/**
 * Represents multiplication of two expressions.
 */
public class Mul extends Operation {

    /**
     * Creates a multiplication operation.
     *
     * @param left left operand
     * @param right right operand
     */
    public Mul(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression diff(String var) {
        return new Add(
                new Mul(left.diff(var), right),
                new Mul(left, right.diff(var))
        );
    }

    @Override
    public Expression simplify() {
        Expression newLeft = left.simplify();
        Expression newRight = right.simplify();

        if ((newLeft instanceof Number l && l.getNumber() == 0)
                || (newRight instanceof Number r && r.getNumber() == 0)) {
            return new Number(0);
        }

        if (newLeft instanceof Number l && l.getNumber() == 1) {
            return newRight;
        }

        if (newRight instanceof Number r && r.getNumber() == 1) {
            return newLeft;
        }

        if (newLeft instanceof Number l && newRight instanceof Number r) {
            return new Number(l.getNumber() * r.getNumber());
        }

        return new Mul(newLeft, newRight);
    }

    @Override
    protected Expression substitute(String var, Expression substituting) {
        return new Mul(
                left.substitute(var, substituting),
                right.substitute(var, substituting)
        );
    }

    @Override
    protected String operator() {
        return "*";
    }
}
