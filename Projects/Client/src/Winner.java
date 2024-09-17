import java.io.Serializable;

public class Winner implements Serializable {
	String username;
	Item item; 
	Double bid; 
	
	public Winner(String username, Item item, Double bid) {
		this.username = username;
		this.item = item;
		this.bid = bid; 
	}
}
