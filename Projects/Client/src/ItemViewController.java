import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ItemViewController implements Initializable  {
	

    @FXML
    private TextField bidInputBox;

    @FXML
    private Button bidInputButton;

    @FXML
    private Label buyoutBidLabel;

    @FXML
    public Label currBidLabel;

    @FXML
    private TextArea currBiddersText;

    @FXML
    private Label itemDescLabel;

    @FXML
    private Label itemLabel;

    @FXML
    private ImageView itemPic;

    @FXML
    private Button logOutButton;

    @FXML
    private Label minBidLabel;

    @FXML
    private Button nextButton;

    @FXML
    private Button pastButton;

    @FXML
    private Button prevButton;

    @FXML
    private Label timeLabel;

    @FXML
    private Label usernameLabel;
    
    public String username; 
    
    String bid; 
    
    Item item; 
    int itemIndex = 0; 

    
    
    
   
   

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { 
		
		usernameLabel.setText(Client.username);
		if (Client.username.equals("Guest")) {
			bidInputBox.setEditable(false); 
			bidInputBox.setText("login to bid");
		}
		new Thread(new Runnable() {

			@Override
			public void run() {	
				Object msg = null; 
				while (Client.socket.isConnected()) {
					try {
						msg = Client.in.readObject();
						//item = null; // change to new item
						String text = null; 
						Double bid = null; 
						ArrayList<String> bids = null; 
						Winner win = null; 
						ErrorMessage er = null; 
						try {
							item = (Item) msg;
							setGUI(item); 
						}
						catch (ClassCastException e ){
							System.out.println("msg was not an item (just a check, ignore)"); 
						}
						try {
							win = (Winner) msg;
							showWinner(win); 
						}
						catch (ClassCastException e ){
							System.out.println("msg was not a winner"); 
						}
						try {
							er = (ErrorMessage) msg;
							errorPop(er); 
						}
						catch (ClassCastException e ){
							System.out.println("msg was not a error"); 
						}
						try {
							bids = (ArrayList<String>) msg; 
							if (bids != null) {
								editTextField(bids);
							}
						} 
						catch (ClassCastException e ){
							System.out.println("msg was not an arraylist"); 
						}
						
						try {
							text = (String) msg;
							System.out.println(msg); 
							try {
					            bid = Double.parseDouble(text);
					            if (bid != null) {
		                			editLabel(text); 
		                		}
					        } catch (NumberFormatException e) {
					            System.out.println(text + " is not an integer.");
					        }
							
						}
						catch (ClassCastException e ){
							System.out.println("was not a string (so should be item!)");
						}						
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						
					} catch (IOException e) {
						Client.closeEverything(Client.socket, Client.in, Client.out);
					}
				}
			}
    		
    	}).start(); 	
		
	}
	
	public void editLabel(String msgFromServer) {
		Platform.runLater(() -> {
	        try {
	            currBidLabel.setText(msgFromServer);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
	}
	
	public void editTextField(ArrayList<String> msgFromServer) {
		Platform.runLater(() -> {
			String full = "";
	        try {
	        	for (String msg : msgFromServer) {
	        		full += msg;
	        		full += "\n";
	        	}
	            currBiddersText.setText(full);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
	}
	
	public void playSound(String soundFile) {
		AudioClip note = new AudioClip(this.getClass().getResource(soundFile).toString());
		note.play();
	} 
	
	public void setGUI(Item item) {
		Platform.runLater(() -> {
	        itemLabel.setText(item.name);
	        Double minB = item.minBid;
	        minBidLabel.setText(minB.toString());
	        Double buyB = item.buyBid;
	        buyoutBidLabel.setText(buyB.toString());
	        itemDescLabel.setText(item.description);
	        Double currB = item.currBid;
	        currBidLabel.setText(currB.toString());
	        Integer sTime = item.startTime;
	        timeLabel.setText(sTime.toString());
	        Image myImage = new Image(getClass().getResourceAsStream(item.image));
	        itemPic.setImage(myImage);
	       
	        
	        // ANIMATIONS
	        RotateTransition rotate = new RotateTransition(); 
	        rotate.setNode(itemPic);
	        rotate.setDuration(Duration.millis(500));
	        rotate.setCycleCount(1); // set # rotations
	        rotate.setByAngle(360); // set angle
	        rotate.setInterpolator(Interpolator.LINEAR); 
	        rotate.setAxis(Rotate.Z_AXIS); // default is Z
	        rotate.play();
	        
	        String bids = ""; 
	        for (String msg : item.bidHistory) {
	        	bids += msg;
	        	bids += "\n";
	        }
	        currBiddersText.setText(bids);
	    });
		
		
		
		
		
	}
	
	@FXML
    void sendBid(ActionEvent event) {
		if (bidInputBox.isEditable()) {
			String bid = bidInputBox.getText();	
			if (!bid.isEmpty()) {

				try {
					Double intBid = Double.parseDouble(bid);
					playSound("tacobell.mp3");
					Client.sendMessageToServer(intBid, "I");
					System.out.println(bid); 
				
				} 
				catch (Exception e) {
					ErrorMessage er = new ErrorMessage("Invalid bid"); 
					errorPop(er); 
				} 
				bidInputBox.clear();
			}
		}
		
		
		
    }
	
	@FXML
    void exit(ActionEvent event) {
		System.exit(0);
    }
	
	@FXML
	void next(ActionEvent event) {
		Client.sendMessageToServer("doesn't matter", "L");
	}

	@FXML
	void prev(ActionEvent event) {
		Client.sendMessageToServer("doesn't matter", "R");
	}
	
	public void showWinner(Winner win) {
		
		Platform.runLater(() -> {
			try {
				FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("WinnerPopUp.fxml"));
				Parent root1 = (Parent) fxmlloader.load();
				WinnerController controller = (WinnerController) fxmlloader.getController();
				controller.setWinnerText(win);
				
				Stage st = new Stage();
				st.setTitle("Winner Pop Up");
				st.setScene(new Scene(root1));
				st.show();
			}
			catch (Exception e) {
				System.out.println("failure loading winner pop up"); 
			}
	    });
	}
	
public void errorPop(ErrorMessage em) {
		
		Platform.runLater(() -> {
			try {
				FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("PopUp.fxml"));
				Parent root1 = (Parent) fxmlloader.load();
				ErrorController controller = (ErrorController) fxmlloader.getController();
				controller.setErrorText(em);
				
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