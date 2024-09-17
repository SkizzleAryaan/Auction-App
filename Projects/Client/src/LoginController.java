import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

	@FXML
    private Button guestButton;

    @FXML
    private Button logButton;

    @FXML
    private TextField logField;

    @FXML
    private PasswordField passwordBox;

    @FXML
    private Button signUpButton;
    
    Client client;
    Stage stage; 
    Scene scene; 
    Parent root; 
    
    
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public void initialize() {
    	logButton.setOnAction(new EventHandler<ActionEvent>() {	
    			public void handle(ActionEvent event) {
    				
    				try {
						 String username = logField.getText();
						 String password = passwordBox.getText(); 
				            if (!username.isEmpty() && !password.isEmpty()) {
				            	//client.connectToServer(username);
				            	
				            	Integer in = -1; 
				            	Client.sendMessageToServer(username, "U");
					            Client.sendMessageToServer(password, "P");
					            try {
					            	Object temp = Client.in.readObject(); 
					            	System.out.println(temp.toString());
					            	
					            	in = (Integer) temp; 
					            	
					            }
					            catch (ClassCastException e) {
					            	e.printStackTrace();
					            }
					          
				            	
				            					            	
				            	if (in == -2) {
				            		popUpFunc("Already signed in on another account"); 
				            	}
				            	else if (in >= 0) {
				            		Client.username = username; 
					            	Client.password = password; 
				            		 FXMLLoader itemloader = new FXMLLoader(getClass().getResource("ItemView.fxml"));
						             root = itemloader.load(); 
						                /*ItemViewController itemController = itemloader.getController();
						                itemController.setClient(client);
						                itemController.setUser(username); */
						                
						              stage = (Stage)((Node)event.getSource()).getScene().getWindow();
						              scene = new Scene(root);
						              stage.setScene(scene);
						              stage.show();
				            	}
				            	else {
				            		popUpFunc("Please enter valid username/password"); 
				            	}
				            	
				               
				                
				            }
				            else {
				            	popUpFunc("Please enter valid username/password"); 
				            }
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				logField.clear();
    				passwordBox.clear();
    			}
    	    	
    		});    
    }
    
    
    
    @FXML
    void signUp(ActionEvent event) {
    	try {
    		FXMLLoader itemloader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
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
    
    public void popUpFunc(String error) {
    	Platform.runLater(() -> {
			try {
				ErrorMessage e = new ErrorMessage(error);
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

}