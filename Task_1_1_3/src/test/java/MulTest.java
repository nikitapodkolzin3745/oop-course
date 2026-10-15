import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Tests multiplication expressions. */
public class MulTest {

    @Test
    void diffMultiplication() {
        assertEquals(
                new Add(
                        new Mul(new Number(1), new Variable("x")),
                        new Mul(new Variable("x"), new Number(1))
                ),
                new Mul(new Variable("x"), new Variable("x")).diff("x")
        );
    }

    @Test
    void simplifyMultiplication() {
        assertEquals(new Number(0),
                new Mul(new Number(0), new Variable("x")).simplify());
        assertEquals(new Number(0),
                new Mul(new Variable("x"), new Number(0)).simplify());
        assertEquals(new Variable("x"),
                new Mul(new Number(1), new Variable("x")).simplify());
        assertEquals(new Variable("x"),
                new Mul(new Variable("x"), new Number(1)).simplify());
        assertEquals(new Number(12),
                new Mul(new Number(3), new Number(4)).simplify());
    }
}