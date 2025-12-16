package dao;

import com.mongodb.client.MongoCollection;
import dto.FriendRequestDto;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FriendRequestDao extends BaseDao<FriendRequestDto> {

    private static FriendRequestDao instance;
    private static Supplier<FriendRequestDao> instanceSupplier = () -> {
        return new FriendRequestDao(MongoConnection.getCollection("FriendRequestDao"));
    };

    private FriendRequestDao(MongoCollection<Document> collection) {
        super(collection);
    }

    public static FriendRequestDao getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = instanceSupplier.get();
        return instance;
    }

    public static void setInstanceSupplier(Supplier<FriendRequestDao> instanceSupplier) {
        FriendRequestDao.instanceSupplier = instanceSupplier;
        FriendRequestDao.instance = null;
    }

    @Override
    Supplier<FriendRequestDto> getFromDocument(Document document) {
        var dto = new FriendRequestDto();
        dto.fromDocument(document);
        return () -> dto;
    }

    public List<FriendRequestDto> findRequestsToUser(String userName) {
        return query("toUser", userName);
    }

    public List<FriendRequestDto> findAcceptedFriends(String userName) {
        List<FriendRequestDto> asSender = collection.find(
                new Document("fromUser", userName).append("status", "accepted")
        ).into(new ArrayList<>()).stream()
                .map(doc -> {
                    var supplier = getFromDocument(doc);
                    var dto = supplier.get();
                    dto.loadUniqueId(doc);
                    return dto;
                }).toList();

        List<FriendRequestDto> asRecipient = collection.find(
                new Document("toUser", userName).append("status", "accepted")
        ).into(new ArrayList<>()).stream()
                .map(doc -> {
                    var supplier = getFromDocument(doc);
                    var dto = supplier.get();
                    dto.loadUniqueId(doc);
                    return dto;
                }).toList();

        List<FriendRequestDto> combined = new ArrayList<>();
        combined.addAll(asSender);
        combined.addAll(asRecipient);
        return combined;
    }

    public FriendRequestDto findExistingRequest(String fromUser, String toUser) {
        List<FriendRequestDto> forward = collection.find(
                new Document("fromUser", fromUser).append("toUser", toUser)
        ).into(new ArrayList<>()).stream()
                .map(doc -> {
                    var supplier = getFromDocument(doc);
                    var dto = supplier.get();
                    dto.loadUniqueId(doc);
                    return dto;
                }).toList();

        if (!forward.isEmpty()) {
            return forward.get(0);
        }

        List<FriendRequestDto> reverse = collection.find(
                new Document("fromUser", toUser).append("toUser", fromUser)
        ).into(new ArrayList<>()).stream()
                .map(doc -> {
                    var supplier = getFromDocument(doc);
                    var dto = supplier.get();
                    dto.loadUniqueId(doc);
                    return dto;
                }).toList();

        return reverse.isEmpty() ? null : reverse.get(0);
    }
}

