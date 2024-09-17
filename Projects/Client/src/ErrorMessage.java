import java.io.Serializable;

public class ErrorMessage implements Serializable{
	String errorMsg; 
	
	public ErrorMessage(String errorMsg) {
		this.errorMsg = errorMsg; 
	}
}
