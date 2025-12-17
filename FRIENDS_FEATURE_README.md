# Friends List Feature Implementation

## Overview

A mutual friend request system where users can send, accept, or decline friend requests. Both parties must approve before becoming friends.

**Implemented by:** Will Young  & AI Agent Assistance
**Date:** December 12, 2025
**Video of Demo with voice-over Link(must use SFSU email):** https://mailsfsu-my.sharepoint.com/:v:/g/personal/924230057_sfsu_edu/IQBPm9KIII4uRbCrhbryZ4BqAUR5_2caE73jivV1WF-LzCA?nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=fIa66f 

---

## Features

- **Send Friend Request** - Users can send friend requests by entering a username
- **Pending Requests** - Recipients see incoming requests with Accept/Decline options
- **Mutual Friendship** - Only accepted requests result in friendship (visible to both users)
- **Friends List** - Displays all confirmed friends with avatar initials

---

## Backend Implementation (Java)

### New Files Created

| File | Description |
|------|-------------|
| `dto/FriendRequestDto.java` | Data model with `fromUser`, `toUser`, `status` fields |
| `dao/FriendRequestDao.java` | MongoDB operations for friend requests |
| `handler/SendFriendRequestHandler.java` | Handles POST `/sendFriendRequest` |
| `handler/GetFriendRequestsHandler.java` | Handles GET `/getFriendRequests` |
| `handler/RespondFriendRequestHandler.java` | Handles POST `/respondFriendRequest` |
| `handler/GetFriendsHandler.java` | Handles GET `/getFriends` |

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/sendFriendRequest` | Send a friend request to another user |
| GET | `/getFriendRequests` | Get pending incoming friend requests |
| POST | `/respondFriendRequest` | Accept or decline a friend request |
| GET | `/getFriends` | Get all accepted friends |

### Request/Response Examples

**Send Friend Request:**
```json
// POST /sendFriendRequest
// Request Body:
{ "toUser": "test23@gmail.com" }

// Response:
{
  "status": true,
  "data": [{
    "fromUser": "test25@gmail.com",
    "toUser": "test23@gmail.com",
    "status": "pending"
  }],
  "message": "Friend request sent"
}
```

**Respond to Friend Request:**
```json
// POST /respondFriendRequest
// Request Body:
{ "requestId": "...", "accept": true }

// Response:
{
  "status": true,
  "data": [{...}],
  "message": "Friend request accepted"
}
```

---

## Frontend Implementation (React/TypeScript)

### New Files Created

| File | Description |
|------|-------------|
| `app/home/FriendsList.tsx` | Main friends list component |

### Modified Files

| File | Change |
|------|--------|
| `app/home/page.tsx` | Added FriendsList component to dashboard |

### Component Features

- Input field to add friends by username
- Pending requests section with Accept/Decline buttons
- Friends list with avatar initials
- Toast notifications for success/error feedback

---

## Database Schema

**Collection:** `FriendRequestDao`

```json
{
  "_id": ObjectId,
  "fromUser": "string (username of sender)",
  "toUser": "string (username of recipient)",
  "status": "string (pending | accepted | declined)"
}
```

---

## User Flow

1. **User A** enters User B's username and clicks "Add Friend"
2. **User B** sees a pending request in their Friends section
3. **User B** clicks "Accept" (or "Decline")
4. **Both users** now see each other in their Friends list

---

## Screenshots

### Friends Section UI
The Friends section appears on the left side of the dashboard with:
- Add friend input field
- Green "Add Friend" button
- List of current friends with initials avatars

### Successful Friend Request (Backend Log)
```
{"status":true,"data":[{"fromUser":"test25@gmail.com","toUser":"test23@gmail.com","status":"pending"}],"message":"Friend request sent"}
```

### Mutual Friendship Confirmed
- **test25@gmail.com** dashboard shows test23@gmail.com as friend
- **test23@gmail.com** dashboard shows test25@gmail.com as friend

---

## Unit Tests

Unit tests are provided for all 4 handlers using TestNG and Mockito.

| Test File | Tests |
|-----------|-------|
| `SendFriendRequestHandlerTests.java` | Success, self-request fails, user not found, duplicate request, unauthorized |
| `GetFriendRequestsHandlerTests.java` | Success, filters pending only, empty list, unauthorized |
| `RespondFriendRequestHandlerTests.java` | Accept success, decline success, not found, wrong user, already responded, unauthorized |
| `GetFriendsHandlerTests.java` | Success, multiple friends, empty list, unauthorized |

**Total: 19 test cases**

To run tests in IntelliJ:
1. Right-click on any test file (e.g., `SendFriendRequestHandlerTests.java`)
2. Select **"Run"**
3. Or right-click on `src/test/java/applogic` folder and select **"Run Tests in 'applogic'"**

---

## Technical Notes

- Authentication is handled via existing `AuthFilter` (cookie-based)
- Friend requests are bidirectional - the system checks both directions when querying
- Duplicate requests are prevented (cannot send if request already exists)
- Users cannot send friend requests to themselves
- Target user must exist in the system

---

## Future Enhancements (Not Implemented)

- Remove/unfriend functionality
- Block list integration (to be implemented by Cole Sanders)

