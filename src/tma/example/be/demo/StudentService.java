package tma.example.be.demo;

import java.util.*;
import java.util.function.*;

class StudentService {

    private List<Student> students = new ArrayList<>();

    public StudentService() {

        students.add(new Student(1, "Anna", 3.8));
        students.add(new Student(2, "John", 2.9));
        students.add(new Student(3, "David", 3.5));
        students.add(new Student(4, "Sophia", 3.9));
        students.add(new Student(5, "Mike", 2.7));
    }

    // In danh sách sinh viên
    public void printStudents(Consumer<Student> action) {
        students.forEach(action);
    }

    // Lọc sinh viên
    public List<Student> filterStudents(Predicate<Student> condition) {
        return students.stream()
                .filter(condition)
                .toList();
    }

    // Functional Interface tự tạo
    public void printFormatted(StudentFormatter formatter) {
        students.forEach(s -> System.out.println(formatter.format(s)));
    }

    // Tìm sinh viên theo ID
    public Optional<Student> findById(int id) {
        return students.stream()
                .filter(s -> s.getId() == id)
                .findFirst();
    }

    // Tính GPA trung bình
    public double averageGpa() {
        return students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
    }
}
