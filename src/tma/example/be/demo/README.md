# Demo: Functional Interface, Lambda, Stream API, Optional

## Cấu trúc file

```
demo/
├── Student.java           # Model: id, name, gpa, major, year
├── StudentFormatter.java  # Custom @FunctionalInterface (1 abstract method)
├── StudentValidator.java  # Custom @FunctionalInterface + default method composition
├── StudentService.java    # Business logic – nơi áp dụng tất cả các khái niệm
└── Main.java              # Entry point – menu 9 tùy chọn, mỗi tùy chọn demo 1 nhóm
```

---

## Luồng hoạt động tổng quát

```
Main.main()
  └─ vòng lặp while(true)
       ├─ printMenu()          → in 9 tùy chọn
       ├─ readMenuChoice()     → đọc input, validate bằng IntPredicate
       └─ switch(choice)
            ├─ case 1  showAllStudents()          [Consumer]
            ├─ case 2  filterStudentsByGpa()      [Predicate + composition]
            ├─ case 3  findStudentById()           [Optional chain]
            ├─ case 4  showStatistics()            [Stream terminal ops]
            ├─ case 5  sortAndDisplay()            [Comparator + sorted]
            ├─ case 6  groupAndPartition()         [groupingBy / partitioningBy]
            ├─ case 7  mapAndTransform()           [Function / BiFunction / UnaryOperator / Supplier]
            ├─ case 8  validateWithComposedRules() [Custom Functional Interface]
            └─ case 9  exit
```

---

## 1. Functional Interface & Lambda

### Định nghĩa

Interface có **đúng 1 abstract method** → có thể implement bằng **lambda** thay vì anonymous class.

### `StudentFormatter` — 1 abstract method thuần túy

```java
@FunctionalInterface
public interface StudentFormatter {
    String format(Student student);   // abstract method duy nhất
}
```

Sử dụng (case 8):

```java
StudentFormatter detailed = s ->
    String.format("ID:%-2d | %-8s | GPA:%.1f ...", s.getId(), ...);
service.printFormatted(detailed);
```

### `StudentValidator` — FI với default method composition

```java
@FunctionalInterface
public interface StudentValidator {
    boolean validate(Student student);

    default StudentValidator and(StudentValidator other) {
        return student -> this.validate(student) && other.validate(student);
    }
    default StudentValidator or(StudentValidator other) {
        return student -> this.validate(student) || other.validate(student);
    }
    default StudentValidator negate() {
        return student -> !this.validate(student);
    }
}
```

`default method` không phá vỡ tính FI, nhưng cho phép **compose** nhiều điều kiện:

```java
// case 8
StudentValidator highGpa  = s -> s.getGpa() >= 3.0;
StudentValidator isSenior = s -> s.getYear() >= 3;
StudentValidator isCS     = s -> s.getMajor().equals("Computer Science");

service.validate(highGpa.and(isSenior));  // GPA ≥ 3.0 VÀ năm 3+
service.validate(highGpa.or(isCS));       // GPA ≥ 3.0 HOẶC ngành CS
service.validate(isCS.negate());          // KHÔNG phải ngành CS
```

---

## 2. Built-in Functional Interfaces (`java.util.function`)

| Interface           | Method                 | Mô tả ngắn                     | Demo         |
| ------------------- | ---------------------- | ------------------------------ | ------------ |
| `Consumer<T>`       | `void accept(T)`       | Nhận T, không trả về           | case 1       |
| `Predicate<T>`      | `boolean test(T)`      | Kiểm tra điều kiện             | case 2       |
| `Function<T,R>`     | `R apply(T)`           | Biến đổi T → R                 | case 7       |
| `BiFunction<T,U,R>` | `R apply(T,U)`         | Biến đổi (T, U) → R            | case 7       |
| `UnaryOperator<T>`  | `T apply(T)`           | Biến đổi T → T (cùng kiểu)     | case 7       |
| `Supplier<T>`       | `T get()`              | Không nhận, trả về T           | case 7       |
| `IntPredicate`      | `boolean test(int)`    | Predicate cho primitive int    | input helper |
| `DoublePredicate`   | `boolean test(double)` | Predicate cho primitive double | input helper |

### Consumer (case 1)

```java
Consumer<Student> printer = System.out::println;  // method reference
service.printStudents(printer);
// StudentService: students.forEach(action) → gọi accept() cho từng phần tử
```

### Predicate + `.and()` composition (case 2)

```java
Predicate<Student> highGpa   = s -> s.getGpa() > minGpa;
Predicate<Student> validName = s -> !s.getName().isBlank();
Predicate<Student> combined  = highGpa.and(validName);  // built-in composition
service.filterStudents(combined);
```

### Function (case 7)

```java
Function<Student, String> toName    = Student::getName;            // method ref
Function<Student, String> toSummary = s -> s.getName() + " ("+s.getGpa()+")"; // lambda
service.mapStudents(toName);
// StudentService: students.stream().map(mapper).toList()
```

### BiFunction (case 7)

```java
BiFunction<String, Double, String> gradeLabel =
    (name, gpa) -> String.format("%-8s → %s", name, gpa >= 3.5 ? "A" : "B"...);
service.buildLabels(gradeLabel);
// StudentService: .map(s -> labelBuilder.apply(s.getName(), s.getGpa()))
```

### UnaryOperator (case 7)

```java
UnaryOperator<Student> bonus = s -> s.withGpa(Math.min(4.0, s.getGpa() + 0.1));
service.applyToAll(bonus);  // input Student → output Student (cùng kiểu)
```

### Supplier (case 7)

```java
Supplier<Student> defaultStudent = () -> new Student(0, "Unknown", 0.0, "N/A", 0);
service.findByIdOrDefault(99, defaultStudent);
// StudentService: findById(id).orElseGet(defaultSupplier)
//   → Supplier.get() chỉ được gọi khi Optional rỗng (lazy)
```

---

## 3. Stream API

### Pipeline

```
Collection.stream()
  → [intermediate ops: filter, map, sorted, ...]  (lazy – chưa thực thi)
  → [terminal op: toList, count, max, reduce, collect, ...]  (kích hoạt)
```

| Operation                 | Loại         | Dùng ở đâu                                      |
| ------------------------- | ------------ | ----------------------------------------------- |
| `filter(Predicate)`       | intermediate | filterStudents, countPassing, findById          |
| `map(Function)`           | intermediate | mapStudents, buildLabels, applyToAll            |
| `mapToDouble`             | intermediate | averageGpa, sumGpa                              |
| `sorted(Comparator)`      | intermediate | sortedBy                                        |
| `findFirst()`             | terminal     | findById → Optional                             |
| `toList()`                | terminal     | filterStudents, mapStudents, sortedBy, validate |
| `count()`                 | terminal     | countPassing                                    |
| `average()`               | terminal     | averageGpa → OptionalDouble                     |
| `reduce()`                | terminal     | sumGpa                                          |
| `max(Comparator)`         | terminal     | findTopStudent → Optional                       |
| `min(Comparator)`         | terminal     | findLowestStudent → Optional                    |
| `collect(groupingBy)`     | terminal     | groupByMajor → Map<String, List>                |
| `collect(partitioningBy)` | terminal     | partitionByGpa → Map<Boolean, List>             |

### Ví dụ tiêu biểu

**filter + toList** (StudentService):

```java
return students.stream()
    .filter(condition)   // giữ phần tử thỏa Predicate
    .toList();
```

**mapToDouble + average** (StudentService):

```java
return students.stream()
    .mapToDouble(Student::getGpa)  // DoubleStream, tránh boxing
    .average()                      // OptionalDouble
    .orElse(0.0);
```

**mapToDouble + reduce** (StudentService):

```java
return students.stream()
    .mapToDouble(Student::getGpa)
    .reduce(0.0, Double::sum);     // identity=0.0, accumulator=cộng dồn
```

**sorted + thenComparing** (Main case 5):

```java
service.sortedBy(
    Comparator.comparingInt(Student::getYear)
              .thenComparing(Comparator.comparingDouble(Student::getGpa).reversed())
);
```

**groupingBy** (StudentService):

```java
return students.stream()
    .collect(Collectors.groupingBy(Student::getMajor));
// → Map< "Computer Science" → [Anna, David, Lucas],
//        "Mathematics"      → [John, Mike, Mia], ... >
```

**partitioningBy** (StudentService):

```java
return students.stream()
    .collect(Collectors.partitioningBy(s -> s.getGpa() >= threshold));
// → Map< true → [sinh viên đạt], false → [sinh viên không đạt] >
```

---

## 4. Optional

`Optional<T>` là container **có thể có hoặc không có** giá trị — tránh `null` và `NullPointerException`.

| Method                                | Hành vi                                                       |
| ------------------------------------- | ------------------------------------------------------------- |
| `ifPresentOrElse(Consumer, Runnable)` | Chạy Consumer nếu có, Runnable nếu rỗng                       |
| `filter(Predicate)`                   | Nếu có giá trị & thỏa điều kiện → giữ; ngược lại → empty      |
| `map(Function)`                       | Nếu có → biến đổi; rỗng → vẫn empty                           |
| `orElse(T)`                           | Trả giá trị mặc định nếu rỗng (luôn tính sẵn)                 |
| `orElseGet(Supplier)`                 | Trả giá trị từ Supplier nếu rỗng (**lazy** – chỉ gọi khi cần) |
| `orElseThrow(Supplier)`               | Ném exception nếu rỗng                                        |
| `ifPresent(Consumer)`                 | Chạy Consumer nếu có giá trị, bỏ qua nếu rỗng                 |

### Chuỗi Optional (case 3)

```java
// findById → Optional.filter → Optional.map → orElse
String name = service.findNameById(id)
    .orElse("N/A");

// StudentService:
return findById(id)
    .filter(s -> s.getGpa() >= 2.0)  // rỗng nếu GPA < 2.0
    .map(Student::getName);           // lấy tên nếu còn giá trị
```

### orElse vs orElseGet

```java
// orElse: Student mặc định được tạo DÙ tìm thấy hay không
student.orElse(new Student(...));

// orElseGet: Supplier chỉ được gọi KHI Optional rỗng (lazy – hiệu quả hơn)
student.orElseGet(() -> new Student(...));
```

### OptionalDouble từ Stream

```java
students.stream()
    .mapToDouble(Student::getGpa)
    .average()          // OptionalDouble – rỗng nếu stream không có phần tử
    .orElse(0.0);
```
