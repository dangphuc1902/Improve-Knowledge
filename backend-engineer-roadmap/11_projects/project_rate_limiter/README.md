# Project: Rate Limiter

## Objective
Protect your APIs from DDoS attacks and brute force abuse by building a Rate Limiter middleware.

## Technologies
- **Backend**: Node.js Express OR Spring Web Filter
- **Storage**: Redis

## Core Requirements
1. Implement an API endpoint `GET /api/public-data` that returns arbitrary JSON.
2. Implement a Rate Limiter middleware/interceptor that runs *before* the API logic executes.
3. **The Rule**: A user (identified by IP Address) can only make 5 requests per minute.
4. If they exceed the limit:
   - Return HTTP Status `429 Too Many Requests`.
   - Include a header `Retry-After: 60` stating when they can try again.
5. If they are within the limit, process the request normally and include headers: 
   - `X-RateLimit-Limit: 5`
   - `X-RateLimit-Remaining: X`

## Architecture Guidelines
- Use the **Token Bucket** or **Fixed Window Counter** algorithm.
- Store the counts in Redis because memory reads must be extremely fast to not slow down every request explicitly.
- In Redis, use the `INCR` command and `EXPIRE` command together to automatically manage the 1-minute windows.

## Advanced Challenges
- Implement a "Sliding Window Log" algorithm in Redis using Sorted Sets (`ZSET`), which is much more precise than Fixed Window.
- Adjust the limits based on API Keys. Free users get 5/min. Premium plan users (passing a specific API Key) get 100/min.
