public class Div extends Operation {

    public Div(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Expression diff(String var) {
        return new Div(
            new Sub(
                new Mul(left.diff(var), right), 
                new Mul(left, right.diff(var))
            ), 
            new Mul(right, right)
        );
    }

    @Override
    public Expression simplify() {
        Expression newLeft = left.simplify();
        Expression newRight = right.simplify();
        if (newLeft instanceof Number l && newRight instanceof Number r) {
            return new Number(l.getNumber() / r.getNumber());
        }
        return new Div(newLeft, newRight);
    }

    @Override
    protected Expression substitute(String var, Expression substituting) {
        return new Div(left.substitute(var, substituting), right.substitute(var, substituting));
    }

    @Override
    protected String operator() {
        return "/";
    }
}