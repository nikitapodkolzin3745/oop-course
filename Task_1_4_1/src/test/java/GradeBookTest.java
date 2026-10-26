import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeBookTest {
    @Test
    void validatesPlanAndAssessmentInputs() {
        assertThrows(IllegalArgumentException.class, () -> new GradeBook(-1));
        GradeBook book = new GradeBook(1);
        assertThrows(IllegalArgumentException.class, () -> book.addAssessment(null, 1,
                GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT));
        assertThrows(IllegalArgumentException.class, () -> book.addAssessment("  ", 1,
                GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT));
        assertThrows(IllegalArgumentException.class, () -> book.addAssessment("Math", 0,
                GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT));
        assertThrows(IllegalArgumentException.class, () -> book.addAssessment("Math", 1,
                null, GradeBook.Grade.EXCELLENT));
        assertThrows(IllegalArgumentException.class, () -> book.addAssessment("Math", 1,
                GradeBook.AssessmentType.EXAM, null));
        assertThrows(IllegalArgumentException.class, () -> book.addAssessment("Math", 1,
                GradeBook.AssessmentType.EXAM, GradeBook.Grade.PASS));
        assertThrows(IllegalArgumentException.class, () -> book.addAssessment("Math", 1,
                GradeBook.AssessmentType.NON_DIFFERENTIATED_CREDIT, GradeBook.Grade.GOOD));
        book.addAssessment("Math", 1, GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT);
        assertThrows(IllegalArgumentException.class, () -> book.addAssessment("Physics", 1,
                GradeBook.AssessmentType.EXAM, GradeBook.Grade.GOOD));
    }

    @Test
    void calculatesAverageAndKeepsOnlyLatestAttempt() {
        GradeBook book = new GradeBook(2);
        assertEquals(OptionalDouble.empty(), book.getAverageGrade());
        book.addAssessment("Math", 1, GradeBook.AssessmentType.EXAM, GradeBook.Grade.GOOD);
        book.addAssessment("Physics", 1, GradeBook.AssessmentType.DIFFERENTIATED_CREDIT,
                GradeBook.Grade.UNSATISFACTORY);
        book.addAssessment("Math", 2, GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT);
        assertEquals(3.5, book.getAverageGrade().orElseThrow());
        assertThrows(IllegalArgumentException.class, () -> book.addAssessment("Chemistry", 2,
                GradeBook.AssessmentType.EXAM, GradeBook.Grade.GOOD));
    }

    @Test
    void permitsBudgetTransferUnlessRecentExamOrPassCreditBlocksIt() {
        GradeBook book = new GradeBook(8);
        book.addAssessment("Old exam", 1, GradeBook.AssessmentType.EXAM, GradeBook.Grade.SATISFACTORY);
        book.addAssessment("Exam 3", 3, GradeBook.AssessmentType.EXAM, GradeBook.Grade.GOOD);
        book.addAssessment("Exam 4", 4, GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT);
        book.addAssessment("Differentiated", 4, GradeBook.AssessmentType.DIFFERENTIATED_CREDIT,
                GradeBook.Grade.SATISFACTORY);
        book.addAssessment("Pass credit", 4, GradeBook.AssessmentType.NON_DIFFERENTIATED_CREDIT,
                GradeBook.Grade.PASS);
        assertTrue(book.canTransferToBudget());

        book.addAssessment("Recent exam", 3, GradeBook.AssessmentType.EXAM, GradeBook.Grade.SATISFACTORY);
        assertFalse(book.canTransferToBudget());
        book.addAssessment("Recent exam", 3, GradeBook.AssessmentType.EXAM, GradeBook.Grade.GOOD);
        book.addAssessment("Failed credit", 2, GradeBook.AssessmentType.NON_DIFFERENTIATED_CREDIT,
                GradeBook.Grade.FAIL);
        assertFalse(book.canTransferToBudget());
    }

    @Test
    void forecastsRedDiplomaAndChecksWorkGradeAndFinalMarks() {
        GradeBook book = new GradeBook(5);
        assertTrue(book.canReceiveRedDiploma());
        book.addAssessment("A", 1, GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT);
        book.addAssessment("B", 2, GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT);
        book.addAssessment("C", 3, GradeBook.AssessmentType.DIFFERENTIATED_CREDIT, GradeBook.Grade.GOOD);
        assertTrue(book.canReceiveRedDiploma());
        book.addAssessment("D", 4, GradeBook.AssessmentType.EXAM, GradeBook.Grade.GOOD);
        assertFalse(book.canReceiveRedDiploma());
        book.addAssessment("D", 4, GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT);
        book.setQualificationWorkGrade(GradeBook.Grade.GOOD);
        assertFalse(book.canReceiveRedDiploma());
        book.setQualificationWorkGrade(GradeBook.Grade.EXCELLENT);
        assertTrue(book.canReceiveRedDiploma());
        assertThrows(IllegalArgumentException.class,
                () -> book.setQualificationWorkGrade(GradeBook.Grade.PASS));
        book.setQualificationWorkGrade(null);
        assertTrue(book.canReceiveRedDiploma());

        book.addAssessment("E", 5, GradeBook.AssessmentType.DIFFERENTIATED_CREDIT,
                GradeBook.Grade.SATISFACTORY);
        assertFalse(book.canReceiveRedDiploma());

        GradeBook failedExam = new GradeBook(1);
        failedExam.addAssessment("Exam", 1, GradeBook.AssessmentType.EXAM,
                GradeBook.Grade.UNSATISFACTORY);
        assertFalse(failedExam.canReceiveRedDiploma());
    }

    @Test
    void anyFailedNondifferentiatedCreditExcludesRedDiploma() {
        GradeBook book = new GradeBook(2);
        book.addAssessment("Exam", 1, GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT);
        book.addAssessment("Credit", 1, GradeBook.AssessmentType.NON_DIFFERENTIATED_CREDIT,
                GradeBook.Grade.FAIL);
        assertFalse(book.canReceiveRedDiploma());
    }

    @Test
    void checksScholarshipAgainstLatestSemesterResults() {
        GradeBook book = new GradeBook(5);
        assertFalse(book.canReceiveIncreasedScholarship());
        book.addAssessment("Exam", 1, GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT);
        assertTrue(book.canReceiveIncreasedScholarship());
        book.addAssessment("Credit", 1, GradeBook.AssessmentType.NON_DIFFERENTIATED_CREDIT,
                GradeBook.Grade.PASS);
        assertTrue(book.canReceiveIncreasedScholarship());
        book.addAssessment("Differentiated", 1, GradeBook.AssessmentType.DIFFERENTIATED_CREDIT,
                GradeBook.Grade.GOOD);
        assertFalse(book.canReceiveIncreasedScholarship());
        book.addAssessment("New term", 2, GradeBook.AssessmentType.EXAM, GradeBook.Grade.EXCELLENT);
        assertTrue(book.canReceiveIncreasedScholarship());
        book.addAssessment("New term credit", 2, GradeBook.AssessmentType.NON_DIFFERENTIATED_CREDIT,
                GradeBook.Grade.FAIL);
        assertFalse(book.canReceiveIncreasedScholarship());
    }
}