package dto;

import org.bson.Document;

public class BlockDto extends BaseDto {

    private String blocker;
    private String blocked;

    public BlockDto() { super(); }

    public BlockDto(String uniqueId) { super(uniqueId); }

    @Override
    public void fromDocument(Document document) {
        this.blocker = document.getString("blocker");
        this.blocked = document.getString("blocked");
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("blocker", blocker)
                .append("blocked", blocked);
    }

    public String getBlocker() { return blocker; }

    public BlockDto setBlocker(String blocker) { this.blocker = blocker; return this; }

    public String getBlocked() { return blocked; }

    public BlockDto setBlocked(String blocked) { this.blocked = blocked; return this; }
}
