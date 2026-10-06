import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Tests expression parsing and evaluation. */
public class ExpressionTest {

    @Test
    void parseNumber() {
        assertEquals(new Number(123), Expression.parse("123"));
    }

    @Test
    void parseVariable() {
        assertEquals(new Variable("x"), Expression.parse("x"));
    }

    @Test
    void parseOperations() {
        assertEquals(new Add(new Number(1), new Number(2)),
                Expression.parse("(1+2)"));
        assertEquals(new Sub(new Number(5), new Number(3)),
                Expression.parse("(5-3)"));
        assertEquals(new Mul(new Number(4), new Number(7)),
                Expression.parse("(4*7)"));
        assertEquals(new Div(new Number(8), new Number(2)),
                Expression.parse("(8/2)"));
    }

    @Test
    void parseNested() {
        Expression expected = new Sub(
                new Mul(
                        new Add(
                                new Number(1),
                                new Mul(new Number(7), new Variable("x"))
                        ),
                        new Variable("y")
                ),
                new Mul(
                        new Add(
                                new Number(52),
                                new Mul(new Number(7), new Variable("x"))
                        ),
                        new Variable("x")
                )
        );

        assertEquals(expected,
                Expression.parse("(((1+(7*x))*y)-((52+(7*x))*x))"));
    }

    @Test
    void parseInvalidThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> Expression.parse("(1+)"));
    }

    @Test
    void parseInvalidNumberThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> Expression.parse("12a"));
    }

    @Test
    void parseWithoutOuterParentheses() {
        assertEquals(new Add(new Number(1), new Number(2)),
                Expression.parse("1+2"));
    }

    @Test
    void parseOperatorPriority() {
        assertEquals(
                new Add(new Number(1),
                        new Mul(new Number(2), new Number(3))),
                Expression.parse("1+2*3")
        );
    }

    @Test
    void parseParenthesesOverridePriority() {
        assertEquals(
                new Mul(new Add(new Number(1), new Number(2)), new Number(3)),
                Expression.parse("(1+2)*3")
        );
    }

    @Test
    void parseLeftAssociativeAddition() {
        assertEquals(
                new Add(new Add(new Number(1), new Number(2)), new Number(3)),
                Expression.parse("1+2+3")
        );
    }

    @Test
    void parseLeftAssociativeMultiplication() {
        assertEquals(
                new Mul(new Mul(new Number(2), new Number(3)), new Number(4)),
                Expression.parse("2*3*4")
        );
    }

    @Test
    void parseMixedOperations() {
        assertEquals(
                new Add(
                        new Mul(new Variable("x"), new Variable("y")),
                        new Div(new Variable("z"), new Number(2))
                ),
                Expression.parse("x*y+z/2")
        );
    }

    @Test
    void parseIgnoresSpaces() {
        assertEquals(
                new Sub(
                        new Add(new Number(1),
                                new Mul(new Number(2), new Number(3))),
                        new Number(4)
                ),
                Expression.parse(" 1 + 2 * 3 - 4 ")
        );
    }

    @Test
    void parseNestedWithoutExtraParentheses() {
        assertEquals(
                new Mul(
                        new Add(
                                new Number(1),
                                new Mul(new Number(7), new Variable("x"))
                        ),
                        new Variable("y")
                ),
                Expression.parse("(1+7*x)*y")
        );
    }

    @Test
    void evalSimple() {
        assertEquals(23, Expression.parse("(x+13)").eval("x = 10"));
    }

    @Test
    void evalMultipleVariables() {
        assertEquals(13,
                Expression.parse("(x+y)").eval("x = 5; y = 8"));
    }

    @Test
    void evalAfterDerivative() {
        String expr = "(((1+(7*x))*y)-((52+(7*x))*x))";

        assertEquals(-983,
                Expression.parse(expr)
                        .diff("x")
                        .eval("x = 67; y = 1"));
    }

    @Test
    void evalMissingVariableThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> Expression.parse("(x+y)").eval("x = 5"));
    }
}
