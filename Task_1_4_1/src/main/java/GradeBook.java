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
        NON_DIFFERENTIATED_CREDIT,
        QUALIFICATION_WORK
    }

    /** Возможная оценка за экзамен или зачет. */
    public enum Grade {
        EXCELLENT(5, true),
        GOOD(4, true),
        SATISFACTORY(3, true),
        UNSATISFACTORY(2, true),
        PASS(0, false),
        FAIL(0, false);

        private final int numericValue;
        private final boolean hasNumericRepresentation;

        Grade(int numericValue, boolean hasNumericRepresentation) {
            this.numericValue = numericValue;
            this.hasNumericRepresentation = hasNumericRepresentation;
        }

        /** Возвращает, имеет ли оценка числовое представление. */
        public boolean hasNumericRepresentation() {
            return hasNumericRepresentation;
        }
    }

    private record AssessmentKey(String subject, AssessmentType type) {
    }

    private record Assessment(int semester, AssessmentType type, Grade grade) {
    }

    private final int plannedGradedAssessments;
    private final int plannedSemesters;
    private final Map<AssessmentKey, Assessment> assessments = new HashMap<>();

    /**
     * Создает зачетную книжку с известным числом оценок, включаемых в приложение к диплому.
     * @param plannedGradedAssessments плановое число экзаменов и дифференцированных зачетов
     * @throws IllegalArgumentException если число отрицательное
     */
    public GradeBook(int plannedGradedAssessments) {
        this(plannedGradedAssessments, 12);
    }

    /**
     * Создает зачетную книжку с планом оценок и продолжительностью обучения.
     * @param plannedGradedAssessments плановое число числовых оценок в приложении к диплому
     * @param plannedSemesters число семестров по учебному плану
     * @throws IllegalArgumentException если хотя бы один параметр отрицательный или равен нулю
     */
    public GradeBook(int plannedGradedAssessments, int plannedSemesters) {
        if (plannedGradedAssessments < 0) {
            throw new IllegalArgumentException("Плановое число оценок не может быть отрицательным");
        }
        if (plannedSemesters < 1) {
            throw new IllegalArgumentException("Число семестров должно быть положительным");
        }
        this.plannedGradedAssessments = plannedGradedAssessments;
        this.plannedSemesters = plannedSemesters;
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
        if (semester < 1 || semester > plannedSemesters) {
            throw new IllegalArgumentException("Номер семестра вне учебного плана");
        }
        if (type == null || grade == null) {
            throw new IllegalArgumentException("Вид аттестации и оценка обязательны");
        }
        boolean correctGradeType = type == AssessmentType.NON_DIFFERENTIATED_CREDIT
            ? !grade.hasNumericRepresentation()
            : grade.hasNumericRepresentation();
        if (!correctGradeType) {
            throw new IllegalArgumentException("Оценка не соответствует виду аттестации");
        }

        AssessmentKey key = new AssessmentKey(subject.trim(), type);
        Assessment previous = assessments.get(key);
        long gradedCount = assessments.values().stream()
            .filter(assessment -> assessment.type() != AssessmentType.QUALIFICATION_WORK)
            .map(Assessment::grade)
            .filter(Grade::hasNumericRepresentation)
            .count();
        boolean addsGradedResult = type != AssessmentType.QUALIFICATION_WORK
            && grade.hasNumericRepresentation()
            && (previous == null || !previous.grade().hasNumericRepresentation());
        if (addsGradedResult && gradedCount >= plannedGradedAssessments) {
            throw new IllegalArgumentException("Число оценок превышает план учебного плана");
        }
        assessments.put(key, new Assessment(semester, type, grade));
        if (type == AssessmentType.QUALIFICATION_WORK) {
            assessments.keySet().removeIf(candidate -> candidate.type() == AssessmentType.QUALIFICATION_WORK
                && !candidate.equals(key));
        }
    }

    /**
     * Возвращает среднее арифметическое всех актуальных числовых оценок.
     * @return средний балл или пустое значение, если числовых оценок еще нет
     */
    public OptionalDouble getAverageGrade() {
        return assessments.values().stream()
            .filter(assessment -> assessment.type() != AssessmentType.QUALIFICATION_WORK)
            .map(Assessment::grade)
            .filter(Grade::hasNumericRepresentation)
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
        boolean qualificationWorkIsNotExcellent = assessments.values().stream()
                .filter(assessment -> assessment.type() == AssessmentType.QUALIFICATION_WORK)
                .anyMatch(assessment -> assessment.grade() != Grade.EXCELLENT);
        if (hasDisqualifyingGrade || qualificationWorkIsNotExcellent) {
            return false;
        }

        long excellentCount = assessments.values().stream()
            .filter(assessment -> assessment.type() != AssessmentType.QUALIFICATION_WORK)
            .filter(assessment -> assessment.grade() == Grade.EXCELLENT)
            .count();
        long gradedCount = assessments.values().stream()
            .filter(assessment -> assessment.type() != AssessmentType.QUALIFICATION_WORK)
            .map(Assessment::grade)
            .filter(Grade::hasNumericRepresentation)
            .count();
        long requiredExcellentCount = (plannedGradedAssessments * 3L + 3L) / 4L;
        return excellentCount + plannedGradedAssessments - gradedCount >= requiredExcellentCount;
    }

    /** Проверяет право на повышенную стипендию в последнем семестре. */
    public boolean canReceiveIncreasedScholarship() {
        return assessments.values().stream()
            .filter(assessment -> assessment.type() != AssessmentType.QUALIFICATION_WORK)
                .mapToInt(Assessment::semester)
                .max()
                .stream()
                .anyMatch(this::hasExcellentResultsInSemester);
    }

    private boolean hasExcellentResultsInSemester(int semester) {
        var semesterAssessments = assessments.values().stream()
                .filter(assessment -> assessment.semester() == semester)
                .filter(assessment -> assessment.type() != AssessmentType.QUALIFICATION_WORK)
                .toList();
        boolean hasNumericGrade = semesterAssessments.stream()
                .map(Assessment::grade)
                .anyMatch(Grade::hasNumericRepresentation);
        boolean allResultsExcellent = semesterAssessments.stream()
            .allMatch(assessment -> assessment.type()
                == AssessmentType.NON_DIFFERENTIATED_CREDIT
                        ? assessment.grade() == Grade.PASS
                        : assessment.grade() == Grade.EXCELLENT);
        return hasNumericGrade && allResultsExcellent;
    }
}