package dao;

import com.mongodb.client.MongoCollection;
import dto.BaseDto;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class BaseDao<T extends BaseDto> {

    final MongoCollection<Document> collection;

    protected BaseDao(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public List<T> query(String key, Object value){
        return collection.find(new Document(key, value))
                .into(new ArrayList<>())
                .stream()
                .map(doc -> {
                    var supplier = getFromDocument(doc);
                    var dto = supplier.get();
                    dto.loadUniqueId(doc);
                    return dto;
                })
                .toList();
    }

    public void put(T dto) {
        if (dto.getUniqueId() == null) {
            collection.insertOne(dto.toDocument());
        } else {
            collection.replaceOne(dto.getObjectId(), dto.toDocument());
        }
    }

    abstract Supplier<T> getFromDocument(Document document);
}
