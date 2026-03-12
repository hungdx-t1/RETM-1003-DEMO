package tma.example.be.demo;

import java.util.*;
import java.util.function.*;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        StudentService service = new StudentService();

        while (true) {

            System.out.println("\n===== STUDENT MANAGEMENT =====");
            System.out.println("1. Show all students");
            System.out.println("2. Filter students by GPA");
            System.out.println("3. Find student by ID");
            System.out.println("4. Average GPA");
            System.out.println("5. Show formatted students");
            System.out.println("6. Exit");

            System.out.print("Choose: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:

                    Consumer<Student> printer = s -> System.out.println(s);
                    service.printStudents(printer);

                    break;

                case 2:

                    System.out.print("Enter GPA: ");
                    double gpa = Double.parseDouble(scanner.nextLine());

                    Predicate<Student> condition = s -> s.getGpa() > gpa;

                    service.filterStudents(condition)
                            .forEach(System.out::println);

                    break;

                case 3:

                    System.out.print("Enter ID: ");
                    int id = scanner.nextInt();

                    Optional<Student> student = service.findById(id);

                    student.ifPresentOrElse(
                            System.out::println,
                            () -> System.out.println("Student not found")
                    );

                    break;

                case 4:

                    System.out.println("Average GPA: " + service.averageGpa());

                    break;

                case 5:

                    StudentFormatter formatter =
                            s -> s.getId() + " | " + s.getName()
                                    + " | GPA: " + s.getGpa();

                    service.printFormatted(formatter);

                    break;

                case 6:

                    System.out.println("Goodbye!");
                    return;
            }
        }
    }
}
