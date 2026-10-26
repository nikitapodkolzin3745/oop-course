import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

/** Электронная зачетная книжка с актуальными результатами аттестаций. */
public class GradeBook {
    /** Вид аттестации по учебной дисциплине. */
    public enum AssessmentType {
        EXAM,
        DIFFERENTIATED_CREDIT,
        NON_DIFFERENTIATED_CREDIT
    }

    /** Возможная оценка за экзамен или зачет. */
    public enum Grade {
        EXCELLENT(5),
        GOOD(4),
        SATISFACTORY(3),
        UNSATISFACTORY(2),
        PASS(null),
        FAIL(null);

        private final Integer numericValue;

        Grade(Integer numericValue) {
            this.numericValue = numericValue;
        }
    }

    private record AssessmentKey(String subject, AssessmentType type) {
    }

    private record Assessment(int semester, AssessmentType type, Grade grade) {
    }

    private final int plannedGradedAssessments;
    private final Map<AssessmentKey, Assessment> assessments = new HashMap<>();
    private Grade qualificationWorkGrade;

    /**
     * Создает зачетную книжку с известным числом оценок, включаемых в приложение к диплому.
     * @param plannedGradedAssessments плановое число экзаменов и дифференцированных зачетов
     * @throws IllegalArgumentException если число отрицательное
     */
    public GradeBook(int plannedGradedAssessments) {
        if (plannedGradedAssessments < 0) {
            throw new IllegalArgumentException("Плановое число оценок не может быть отрицательным");
        }
        this.plannedGradedAssessments = plannedGradedAssessments;
    }

    /**
     * Добавляет или заменяет итоговый результат аттестации по дисциплине.
     * Повторная сдача заменяет прежний итог по тому же предмету и виду аттестации.
     * @param subject название дисциплины
     * @param semester номер семестра, начиная с 1
     * @param type вид аттестации
     * @param grade итоговая оценка
     * @throws IllegalArgumentException если параметры некорректны или превышен учебный план
     */
    public void addAssessment(String subject, int semester, AssessmentType type, Grade grade) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Название дисциплины не должно быть пустым");
        }
        if (semester < 1) {
            throw new IllegalArgumentException("Номер семестра должен быть положительным");
        }
        if (type == null || grade == null) {
            throw new IllegalArgumentException("Вид аттестации и оценка обязательны");
        }
        boolean correctGradeType = type == AssessmentType.NON_DIFFERENTIATED_CREDIT
                ? grade == Grade.PASS || grade == Grade.FAIL
                : grade.numericValue != null;
        if (!correctGradeType) {
            throw new IllegalArgumentException("Оценка не соответствует виду аттестации");
        }

        AssessmentKey key = new AssessmentKey(subject.trim(), type);
        Assessment previous = assessments.get(key);
        long gradedCount = assessments.values().stream()
                .filter(assessment -> assessment.grade().numericValue != null)
                .count();
        boolean addsGradedResult = grade.numericValue != null
                && (previous == null || previous.grade().numericValue == null);
        if (addsGradedResult && gradedCount >= plannedGradedAssessments) {
            throw new IllegalArgumentException("Число оценок превышает план учебного плана");
        }
        assessments.put(key, new Assessment(semester, type, grade));
    }

    /**
     * Возвращает среднее арифметическое всех актуальных числовых оценок.
     * @return средний балл или пустое значение, если числовых оценок еще нет
     */
    public OptionalDouble getAverageGrade() {
        return assessments.values().stream()
                .map(Assessment::grade)
                .filter(grade -> grade.numericValue != null)
                .mapToInt(grade -> grade.numericValue)
                .average();
    }

    /** Проверяет условия перевода на бюджет. */
    public boolean canTransferToBudget() {
        Set<Integer> recentExamSemesters = assessments.values().stream()
                .filter(assessment -> assessment.type() == AssessmentType.EXAM)
                .map(Assessment::semester)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .collect(Collectors.toSet());

        boolean hasRecentSatisfactoryExam = assessments.values().stream()
                .filter(assessment -> assessment.type() == AssessmentType.EXAM)
                .filter(assessment -> recentExamSemesters.contains(assessment.semester()))
                .anyMatch(assessment -> assessment.grade() == Grade.SATISFACTORY);
        boolean hasFailedPassCredit = assessments.values().stream()
            .anyMatch(assessment -> assessment.type()
                == AssessmentType.NON_DIFFERENTIATED_CREDIT
                        && assessment.grade() == Grade.FAIL);
        return !hasRecentSatisfactoryExam && !hasFailedPassCredit;
    }

    /** Прогнозирует возможность получения диплома с отличием. */
    public boolean canReceiveRedDiploma() {
        boolean hasDisqualifyingGrade = assessments.values().stream()
                .anyMatch(assessment -> assessment.grade() == Grade.SATISFACTORY
                || assessment.grade() == Grade.UNSATISFACTORY
                        || assessment.grade() == Grade.FAIL);
        if (hasDisqualifyingGrade
                || (qualificationWorkGrade != null && qualificationWorkGrade != Grade.EXCELLENT)) {
            return false;
        }

        long excellentCount = assessments.values().stream()
                .filter(assessment -> assessment.grade() == Grade.EXCELLENT)
                .count();
        long gradedCount = assessments.values().stream()
                .filter(assessment -> assessment.grade().numericValue != null)
                .count();
        long requiredExcellentCount = (plannedGradedAssessments * 3L + 3L) / 4L;
        return excellentCount + plannedGradedAssessments - gradedCount >= requiredExcellentCount;
    }

    /** Записывает оценку за квалификационную работу; {@code null} — до защиты. */
    public void setQualificationWorkGrade(Grade grade) {
        if (grade != null && grade.numericValue == null) {
            throw new IllegalArgumentException("За квалификационную работу нужна числовая оценка");
        }
        qualificationWorkGrade = grade;
    }

    /** Проверяет право на повышенную стипендию в последнем семестре. */
    public boolean canReceiveIncreasedScholarship() {
        return assessments.values().stream()
                .mapToInt(Assessment::semester)
                .max()
                .stream()
                .anyMatch(this::hasExcellentResultsInSemester);
    }

    private boolean hasExcellentResultsInSemester(int semester) {
        var semesterAssessments = assessments.values().stream()
                .filter(assessment -> assessment.semester() == semester)
                .toList();
        boolean hasNumericGrade = semesterAssessments.stream()
                .anyMatch(assessment -> assessment.grade().numericValue != null);
        boolean allResultsExcellent = semesterAssessments.stream()
            .allMatch(assessment -> assessment.type()
                == AssessmentType.NON_DIFFERENTIATED_CREDIT
                        ? assessment.grade() == Grade.PASS
                        : assessment.grade() == Grade.EXCELLENT);
        return hasNumericGrade && allResultsExcellent;
    }
}