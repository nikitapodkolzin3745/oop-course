import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Tests variable expressions. */
public class VariableTest {

    @Test
    void variableRecognition() {
        assertTrue(Variable.isVariable("abc", 0, 3));
        assertTrue(Variable.isVariable("_x1", 0, 3));
        assertFalse(Variable.isVariable("", 0, 0));
        assertFalse(Variable.isVariable("1abc", 0, 4));
    }

    @Test
    void diffVariable() {
        assertEquals(new Number(1), new Variable("x").diff("x"));
        assertEquals(new Number(0), new Variable("y").diff("x"));
    }

    @Test
    void variableEquals() {
        assertEquals(new Variable("x"), new Variable("x"));
        assertNotEquals(new Variable("x"), new Variable("y"));
    }
}