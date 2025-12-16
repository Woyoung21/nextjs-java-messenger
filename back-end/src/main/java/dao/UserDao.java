package dao;

import com.mongodb.client.MongoCollection;
import dto.UserDto;
import org.bson.Document;

import java.util.function.Supplier;

// TODO fill this out
public class UserDao extends BaseDao<UserDto> {

    private static UserDao instance;
    private static Supplier<UserDao> instanceSupplier = () -> {
        return new UserDao(MongoConnection.getCollection("UserDao"));
    };

    private UserDao(MongoCollection<Document> collection) {
        super(collection);
    }

    public static UserDao getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = instanceSupplier.get();
        return instance;
    }

    public static void setInstanceSupplier(Supplier<UserDao> instanceSupplier){
        UserDao.instanceSupplier = instanceSupplier;
        UserDao.instance = null;
    }

    @Override
    Supplier<UserDto> getFromDocument(Document document) {
        var auth = new UserDto();
        auth.fromDocument(document);
        return () -> auth;
    }
}
