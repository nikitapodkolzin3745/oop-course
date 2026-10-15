import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Tests numeric expressions. */
public class NumberTest {

    @Test
    void diffConstant() {
        assertEquals(new Number(0), new Number(7).diff("x"));
    }

    @Test
    void numberEquals() {
        assertEquals(new Number(5), new Number(5));
        assertNotEquals(new Number(5), new Number(6));
    }
}