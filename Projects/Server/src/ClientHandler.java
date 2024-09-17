import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import com.mongodb.client.model.Filters;

public class ClientHandler implements Runnable {

	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
      
	
	private Socket socket; 
	//private BufferedReader bufferedReader;
	//private BufferedWriter bufferedWriter; 
	public ObjectOutputStream objectOutputStream;
	public ObjectInputStream objectInputStream; 
	
	// add password for login later
	private String clientUsername; 
	private String clientPassword; 
	public boolean gotUsername;
	public boolean gotPassword;
	String messageFromClient;
	Double bid; 
	boolean loggedIn = false; 
	
	Item currItem;
	int itemIndex = 0; 
	private final Object lock = new Object();
	boolean isBid; 
	
	
	public ClientHandler(Socket socket) {
		try {
			this.socket = socket;
			//this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			//this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
	        // create an object output stream from the output stream so we can send an object through it
	         this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
	        // create a DataInputStream so we can read data from it.
	         this.objectInputStream = new ObjectInputStream(socket.getInputStream());

	        
			// waiting until it gets username and will need to verify login later
			//this.clientUsername = bufferedReader.readLine(); 
			clientHandlers.add(this); 
			
		} 
		catch (IOException e) {
			closeEverything(socket, objectInputStream, objectOutputStream); 
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		messageFromClient = null; 
			while (socket.isConnected()) {
				isBid = false;
				try {		
					// verify username and password later
					messageFromClient = (String) objectInputStream.readObject();
					if (messageFromClient.equals("US")) {
						// add new user/pass
						messageFromClient = (String) objectInputStream.readObject();
						String messageFromClient2 = (String) objectInputStream.readObject();
						
						
						PasswordAuthentication passwordAuth = new PasswordAuthentication();
						String hashedPassword = passwordAuth.hash(messageFromClient2.toCharArray());
						
						//User u = Server.collection2.find(Filters.eq("username", messageFromClient)).first();
						User user = Server.collection2.find(
							    Filters.and(
							        Filters.eq("username", messageFromClient),
							        Filters.eq("password", hashedPassword)
							    )
							).first();
						if (user == null) {
							User newU = new User(messageFromClient, hashedPassword);
							Server.collection2.insertOne(newU);
						}
					}
					else if (messageFromClient.equals("U")) {
						messageFromClient = (String) objectInputStream.readObject();				
						String messageFromClient2 = (String) objectInputStream.readObject();
						PasswordAuthentication passwordAuth = new PasswordAuthentication();
						//String hashedPassword = passwordAuth.hash(messageFromClient2.toCharArray());
						User user = Server.collection2.find(Filters.eq("username", messageFromClient)).first();
						String hashpass = null;
						boolean isAuthenticated = false; 
						if (user != null) {
						    hashpass = user.getPassword();
						    isAuthenticated = passwordAuth.authenticate(messageFromClient2.toCharArray(), hashpass);
						}
						boolean duplicate = false;
						if (user != null) {
						    for (ClientHandler c : clientHandlers) {
						        if (c != this && c.clientUsername != null && c.clientPassword != null) {
						            if (c.clientUsername.equals(messageFromClient) && c.clientPassword.equals(messageFromClient2)) {
						                duplicate = true;
						                break; // No need to continue checking if duplicate is found
						            }
						        }        
						    }
						}
						if (duplicate) {
						    this.objectOutputStream.writeObject(-2);
						    this.objectOutputStream.reset(); // if necessary
						    System.out.println("Duplicate user detected: " + messageFromClient);
						}
						if (isAuthenticated) {
							clientUsername = messageFromClient; 
							clientPassword = messageFromClient2; 
							gotUsername = true;
							loggedIn = true;
							currItem = Server.getItems().get(itemIndex); 
							this.objectOutputStream.writeObject(1);
							this.objectOutputStream.writeObject(currItem);
							this.objectOutputStream.reset(); // if necessary
							broadcastMessage("Server: " + clientUsername + " entered");
						}	
						else {
							this.objectOutputStream.writeObject(-1);
							this.objectOutputStream.reset(); // if necessary
							System.out.println("did not receive valid login");
						}
						
						
					} 
					else {
						if (messageFromClient.equals("I")) {
							
							bid = null; 
							try {
								bid = (Double) objectInputStream.readObject(); 
								if (bid != null) {		
									synchronized(lock) {
										if (!currItem.over) {
											if (bid == currItem.buyBid) {
												currItem.currBid = bid; 
												String output = clientUsername + " BOUGHT FOR ¥ " + bid;
												Item item = Server.collection.find(Filters.eq("name", currItem.name)).first();
												item.setBid(bid);
												currItem.over = true; 
												item.setOver(true);		
												currItem.bidHistory.add(output); 
												item.setBidHistory(currItem.bidHistory);
												Server.collection.findOneAndReplace(Filters.eq("name", currItem.name), item);
												
												
												broadcastMessage(bid.toString()); // add unique alert
												broadcastMessage(currItem.bidHistory);
												
												// add popup here
												Winner win1 = new Winner(clientUsername, currItem, bid);
												broadcastAllMessage(win1);
												
												
												
											}
											else if (bid >= currItem.minBid && bid > currItem.currBid && currItem.currBid != currItem.buyBid) {
												currItem.currBid = bid; 
												Item item = Server.collection.find(Filters.eq("name", currItem.name)).first();
												item.setBid(bid);
												currItem.bidHistory.add(clientUsername + " bid ¥ " + bid); 
												item.setBidHistory(currItem.bidHistory);
												Server.collection.findOneAndReplace(Filters.eq("name", currItem.name), item);
												System.out.println(clientUsername + ": " + bid.toString()); 
												broadcastMessage(bid.toString());
												broadcastMessage(currItem.bidHistory);
											}
											else {
												// send an eror message to that speciifc clienthandler to trigger pop up
												ErrorMessage em = new ErrorMessage("Too low, please bid higher!"); 
												this.objectOutputStream.writeObject(em);
												this.objectOutputStream.reset();
											}
										}
										else {
											ErrorMessage em = new ErrorMessage("This item auction has closed"); 
											this.objectOutputStream.writeObject(em);
											this.objectOutputStream.reset();
										}
										
									}
								}
										
																							
							} catch (ClassNotFoundException | ClassCastException e) {
								ErrorMessage em = new ErrorMessage("Invalid bid"); 
								this.objectOutputStream.writeObject(em);
								this.objectOutputStream.reset();
							}
						}	
						
						if (messageFromClient.equals("L")) {
							if (itemIndex != Server.getItems().size()-1) {
								itemIndex++;
							}
							else {
								itemIndex = 0; 
							}
							
							currItem = Server.getItems().get(itemIndex); 
							this.objectOutputStream.writeObject(currItem);
							this.objectOutputStream.reset();
							//this.objectOutputStream.writeObject(currItem.currBid);
							
							
						}
						if (messageFromClient.equals("R")) {
							if (itemIndex != 0) {
								itemIndex--;
							}
							else {
								itemIndex = Server.getItems().size()-1; 
							}
							
							currItem = Server.getItems().get(itemIndex); 
							this.objectOutputStream.writeObject(currItem);
							this.objectOutputStream.reset();
							//this.objectOutputStream.writeObject(currItem.currBid);
							
						}
						
					}
				}
				catch (ClassNotFoundException | ClassCastException e) {
					System.out.println("Invalid command"); 
				}
				catch (IOException e) {
					closeEverything(socket, objectInputStream, objectOutputStream);
					break; 
				}
			}
		
		
		
	}
	
	/*public void handleBidding() {
		
	} */
	
	public void broadcastMessage(Object messageToSend) {
		Iterator<ClientHandler> iterator = clientHandlers.iterator();
	    while (iterator.hasNext()) {
	        ClientHandler clientHandler = iterator.next();
	        try {
	        	if (this.itemIndex == clientHandler.itemIndex && clientHandler.loggedIn) {
	        		clientHandler.objectOutputStream.writeObject(messageToSend);
	        		clientHandler.objectOutputStream.reset();
	        	} 
	            //clientHandler.objectOutputStream.writeObject(messageToSend);
	        } catch (IOException e) {
	            iterator.remove(); // Remove the current clientHandler from the list
	            clientHandler.closeEverything(socket, objectInputStream, objectOutputStream);
	        }
	    }
	}
	
	public void broadcastAllMessage(Object messageToSend) {
		Iterator<ClientHandler> iterator = clientHandlers.iterator();
	    while (iterator.hasNext()) {
	        ClientHandler clientHandler = iterator.next();
	        try {
	        	if (clientHandler.loggedIn) {
	        		clientHandler.objectOutputStream.writeObject(messageToSend);
		        	clientHandler.objectOutputStream.reset();
	        	}
	        	
	        	
	            //clientHandler.objectOutputStream.writeObject(messageToSend);
	        } catch (IOException e) {
	            iterator.remove(); // Remove the current clientHandler from the list
	            clientHandler.closeEverything(socket, objectInputStream, objectOutputStream);
	        }
	    }
	}
	
	public void removeClientHandler() {
		clientHandlers.remove(this); 
		broadcastMessage("Bidder " + clientUsername + " has left the auction");
	}
	
	public void closeEverything(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
		removeClientHandler(); 
		try {
			if (objectInputStream != null) {
				objectInputStream.close();
			}
			if (objectOutputStream != null) {
				objectOutputStream.close();
			}
			if (socket != null) {
				socket.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}