import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MainTest {

    @Test
    void sortRandomArray() {
        int[] arr = {5, 10, -1, 7, 14, 42};

        Main.sort(arr);

        assertArrayEquals(
            new int[]{-1, 5, 7, 10, 14, 42},
            arr
        );
    }

    @Test
    void sortAlreadySortedArray() {
        int[] arr = {1, 2, 3, 4, 5};

        Main.sort(arr);

        assertArrayEquals(
            new int[]{1, 2, 3, 4, 5},
            arr
        );
    }

    @Test
    void sortReverseArray() {
        int[] arr = {5, 4, 3, 2, 1};

        Main.sort(arr);

        assertArrayEquals(
            new int[]{1, 2, 3, 4, 5},
            arr
        );
    }

    @Test
    void sortWithDuplicates() {
        int[] arr = {3, 1, 3, 2, 1};

        Main.sort(arr);

        assertArrayEquals(
            new int[]{1, 1, 2, 3, 3},
            arr
        );
    }

    @Test
    void sortEmptyArray() {
        int[] arr = {};

        Main.sort(arr);

        assertArrayEquals(new int[]{}, arr);
    }
}