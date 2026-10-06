import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Tests division expressions. */
public class DivTest {

    @Test
    void diffDivision() {
        assertEquals(
                new Div(
                        new Sub(
                                new Mul(new Number(1), new Variable("y")),
                                new Mul(new Variable("x"), new Number(0))
                        ),
                        new Mul(new Variable("y"), new Variable("y"))
                ),
                new Div(new Variable("x"), new Variable("y")).diff("x")
        );
    }

    @Test
    void simplifyDivision() {
        assertEquals(new Number(4),
                new Div(new Number(8), new Number(2)).simplify());
    }
}