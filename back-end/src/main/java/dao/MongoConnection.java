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
            String runningInDocker = System.getenv("RUNNING_IN_DOCKER");
            String host = "localhost";
            if (runningInDocker != null && !runningInDocker.isEmpty()) {
                host = "host.docker.internal";
            }
            mongoClient = new MongoClient(host, 27017);
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
