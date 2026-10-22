EY – Technical Interview Experience | Java | Spring Boot | Microservices | AWS | Kafka

Sharing the questions from my first technical interview round at EY. These are based on my interview experience and may be useful for others preparing for similar roles.

Java

1. Difference between HashMap and ConcurrentHashMap.
2. Explain Java Memory Management and different JVM memory areas.
3. What is the PC Register?
4. Explain String Pool vs Heap memory.
5. What happens when we use new String("hello") if "hello" already exists in the String Pool?
6. Difference between == and .equals() for String.
7. Difference between ExecutorService and CompletableFuture.
8. Does CompletableFuture create its own thread pool? Which pool does it use by default?

System Design / Microservices
9. Design a fund transfer system capable of handling millions of transactions.
10. What happens if Account A is debited but Account B’s credit fails?
11. How would you prevent duplicate transactions if the user clicks multiple times?
12. How would you ensure transactions for an account are processed in order?
13. How would Kafka partitions help with ordering?
14. Which design would you use to avoid duplicate transactions and ensure correct processing?
15. Explain how the Saga pattern can be used for fund transfer.
16. Explain the architecture/flow of a Saga-based fund transfer system.
17. Which database would you choose and why?
18. How do microservices communicate with each other?
19. When would you use REST vs Kafka?
20. How would you handle cascading failures in microservices?
21. How would you process 1 lakh messages/second in Kafka?

Spring
22. Explain the flow of Spring MVC from request to response.
23. What is the N+1 problem in Hibernate/JPA and how would you solve it?

Security
24. Difference/relationship between OAuth 2.0, JWT and OpenID Connect.
25. Explain JWT structure and JWT authentication/validation flow.

AWS
26. Difference between EC2, ECS and Lambda.
27. How do you monitor applications in AWS?
28. Have you deployed microservices on AWS? Explain the deployment flow.

Database & Troubleshooting
29. How would you identify and resolve a database bottleneck?
30. How would you debug a 401 Unauthorized error?
31. How would you debug a 404 Not Found error?

Coding Question
32. Given a list of account transactions such as A:+100, B:+200, A:-50, C:+300, B:-100, A:+150, calculate the final balance for each account.

* Explain the approach/logic.
* Write the code.
* What is the time complexity?
* What is the space complexity?
* Can you solve it using Java 8 features such as Map.merge()?

Overall, the discussion covered Core Java, JVM, Spring MVC, Microservices, Kafka, System Design, Security, AWS, Database optimization, troubleshooting, and Java coding.