# ❓ Core Java & OOP - Câu Hỏi Phỏng Vấn

> Tổng hợp câu hỏi thường gặp trong phỏng vấn Java Backend.

---

## 🔥 Câu Hỏi Về OOP

### Q1: Bốn nguyên lý của OOP là gì?

**Trả lời:**
1. **Encapsulation (Đóng gói):** Ẩn dữ liệu nội bộ, chỉ expose qua public interface. VD: `private` fields với getter/setter.
2. **Inheritance (Kế thừa):** Class con kế thừa đặc điểm từ class cha, dùng `extends`. Giúp tái sử dụng code.
3. **Polymorphism (Đa hình):** Một method/interface có nhiều hành vi khác nhau. Có 2 loại: Compile-time (overloading) và Runtime (overriding).
4. **Abstraction (Trừu tượng):** Ẩn chi tiết implementation, chỉ expose những gì cần thiết. Thực hiện qua Abstract class và Interface.

---

### Q2: Khác biệt giữa Abstract Class và Interface là gì?

**Trả lời:**

| Tiêu chí | Abstract Class | Interface |
|----------|---------------|-----------|
| Kế thừa | Extends 1 class | Implements nhiều interface |
| Constructor | Có | Không có |
| Fields | Mọi access modifier | Chỉ `public static final` |
| Methods (Java 8+) | Abstract + Concrete | Abstract + Default + Static |
| Khi dùng | Shared base implementation | Định nghĩa contract |

**Khi nào dùng Abstract Class?** → Khi có shared code giữa các subclass (IS-A relationship)  
**Khi nào dùng Interface?** → Khi muốn define contract, một class có thể LIKE-A nhiều thứ

---

### Q3: Overloading vs Overriding khác nhau như thế nào?

**Trả lời:**
- **Overloading (Compile-time polymorphism):** Cùng tên method, khác parameter list (số lượng, kiểu, thứ tự). Xảy ra trong **cùng class**.
- **Overriding (Runtime polymorphism):** Subclass ghi đè method của parent class với **cùng signature**. Xảy ra qua **inheritance**.

```java
// Overloading - cùng class
void print(int x) { }
void print(String s) { }

// Overriding - khác class
class Parent { void show() { System.out.println("Parent"); } }
class Child extends Parent { 
    @Override void show() { System.out.println("Child"); }  // Override
}
```

---

### Q4: `this` và `super` dùng để làm gì?

**Trả lời:**
- **`this`**: Tham chiếu đến instance hiện tại của class. Dùng để:
  - Phân biệt instance variable với local variable
  - Gọi constructor khác trong cùng class (`this()`)
  - Truyền instance hiện tại làm argument
- **`super`**: Tham chiếu đến parent class. Dùng để:
  - Gọi constructor parent (`super()`)
  - Gọi method parent đã bị override (`super.method()`)

---

### Q5: `final`, `finally`, `finalize` khác nhau như thế nào?

**Trả lời:**
- **`final`**: Keyword để ngăn thay đổi. `final variable` = constant, `final method` = không override được, `final class` = không kế thừa được.
- **`finally`**: Block trong try-catch-finally, **luôn luôn** được thực thi dù có exception hay không.
- **`finalize()`**: Method được GC gọi trước khi thu hồi object. **Deprecated** từ Java 9, không nên dùng.

---

### Q6: Static method và Instance method khác nhau thế nào?

**Trả lời:**
- **Static method**: Thuộc về class, gọi qua `ClassName.method()`, không thể access `this` hoặc instance variables.
- **Instance method**: Thuộc về object, cần tạo instance để gọi, có thể access instance variables.

```java
class MathUtil {
    static int add(int a, int b) { return a + b; }  // static
    
    private int multiplier;
    int multiply(int x) { return x * multiplier; }   // instance
}
```

---

### Q7: Garbage Collection hoạt động như thế nào?

**Trả lời:**  
GC tự động thu hồi bộ nhớ của object không còn được reference đến trong Heap:
1. **Marking:** Đánh dấu object nào còn được sử dụng (live objects)
2. **Sweeping:** Thu hồi bộ nhớ của unreachable objects
3. **Compacting:** Compact lại bộ nhớ (tùy thuật toán)

**Các thuật toán GC:**
- **Serial GC**: Single-thread, phù hợp app nhỏ
- **Parallel GC**: Multi-thread, tốt cho throughput
- **G1GC**: Balanced, mặc định từ Java 9
- **ZGC**: Low-latency, phù hợp hệ thống real-time

---

### Q8: String Pool là gì? Tại sao String là immutable?

**Trả lời:**  
**String Pool**: Vùng bộ nhớ đặc biệt trong Heap lưu trữ String literals. Khi tạo `"Hello"`, JVM kiểm tra Pool trước - nếu đã có thì trả reference cũ, nếu chưa thì tạo mới. Giúp tiết kiệm bộ nhớ.

**Tại sao immutable?**
- **Thread safety**: Nhiều thread có thể dùng chung mà không cần sync
- **Security**: Password, URL không bị thay đổi ngoài ý muốn
- **Caching**: HashCode có thể cache
- **String Pool**: Chỉ work khi String immutable

---

### Q9: `==` vs `.equals()` khác nhau thế nào?

**Trả lời:**
- **`==`**: So sánh **reference** (địa chỉ bộ nhớ) với primitive types hoặc object references
- **`.equals()`**: So sánh **nội dung** của object (nếu class override equals())

```java
String s1 = "Hello";
String s2 = "Hello";
String s3 = new String("Hello");

s1 == s2;        // true (cùng Pool reference)
s1 == s3;        // false (khác object)
s1.equals(s3);   // true (cùng nội dung)
```

---

### Q10: Explain Java Memory Model (Stack và Heap)?

**Trả lời:**
- **Stack**: 
  - Lưu method calls, local variables, primitive values
  - LIFO structure, mỗi thread có Stack riêng
  - Tự giải phóng khi method return
  - Nhanh hơn Heap
- **Heap**: 
  - Lưu object instances và arrays
  - Chia sẻ giữa tất cả threads
  - GC quản lý
  - Phân ra: Young Generation, Old Generation, Metaspace

```java
void method() {
    int x = 10;           // Stack (primitive)
    String s = "hello";   // Stack (reference) → Heap (String object)
    Person p = new Person(); // Stack (reference) → Heap (Person object)
}
```

---

## 🔥 Câu Hỏi Về Java Basics

### Q11: JDK, JRE, JVM khác nhau ra sao?

**Trả lời:**
- **JVM (Java Virtual Machine)**: Thực thi bytecode, trừu tượng hóa phần cứng
- **JRE (Java Runtime Environment)**: JVM + thư viện chuẩn Java (chỉ để chạy)
- **JDK (Java Development Kit)**: JRE + compiler, debugger, tools (để phát triển)

---

### Q12: Java có pass-by-value hay pass-by-reference?

**Trả lời:**  
Java luôn là **pass-by-value**, KHÔNG có pass-by-reference.

- Với **primitive**: copy giá trị thực
- Với **object**: copy giá trị của **reference** (địa chỉ), không phải copy object

```java
void modify(int x) { x = 100; }       // Không ảnh hưởng gốc
void modify(int[] arr) { arr[0] = 100; } // Ảnh hưởng vì copy reference đến cùng array
```

---

### Q13: Checked Exception vs Unchecked Exception?

**Trả lời:**
- **Checked Exception**: Phải handle lúc compile time (try-catch hoặc throws). VD: `IOException`, `SQLException`
- **Unchecked Exception** (RuntimeException): Không bắt buộc handle. VD: `NullPointerException`, `ArrayIndexOutOfBoundsException`
- **Error**: Không nên catch (hệ thống level). VD: `OutOfMemoryError`, `StackOverflowError`

---

### Q14: `StringBuilder` vs `StringBuffer` khác nhau như thế nào?

**Trả lời:**
- **StringBuilder**: Mutable, **KHÔNG thread-safe**, hiệu suất cao hơn → dùng trong single-thread
- **StringBuffer**: Mutable, **Thread-safe** (synchronized methods) → dùng trong multi-thread

---

### Q15: Wrapper Classes là gì? Autoboxing là gì?

**Trả lời:**  
**Wrapper Classes**: Các class bao bọc primitive types (Integer, Double, Boolean, Character...). Cần thiết khi dùng Collections (chỉ chấp nhận Object).

**Autoboxing**: Tự động convert primitive → Wrapper  
**Unboxing**: Tự động convert Wrapper → primitive

```java
Integer i = 42;   // Autoboxing: int → Integer
int j = i;        // Unboxing: Integer → int
```

**Lưu ý performance**: Autoboxing trong vòng lặp lớn tạo ra nhiều object → cân nhắc

---

## 📝 Mock Interview

**Câu hỏi tổng hợp (tự trả lời thành lời trong 2 phút):**

1. "Giải thích cho tôi nghe về 4 nguyên lý OOP với ví dụ thực tế"
2. "Khi nào bạn dùng Abstract Class, khi nào dùng Interface?"
3. "Chuyện gì xảy ra nếu bạn override equals() mà không override hashCode()?"
4. "Tại sao Java không có multiple inheritance với class?"
5. "Giải thích String immutability và tác động hiệu suất của nó"

---

*📌 Xem tiếp: [Theory.md](Theory.md) | [Practice-Exercises.md](Practice-Exercises.md)*
