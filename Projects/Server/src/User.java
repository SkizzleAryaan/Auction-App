import org.bson.types.ObjectId;

public class User  {
	/**
	 * 
	 */
	private ObjectId id;
	String username;
	String password; 
	
	public User() {}
	
	public User(String username, String password) {
		this.username = username;
		this.password = password; 
 
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}		
}
