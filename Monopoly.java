import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
public class Monopoly{
	static JFrame frame = new JFrame("Monopoly");
	static MonopolyGameHandler gameHandler;
	static LobbyPanel lobbyPanel;
	static MenuPanel menuPanel;
	static String[] sargs;
	static SoundThread sound;
	static ImageIcon[] sound_button_icons = {new ImageIcon(new ImageIcon("ui/sound.jpg").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)),
							  new ImageIcon(new ImageIcon("ui/mute.jpg").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT))};
	static JButton sound_button = new JButton(sound_button_icons[0]);
	static boolean mute = false;
	
	public static void main(String[] args){
		frame.setIconImage(new ImageIcon("/ui/frame_icon.png").getImage());
		sargs = args;
		lobbyPanel = new LobbyPanel();
		menuPanel = new MenuPanel();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		frame.getContentPane().setBackground(new Color(170,224,250));
		sound = new SoundThread("sounds/play.wav", true);
		showMenu();
		addActionListeners();
		//showMenu();
	}
	
	public static void showMenu(){
		try{
			menuPanel.is_open = false;
		}catch(NullPointerException npe){}
		try{
			lobbyPanel.is_open = false;
		}catch(NullPointerException npe){}
		try{
			gameHandler.gamepane_open = false;
		}catch(NullPointerException npe){}
		gameHandler = null;
		
		frame.getContentPane().removeAll();
		frame.add(menuPanel);
		menuPanel.setSize(menuPanel.getPreferredSize());
		int xLoc = (int)((float)frame.getWidth()/2.0)-(int)((float)menuPanel.getSize().getWidth()/2.0);
		int yLoc = (int)((float)frame.getHeight()/2.0)-(int)((float)menuPanel.getSize().getHeight()/1.5);
		menuPanel.setLocation(xLoc, yLoc);
		
		frame.add(sound_button);
		sound_button.setBorderPainted(false);
        sound_button.setContentAreaFilled(false);
		sound_button.setOpaque(false);
		sound_button.setSize(sound_button.getPreferredSize());
		sound_button.setLocation(frame.getWidth()-sound_button.getWidth(), 0);
		
		
		frame.repaint();
		frame.revalidate();
		menuPanel.is_open = true;
		//System.out.println(Monopoly.sound.clip.isRunning());
	}
	
	public static void showLobby(boolean is_host){
		try{
			menuPanel.is_open = false;
		}catch(NullPointerException npe){}
		try{
			lobbyPanel.is_open = false;
		}catch(NullPointerException npe){}
		try{
			gameHandler.gamepane_open = false;
		}catch(NullPointerException npe){}
		frame.getContentPane().removeAll();
		
		frame.add(lobbyPanel);
		lobbyPanel.setSize((int)lobbyPanel.getPreferredSize().getWidth(), (int)lobbyPanel.getPreferredSize().getHeight());
		int xLoc = (int)((float)frame.getWidth()/2.0)-(int)((float)lobbyPanel.getSize().getWidth()/2.0);
		int yLoc = (int)((float)frame.getHeight()/2.0)-(int)((float)lobbyPanel.getSize().getHeight()/1.5);
		lobbyPanel.setLocation(xLoc, yLoc);
		
		frame.add(sound_button);
		sound_button.setBorderPainted(false);
        sound_button.setContentAreaFilled(false);
		sound_button.setOpaque(false);
		sound_button.setSize(sound_button.getPreferredSize());
		sound_button.setLocation(frame.getWidth()-sound_button.getWidth(), 0);
		
		frame.repaint();
		frame.revalidate();
		lobbyPanel.is_open = true;
		lobbyPanel.update();
		//System.out.println(Monopoly.sound.clip.isRunning());
	}
	
	public static void showGame(){
		try{
			menuPanel.is_open = false;
		}catch(NullPointerException npe){}
		try{
			lobbyPanel.is_open = false;
		}catch(NullPointerException npe){}
		try{
			gameHandler.gamepane_open = false;
		}catch(NullPointerException npe){}
		frame.getContentPane().removeAll();
		
		frame.add(gameHandler.board);
		frame.add(gameHandler.playerStatusPanel);
		frame.add(gameHandler.forexStatusPanel);
		frame.add(gameHandler.currencyExchangePanel);
		frame.add(gameHandler.roll_dice_button);
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//System.out.println(gameHandler.board.getSize());
		//System.out.println(gameHandler.board.squares[0].getX() + ", " + gameHandler.board.squares[0].getY());
		//System.out.println(gameHandler.board.getSize());
		//gameHandler.playerStatusPanel.setSize(gameHandler.playerStatusPanel.getPreferredSize());
		gameHandler.forexStatusPanel.setLocation(gameHandler.board.getWidth() + 5, 5);
		gameHandler.forexStatusPanel.setSize(385, (int)gameHandler.forexStatusPanel.getPreferredSize().getHeight()+15);
		
		gameHandler.currencyExchangePanel.setLocation(gameHandler.board.getWidth() + 5, gameHandler.forexStatusPanel.getHeight()+ 10);
		gameHandler.currencyExchangePanel.setSize(gameHandler.currencyExchangePanel.getPreferredSize());
		gameHandler.roll_dice_button.setLocation(gameHandler.board.getWidth()+gameHandler.forexStatusPanel.getWidth() + 15, gameHandler.currencyExchangePanel.getHeight() + 30);
		//gameHandler.roll_dice_button.setLocation( (int)((float)gameHandler.board.getWidth()/2.0), (int)((float)gameHandler.board.getHeight()/2.0) );
		gameHandler.roll_dice_button.setSize(gameHandler.roll_dice_button.getPreferredSize());
		
		frame.add(gameHandler.current_turn_label);
		gameHandler.current_turn_label.setSize(gameHandler.current_turn_label.getPreferredSize());
		gameHandler.current_turn_label.setLocation(gameHandler.board.getWidth() + gameHandler.forexStatusPanel.getWidth() + 20, 10);
		
		frame.add(gameHandler.dice_icon_label[0]);
		frame.add(gameHandler.dice_icon_label[1]);
		gameHandler.dice_icon_label[0].setSize(gameHandler.dice_icon_label[0].getPreferredSize());
		gameHandler.dice_icon_label[1].setSize(gameHandler.dice_icon_label[1].getPreferredSize());
		gameHandler.dice_icon_label[0].setLocation(gameHandler.board.getWidth()+ gameHandler.forexStatusPanel.getWidth()+40, gameHandler.current_turn_label.getHeight()+25);
		gameHandler.dice_icon_label[1].setLocation(gameHandler.board.getWidth() + gameHandler.forexStatusPanel.getWidth() + gameHandler.dice_icon_label[0].getWidth() + 55, gameHandler.current_turn_label.getHeight()+25);
		
		gameHandler.playerStatusPanel.setLocation(gameHandler.board.getWidth()+5, gameHandler.forexStatusPanel.getHeight() + gameHandler.currencyExchangePanel.getHeight() + 10);
		gameHandler.playerStatusPanel.setSize(650, 515);
		//gameHandler.asdf.setSize(gameHandler.asdf.getPreferredSize());
		//gameHandler.asdf.setLocation(gameHandler.board.getWidth()+105, gameHandler.playerStatusPanel.getHeight()+ gameHandler.currencyExchangePanel.getHeight() + 30);
		//System.out.println(gameHandler.playerStatusPanel.getX());
		for(int i = 0; i < 8; i++){
			int x_dest = (int)gameHandler.board.squares[0].getLocation().getX() + ((int)(gameHandler.board.squares[0].getSize().getWidth()/2.0))-25;
			int y_dest = (int)gameHandler.board.squares[0].getLocation().getY() + ((int)(gameHandler.board.squares[0].getSize().getHeight()/2.0))-25;
			Point destination = new Point(x_dest, y_dest);
			gameHandler.board.tokens[i].setLocation(destination);
		}
		
		frame.add(sound_button);
		sound_button.setBorderPainted(false);
        sound_button.setContentAreaFilled(false);
		sound_button.setOpaque(false);
		sound_button.setSize(sound_button.getPreferredSize());
		sound_button.setLocation(frame.getWidth()-sound_button.getWidth(), 0);
		
		System.out.println(gameHandler.currencyExchangePanel.exchange_currency_button.getPreferredSize());
		gameHandler.registerNames();
		gameHandler.playerStatusPanel.reBorder();
		gameHandler.initFreeProperties();
		gameHandler.board.refresh();
		frame.repaint();
		frame.revalidate();
		
		gameHandler.gamepane_open = true;
		//System.out.println(Monopoly.sound.clip.isRunning());
		/*new Thread(){
			public void run(){
				gameHandler.board.moveToken(1, gameHandler.board.squares[6].getLocation());
			}
		}.start();
		gameHandler.board.moveToken(0, gameHandler.board.squares[4].getLocation());*/
	}
	
	public static void addActionListeners(){
		menuPanel.createLobbyButton.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					new SoundThread("sounds/button.wav", false);
					gameHandler = new MonopolyGameHandler(true);
					gameHandler.mn.initServer();
					gameHandler.mn.startWaiting();
					gameHandler.mn.name = (menuPanel.ign_field.getText().equals("")?"blank":menuPanel.ign_field.getText());
					showLobby(true);
				}
			}
		);
		
		menuPanel.joinLobbyButton.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					new SoundThread("sounds/button.wav", false);
					int portn = 1000;
					boolean port_valid;
					/*String[] conn_data = ConnectForm.askInput();
					if(conn_data == null){
						return;
					}else{
						try{
							portn = Integer.parseInt(conn_data[1]);
						}catch(NumberFormatException nfe){return;}
						gameHandler = new MonopolyGameHandler(false);
						gameHandler.mn.name = (menuPanel.ign_field.getText().equals("")?"blank":menuPanel.ign_field.getText());
						if(gameHandler.mn.connect(conn_data[0], portn)){
							showLobby(false);
						}else{
							gameHandler.nullify();
							gameHandler = null;
						}
					}*/
					new SoundThread("sounds/popup.wav", false);
					String ip = JOptionPane.showInputDialog(frame, "Enter IP:");
					if(ip == null || ip.equals("")){
						return;
					}
					try{
						String port = JOptionPane.showInputDialog(frame, "Enter port:");
						if(port == null || port.equals("")){
							return;
						}
						portn = Integer.parseInt(port);
					}catch(NumberFormatException nfe){}
					gameHandler = new MonopolyGameHandler(false);
					gameHandler.mn.name = (menuPanel.ign_field.getText().equals("")?"blank":menuPanel.ign_field.getText());
					if(gameHandler.mn.connect(ip, portn)){
						showLobby(false);
					}else{
						gameHandler.nullify();
						gameHandler = null;
					}
				}
			}
		);
		
		menuPanel.exitGameButton.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					new SoundThread("sounds/button.wav", false);
					System.exit(0);
				}
			}
		);
		
		lobbyPanel.messageField.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					new SoundThread("sounds/button.wav", false);
					System.out.println("Lobby send field");
					if(lobbyPanel.messageField.getText() == null || lobbyPanel.messageField.getText().equals("")){
						return;
					}
					gameHandler.mn.sendToAll(MonopolyNetwork.LOBBY_CHAT_MESSAGE+";"+lobbyPanel.messageField.getText());
					lobbyPanel.lobby_chat.setText(lobbyPanel.lobby_chat.getText()+"You:"+lobbyPanel.messageField.getText()+"\n");
					lobbyPanel.messageField.setText("");
				}
			}
		);
		
		lobbyPanel.send.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					new SoundThread("sounds/button.wav", false);
					System.out.println("Lobby send send");
					if(lobbyPanel.messageField.getText() == null || lobbyPanel.messageField.getText().equals("")){
						return;
					}
					gameHandler.mn.sendToAll(MonopolyNetwork.LOBBY_CHAT_MESSAGE+";"+lobbyPanel.messageField.getText());
					lobbyPanel.lobby_chat.setText(lobbyPanel.lobby_chat.getText()+"You:"+lobbyPanel.messageField.getText()+"\n");
					lobbyPanel.messageField.setText("");
				}
			}
		);
		
		lobbyPanel.start_game.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					new SoundThread("sounds/button.wav", false);
					if(Monopoly.gameHandler.mn.is_host){
						Monopoly.gameHandler.mn.sendToAll(""+MonopolyNetwork.LOBBY_START_GAME);
						showGame();
						gameHandler.startGame();
					}
				}
			}
		);
		
		lobbyPanel.leave_lobby_button.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					new SoundThread("sounds/button.wav", false);
					if(Monopoly.gameHandler.mn.is_host){
						Monopoly.gameHandler.mn.sendToAll(""+MonopolyNetwork.LOBBY_FORCE_LEAVE);
						gameHandler.nullify();
						showMenu();
					}else{
						gameHandler.nullify();
						showMenu();
					}
					lobbyPanel.lobby_chat.setText("");
					lobbyPanel.messageField.setText("");
				}
			}
		);
		
		frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e)
            {
				try{
					try{
						gameHandler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + gameHandler.your_index + ";");
						gameHandler.mn.serverSocket.close();
					}catch(NullPointerException npe){}
				}catch(IOException ex){}
                e.getWindow().dispose();
				//System.exit(0);
            }
        });
		
		sound_button.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(!Monopoly.mute){
						System.out.println("MUTED");
						Monopoly.sound.mute();
						mute = true;
						sound_button.setIcon(sound_button_icons[1]);
					}else{
						System.out.println("NOT MUTED");
						mute = false;
						Monopoly.sound = new SoundThread("sounds/play.wav", true);
						sound_button.setIcon(sound_button_icons[0]);
					}
				}
			}
		);
	}
}
