**Block Feature README**

**Implemented by:** Cole Sanders  & AI Agent Assistance

**Date:** December 16, 2025

**Link for Recording(Made by Will Young)**: https://mailsfsu-my.sharepoint.com/:v:/g/personal/924230057_sfsu_edu/IQADZaqGCRMLQrCWhyr-KFUvAXeauWDTlDkEM2ouUjMXd8w?xsdata=MDV8MDJ8Y3NhbmRlcnNAc2ZzdS5lZHV8MmQ5MWI3NDRjNDRhNGI3YzY3MTgwOGRlM2RiYzEwNmZ8ZDhmYmUzMzU4MjJjNDFhOTg3NzQ3ZjE2NzA5YWFjOWZ8MHwwfDYzOTAxNjA3NTEwNjE1MjUwN3xVbmtub3dufFRXRnBiR1pzYjNkOGV5SkZiWEIwZVUxaGNHa2lPblJ5ZFdVc0lsWWlPaUl3TGpBdU1EQXdNQ0lzSWxBaU9pSlhhVzR6TWlJc0lrRk9Jam9pVFdGcGJDSXNJbGRVSWpveWZRPT18MHx8fA%3d%3d&sdata=c1ZRRXBNVFYzZitDamREOE0zdzBaVlRZRVhWVTQxVEh5eEZHUXFMakwwTT0%3d 

- **Overview**: The block feature lets authenticated users block and unblock other users and view their blocked list. Blocking prevents interactions (tests cover messaging/friend requests blocked when appropriate).

**Backend: core classes & routes**
- **DTO**: `BlockDto` — [back-end/src/main/java/dto/BlockDto.java](back-end/src/main/java/dto/BlockDto.java)
- **DAO**: `BlockDao` — [back-end/src/main/java/dao/BlockDao.java](back-end/src/main/java/dao/BlockDao.java)
- **Handlers / Routes** (registered in the handler factory):
  - `/blockUser` → [back-end/src/main/java/handler/BlockUserHandler.java](back-end/src/main/java/handler/BlockUserHandler.java)
  - `/unblockUser` → [back-end/src/main/java/handler/UnblockUserHandler.java](back-end/src/main/java/handler/UnblockUserHandler.java)
  - `/getBlockedUsers` → [back-end/src/main/java/handler/GetBlockedUsersHandler.java](back-end/src/main/java/handler/GetBlockedUsersHandler.java)

**Backend behavior summary**
- Requests must be authenticated (see `AuthFilter`). If not authenticated, handlers return 401.
- `BlockDto` fields: `blocker` (who blocked) and `blocked` (username blocked).
- `/blockUser` (POST): expects a JSON body with `blocked` (username). Validates target exists, prevents blocking self, prevents duplicates, then inserts a `BlockDto`. Responds with a `RestApiAppResponse` containing the created block object on success.
- `/unblockUser` (POST): expects a JSON body with `blocked`. Removes the block for the authenticated user and returns success.
- `/getBlockedUsers` (GET): returns a list of `BlockDto` records for the authenticated user.

**Frontend**
- Main component: [front-end/app/home/BlockedList.tsx](front-end/app/home/BlockedList.tsx)
- Frontend routes used:
  - `POST /api/blockUser` — body `{ "blocked": "username" }`
  - `POST /api/unblockUser` — body `{ "blocked": "username" }`
  - `GET /api/getBlockedUsers`
- The component shows a small UI to add a username to block, lists blocked users, and enables unblocking. It expects standard JSON responses with a `status` boolean and optional `data` or `message` fields.

**Sample requests & expected responses**
- Block user (success)

  Request: POST /api/blockUser

  Body: { "blocked": "alice" }

  Response (200):

  { "status": true, "data": [ { "blocker": "bob", "blocked": "alice" } ], "message": "User blocked" }

- Get blocked users

  Request: GET /api/getBlockedUsers

  Response (200):

  { "status": true, "data": [ { "blocker": "bob", "blocked": "alice" } ], "message": null }

- Unblock user

  Request: POST /api/unblockUser

  Body: { "blocked": "alice" }

  Response (200):

  { "status": true, "data": null, "message": "User unblocked" }

**Testing & notes**
- Unit/integration tests exist under `back-end/src/test/java/applogic/BlockTests.java` and the target surefire reports show passing block tests in the repo.
- The DAO stores documents in the `BlockDao` collection with fields `blocker` and `blocked`.
