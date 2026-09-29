import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ExpressionTest {

    // ---------- Parsing ----------

    @Test
    void parseNumber() {
        assertEquals("123", Expression.parse("123").toString());
    }

    @Test
    void parseVariable() {
        assertEquals("x", Expression.parse("x").toString());
    }

    @Test
    void parseOperations() {
        assertEquals("(1+2)", Expression.parse("(1+2)").toString());
        assertEquals("(5-3)", Expression.parse("(5-3)").toString());
        assertEquals("(4*7)", Expression.parse("(4*7)").toString());
        assertEquals("(8/2)", Expression.parse("(8/2)").toString());
    }

    @Test
    void parseNested() {
        String expr = "(((1+(7*x))*y)-((52+(7*x))*x))";
        assertEquals(expr, Expression.parse(expr).toString());
    }

    @Test
    void parseInvalidThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> Expression.parse("(1+)"));
    }

    // ---------- Number / Variable ----------

    @Test
    void numberRecognition() {
        assertTrue(Number.isNumber("123", 0, 3));
        assertFalse(Number.isNumber("", 0, 0));
        assertFalse(Number.isNumber("12a", 0, 3));
    }

    @Test
    void variableRecognition() {
        assertTrue(Variable.isVariable("abc", 0, 3));
        assertTrue(Variable.isVariable("_x1", 0, 3));
        assertFalse(Variable.isVariable("", 0, 0));
        assertFalse(Variable.isVariable("1abc", 0, 4));
    }

    // ---------- Differentiation ----------

    @Test
    void diffConstant() {
        assertEquals("0", Expression.parse("7").diff("x").toString());
    }

    @Test
    void diffVariable() {
        assertEquals("1", Expression.parse("x").diff("x").toString());
        assertEquals("0", Expression.parse("y").diff("x").toString());
    }

    @Test
    void diffAddition() {
        assertEquals("(1+0)",
                Expression.parse("(x+y)").diff("x").toString());
    }

    @Test
    void diffSubtraction() {
        assertEquals("(1-0)",
                Expression.parse("(x-y)").diff("x").toString());
    }

    @Test
    void diffMultiplication() {
        assertEquals("((1*x)+(x*1))",
                Expression.parse("(x*x)").diff("x").toString());
    }

    @Test
    void diffDivision() {
        assertEquals("(((1*y)-(x*0))/(y*y))",
                Expression.parse("(x/y)").diff("x").toString());
    }

    // ---------- Simplification ----------

    @Test
    void simplifyAddition() {
        assertEquals("x", Expression.parse("(0+x)").simplify().toString());
        assertEquals("x", Expression.parse("(x+0)").simplify().toString());
        assertEquals("5", Expression.parse("(2+3)").simplify().toString());
    }

    @Test
    void simplifyMultiplication() {
        assertEquals("0", Expression.parse("(0*x)").simplify().toString());
        assertEquals("0", Expression.parse("(x*0)").simplify().toString());
        assertEquals("x", Expression.parse("(1*x)").simplify().toString());
        assertEquals("x", Expression.parse("(x*1)").simplify().toString());
        assertEquals("12", Expression.parse("(3*4)").simplify().toString());
    }

    @Test
    void simplifySubtraction() {
        assertEquals("0", Expression.parse("(x-x)").simplify().toString());
        assertEquals("5", Expression.parse("(8-3)").simplify().toString());
    }

    @Test
    void simplifyDivision() {
        assertEquals("4", Expression.parse("(8/2)").simplify().toString());
    }

    @Test
    void simplifyNested() {
        assertEquals("(x+x)",
                Expression.parse("((0+x)+(x+0))").simplify().toString());
    }

    // ---------- Evaluation ----------

    @Test
    void evalSimple() {
        assertEquals(23,
                Expression.parse("(x+13)").eval("x = 10"));
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
        assertThrows(IllegalStateException.class,
                () -> Expression.parse("(x+y)").eval("x = 5"));
    }

    // ---------- Equals ----------

    @Test
    void numberEquals() {
        assertEquals(new Number(5), new Number(5));
        assertNotEquals(new Number(5), new Number(6));
    }

    @Test
    void variableEquals() {
        assertEquals(new Variable("x"), new Variable("x"));
        assertNotEquals(new Variable("x"), new Variable("y"));
    }

    @Test
    void operationEquals() {
        assertEquals(
                Expression.parse("(x+y)"),
                Expression.parse("(x+y)")
        );

        assertNotEquals(
                Expression.parse("(x+y)"),
                Expression.parse("(y+x)")
        );
    }
}
