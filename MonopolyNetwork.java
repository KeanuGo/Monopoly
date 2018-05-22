import java.net.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
/*
the awesome idea: data is sent in a pattern of ([data_code;(arguments)*)
i.e Position update{
		Code statement send(MonopolyNetwork.P_MOVE, {"23"});
		Data sent: "0;2;23" where
		0 = data code
		2 = which player to update
		23 = tile number to be placed
	}
	p.s.	still no idea how to deal with update in properties and sending the whole info of player(PlayerData class)
	p.p.s	this is just the mechanism for each player data, still no idea how other variables are manages (i.e current_turn, timer, after getting a chance/community chest card?)
	
	Hence
		a single roll of die may result to: (1 or more data sends to each player)
			-position update
			-zero or more balance updates
		a mortgage may result to: (preferrably 2 data sends to each player)
			-balance update
			-properties update
*/
public class MonopolyNetwork implements ActionListener{
	SoundThread sound;
	public static final int P_MOVE = 0, P_BALANCE = 1, P_PROPERTIES = 2, P_ASK_TO_SELL = 3, P_CARD_REQUEST = 4, P_YOUR_TURN = 5, P_STATUS = 6, P_LOSE = 7, P_USE_CARD = 8, P_CHANGE_DICE_ICON = 99;//data code
	public static final int FOREX_UPDATE = 9;
	public static final int LOBBY_INDEX_UPDATE = 10, LOBBY_START_GAME = 11, LOBBY_FORCE_LEAVE = 12, LOBBY_CHAT_MESSAGE = 13, LOBBY_FOUND_WINNER = 14, LOBBY_FORCE_CLOSE_JDIALOGS = 15;
	public static final int H_TURN_DONE = 21, H_ASK_TO_SELL_REPLIED = 22;
	ClientHandler[] clients = new ClientHandler[7];
	MonopolyGameHandler handler;
	Timer timer = new Timer(5000, this);
	ServerSocket serverSocket;
	int clientsNum = 0;
	int myIndex = 0;
	boolean isWaiting = false;
	PlayerWaitThread waitThread = null;
	boolean is_host;
	String name = "blank";
	
	int serverPort;
	
	public MonopolyNetwork(MonopolyGameHandler handler, boolean is_host){
		System.out.println("network: " + is_host);
		this.is_host = is_host;
		this.handler = handler;
		//initServer();
		//startWaiting();
	}
	
	public void initServer(){
		serverSocket = null;
		try{
			for(int i = 1000; i < 2000; i++){
				if(available(i)){
					serverSocket = new ServerSocket(i);
					System.out.println("Sever Created. Current Port: " + i);
					serverPort = i;
					break;
				}
			}
		}catch(IOException ex){}
	}
	
	public void startWaiting(){
		isWaiting = true;
		timer.start();
		waitThread = null;
		waitThread = new PlayerWaitThread();
		waitThread.start();
	}
	
	public void stopWaiting(){
		isWaiting = false;
		waitThread = null;
	}
	
	public boolean connect(String IP, int port){
		if(is_host){
			return false;
		}
		try{
			Socket host = new Socket(IP, port);
			PrintWriter out = new PrintWriter(host.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(host.getInputStream()));
			out.println(serverPort+"");
			int players_in_host = Integer.parseInt(waitReply(in));
			//System.out.println("Connected to host. num of players: " + players_in_host);
			handler.your_index = players_in_host+1;
			System.out.println("your index: " + handler.your_index);
			out.println("g");
			for(int i = 1; i < players_in_host+1; i++){
				//System.out.println("waiting another client info");
				String[] messages = waitReply(in).split(";");
				//System.out.println("client " + (i+1) + ": " + messages[0] + ";" + messages[1]);
				clients[i] = new ClientHandler(new Socket(messages[0], Integer.parseInt(messages[1])), i, Integer.parseInt(messages[1]), messages[2]);
				clientsNum++;
			}
			out.println(name);
			String host_name = waitReply(in);
			initServer();
			startWaiting();
			clients[0] = new ClientHandler(host, 0, port, host_name);
			clients[0].start();
			for(int i = 1; i < players_in_host+1; i++){
				clients[i].start();
			}
			clientsNum++;
		}catch(IOException io_ex){io_ex.printStackTrace();return false;}
		if(Monopoly.lobbyPanel.is_open){
			Monopoly.lobbyPanel.update();
		}
		return true;
	}
	
	public String waitReply(BufferedReader in){
		String ret;
		int i = 0;
		try{
			while((ret = in.readLine()) == null){
				System.out.print("");
			}
		}catch(IOException io_ex){return null;}
		return ret;
	}
	
	private class PlayerWaitThread extends Thread{
		public void run(){
			while(isWaiting){
				try{
					Socket buffer = serverSocket.accept();
					int serverPort = 999;
					String name_ = "blank";
					if(is_host){
						try{
							PrintWriter out = new PrintWriter(buffer.getOutputStream(), true);
							BufferedReader in = new BufferedReader(new InputStreamReader(buffer.getInputStream()));
							String sp = waitReply(in);
							out.println(clientsNum);
							if(waitReply(in).equals("g")){
								for(int i = 0; i < clientsNum; i++){
									//System.out.println(clients[i].clientSocket.getLocalAddress().toString().substring(1));
									out.println(clients[i].clientSocket.getLocalAddress().toString().substring(1) + ";" + clients[i].client_server_port + ";" + clients[i].client_name + ";");
								}
								//System.out.println("a client connected with wait server: " + sp);
								serverPort = Integer.parseInt(sp);
								name_ = waitReply(in);
								out.println(name);
							}
						}catch(IOException ex){ex.printStackTrace();continue;}
					}
					clients[clientsNum] = new ClientHandler(buffer, clientsNum, serverPort, name_);
					if(clients[clientsNum] != null){
						clients[clientsNum].start();
						clientsNum++;
						System.out.println("Number of clients: " + clientsNum);
						Monopoly.lobbyPanel.update();
					}else{
						System.out.println("Could not connect");
					}
					if(clientsNum >= 7){
						stopWaiting();
					}
				}catch(IOException ex){}
				catch(ArrayIndexOutOfBoundsException aioooe){}
				catch(NullPointerException npe){}
			}
		}
		
		public String waitReply(BufferedReader in){
			String ret;
			//System.out.println("wait reply");
			try{
				while((ret = in.readLine()) == null){
					System.out.print("");
				}
			}catch(IOException io_ex){return null;}
			return ret;
		}
	}
	
	public class ClientHandler extends Thread{
		Socket clientSocket;
        PrintWriter out;
        BufferedReader in;
		int index;
		int client_server_port;
		String client_name = "blank";
		
		public ClientHandler(Socket socket, int index, int client_server_port, String clients_name){
			super(""+client_server_port);
			System.out.println("A client connected, index: " + index + ", port: " + client_server_port);
			client_name = clients_name;
			this.client_server_port = client_server_port;
            this.clientSocket = socket;
			this.index = index;
			try{
				out = new PrintWriter(this.clientSocket.getOutputStream(), true);
			}catch(IOException ex){}
        }
		
		public void run(){
				try{
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String inputLine = "";
					//System.out.println("started");
					while(true){
						inputLine = in.readLine();
						//System.out.println(index+" Read: " + inputLine);
						if((inputLine) != null){
							String[] message = inputLine.split(";");
							if(message[0].equals("exit")){
								//this may actually be useless, when all is done
								dissolve(index);
								System.out.println("A Client Disconnected");
								break;
							}
							try{
								if(Monopoly.lobbyPanel.is_open){
									if(Integer.parseInt(message[0]) == LOBBY_INDEX_UPDATE){
										handler.your_index = Integer.parseInt(message[1]);
										System.out.println("your index updated to " + handler.your_index);
										Monopoly.lobbyPanel.update();
									}else if(Integer.parseInt(message[0]) == LOBBY_START_GAME){
										
										System.out.println("your index starting game is " + handler.your_index);
										Monopoly.showGame();
									}else if(Integer.parseInt(message[0]) == LOBBY_FORCE_LEAVE){
										Monopoly.showMenu();
									}else if(Integer.parseInt(message[0]) == LOBBY_CHAT_MESSAGE){
										new SoundThread("sounds/alert.wav", false);
										Monopoly.lobbyPanel.lobby_chat.setText(Monopoly.lobbyPanel.lobby_chat.getText()+client_name+":"+message[1]+"\n");
									}
								}else if(Monopoly.gameHandler.gamepane_open){
									if(Integer.parseInt(message[0]) == P_MOVE){
										//signifies moving a single player
										//System.out.println("Message move acknowledged");
										handler.board.moveToken(Integer.parseInt(message[1]), Integer.parseInt(message[2]));
										startWaiting();
									}else if(Integer.parseInt(message[0]) == P_BALANCE){
										//signifies updating a value of a single player
										//System.out.println("Update balance acknowledged");
										handler.updateBalance(Integer.parseInt(message[1]), Float.parseFloat(message[2]), Integer.parseInt(message[3]));
									}else if(Integer.parseInt(message[0]) == P_PROPERTIES){
										//signifies updating the properties of a single player owns
										if(Integer.parseInt(message[1]) == 1){
											//addprop
											handler.buyProperty(Integer.parseInt(message[2]), Integer.parseInt(message[3]));
										}else if(Integer.parseInt(message[1]) == 0){
											//delete prop
											Property p = handler.pData[Integer.parseInt(message[2])].properties.get(Integer.parseInt(message[3]));
											handler.pData[Integer.parseInt(message[2])].properties.remove(p);
											p.owner = -1;
											p.level = 0;
											handler.playerStatusPanel.updatePropertiesPanel(Integer.parseInt(message[2]));
										}else if(Integer.parseInt(message[1]) == 2){
											Property p = handler.pData[Integer.parseInt(message[2])].properties.get(Integer.parseInt(message[3]));
											p.level++;
											handler.playerStatusPanel.updatePropertiesPanel(Integer.parseInt(message[2]));
										}
									}else if(Integer.parseInt(message[0]) == P_STATUS){
										//System.out.println("lol");
										try{
											if(Integer.parseInt(message[2]) == 0){
												handler.pData[Integer.parseInt(message[1])].has_bail_chance = true;
												continue;
											}else if(Integer.parseInt(message[2]) == 1){
												handler.pData[Integer.parseInt(message[1])].has_bail_chance = false;
												continue;
											}
										}catch(NumberFormatException asdfa){}
										if(message[2].startsWith("Turn")){
											handler.current_turn_label.setText("Current Turn: Player " + (Integer.parseInt(message[1])+1) + "("+ handler.pData[(Integer.parseInt(message[1])+1)].name + ")");
											handler.current_turn_label.setSize(handler.current_turn_label.getPreferredSize());
										}
										handler.updateStatus(Integer.parseInt(message[1]), message[2]);
									}else if(Integer.parseInt(message[0]) == P_USE_CARD){
										int pNum = Integer.parseInt(message[2]);
										String card_detail = message[3];
										if(Integer.parseInt(message[1]) == 1){
											//System.out.println("from host: " + "P" + (pNum+1) + "used cc card: "+ card_detail);
											new Thread(){
												public void run(){
													new SoundThread("sounds/popup.wav", false);
													JOptionPane.showMessageDialog(null, "Player " + (pNum+1) + " used community chest card: " + card_detail);
												}
											}.start();
										}else if(Integer.parseInt(message[1]) == 2){
											//System.out.println("from host: " + "P" + (pNum+1) + "used c card: "+ card_detail);
											new Thread(){
												public void run(){
													new SoundThread("sounds/popup.wav", false);
													JOptionPane.showMessageDialog(null, "Player " + (pNum+1) + " used chance card: " + card_detail);
												}
											}.start();
										}
									}else if(Integer.parseInt(message[0]) == P_CHANGE_DICE_ICON){
										handler.dice_icon_label[0].setIcon(handler.dice_icons[Integer.parseInt(message[1])]);
										handler.dice_icon_label[1].setIcon(handler.dice_icons[Integer.parseInt(message[2])]);
									}else if(Integer.parseInt(message[0]) == P_LOSE){
										handler.updateStatus(Integer.parseInt(message[1]), "Lost");
									}else if(Integer.parseInt(message[0]) == FOREX_UPDATE){
										//System.out.println("UPDATE FOREX");
										for(int i = 0; i < 4; i++){
											//System.out.println(handler.forexStatusPanel.forex.currency_to_dollar_ratio[i]);
											handler.forexStatusPanel.forex.currency_to_dollar_ratio[i] = Float.parseFloat(message[i+1]);
											//System.out.println(handler.forexStatusPanel.forex.currency_to_dollar_ratio[i]);
										}
										handler.forexStatusPanel.update();
									}else if(Integer.parseInt(message[0]) == LOBBY_FORCE_CLOSE_JDIALOGS){
										handler.forceCloseJDialogs();
									}
									if(is_host){
										if(Integer.parseInt(message[0]) == H_TURN_DONE){
											System.out.println("Current_turn:" + handler.current_turn);
											//if(Integer.parseInt(message[1]) == handler.current_turn){
												handler.turn_responded = true;
											//}else{
												System.out.println("respond error");
											//}
										}else if(Integer.parseInt(message[0]) == P_CARD_REQUEST){
											handler.getCard(Integer.parseInt(message[1]), Integer.parseInt(message[2]));
										}else if(Integer.parseInt(message[0]) == H_ASK_TO_SELL_REPLIED){
											handler.cards.ask_to_sell_replied = true;
											System.out.println("came here");
										}
									}else{
										if(Integer.parseInt(message[0]) == P_YOUR_TURN){
											if(handler.pData[handler.your_index].status.equals("Lost")){
												clients[0].out.println(H_TURN_DONE+";"+(handler.your_index)+";");
											}else{
												handler.ttt.startTurn();
												handler.roll_dice_button.setEnabled(true);
												//handler.current_turn_label.setText("Turn: You");
												//handler.current_turn_label.setSize(handler.current_turn_label.getPreferredSize());
											}
										}else if(Integer.parseInt(message[0]) == P_ASK_TO_SELL){
											handler.sellPropertyJOption(handler.your_index, "You need to pay " + Float.parseFloat(message[1]) + " " + Forex.currencies[Integer.parseInt(message[2])] + ". Choose a property to sell", Float.parseFloat(message[1]), Integer.parseInt(message[2]));
											sendToAll(H_ASK_TO_SELL_REPLIED+";");
											System.out.println("came here too");
										}else if(Integer.parseInt(message[0]) == LOBBY_FOUND_WINNER){
											handler.declareWinner(Integer.parseInt(message[1]));
											handler.nullify();
											Monopoly.showMenu();
										}
									}
								}
							}catch(ArrayIndexOutOfBoundsException aioooe){
								System.out.println("aioooe: " + inputLine);
								System.out.println("Message reading error!");
							}catch(NumberFormatException nfe){
								System.out.println("nfe: " + inputLine);
								System.out.println("Message reading error!");
							}
						}
					}
					this.close();
				}catch(IOException ex){ex.printStackTrace();dissolve(index);}
		}
		
		/*public void send(int move_type, String[] args){
			String message = "";
			//try{
				if(move_type == P_MOVE){
					message += "0;"+(myIndex)+";"+args[0];
				}else if(move_type == P_BALANCE){
					message += "1;"+(myIndex)+";"+args[0];
				}else if(move_type == P_PROPERTIES){
					
				}else if(move_type == P_DATA_CHECK){
					message += "3;"+(myIndex)+";"+args[0]+";";
				}
				out.println(message);
			//}catch(IOException ex){}
		}*/
		public void close(){
			try{
				in.close();
				//out.close();
				clientSocket.close();
			}catch(IOException e){}
		}
	}
	
	public void sendToAll(String message){
		for(int i = 0; i < 7; i++){
			if(!(clients[i]==null)){
				clients[i].out.println(message);
			}
		}
	}
	
	public void dissolve(int index){
		clients[index] = null;
		for(int j = index+1; j < 7; j++){
			System.out.println("" + j + clients[j]);
			clients[j-1] = clients[j];
			try{
				clients[j].index = j-1;
			}catch(NullPointerException npe){}
			clients[j] = null;
		}
		clientsNum--;
		System.out.println("Number of Clients: "+ clientsNum);
		if(Monopoly.lobbyPanel.is_open){
			if(is_host){
				Monopoly.lobbyPanel.update();
				for(int i = 0; i < clientsNum; i++){
					clients[i].out.println(LOBBY_INDEX_UPDATE+";"+(i+1));
				}
			}
		}else if(Monopoly.gameHandler.gamepane_open && index == 0 && !is_host){
			new SoundThread("sounds/popup.wav", false);
			JOptionPane.showMessageDialog(null, "Host has left the game!");
			handler.nullify();
			Monopoly.showMenu();
		}
	}
	
	public static boolean available(int port){
		int MIN_PORT_NUMBER = 999, MAX_PORT_NUMBER = 2000;
		if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}
	
	public void actionPerformed(ActionEvent e){
		//System.out.println("went here");
		//String[] args = {};
		//clients[0].send(P_DATA_CHECK, args);
	}
}