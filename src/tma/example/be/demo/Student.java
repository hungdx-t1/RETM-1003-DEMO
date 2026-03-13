package tma.example.be.demo;

class Student {

    private int id;
    private String name;
    private double gpa;
    private String major;
    private int year;

    public Student(int id, String name, double gpa, String major, int year) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
        this.major = major;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getGpa() {
        return gpa;
    }

    public String getMajor() {
        return major;
    }

    public int getYear() {
        return year;
    }

    /** Trả về bản sao Student với GPA mới (dùng cho UnaryOperator). */
    public Student withGpa(double newGpa) {
        return new Student(id, name, newGpa, major, year);
    }

    @Override
    public String toString() {
        return String.format("[%d] %-8s | GPA: %.1f | %-20s | Year %d",
                id, name, gpa, major, year);
    }
}
