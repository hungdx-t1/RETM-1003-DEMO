package tma.example.be.demo;

import java.util.*;
import java.util.function.*;

public class Main {

    private static final int MIN_MENU_OPTION = 1;
    private static final int MAX_MENU_OPTION = 9;

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            StudentService service = new StudentService();

            while (true) {

                printMenu();
                int choice = readMenuChoice(scanner);

                switch (choice) {
                    case 1 -> showAllStudents(service);
                    case 2 -> filterStudentsByGpa(scanner, service);
                    case 3 -> findStudentById(scanner, service);
                    case 4 -> showStatistics(service);
                    case 5 -> sortAndDisplay(service);
                    case 6 -> groupAndPartition(scanner, service);
                    case 7 -> mapAndTransform(service);
                    case 8 -> validateWithComposedRules(service);
                    case 9 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n========== STUDENT MANAGEMENT ==========");
        System.out.println("1. Show all students           [Consumer]");
        System.out.println("2. Filter by GPA               [Predicate + composition]");
        System.out.println("3. Find student by ID          [Optional chain]");
        System.out.println("4. GPA statistics              [Stream terminal ops]");
        System.out.println("5. Sort students               [Comparator + sorted]");
        System.out.println("6. Group & partition           [groupingBy / partitioningBy]");
        System.out.println("7. Map & transform             [Function / BiFunction / UnaryOperator / Supplier]");
        System.out.println("8. Validate with composed rules[Custom FunctionalInterface]");
        System.out.println("9. Exit");
    }

    // ── Case 1: Consumer ──────────────────────────────────────────────────
    private static void showAllStudents(StudentService service) {
        // Consumer<Student>: method reference thay thế lambda s ->
        // System.out.println(s)
        Consumer<Student> printer = System.out::println;
        service.printStudents(printer);
    }

    // ── Case 2: Predicate + composition ──────────────────────────────────
    private static void filterStudentsByGpa(Scanner scanner, StudentService service) {
        double minGpa = readDouble(scanner, "Enter min GPA (0.0 – 4.0): ",
                v -> v >= 0.0 && v <= 4.0, "GPA must be between 0.0 and 4.0.");

        // Predicate lambda
        Predicate<Student> highGpa = s -> s.getGpa() > minGpa;
        // Predicate.and – composition
        Predicate<Student> validName = s -> !s.getName().isBlank();
        Predicate<Student> combined = highGpa.and(validName);

        List<Student> result = service.filterStudents(combined);
        if (result.isEmpty()) {
            System.out.println("No students found.");
        } else {
            result.forEach(System.out::println);
        }
    }

    // ── Case 3: Optional chain ────────────────────────────────────────────
    private static void findStudentById(Scanner scanner, StudentService service) {
        int id = readInt(scanner, "Enter ID: ", v -> v > 0, "ID must be a positive integer.");

        // Optional.ifPresentOrElse
        service.findById(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Student not found."));

        // Optional.filter → Optional.map → Optional.orElse
        String name = service.findNameById(id)
                .orElse("N/A (not found or GPA < 2.0)");
        System.out.println("Name (GPA ≥ 2.0): " + name);

        // Optional.orElseThrow
        try {
            Student found = service.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No student with ID " + id));
            System.out.println("orElseThrow result: " + found.getName());
        } catch (NoSuchElementException e) {
            System.out.println("orElseThrow caught: " + e.getMessage());
        }
    }

    // ── Case 4: Stream terminal ops ───────────────────────────────────────
    private static void showStatistics(StudentService service) {
        System.out.printf("Average GPA     : %.2f%n", service.averageGpa()); // average()
        System.out.printf("Total GPA (reduce): %.2f%n", service.sumGpa()); // reduce()
        System.out.printf("Passing (≥ 3.0) : %d student(s)%n", service.countPassing(3.0)); // count()
        System.out.printf("Passing (≥ 2.0) : %d student(s)%n", service.countPassing(2.0));

        // Stream.max / min – trả về Optional<Student>
        service.findTopStudent()
                .ifPresent(s -> System.out.println("Top student     : " + s));
        service.findLowestStudent()
                .ifPresent(s -> System.out.println("Lowest student  : " + s));
    }

    // ── Case 5: Comparator.comparing + Stream.sorted ──────────────────────
    private static void sortAndDisplay(StudentService service) {
        System.out.println("── Sorted by GPA (desc) ──");
        // Comparator.comparingDouble + reversed()
        service.sortedBy(Comparator.comparingDouble(Student::getGpa).reversed())
                .forEach(System.out::println);

        System.out.println("\n── Sorted by name (A→Z) ──");
        // Comparator.comparing với method reference
        service.sortedBy(Comparator.comparing(Student::getName))
                .forEach(System.out::println);

        System.out.println("\n── Sorted by year (asc), then GPA (desc) ──");
        // Comparator.thenComparing
        service.sortedBy(
                Comparator.comparingInt(Student::getYear)
                        .thenComparing(Comparator.comparingDouble(Student::getGpa).reversed()))
                .forEach(System.out::println);
    }

    // ── Case 6: groupingBy + partitioningBy ───────────────────────────────
    private static void groupAndPartition(Scanner scanner, StudentService service) {
        // Collectors.groupingBy
        System.out.println("── Group by major ──");
        service.groupByMajor().forEach((major, list) -> {
            System.out.println("[" + major + "]");
            list.forEach(s -> System.out.println("  " + s));
        });

        // Collectors.partitioningBy
        double threshold = readDouble(scanner, "\nPartition GPA threshold: ",
                v -> v >= 0.0 && v <= 4.0, "GPA must be between 0.0 and 4.0.");
        Map<Boolean, List<Student>> partition = service.partitionByGpa(threshold);

        System.out.println("PASS (GPA ≥ " + threshold + "):");
        partition.get(true).forEach(s -> System.out.println("  " + s));
        System.out.println("FAIL (GPA < " + threshold + "):");
        partition.get(false).forEach(s -> System.out.println("  " + s));
    }

    // ── Case 7: Function / BiFunction / UnaryOperator / Supplier ──────────
    private static void mapAndTransform(StudentService service) {
        // Function<Student, String>: map sang tên
        System.out.println("── Function: Student → name ──");
        Function<Student, String> toName = Student::getName;
        service.mapStudents(toName).forEach(System.out::println);

        // Function<Student, String>: map sang chuỗi tóm tắt
        System.out.println("\n── Function: Student → \"Name (GPA)\" ──");
        Function<Student, String> toSummary = s -> s.getName() + " (" + s.getGpa() + ")";
        service.mapStudents(toSummary).forEach(System.out::println);

        // BiFunction<String, Double, String>: kết hợp hai trường
        System.out.println("\n── BiFunction: name + GPA → grade label ──");
        BiFunction<String, Double, String> gradeLabel = (name, gpa) -> String.format("%-8s → %s", name,
                gpa >= 3.5 ? "A" : gpa >= 3.0 ? "B" : gpa >= 2.0 ? "C" : "F");
        service.buildLabels(gradeLabel).forEach(System.out::println);

        // UnaryOperator<Student>: tăng GPA +0.1 (bonus)
        System.out.println("\n── UnaryOperator: apply +0.1 GPA bonus ──");
        UnaryOperator<Student> bonus = s -> s.withGpa(Math.min(4.0, s.getGpa() + 0.1));
        service.applyToAll(bonus).forEach(System.out::println);

        // Supplier<Student>: tạo sinh viên mặc định khi không tìm thấy
        System.out.println("\n── Supplier: findByIdOrDefault(99) ──");
        Supplier<Student> defaultStudent = () -> new Student(0, "Unknown", 0.0, "N/A", 0);
        System.out.println(service.findByIdOrDefault(99, defaultStudent));
    }

    // ── Case 8: Custom StudentValidator với composition ────────────────────
    private static void validateWithComposedRules(StudentService service) {
        // Ba StudentValidator cơ bản (lambda)
        StudentValidator highGpa = s -> s.getGpa() >= 3.0;
        StudentValidator isSenior = s -> s.getYear() >= 3;
        StudentValidator isCS = s -> s.getMajor().equals("Computer Science");

        // and: GPA ≥ 3.0 VÀ năm 3+
        System.out.println("── GPA ≥ 3.0 AND Year ≥ 3 ──");
        service.validate(highGpa.and(isSenior)).forEach(System.out::println);

        // or: GPA ≥ 3.0 HOẶC là ngành Computer Science
        System.out.println("\n── GPA ≥ 3.0 OR Computer Science ──");
        service.validate(highGpa.or(isCS)).forEach(System.out::println);

        // negate: NOT Computer Science
        System.out.println("\n── NOT Computer Science ──");
        service.validate(isCS.negate()).forEach(System.out::println);

        // StudentFormatter (custom @FunctionalInterface có sẵn)
        System.out.println("\n── StudentFormatter: detailed format ──");
        StudentFormatter detailed = s -> String.format("ID:%-2d | %-8s | GPA:%.1f | %-20s | Yr%d",
                s.getId(), s.getName(), s.getGpa(), s.getMajor(), s.getYear());
        service.printFormatted(detailed);
    }

    // ── Input helpers ─────────────────────────────────────────────────────
    private static int readMenuChoice(Scanner scanner) {
        return readInt(scanner, "Choose: ",
                v -> v >= MIN_MENU_OPTION && v <= MAX_MENU_OPTION,
                "Please select from 1 to " + MAX_MENU_OPTION + ".");
    }

    private static int readInt(Scanner scanner, String prompt,
            IntPredicate validator, String validationMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (validator.test(value))
                    return value;
                System.out.println(validationMessage);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static double readDouble(Scanner scanner, String prompt,
            DoublePredicate validator, String validationMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (validator.test(value))
                    return value;
                System.out.println(validationMessage);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
