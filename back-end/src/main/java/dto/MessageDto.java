package dto;

import org.bson.Document;

import java.time.Instant;

public class MessageDto extends BaseDto {

    private String fromId;
    private String toId;
    private String message;
    private Long timestamp;
    private String conversationId;

    public MessageDto() {
        timestamp = Instant.now().toEpochMilli();
    }

    @Override
    public void fromDocument(Document document) {
        this.fromId = document.getString("fromId");
        this.toId = document.getString("toId");
        this.message = document.getString("message");
        this.timestamp = document.getLong("timestamp");
        this.conversationId = document.getString("conversationId");
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("fromId", fromId);
        doc.append("toId", toId);
        doc.append("message", message);
        doc.append("timestamp", timestamp);
        doc.append("conversationId", conversationId);
        return doc;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
