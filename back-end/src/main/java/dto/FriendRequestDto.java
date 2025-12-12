package dto;

import org.bson.Document;

public class FriendRequestDto extends BaseDto {

    private String fromUser;
    private String toUser;
    private String status;

    public FriendRequestDto() {
        super();
    }

    public FriendRequestDto(String uniqueId) {
        super(uniqueId);
    }

    @Override
    public void fromDocument(Document document) {
        this.fromUser = document.getString("fromUser");
        this.toUser = document.getString("toUser");
        this.status = document.getString("status");
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("fromUser", fromUser)
                .append("toUser", toUser)
                .append("status", status);
    }

    public String getFromUser() {
        return fromUser;
    }

    public FriendRequestDto setFromUser(String fromUser) {
        this.fromUser = fromUser;
        return this;
    }

    public String getToUser() {
        return toUser;
    }

    public FriendRequestDto setToUser(String toUser) {
        this.toUser = toUser;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public FriendRequestDto setStatus(String status) {
        this.status = status;
        return this;
    }
}

