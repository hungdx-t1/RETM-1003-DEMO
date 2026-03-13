package tma.example.be.demo;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

class StudentService {

    private final List<Student> students = new ArrayList<>();

    public StudentService() {
        students.add(new Student(1, "Anna", 3.8, "Computer Science", 3));
        students.add(new Student(2, "John", 2.9, "Mathematics", 2));
        students.add(new Student(3, "David", 3.5, "Computer Science", 4));
        students.add(new Student(4, "Sophia", 3.9, "Physics", 1));
        students.add(new Student(5, "Mike", 2.7, "Mathematics", 3));
        students.add(new Student(6, "Emily", 3.2, "Physics", 2));
        students.add(new Student(7, "Lucas", 1.8, "Computer Science", 1));
        students.add(new Student(8, "Mia", 3.6, "Mathematics", 4));
    }

    // ── Consumer ──────────────────────────────────────────────────────────
    /** In danh sách sinh viên bằng Consumer<Student> truyền vào. */
    public void printStudents(Consumer<Student> action) {
        students.forEach(action);
    }

    // ── Predicate ─────────────────────────────────────────────────────────
    /** Lọc sinh viên theo điều kiện Predicate. */
    public List<Student> filterStudents(Predicate<Student> condition) {
        return students.stream()
                .filter(condition)
                .toList();
    }

    // ── Custom Functional Interface: StudentFormatter ─────────────────────
    /** In sinh viên với định dạng tùy chỉnh qua StudentFormatter. */
    public void printFormatted(StudentFormatter formatter) {
        students.forEach(s -> System.out.println(formatter.format(s)));
    }

    // ── Optional ──────────────────────────────────────────────────────────
    /** Tìm sinh viên theo ID – trả về Optional<Student>. */
    public Optional<Student> findById(int id) {
        return students.stream()
                .filter(s -> s.getId() == id)
                .findFirst();
    }

    /**
     * Lấy tên sinh viên theo ID qua chuỗi Optional:
     * Optional.filter → Optional.map → kết quả Optional<String>.
     */
    public Optional<String> findNameById(int id) {
        return findById(id)
                .filter(s -> s.getGpa() >= 2.0) // Optional.filter
                .map(Student::getName); // Optional.map
    }

    // ── Function ──────────────────────────────────────────────────────────
    /** Ánh xạ danh sách sinh viên sang kiểu R bằng Function<Student, R>. */
    public <R> List<R> mapStudents(Function<Student, R> mapper) {
        return students.stream()
                .map(mapper)
                .toList();
    }

    // ── BiFunction ────────────────────────────────────────────────────────
    /** Tạo nhãn tùy chỉnh bằng BiFunction<String, Double, String>. */
    public List<String> buildLabels(BiFunction<String, Double, String> labelBuilder) {
        return students.stream()
                .map(s -> labelBuilder.apply(s.getName(), s.getGpa()))
                .toList();
    }

    // ── Supplier ──────────────────────────────────────────────────────────
    /** Tìm sinh viên theo ID; nếu không có thì gọi Supplier để lấy mặc định. */
    public Student findByIdOrDefault(int id, Supplier<Student> defaultSupplier) {
        return findById(id).orElseGet(defaultSupplier); // Optional.orElseGet(Supplier)
    }

    // ── UnaryOperator ─────────────────────────────────────────────────────
    /** Áp dụng biến đổi UnaryOperator<Student> lên toàn bộ danh sách. */
    public List<Student> applyToAll(UnaryOperator<Student> transform) {
        return students.stream()
                .map(transform)
                .toList();
    }

    // ── Stream: sorted ────────────────────────────────────────────────────
    /** Sắp xếp danh sách theo Comparator truyền vào. */
    public List<Student> sortedBy(Comparator<Student> comparator) {
        return students.stream()
                .sorted(comparator)
                .toList();
    }

    // ── Stream: groupingBy ────────────────────────────────────────────────
    /** Nhóm sinh viên theo ngành học bằng Collectors.groupingBy. */
    public Map<String, List<Student>> groupByMajor() {
        return students.stream()
                .collect(Collectors.groupingBy(Student::getMajor));
    }

    // ── Stream: partitioningBy ────────────────────────────────────────────
    /** Phân chia đạt/không đạt theo ngưỡng GPA bằng Collectors.partitioningBy. */
    public Map<Boolean, List<Student>> partitionByGpa(double threshold) {
        return students.stream()
                .collect(Collectors.partitioningBy(s -> s.getGpa() >= threshold));
    }

    // ── Stream terminal ops ───────────────────────────────────────────────
    /** Trung bình GPA – mapToDouble + average trả về OptionalDouble. */
    public double averageGpa() {
        return students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
    }

    /** Tổng GPA – mapToDouble + reduce. */
    public double sumGpa() {
        return students.stream()
                .mapToDouble(Student::getGpa)
                .reduce(0.0, Double::sum);
    }

    /** Đếm số sinh viên đạt ngưỡng GPA bằng Stream.count. */
    public long countPassing(double threshold) {
        return students.stream()
                .filter(s -> s.getGpa() >= threshold)
                .count();
    }

    /** Sinh viên GPA cao nhất – Stream.max + Optional. */
    public Optional<Student> findTopStudent() {
        return students.stream()
                .max(Comparator.comparingDouble(Student::getGpa));
    }

    /** Sinh viên GPA thấp nhất – Stream.min + Optional. */
    public Optional<Student> findLowestStudent() {
        return students.stream()
                .min(Comparator.comparingDouble(Student::getGpa));
    }

    // ── Custom Functional Interface: StudentValidator ─────────────────────
    /** Lọc sinh viên bằng StudentValidator có thể compose. */
    public List<Student> validate(StudentValidator validator) {
        return students.stream()
                .filter(validator::validate)
                .toList();
    }
}
