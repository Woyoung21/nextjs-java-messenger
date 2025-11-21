package dto;

import org.bson.Document;

public class UserDto extends BaseDto {

    private String userName;
    private String password;
    private Integer totalConversations = 0;
    private Integer messagesSent = 0;
    private Integer messagesRecieved = 0;

    public UserDto() {
        super();
    }

    @Override
    public void fromDocument(Document document) {
        this.userName = document.getString("userName");
        this.password = document.getString("password");
        this.messagesRecieved = document.getInteger("messagesReceived");
        this.messagesSent = document.getInteger("messagesSent");
    }

    @Override
    public Document toDocument() {
        var doc = new Document()
                .append("messagesSent", messagesSent)
                .append("messagesReceived", messagesRecieved)
                .append("userName", userName)
                .append("password", password);
        return doc;
    }

    public UserDto(String uniqueId) {
        super(uniqueId);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public UserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public Integer getTotalConversations() {
        return totalConversations;
    }

    public UserDto setTotalConversations(Integer totalConversations) {
        this.totalConversations = totalConversations;
        return this;
    }

    public Integer getMessagesSent() {
        return messagesSent;
    }

    public UserDto setMessagesSent(Integer messagesSent) {
        this.messagesSent = messagesSent;
        return this;
    }

    public Integer getMessagesRecieved() {
        return messagesRecieved;
    }

    public UserDto setMessagesRecieved(Integer messagesRecieved) {
        this.messagesRecieved = messagesRecieved;
        return this;
    }
}
