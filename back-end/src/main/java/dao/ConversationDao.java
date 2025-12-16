package dao;

import com.mongodb.client.MongoCollection;
import dto.ConversationDto;
import org.bson.Document;

import java.util.function.Supplier;

// TODO fill this out
public class ConversationDao extends BaseDao<ConversationDto> {

    private static ConversationDao instance;
    private static Supplier<ConversationDao> instanceSupplier = () -> {
        return new ConversationDao(MongoConnection.getCollection("ConversationDao"));
    };

    private ConversationDao(MongoCollection<Document> collection) {
        super(collection);
    }

    public static ConversationDao getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = instanceSupplier.get();
        return instance;
    }

    public static void setInstanceSupplier(Supplier<ConversationDao> instanceSupplier){
        ConversationDao.instanceSupplier = instanceSupplier;
        ConversationDao.instance = null;
    }

    @Override
    Supplier<ConversationDto> getFromDocument(Document document) {
        var auth = new ConversationDto();
        auth.fromDocument(document);
        return () -> auth;
    }

}
