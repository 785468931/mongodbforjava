package com.li.mongodb.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import com.li.dbbase.Config;
import com.li.mongodb.utils.TimeUtil;
import com.li.mongodb.vo.Log;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.util.JSON;

@SuppressWarnings({ "rawtypes", "unused" })
public class PayLogMongoDBUtils {

	private MongoClient mongoClient = Config.mongoClient;
	private MongoDatabase db = Config.db;
	private MongoCollection collection = db.getCollection("payLog");
	
	// private static MongoCollection collection;

	// static {
	//
	// mongoClient = new MongoClient(Config.MONGODB_URL,
	// Config.MONGODB_URL_PORT);
	// db = mongoClient.getDatabase(Config.MONGODB_DATABASES);
	//
	// }
	
	
	public static void main(String[] args) {

		new PayLogMongoDBUtils().payLogListQuery(0, 2);

		 new PayLogMongoDBUtils().payLogInsert("13421747372", "1380099920000",
		 "0000", "success",
		 "<ErrorMsgContent><errorCode>0000</errorCode><errorMessage>交易成功</errorMessage></ErrorMsgContent>",
		 "<ErrorMsgContent><errorCode>0000</errorCode><errorMessage>交易成功</errorMessage></ErrorMsgContent>",
		 "success","success");
		
//		long total = new PayLogMongoDBUtils().payLogListQuerySize();
//		System.out.println(total);
		
//		System.out.println(new PayLogMongoDBUtils().payLogDelete("1380099920000"));
		
		
		// testListQuerySize();
		// testListQuery(0, 10);
		// testOneQuery();
		// testBatchInsert();
		// testListQuerySize();
	}

	public String payLogListQuery(int page, int total) {
		if (page <= 0) {
			page = 1;
		}
		page = (page - 1) * total;

		StringBuilder strBui = new StringBuilder();

		
		long totalCount=(long)payLogListQuerySize(); 
		
		if((totalCount%total)==0){
			totalCount = (totalCount/total==0?1:totalCount/total);
			}else{
				totalCount = (totalCount/total+1);
			}
		
//		MongoCollection collection = db.getCollection("payLog");
		FindIterable iterable = collection.find().skip(page).limit(total);
		MongoCursor cursor = iterable.iterator();

		strBui.append("{");
		
		strBui.append("\"rows\":");
		strBui.append("[");
		boolean flag=true;
		while (cursor.hasNext()) {
			if(!flag){
				strBui.append(",");
			}
			flag=false;
			Document user = (Document) cursor.next();
			strBui.append(user.toJson());
		}
		strBui.append("]");
		
		
		strBui.append(",\"total\":"+totalCount);
		strBui.append("}");
		cursor.close();
		return strBui.toString();
	}

	public long payLogListQuerySize() {
//		MongoCollection collection = db.getCollection("payLog");
		return collection.count();
	}

	

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param phoneNo 手机号
	 * @param orderNo 订单号
	 * @param sendContent 发送的内容
	 * @param retContent  返回的内容
	 */
	public void payLogInsert(String phoneNo, String orderNo, String errorCode,
			String errorMessage, String sendContent, String retContent,String encryptSendContent,String encryptRetContent) {
		MongoCollection collection = db.getCollection("payLog");
		 Log log=new Log();
		Document doc = new Document();
		doc.put("orderNo", orderNo);
		doc.put("phoneNo", phoneNo);
		doc.put("errorCode", errorCode);
		doc.put("errorMessage", errorMessage);
		doc.put("createTime", TimeUtil.getTimeUni());
		doc.put("sendContent", sendContent);
		doc.put("retContent", retContent);
		doc.put("encryptSendContent", encryptSendContent);
		doc.put("encryptRetContent", encryptRetContent);
		collection.insertOne(doc);

	}

	
	public String payLogWhereQuery(int page, int total,String phoneNo) {
		if (page <= 0) {
			page = 1;
		}
		page = (page - 1) * total;

		StringBuilder strBui = new StringBuilder();

		 BasicDBObject query = new BasicDBObject();
		 query.put("phoneNo", phoneNo);
		long totalCount=(long)payLogListQuerySize(); 
		
		if((totalCount%total)==0){
			totalCount = (totalCount/total==0?1:totalCount/total);
			}else{
				totalCount = (totalCount/total+1);
			}
		
		
		FindIterable iterable = collection.find(query).skip(page).limit(total).sort(new BasicDBObject("createTime",-1));;
		MongoCursor cursor = iterable.iterator();

		strBui.append("{");
		
		strBui.append("\"rows\":");
		strBui.append("[");
		boolean flag=true;
		while (cursor.hasNext()) {
			if(!flag){
				strBui.append(",");
			}
			flag=false;
			Document user = (Document) cursor.next();
//			System.out.println(user.toJson());
			strBui.append(user.toJson());
		}
		strBui.append("]");
		
		
		strBui.append(",\"total\":"+totalCount);
		strBui.append("}");
		 System.out.println(strBui.toString());
		cursor.close();
		return null;
	}
	// test delete

	public long payLogDelete(String orderNo) {
		BasicDBObject query = new BasicDBObject("orderNo", orderNo);
		DeleteResult dresule=collection.deleteOne(query);
		return dresule.getDeletedCount();
	}

	

	
}
