import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class Server {
	
	private ServerSocket serverSocket; 
	public static ArrayList<Item> items = new ArrayList<>(); // synchronize later
	private final Lock lock = new ReentrantLock();
	
	

    private static MongoClient mongo; 
	private static MongoDatabase database;
	public static MongoCollection<Item> collection; 
	public static MongoCollection<User> collection2; 
	
	private static final String URI = "mongodb+srv://aryaansaxena:Flaming7@auctioncluster.agra35q.mongodb.net/?retryWrites=true&w=majority&appName=AuctionCluster"; 
	private static final String DB = "auction";
	private static final String COLLECTION = "auctionItems";
	private static final String COLLECTION2 = "users";
	
	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket; 
	}
	
	public void startServer() {		
		try {
				
			while (!serverSocket.isClosed()) {
				Socket socket = serverSocket.accept(); 
				System.out.println("New client connected");
				ClientHandler clientHandler = new ClientHandler(socket); 
				
				Thread thread = new Thread(clientHandler); 
				thread.start(); 
			}
		}
		catch (IOException e) {
			
		}
	}
	
	public void handleTimerFinish(Item item) {
		
	}
 	
	
	public void closeServerSocket() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			} 
		}
		catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	
	public static ArrayList<Item> getItems() {
		return items; 
	}
	
    public static void main(String[] args) throws IOException {
    	CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        
        mongo = MongoClients.create(URI);
        database = mongo.getDatabase(DB).withCodecRegistry(pojoCodecRegistry);
    	collection = database.getCollection(COLLECTION, Item.class);
    	collection2 = database.getCollection(COLLECTION2, User.class);
    	ping();
        
    	ArrayList<String> dummy = new ArrayList<>();
    	dummy.add("d");
    	Item i1 = new Item("Arknights", "Tower Defense Game with Anime Characters", "arknights.jpg", 0, 20, 100,  60, false);
    	Item item1 = collection.find(Filters.eq("name", "Arknights")).first();
    	if (item1 == null) {
    	    // insert
    		collection.insertOne(i1);
    	} 
    	items.add(i1);
    	
    	Item i2 = new Item("Elden Ring", "Dark Souls with Open World", "eldenring.png", 0, 10, 1000,  90, false);
    	Item item2 = collection.find(Filters.eq("name", "Elden Ring")).first();
    	if (item2 == null) {
    	    // insert
    		collection.insertOne(i2);
    	} 
    	items.add(i2);
    	
    	Item i3 = new Item("Helldivers 2", "Co-op FPS where you spread democracy", "helldivers.jpg", 0, 1, 99999,  45, false);
    	Item item3 = collection.find(Filters.eq("name", "Helldivers 2")).first();
    	if (item3 == null) {
    	    // insert
    		collection.insertOne(i3);
    	} 
    	items.add(i3);
    	
    	Item i4 = new Item("Hollow Knight", "Metroidvania", "hollowknight.jpg", 0, 7, 101,  30, false);
    	Item item4 = collection.find(Filters.eq("name", "Hollow Knight")).first();
    	if (item4 == null) {
    	    // insert
    		collection.insertOne(i4);
    	} 
    	items.add(i4);
    	
    	Item i5 = new Item("Lethal Company", "Co-op horror", "lethalcompany.jpg", 0, 15, 100,  60, false);
    	Item item5 = collection.find(Filters.eq("name", "Lethal Company")).first();
    	if (item5 == null) {
    	    // insert
    		collection.insertOne(i5);
    	} 
    	items.add(i5);
    	
    	
    	Item i6 = new Item("Persona 3", "Best game", "persona.jpg", 0, 13, 331, 20, false);
    	Item item6 = collection.find(Filters.eq("name", "Persona 3")).first();
    	if (item6 == null) {
    	    // insert
    		collection.insertOne(i6);
    	} 
    	items.add(i6);
    	
    	// remember from database
    	for (int i = 0; i < items.size(); i++) {
    		Item item = collection.find(Filters.eq("name", items.get(i).name)).first();
    		items.get(i).setBid(item.getBid());
    		items.get(i).setOver(item.getOver());
    		items.get(i).setBidHistory(item.getBidHistory());
    	}
    	
    	
    	
    	ServerSocket serverSocket = new ServerSocket(12345); // Port number
        Server server = new Server(serverSocket); 
        System.out.println("Server is running...");
        server.startServer();      
    }
    
    
	
	public static void ping() {
		try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = database.runCommand(command);
            System.out.println("Connected successfully to server.");
        } catch (MongoException me) {
            System.err.println("An error occurred while attempting to run a command: " + me);
        }
	}
}

