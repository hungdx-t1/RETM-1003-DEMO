package tma.example.be.demo;

/**
 * Custom Functional Interface – minh họa định nghĩa FI và composition.
 *
 * <p>
 * Default methods {@code and}, {@code or}, {@code negate} cho phép kết hợp
 * nhiều điều kiện mà không cần viết thêm lớp trung gian.
 */
@FunctionalInterface
public interface StudentValidator {

  boolean validate(Student student);

  /** Kết hợp AND: cả hai điều kiện phải đúng. */
  default StudentValidator and(StudentValidator other) {
    return student -> this.validate(student) && other.validate(student);
  }

  /** Kết hợp OR: ít nhất một điều kiện đúng. */
  default StudentValidator or(StudentValidator other) {
    return student -> this.validate(student) || other.validate(student);
  }

  /** Đảo ngược điều kiện. */
  default StudentValidator negate() {
    return student -> !this.validate(student);
  }
}
