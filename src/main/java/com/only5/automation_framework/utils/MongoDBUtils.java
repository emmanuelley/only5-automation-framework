package com.only5.automation_framework.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MongoDBUtils {

    MongoClient client = null;
    DB db = null;

    public MongoDBUtils(List<ServerAddress> addressList, List<MongoCredential> credentials) throws UnknownHostException {
        client = new MongoClient(addressList, credentials);
    }

    public void connect(String dbName) {
        db = client.getDB(dbName);
    }

    //List Collections
    public DBCollection getCollection(String collection) {
        return db.getCollection(collection);
    }

    //Insert Collections
    public <T> T insert(String collection, DBObject obj) {
        db.getCollection(collection).insert(obj);
        return (T) obj.get("_id").toString();
    }

    public DBObject and(String collection, List<DBObject> conditions) {
        DBObject bObj = new BasicDBObject();
        bObj.put("$and", conditions);
        return db.getCollection(collection).findOne(bObj);
    }

    public List<DBObject> andAll(String collection, List<DBObject> conditions) {
        DBObject bObj = new BasicDBObject();
        List<DBObject> list = new ArrayList<>();
        bObj.put("$and", conditions);
        DBCursor cursor = db.getCollection(collection).find(bObj);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(obj);
        }
        return list;
    }

    public <T> T update(String collection, DBObject obj, DBObject obj1) {
        db.getCollection(collection).update(obj, obj1);
        return (T) obj.toString();
    }
}
