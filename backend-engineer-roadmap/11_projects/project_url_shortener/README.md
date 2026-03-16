# Project: URL Shortener

## Objective
Build a system similar to Bitly, allowing users to submit long URLs and receive short, compact URLs in return. Clicking the short URL redirects them to the original address.

## Technologies
- **Backend**: Node.js/Express OR Java/Spring Boot
- **Database**: Redis (for extreme fast reads) + PostgreSQL (for persistence)
- **Hash function**: Base62 Encoding

## Core Requirements
1. `POST /api/shorten`
   - Accepts: `{ "longUrl": "https://en.wikipedia.org/wiki/Systems_design" }`
   - Returns: `{ "shortUrl": "http://localhost:3000/xyz123" }`
2. `GET /:shortCode`
   - Redirects (HTTP 301 or 302) to the original long URL.
3. Keep track of click counts for each shortened URL.

## Architecture Guidelines
- Use a Counter approach or Random Length String to generate IDs. Convert integer IDs to Base62.
- Cache the mappings in Redis. When `/:shortCode` is hit, check Redis first. If not found, check the database. 

## Advanced Challenges 
- Setup an eviction policy for URLs not clicked in over 1 year.
- Implement rate-limiting so a single IP can't generate 10,000 links per minute.
