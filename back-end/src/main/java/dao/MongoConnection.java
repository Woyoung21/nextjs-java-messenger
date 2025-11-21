package dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.function.Supplier;

public class MongoConnection {

    private static MongoClient mongoClient;

    private static Supplier<MongoDatabase> clientSupplier = () -> {
        if(mongoClient == null){
            mongoClient = new MongoClient("localhost", 27017);
        }
        return mongoClient.getDatabase("Homework2");
    };

    public static void setClientSupplier(Supplier<MongoDatabase> clientSupplier){
        MongoConnection.clientSupplier = clientSupplier;
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        return clientSupplier.get().getCollection(collectionName);
    }

}
