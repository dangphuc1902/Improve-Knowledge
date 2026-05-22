# 💻 Core Java & OOP - Bài Tập Thực Hành

> Các bài tập coding để củng cố lý thuyết OOP.

---

## Bài Tập 1: Encapsulation - BankAccount

**Yêu cầu:** Xây dựng class `BankAccount` với các tính năng:
- Thuộc tính private: `accountNumber`, `owner`, `balance`
- Nạp tiền (deposit) với validation
- Rút tiền (withdraw) với validation
- Chuyển khoản (transfer) giữa 2 tài khoản

```java
public class BankAccount {
    private String accountNumber;
    private String owner;
    private double balance;

    public BankAccount(String accountNumber, String owner, double initialBalance) {
        // TODO: Validate initialBalance >= 0
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = initialBalance;
    }

    // TODO: Implement deposit(double amount) - amount phải > 0
    public boolean deposit(double amount) {
        // Your code here
        return false;
    }

    // TODO: Implement withdraw(double amount) - không được rút hơn balance
    public boolean withdraw(double amount) {
        // Your code here
        return false;
    }

    // TODO: Implement transfer(BankAccount target, double amount)
    public boolean transfer(BankAccount target, double amount) {
        // Your code here
        return false;
    }

    // Getters
    public double getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public String getOwner() { return owner; }

    @Override
    public String toString() {
        return String.format("Account[%s, Owner: %s, Balance: %.2f]",
                accountNumber, owner, balance);
    }
}

// Test class
public class BankTest {
    public static void main(String[] args) {
        BankAccount acc1 = new BankAccount("ACC001", "Nguyễn Văn A", 1000.0);
        BankAccount acc2 = new BankAccount("ACC002", "Trần Thị B", 500.0);

        acc1.deposit(500);       // balance = 1500
        acc1.withdraw(200);      // balance = 1300
        acc1.transfer(acc2, 300); // acc1=1000, acc2=800

        System.out.println(acc1);
        System.out.println(acc2);
    }
}
```

---

## Bài Tập 2: Inheritance & Polymorphism - Shape Hierarchy

**Yêu cầu:** Xây dựng hệ thống hình học với OOP.

```java
// Base abstract class
public abstract class Shape {
    protected String color;

    public Shape(String color) {
        this.color = color;
    }

    public abstract double area();
    public abstract double perimeter();

    public void describe() {
        System.out.printf("Hình %s màu %s: Diện tích=%.2f, Chu vi=%.2f%n",
                getClass().getSimpleName(), color, area(), perimeter());
    }
}

// TODO: Implement Circle extends Shape
public class Circle extends Shape {
    private double radius;
    // Constructor, area(), perimeter()
}

// TODO: Implement Rectangle extends Shape
public class Rectangle extends Shape {
    private double width, height;
    // Constructor, area(), perimeter()
}

// TODO: Implement Triangle extends Shape
public class Triangle extends Shape {
    private double a, b, c; // 3 cạnh
    // Constructor, area() (Heron's formula), perimeter()
}

// Test polymorphism
public class ShapeTest {
    public static void main(String[] args) {
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Circle("đỏ", 5.0));
        shapes.add(new Rectangle("xanh", 4.0, 6.0));
        shapes.add(new Triangle("vàng", 3.0, 4.0, 5.0));

        // Polymorphism in action
        for (Shape shape : shapes) {
            shape.describe();  // Gọi đúng method của từng subclass
        }

        // Tìm shape có diện tích lớn nhất
        Shape largest = shapes.stream()
                .max(Comparator.comparingDouble(Shape::area))
                .orElseThrow();
        System.out.println("Diện tích lớn nhất: " + largest.area());
    }
}
```

---

## Bài Tập 3: Interface - Design Hệ Thống Thanh Toán

**Yêu cầu:** Thiết kế hệ thống thanh toán với các phương thức khác nhau.

```java
// Interface thanh toán
public interface Payable {
    boolean processPayment(double amount);
    String getPaymentMethod();
    
    default void printReceipt(double amount) {
        System.out.printf("Thanh toán %,.0f VND qua %s%n", amount, getPaymentMethod());
    }
}

// Interface hoàn tiền
public interface Refundable {
    boolean processRefund(double amount);
}

// TODO: Implement CreditCard - implements Payable, Refundable
public class CreditCard implements Payable, Refundable {
    private String cardNumber;
    private double creditLimit;

    // TODO: Implement all methods
}

// TODO: Implement BankTransfer - implements Payable only
public class BankTransfer implements Payable {
    private String bankAccount;
    // TODO: Implement all methods
}

// TODO: Implement EWallet (ví điện tử) - implements Payable, Refundable
public class EWallet implements Payable, Refundable {
    private double balance;
    // TODO: Implement all methods
}

// Test
public class PaymentTest {
    public static void main(String[] args) {
        Payable payment = new CreditCard("4111-1111-1111-1111", 10000000);
        payment.processPayment(500000);
        payment.printReceipt(500000);

        if (payment instanceof Refundable refundable) {  // Java 16 pattern matching
            refundable.processRefund(100000);
        }
    }
}
```

---

## Bài Tập 4: Static & Final

**Yêu cầu:** Implement Singleton pattern (preview cho Design Patterns).

```java
// Singleton - chỉ có 1 instance trong toàn bộ app
public class DatabaseConnection {
    private static DatabaseConnection instance;  // static field
    private String connectionUrl;
    private int connectionCount = 0;
    
    // Private constructor - ngăn tạo instance từ bên ngoài
    private DatabaseConnection() {
        this.connectionUrl = "jdbc:mysql://localhost:3306/mydb";
    }

    // TODO: Implement getInstance() thread-safe
    public static DatabaseConnection getInstance() {
        // Gợi ý: dùng synchronized hoặc double-checked locking
        // Your code here
        return null;
    }

    public void connect() {
        connectionCount++;
        System.out.println("Kết nối #" + connectionCount + " tới " + connectionUrl);
    }
}

// Test Singleton
public class SingletonTest {
    public static void main(String[] args) {
        DatabaseConnection conn1 = DatabaseConnection.getInstance();
        DatabaseConnection conn2 = DatabaseConnection.getInstance();

        System.out.println("Cùng instance? " + (conn1 == conn2));  // Phải là true

        conn1.connect();
        conn2.connect();  // connectionCount phải là 2
    }
}
```

---

## Bài Tập 5: Equals & HashCode

**Yêu cầu:** Implement equals() và hashCode() đúng chuẩn.

```java
public class Student {
    private String studentId;
    private String name;
    private double gpa;

    public Student(String studentId, String name, double gpa) {
        this.studentId = studentId;
        this.name = name;
        this.gpa = gpa;
    }

    // TODO: Override equals() - hai Student bằng nhau khi có cùng studentId
    @Override
    public boolean equals(Object o) {
        // Your code here
        return false;
    }

    // TODO: Override hashCode() consistent với equals()
    @Override
    public int hashCode() {
        // Your code here
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Student[%s, %s, GPA=%.1f]", studentId, name, gpa);
    }
}

// Test
public class StudentTest {
    public static void main(String[] args) {
        Student s1 = new Student("SV001", "Nguyễn A", 8.5);
        Student s2 = new Student("SV001", "Nguyễn A (copy)", 7.0);  // cùng ID
        Student s3 = new Student("SV002", "Trần B", 9.0);

        System.out.println("s1.equals(s2): " + s1.equals(s2));  // true (cùng ID)
        System.out.println("s1.equals(s3): " + s1.equals(s3));  // false

        // Test HashSet (dựa vào equals + hashCode)
        Set<Student> students = new HashSet<>();
        students.add(s1);
        students.add(s2);  // Nếu equals() đúng, không thêm vào (duplicate)
        students.add(s3);
        System.out.println("Số sinh viên (unique): " + students.size());  // Phải là 2
    }
}
```

---

## Bài Tập 6: Bài Tổng Hợp - Hệ Thống Quản Lý Nhân Viên

**Yêu cầu:** Xây dựng hệ thống quản lý nhân viên sử dụng đầy đủ OOP.

```java
// Enum cho loại nhân viên
public enum EmployeeType {
    FULL_TIME, PART_TIME, CONTRACT
}

// Interface tính lương
public interface Compensable {
    double calculateSalary();
    double calculateBonus();
    
    default double totalCompensation() {
        return calculateSalary() + calculateBonus();
    }
}

// Abstract class Employee
public abstract class Employee implements Compensable {
    protected String id;
    protected String name;
    protected String department;
    protected EmployeeType type;

    public Employee(String id, String name, String department, EmployeeType type) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.type = type;
    }

    // Abstract: subclass tự implement
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("[%s] %s - %s | %s | Lương: %,.0f VND",
                id, name, getRole(), department, totalCompensation());
    }
}

// TODO: Implement FullTimeEmployee
// - Có baseSalary
// - calculateSalary() = baseSalary
// - calculateBonus() = 10% baseSalary
public class FullTimeEmployee extends Employee {
    // Your code here
}

// TODO: Implement PartTimeEmployee
// - Có hourlyRate và hoursWorked
// - calculateSalary() = hourlyRate * hoursWorked
// - calculateBonus() = 0
public class PartTimeEmployee extends Employee {
    // Your code here
}

// TODO: Implement Manager extends FullTimeEmployee
// - Có thêm teamBonus
// - Override calculateBonus() = 15% baseSalary + teamBonus
public class Manager extends FullTimeEmployee {
    // Your code here
}

// TODO: Implement EmployeeManager (quản lý danh sách)
public class EmployeeManager {
    private List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee e) { employees.add(e); }

    // TODO: Lấy tất cả employee của một department
    public List<Employee> getByDepartment(String dept) { return null; }

    // TODO: Tính tổng chi phí lương
    public double totalPayroll() { return 0; }

    // TODO: Sắp xếp theo lương giảm dần
    public List<Employee> sortBySalaryDesc() { return null; }

    public void printAll() {
        employees.forEach(System.out::println);
    }
}

// Main test
public class Main {
    public static void main(String[] args) {
        EmployeeManager mgr = new EmployeeManager();

        mgr.addEmployee(new FullTimeEmployee("E001", "Nguyễn A", "IT", 20_000_000));
        mgr.addEmployee(new PartTimeEmployee("E002", "Trần B", "Marketing", 150_000, 80));
        mgr.addEmployee(new Manager("E003", "Lê C", "IT", 35_000_000, 5_000_000));
        mgr.addEmployee(new FullTimeEmployee("E004", "Phạm D", "IT", 18_000_000));

        System.out.println("=== DANH SÁCH NHÂN VIÊN ===");
        mgr.printAll();

        System.out.println("\n=== NHÂN VIÊN PHÒNG IT ===");
        mgr.getByDepartment("IT").forEach(System.out::println);

        System.out.printf("%n=== TỔNG CHI PHÍ LƯƠNG: %,.0f VND ===%n", mgr.totalPayroll());
    }
}
```

---

## 📊 Đáp Án Gợi Ý

Sau khi tự làm, hãy đối chiếu:
1. **BankAccount**: Validate trong setter, sử dụng `synchronized` nếu cần thread-safe
2. **Shape**: Dùng Heron's formula: `s = (a+b+c)/2, area = sqrt(s*(s-a)*(s-b)*(s-c))`
3. **Singleton**: Double-checked locking với `volatile` keyword
4. **Equals/HashCode**: Dùng `Objects.equals()` và `Objects.hash()`

---

*📌 Tiếp theo: [02-Advanced-Java/Theory.md](../02-Advanced-Java/Theory.md)*
