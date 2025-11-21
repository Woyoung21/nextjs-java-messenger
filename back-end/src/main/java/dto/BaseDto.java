package dto;

import org.bson.Document;
import org.bson.types.ObjectId;

public abstract class BaseDto {

    protected String uniqueId;

    public BaseDto(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public BaseDto() {
    }

    public BaseDto(Document document){
        fromDocument(document);
    }

    abstract void fromDocument(Document document);

    public String getUniqueId() {
        return uniqueId;
    }

    public abstract Document toDocument();

    public void loadUniqueId(Document document) {
        if (document.getObjectId("_id") != null) {
            uniqueId = document.getObjectId("_id").toHexString();
        }
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Document getObjectId() {
        return new Document("_id", new ObjectId(uniqueId));
    }
}