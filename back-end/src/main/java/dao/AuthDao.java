package dao;

import com.mongodb.client.MongoCollection;
import dto.AuthDto;
import org.bson.Document;

import java.util.function.Supplier;

public class AuthDao extends BaseDao<AuthDto> {

    private static AuthDao instance;
    private static Supplier<AuthDao> instanceSupplier = () -> {
        return new AuthDao(MongoConnection.getCollection("AuthDao"));
    };

    private AuthDao(MongoCollection<Document> collection) {
        super(collection);
    }

    @Override
    Supplier<AuthDto> getFromDocument(Document document) {
        var auth = new AuthDto();
        auth.fromDocument(document);
        return () -> auth;
    }

    public static AuthDao getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = instanceSupplier.get();
        return instance;
    }

    public static void setInstanceSupplier(Supplier<AuthDao> instanceSupplier){
        AuthDao.instanceSupplier = instanceSupplier;
    }
}
