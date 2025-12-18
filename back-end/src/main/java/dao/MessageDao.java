package dao;

import com.mongodb.client.MongoCollection;
import dto.MessageDto;
import org.bson.Document;

import java.util.function.Supplier;

// TODO fill this out
public class MessageDao extends BaseDao<MessageDto> {

    private static MessageDao instance;
    private static Supplier<MessageDao> instanceSupplier = () -> {
        return new MessageDao(MongoConnection.getCollection("MessageDao"));
    };

    private MessageDao(MongoCollection<Document> collection) {
        super(collection);
    }

    public static MessageDao getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = instanceSupplier.get();
        return instance;
    }
    public void deleteByConversationId(String conversationId) {
        collection.deleteMany(new Document("conversationId", conversationId));
    }

    public static void setInstanceSupplier(Supplier<MessageDao> instanceSupplier){
        MessageDao.instanceSupplier = instanceSupplier;
        MessageDao.instance = null;
    }

    @Override
    Supplier<MessageDto> getFromDocument(Document document) {
        var auth = new MessageDto();
        auth.fromDocument(document);
        return () -> auth;
    }

}
