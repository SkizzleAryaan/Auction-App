import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {

    @FXML
    private Button guestButton;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField signPassText;

    @FXML
    private Button signUpButton;

    @FXML
    private TextField signUserText;
    
    Stage stage; 
    Scene scene; 
    Parent root; 

    @FXML
    void logIN(ActionEvent event) {
    	try {
    		FXMLLoader itemloader = new FXMLLoader(getClass().getResource("LogIn.fxml"));
    		root = itemloader.load(); 
        
    		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    		scene = new Scene(root);
    		stage.setScene(scene);
    		stage.show();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    @FXML
    void signUp(ActionEvent event) {
    	try {
    		String username = signUserText.getText();
			 String password = signPassText.getText(); 
	            if (!username.isEmpty() && !password.isEmpty()) {
	            	//client.connectToServer(username); 
	            	Client.sendMessageToServer(username, "US");
	            	Client.sendMessageToServer(password, "PS");
	            	
	                FXMLLoader itemloader = new FXMLLoader(getClass().getResource("LogIn.fxml"));
	                root = itemloader.load(); 
	                
	                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
	                scene = new Scene(root);
	                stage.setScene(scene);
	                stage.show();
	                
	            } else {
	            	Platform.runLater(() -> {
	        			try {
	        				ErrorMessage e = new ErrorMessage("Please enter valid username/password");
	        				FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("PopUp.fxml"));
	        				Parent root1 = (Parent) fxmlloader.load();
	        				ErrorController controller = (ErrorController) fxmlloader.getController();
	        				controller.setErrorText(e);
	        				
	        				Stage st = new Stage();
	        				st.setTitle("Error popUp");
	        				st.setScene(new Scene(root1));
	        				st.show();
	        			}
	        			catch (Exception e) {
	        				System.out.println("failure loading error pop up"); 
	        			}
	        	    });
	            }
	            signUserText.clear();
	            signPassText.clear();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void startAsGuest(ActionEvent event) {
    	try {
			 String username = "Guest";
			 String password = "None"; 
	            if (!username.isEmpty() && !password.isEmpty()) {
	            	//client.connectToServer(username);
	            	Client.username = username; 
	            	Client.password = password; 
	            	Client.sendMessageToServer(username, "U");
	            	Client.sendMessageToServer(password, "P");
	                FXMLLoader itemloader = new FXMLLoader(getClass().getResource("ItemView.fxml"));
	                root = itemloader.load(); 
	                
	                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
	                scene = new Scene(root);
	                stage.setScene(scene);
	                stage.show();
	                
	            }
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
