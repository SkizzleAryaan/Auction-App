import java.io.*;
import java.net.*;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Client extends Application {
    public static Socket socket;
    public static ObjectOutputStream out; // Changed to BufferedWriter
    public static ObjectInputStream in; 
    // handle login stuff before creating a socket, pass to clienthandler
    public static String username; 
    public static String password; 
    
    @FXML 
    Label currBidLabel; 
    
   
    Parent root;
    
    Stage stage; 
       
    
    public static void sendMessageToServer(Object msgToServer, String type) {
    	try {
    		if (msgToServer != null && (type.equals("U"))) {
    			//System.out.println(msgToServer);
    			out.writeObject("U");
    			out.writeObject((String) msgToServer);
    		}
    		else if (type.equals("P")) {
    			out.writeObject((String) msgToServer);
    		}
    		else if (msgToServer != null && type.equals("I")) {
				out.writeObject("I");
				out.writeObject(msgToServer);
			}
			else if (msgToServer != null && type.equals("L")) { // shift left item
				out.writeObject("L");
			}		
			else if (msgToServer != null && type.equals("R")) { // shift right item
				out.writeObject("R"); 
			}
			else if (msgToServer != null && (type.equals("US"))) {
				out.writeObject("US");
				out.writeObject(msgToServer);
			}
			else if (msgToServer != null && type.equals("PS")) {
				out.writeObject(msgToServer);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			closeEverything(socket, in, out);
		}  
    }
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	FXMLLoader logloader = new FXMLLoader(getClass().getResource("LogIn.fxml"));  
        root = logloader.load(); 
        Scene scene = new Scene(root);
        
        
        
        stage = primaryStage; 
        stage.setScene(scene);
        stage.setTitle("Client");
        stage.show(); ; 
        	
        
    }
    
   
    
    
    public static void main(String[] args) throws UnknownHostException, IOException {  
      
    	socket = new Socket("localhost", 12345); // Server's IP and Port
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
              
        launch(args);
    }
    
    public static void closeEverything(Socket socket, ObjectInputStream bufferedReader, ObjectOutputStream bufferedWriter) {
		try {
			if (socket != null) {
				socket.close();
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}	
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}