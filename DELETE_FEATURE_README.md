# **Delete Conversation Feature README**

**Implemented by:** *Vansh Singh* & AI Agent Assistance  
**Date:** December 17, 2025

---

## **Overview**

The **Delete Conversation** feature allows authenticated users to permanently remove all messages in a chat between themselves and another user.  
This operation is *idempotent* — meaning it can be safely repeated even if the conversation is already empty.  
After deletion, sending a new message starts a fresh clean conversation thread.

---

## **Backend: core classes & route**

### **Modified / New Files**
- **DAO:** `MessageDao` — added `deleteByConversationId`  
  *(back-end/src/main/java/dao/MessageDao.java)*
- **Handler:** `DeleteConversationHandler`  
  *(back-end/src/main/java/handler/DeleteConversationHandler.java)*
- **Routing:** Added `/deleteConversation` in `HandlerFactory`  
  *(back-end/src/main/java/handler/HandlerFactory.java)*

### **Backend behavior summary**

- All requests must be authenticated (`AuthFilter`).
- The frontend passes the conversation ID (URL-encoded), and the handler decodes it.
- The handler deletes all messages matching that `conversationId` from the `MessageDao` MongoDB collection.
- Also attempts to delete conversation metadata (optional, harmless if not present).
- Returns:
  ```json
  { "status": true, "data": null, "message": "Conversation deleted" }
  ```
- Safe to call multiple times — always returns success.

### **Key Method: MessageDao.deleteByConversationId**
```java
public void deleteByConversationId(String conversationId) {
    collection.deleteMany(new Document("conversationId", conversationId));
}
```

---

## **Frontend**

### **File modified**
- `front-end/app/home/ChatBar.tsx`

### **Frontend route used**
- `POST /api/deleteConversation?conversationId=<id>`

### **UI Behavior**
- Added a **“Delete chat”** button next to “Send”
- On click:
  1. Computes the conversation ID (`alice@gmail.com_bob@gmail.com`)
  2. Sends POST request to backend delete route
  3. Clears messages from UI
  4. Shows a confirmation alert

### **Sample frontend request**
```ts
await fetch(`/api/deleteConversation?conversationId=${encodeURIComponent(conversationId)}`, {
  method: "POST"
});
```

---

## **Sample Requests & Expected Responses**

### **Delete conversation (success)**  
**Request:**  
`POST /api/deleteConversation?conversationId=alice%40gmail.com_bob%40gmail.com`

**Response (200):**
```json
{
  "status": true,
  "data": null,
  "message": "Conversation deleted"
}
```

### **After deletion**
Querying Mongo:

```js
db.MessageDao.find({ conversationId: "alice@gmail.com_bob@gmail.com" })
```

returns:

```
[]
```

---

## **Unit Testing**

### **Test File**
`back-end/src/test/java/applogic/DeleteConversationHandlerTests.java`

### **Coverage**
- Successful delete
- Unauthorized delete
- DAO deletion calls verified (Mockito)
- Idempotent behavior (no errors when conversation is already empty)


All tests pass under Maven Surefire.

---

## **Demo Video**
*A short video demonstrating:*
1. Opening a chat  
2. Sending messages  
3. Clicking **Delete chat**  
4. Messages disappearing
5. shows mongo db messages to double check

 **https://youtu.be/mZGIR_PKRcs**

---
