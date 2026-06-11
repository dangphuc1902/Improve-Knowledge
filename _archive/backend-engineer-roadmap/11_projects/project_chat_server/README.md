# Project: Chat Server

## Objective
Build a real-time, bidirectional chat application utilizing WebSockets to handle concurrent user messaging.

## Technologies
- **Backend**: Node.js with `socket.io` OR Java Spring Boot WebSockets
- **Database**: MongoDB (great for unstructured chat histories)
- **Frontend**: A simple HTML/JS page to verify functionality.

## Core Requirements
1. Users can connect to the server with a username.
2. Users can join specific chat "rooms" or "channels".
3. When a user sends a message, it is instantly broadcasted to everyone else in that room.
4. When a user disconnects, broadcast a "User has left" notification.
5. Save chat history to the database. When joining a room, fetch the last 50 messages.

## Architecture Guidelines
- Unlike REST HTTP endpoints, WebSockets keep a permanent TCP connection open. Design your application state to keep track of Active sockets in memory maps.
- Look into handling disconnect events gracefully to clean up memory.

## Advanced Challenges (Scaling)
- If you have 2 Node.js servers behind a Load Balancer, User A (Server 1) can't easily send an instant message to User B (Server 2). 
- **Challenge**: Implement a Redis Pub/Sub adapter so messages broadcast across multiple backend server instances simultaneously.
