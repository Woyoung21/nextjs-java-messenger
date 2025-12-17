package dao;

import com.mongodb.client.MongoCollection;
import dto.BlockDto;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BlockDao extends BaseDao<BlockDto> {

    private static BlockDao instance;
    private static Supplier<BlockDao> instanceSupplier = () -> {
        return new BlockDao(MongoConnection.getCollection("BlockDao"));
    };

    private BlockDao(MongoCollection<Document> collection) {
        super(collection);
    }

    public static BlockDao getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = instanceSupplier.get();
        return instance;
    }

    public static void setInstanceSupplier(Supplier<BlockDao> instanceSupplier) {
        BlockDao.instanceSupplier = instanceSupplier;
        BlockDao.instance = null;
    }

    @Override
    Supplier<BlockDto> getFromDocument(Document document) {
        var dto = new BlockDto();
        dto.fromDocument(document);
        return () -> dto;
    }

    public List<BlockDto> findBlockedByUser(String userName) {
        return query("blocker", userName);
    }

    public boolean isBlocked(String blocker, String blocked) {
        List<BlockDto> result = collection.find(
                new Document("blocker", blocker).append("blocked", blocked)
        ).into(new ArrayList<>()).stream().map(doc -> {
            var supplier = getFromDocument(doc);
            var dto = supplier.get();
            dto.loadUniqueId(doc);
            return dto;
        }).toList();
        return !result.isEmpty();
    }

    public void removeBlock(String blocker, String blocked) {
        collection.deleteOne(new Document("blocker", blocker).append("blocked", blocked));
    }
}
