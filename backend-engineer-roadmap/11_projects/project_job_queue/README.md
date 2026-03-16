# Project: Job Queue System

## Objective
Build an asynchronous background processing system. Some tasks (like resizing a 4K image, or generating a massive PDF report) take 10+ seconds. Web servers cannot leave HTTP requests open that long. Implement a queuing system.

## Technologies
- **Backend**: Java / Node.js
- **Message Broker**: RabbitMQ or Redis (via BullMQ)

## Core Requirements
1. Create a REST API: `POST /api/generate-report`
   - This endpoint DOES NOT generate the report inline.
   - It publishes a message/event to a queue ("report_job_queued" event).
   - It instantly returns an HTTP 202 Accepted status with a tracking ID: `{"jobId": "12345", "status": "processing"}`.
2. Create a Background Worker Script that listens to the Message Queue.
   - When it detects a message, it "sleeps" for 10 seconds to simulate heavy computational work.
   - Upon completion, it updates a database record marking the job status as "Complete".
3. Client can poll `GET /api/jobs/12345` to see if the status is "Complete".

## Architectural Guidelines
- Completely separate the Web API codebase from the Worker codebase. They should only share the knowledge of the Database and the Message Broker.

## Advanced Challenges
- **Retry Mechanism**: If the worker script crashes halfway through the 10 seconds, ensure the job isn't lost forever. It should go back into the queue for a retry.
- Implement a Dead Letter Queue (DLQ) for jobs that fail 3 times in a row.
