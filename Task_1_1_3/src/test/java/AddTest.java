import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Tests addition expressions. */
public class AddTest {

    @Test
    void diffAddition() {
        assertEquals(
                new Add(new Number(1), new Number(0)),
                new Add(new Variable("x"), new Variable("y")).diff("x")
        );
    }

    @Test
    void simplifyAddition() {
        assertEquals(new Variable("x"),
                new Add(new Number(0), new Variable("x")).simplify());
        assertEquals(new Variable("x"),
                new Add(new Variable("x"), new Number(0)).simplify());
        assertEquals(new Number(5),
                new Add(new Number(2), new Number(3)).simplify());
    }
}