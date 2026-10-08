import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Tests shared operation behavior. */
public class OperationTest {

    @Test
    void sameOperationAndOperandsAreEqual() {
        assertEquals(
                new Add(new Variable("x"), new Variable("y")),
                new Add(new Variable("x"), new Variable("y"))
        );
    }

    @Test
    void differentOperandsAreNotEqual() {
        assertNotEquals(
                new Add(new Variable("x"), new Variable("y")),
                new Add(new Variable("y"), new Variable("x"))
        );
    }

    @Test
    void differentOperationTypesAreNotEqual() {
        Expression left = new Number(1);
        Expression right = new Number(2);

        assertNotEquals(
                new Add(left, right), new Sub(left, right)
        );
        assertNotEquals(
                new Sub(left, right), new Add(left, right)
        );
        assertNotEquals(
                new Add(left, right), new Mul(left, right)
        );
        assertNotEquals(
                new Add(left, right), new Div(left, right)
        );
    }

    @Test
    void nullAndNonOperationAreNotEqual() {
        Operation operation = new Add(new Number(1), new Number(2));

        assertNotEquals(operation, null);
        assertNotEquals(operation, new Number(3));
        assertNotEquals(operation, "not an operation");
    }
}