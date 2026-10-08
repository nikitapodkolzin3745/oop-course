import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Tests subtraction expressions. */
public class SubTest {

    @Test
    void diffSubtraction() {
        assertEquals(
                new Sub(new Number(1), new Number(0)),
                new Sub(new Variable("x"), new Variable("y")).diff("x")
        );
    }

    @Test
    void simplifySubtraction() {
        assertEquals(new Number(0),
                new Sub(new Variable("x"), new Variable("x")).simplify());
        assertEquals(new Number(5),
                new Sub(new Number(8), new Number(3)).simplify());
    }
}