package dto;

import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

public class ConversationDto extends BaseDto {

    private String fromId;
    private String toId;
    private String conversationId;
    private Integer messageCount = 0;

    public ConversationDto(String fromId, String toId) {
        super();
        this.fromId = fromId;
        this.toId = toId;
        this.conversationId = makeUniqueId(fromId, toId);
    }

    public ConversationDto() {
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

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    static public String makeUniqueId(String fromId, String toId){
        return List.of(fromId, toId)
                .stream()
                .sorted()
                .collect(Collectors.joining("_"));
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public ConversationDto setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
        return this;
    }

    @Override
    public void fromDocument(Document document) {
        this.fromId = document.getString("fromId");
        this.toId = document.getString("toId");
        this.messageCount = document.getInteger("messageCount");
        this.conversationId = document.getString("conversationId");
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("fromId", fromId);
        doc.append("toId", toId);
        doc.append("messageCount", messageCount);
        doc.append("conversationId", conversationId);
        return doc;
    }
}
