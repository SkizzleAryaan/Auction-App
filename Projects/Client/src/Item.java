import java.io.Serializable;
import java.util.ArrayList;
import org.bson.types.ObjectId;

public class Item implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String name;
	private ObjectId id;
	String description; 
	double minBid;
	double buyBid; 
	int startTime; // seconds
	double currBid; 
	String image; // file path
	ArrayList<String> bidHistory = new ArrayList<String>();
	boolean over; 
	
	public Item() {}
	
	public Item(String name, String desc, String image, double currBid, double minBid, double buyBid, int startTime, boolean over) {
		this.name = name;
		this.description = desc; 
		this.image = image;
		this.minBid = minBid; 
		this.currBid = currBid; 
		this.buyBid = buyBid; 
		this.startTime = startTime; 
		this.over = false; 
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDesc() {
		return description;
	}

	public void setDesc(String desc) {
		this.description = desc;
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public double getBid() {
		return currBid;
	}

	public void setBid(double currBid) {
		this.currBid = currBid;
	}
	
	public double getMinBid() {
		return minBid;
	}

	public void setMinBid(double minBid) {
		this.minBid = minBid;
	}
	
	public double getBuyBid() {
		return buyBid;
	}

	public void setBuyBid(double buyBid) {
		this.buyBid = buyBid;
	}
	
	public int getTime() {
		return startTime;
	}

	public void setTime(int startTime) {
		this.startTime = startTime;
	}
	
	public boolean getOver() {
		return over;
	}

	public void setOver(boolean over) {
		this.over = over;
	}	
	
	public ArrayList<String> getBidHistory() {
		return bidHistory;
	}

	public void setBidHistory(ArrayList<String> bidHistory) {
		this.bidHistory = bidHistory;
	} 
}
