# CPU Scheduling
# Lập lịch CPU

## Concept Explanation
## Giải thích khái niệm
The CPU Scheduler is an OS component that decides which process or thread runs at a given point in time. It aims to maximize CPU utilization, throughput, and fairness while minimizing wait times and response times.
Bộ lập lịch CPU là một thành phần của HĐH quyết định quá trình hoặc luồng nào chạy tại một thời điểm nhất định. Nó nhằm mục đích tối đa hóa việc sử dụng CPU, thông lượng và sự công bằng đồng thời giảm thiểu thời gian chờ đợi và thời gian phản hồi.

### Common Scheduling Algorithms
### Các thuật toán lập lịch phổ biến

1. **First-Come, First-Served (FCFS)**:
1. **Đến trước, phục vụ trước (FCFS)**:
   - Processes are executed in the order they arrive.
   - Các quá trình được thực hiện theo thứ tự chúng đến.
   - Non-preemptive.
   - Không ưu tiên.
   - **Problem**: "Convoy Effect" - short processes get stuck waiting for a long process to finish.
   - **Vấn đề**: "Hiệu ứng đoàn xe" - các quá trình ngắn bị kẹt lại chờ một quá trình dài kết thúc.

2. **Shortest Job First (SJF)**:
2. **Công việc ngắn nhất trước (SJF)**:
   - Preemptive or Non-Preemptive.
   - Ưu tiên hoặc không ưu tiên.
   - Executes the process with the shortest estimated burst time.
   - Thực hiện quá trình với thời gian bùng nổ ước tính ngắn nhất.
   - **Problem**: "Starvation" - long processes may never run if short processes keep arriving.
   - **Vấn đề**: "Đói" - các quá trình dài có thể không bao giờ chạy nếu các quá trình ngắn liên tục đến.

3. **Round Robin (RR)**:
3. **Xoay vòng (RR)**:
   - Each process is assigned a fixed time slot (Time Quantum).
   - Mỗi quá trình được gán một khoảng thời gian cố định (Lượng tử thời gian).
   - If the process doesn't finish within the quantum, it is preempted and put at the back of the queue.
   - Nếu quá trình không kết thúc trong lượng tử, nó sẽ bị ưu tiên và đặt ở cuối hàng đợi.
   - Great for time-sharing systems (like your PC).
   - Tuyệt vời cho các hệ thống chia sẻ thời gian (như PC của bạn).

4. **Multilevel Feedback Queue**:
4. **Hàng đợi phản hồi đa cấp**:
   - Multiple queues with different priority levels.
   - Nhiều hàng đợi với các mức độ ưu tiên khác nhau.
   - Processes can move between queues (e.g., if a process uses too much CPU, it's demoted to a lower priority queue).
   - Các quá trình có thể di chuyển giữa các hàng đợi (ví dụ: nếu một quá trình sử dụng quá nhiều CPU, nó sẽ bị hạ xuống hàng đợi có mức độ ưu tiên thấp hơn).

## Practical Example: Simulating Round Robin (Java)
## Ví dụ thực tế: Mô phỏng Round Robin (Java)

```java
import java.util.LinkedList;
import java.util.Queue;

class Process {
    String name;
    int remainingTime;

    public Process(String name, int burstTime) {
        this.name = name;
        this.remainingTime = burstTime;
    }
}

public class RoundRobinSimulation {
    public static void main(String[] args) {
        Queue<Process> queue = new LinkedList<>();
        queue.add(new Process("P1", 10));
        queue.add(new Process("P2", 4));
        queue.add(new Process("P3", 5));

        int timeQuantum = 3;
        int time = 0;

        System.out.println("Starting Round Robin (Quantum = " + timeQuantum + ")");

        while (!queue.isEmpty()) {
            Process p = queue.poll();

            int runTime = Math.min(p.remainingTime, timeQuantum);
            time += runTime;
            p.remainingTime -= runTime;

            System.out.println("Time " + time + ": " + p.name + " runs for " + runTime + " (Remaining: " + p.remainingTime + ")");

            if (p.remainingTime > 0) {
                queue.add(p); // Put back in queue if not finished
            } else {
                System.out.println(" >>> " + p.name + " finished!");
            }
        }
    }
}
```

## Exercises
## Bài tập
1. Modify the Java Round Robin simulation to calculate the average turnaround time and average waiting time for the processes.
1. Sửa đổi mô phỏng Round Robin của Java để tính thời gian quay vòng trung bình và thời gian chờ đợi trung bình cho các quá trình.
2. Explain the concept of "Context Switch". What components of the CPU need to be saved and restored during a context switch?
2. Giải thích khái niệm "Chuyển đổi ngữ cảnh". Những thành phần nào của CPU cần được lưu và khôi phục trong quá trình chuyển đổi ngữ cảnh?
3. How does I/O affect CPU scheduling? If a process makes an I/O request, what should the scheduler do?
3. I/O ảnh hưởng đến việc lập lịch CPU như thế nào? Nếu một quá trình đưa ra yêu cầu I/O, bộ lập lịch phải làm gì?

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Be prepared to trace through an SJF or Round Robin algorithm given a set of processes, arrival times, and burst times.
- Hãy chuẩn bị để theo dõi qua một thuật toán SJF hoặc Round Robin với một tập hợp các quá trình, thời gian đến và thời gian bùng nổ.
- Understand Preemptive vs Non-Preemptive scheduling.
- Hiểu về lập lịch ưu tiên và không ưu tiên.
- Starvation: what causes it and how does "aging" solve it?
- Đói: nguyên nhân gây ra nó và "lão hóa" giải quyết nó như thế nào?
