# 📖 Core Java & OOP - Lý Thuyết

> **Tuần 1 | Mức độ: Nền tảng bắt buộc**

---

## 1. Kiến Trúc Java (JVM, JDK, JRE)

```
JDK (Java Development Kit)
  └── JRE (Java Runtime Environment)
        └── JVM (Java Virtual Machine)
              ├── Class Loader
              ├── Runtime Memory Areas
              │     ├── Heap (object storage)
              │     ├── Stack (method frames)
              │     ├── Method Area (class metadata)
              │     └── PC Register / Native Stack
              └── Execution Engine
                    ├── Interpreter
                    ├── JIT Compiler
                    └── Garbage Collector
```

**JDK:** Bộ công cụ phát triển Java (bao gồm compiler `javac`, debugger, tools)  
**JRE:** Môi trường chạy Java (JVM + thư viện chuẩn)  
**JVM:** Máy ảo thực thi bytecode → cho phép Java "Write Once, Run Anywhere"

---

## 2. Các Kiểu Dữ Liệu Java

### Primitive Types (8 kiểu)

| Kiểu | Kích thước | Giá trị mặc định | Ví dụ |
|------|-----------|-----------------|-------|
| `byte` | 8-bit | 0 | `byte b = 127;` |
| `short` | 16-bit | 0 | `short s = 30000;` |
| `int` | 32-bit | 0 | `int i = 100;` |
| `long` | 64-bit | 0L | `long l = 100L;` |
| `float` | 32-bit | 0.0f | `float f = 3.14f;` |
| `double` | 64-bit | 0.0d | `double d = 3.14;` |
| `char` | 16-bit | '\u0000' | `char c = 'A';` |
| `boolean` | 1-bit | false | `boolean flag = true;` |

### Reference Types
- **String**: Immutable, stored in String Pool
- **Array**: Fixed-size collection
- **Object**: Parent của mọi class

---

## 3. Bốn Nguyên Lý OOP

### 3.1 Encapsulation (Đóng gói)
Che giấu dữ liệu nội bộ, chỉ expose qua public methods.

```java
public class BankAccount {
    private double balance;  // private = ẩn dữ liệu
    private String owner;

    // Getter - chỉ đọc
    public double getBalance() {
        return balance;
    }

    // Setter - kiểm soát thay đổi
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }
}
```

**Lợi ích:** Bảo vệ dữ liệu, dễ thay đổi implementation, tăng maintainability.

---

### 3.2 Inheritance (Kế thừa)
Class con kế thừa thuộc tính và phương thức từ class cha.

```java
// Class cha
public class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public void eat() {
        System.out.println(name + " đang ăn");
    }

    public void makeSound() {
        System.out.println("...");
    }
}

// Class con
public class Dog extends Animal {
    private String breed;

    public Dog(String name, String breed) {
        super(name);  // gọi constructor cha
        this.breed = breed;
    }

    @Override  // Override method cha
    public void makeSound() {
        System.out.println(name + " sủa: Woof!");
    }

    // Method riêng của Dog
    public void fetch() {
        System.out.println(name + " đi lấy đồ!");
    }
}
```

**Lưu ý:**
- Java chỉ hỗ trợ **single inheritance** (1 cha duy nhất)
- Dùng `super` để gọi constructor/method cha
- `final class` không thể bị kế thừa

---

### 3.3 Polymorphism (Đa hình)

**Compile-time (Overloading):** Cùng tên method, khác tham số.
```java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public double add(double a, double b) { return a + b; }
    public int add(int a, int b, int c) { return a + b + c; }
}
```

**Runtime (Overriding):** Class con ghi đè method class cha.
```java
Animal animal = new Dog("Rex", "Labrador");
animal.makeSound();  // In ra: "Rex sủa: Woof!" → đây là runtime polymorphism
```

---

### 3.4 Abstraction (Trừu tượng)

**Abstract Class:**
```java
public abstract class Shape {
    protected String color;

    public Shape(String color) {
        this.color = color;
    }

    // Abstract method - bắt buộc subclass phải implement
    public abstract double calculateArea();

    // Concrete method - có implementation
    public void displayColor() {
        System.out.println("Màu: " + color);
    }
}

public class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}
```

**Interface:**
```java
public interface Drawable {
    void draw();  // abstract by default
    
    default void print() {  // default method (Java 8+)
        System.out.println("Đang in...");
    }
    
    static void description() {  // static method (Java 8+)
        System.out.println("Interface Drawable");
    }
}

public interface Resizable {
    void resize(double factor);
}

// Class implement nhiều interface
public class Square extends Shape implements Drawable, Resizable {
    private double side;

    public Square(String color, double side) {
        super(color);
        this.side = side;
    }

    @Override
    public double calculateArea() { return side * side; }

    @Override
    public void draw() { System.out.println("Vẽ hình vuông"); }

    @Override
    public void resize(double factor) { this.side *= factor; }
}
```

---

## 4. Abstract Class vs Interface

| Tiêu chí | Abstract Class | Interface |
|----------|---------------|-----------|
| Từ khóa | `abstract class` | `interface` |
| Kế thừa | Extends 1 class | Implements nhiều interface |
| Constructor | Có | Không có |
| Fields | Có (mọi access modifier) | Chỉ `public static final` |
| Methods | Abstract + Concrete | Abstract + Default + Static |
| Access Modifier | Mọi loại | `public` mặc định |
| Khi nào dùng | Có shared code, IS-A relationship | Define contract, LIKE-A |

---

## 5. Java Keywords Quan Trọng

| Keyword | Mô tả |
|---------|-------|
| `static` | Thuộc về class, không phải instance |
| `final` | Không thể thay đổi (biến), override (method), kế thừa (class) |
| `this` | Tham chiếu đến instance hiện tại |
| `super` | Tham chiếu đến class cha |
| `instanceof` | Kiểm tra kiểu đối tượng |
| `volatile` | Đảm bảo visibility trong multi-threading |
| `synchronized` | Đồng bộ hóa thread |
| `transient` | Bỏ qua khi serialize |
| `native` | Method được implement bằng ngôn ngữ native |

---

## 6. Access Modifiers

| Modifier | Class | Package | Subclass | World |
|----------|-------|---------|----------|-------|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| *(default)* | ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

---

## 7. Garbage Collection

- JVM tự động quản lý bộ nhớ (heap)
- **GC** thu hồi object không còn được reference đến
- Thuật toán GC phổ biến: **Mark-and-Sweep**, **G1GC**, **ZGC**
- Không thể gọi GC trực tiếp, chỉ gợi ý: `System.gc()`
- **Memory leaks** xảy ra khi reference không được giải phóng (VD: static collections)

---

## 8. String Pool & String Immutability

```java
String s1 = "Hello";          // từ String Pool
String s2 = "Hello";          // cùng reference trong Pool
String s3 = new String("Hello"); // object mới trên Heap

System.out.println(s1 == s2);      // true (cùng reference)
System.out.println(s1 == s3);      // false (khác object)
System.out.println(s1.equals(s3)); // true (cùng nội dung)

// String là immutable:
String str = "Java";
str = str + " Developer";  // tạo String object MỚI, str cũ không đổi
```

**StringBuilder vs StringBuffer:**
- `StringBuilder`: Mutable, KHÔNG thread-safe, nhanh hơn
- `StringBuffer`: Mutable, Thread-safe (synchronized), chậm hơn

---

## 9. Wrapper Classes & Autoboxing

```java
// Primitive → Wrapper (Boxing)
Integer i = Integer.valueOf(42);
Integer j = 42;  // Autoboxing

// Wrapper → Primitive (Unboxing)
int x = i.intValue();
int y = i;  // Auto-unboxing

// Utility methods
Integer.parseInt("123");    // String → int
Integer.toString(123);      // int → String
Integer.MAX_VALUE;          // 2147483647
```

---

## 10. Equals & HashCode Contract

```java
public class Person {
    private String name;
    private int age;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
```

**Quy tắc:**
- Nếu `a.equals(b)` → `a.hashCode() == b.hashCode()` (bắt buộc)
- Nếu `a.hashCode() == b.hashCode()` → không chắc `a.equals(b)` (collision)
- Override equals thì **phải** override hashCode

---

*📌 Tiếp theo: [Interview Q&A](Interview-QA.md) | [Bài tập thực hành](Practice-Exercises.md)*
