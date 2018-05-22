import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LobbyPanel extends JPanel{
	ImageIcon[] startGameIcons = {new ImageIcon("ui/StartGameButton.png"), new ImageIcon("ui/StartGameButton_hover.png"), new ImageIcon("ui/StartGameButton_mouse_down.png")};
	ImageIcon[] leaveLobbyIcons = {new ImageIcon("ui/LeaveLobbyButton.png"), new ImageIcon("ui/LeaveLobbyButton_hover.png"), new ImageIcon("ui/LeaveLobbyButton_mouse_down.png")};
	ImageIcon[] lobbySendIcons = {new ImageIcon("ui/LobbySendButton.png"), new ImageIcon("ui/LobbySendButton_hover.png"), new ImageIcon("ui/LobbySendButton_mouse_down.png")};
	JPanel[] playerPanel = new JPanel[8];
	JLabel[] playIPLabel = new JLabel[8];
	JTextField messageField = new JTextField(15);
	JButton send = new JButton(lobbySendIcons[0]);
	JButton start_game = new JButton(startGameIcons[0]);
	JButton leave_lobby_button = new JButton(leaveLobbyIcons[0]);
	JPanel p = new JPanel(new GridLayout(8,1));
	boolean is_open = false;
	
	JTextArea lobby_chat = new JTextArea(10,30);
	JScrollPane lobby_scroll = new JScrollPane(lobby_chat);
	
	JPanel host_data_panel = new JPanel(new GridLayout(1,2));
	JLabel host_IP = new JLabel();
	JLabel host_port = new JLabel();
	public LobbyPanel(){
		super();
		this.setLayout(new BorderLayout());
		host_IP.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 20));
		host_port.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 20));
		host_data_panel.add(host_IP);
		host_data_panel.add(host_port);
		this.add(host_data_panel, BorderLayout.NORTH);
		p.setBorder(new LineBorder(Color.GRAY));
		for(int i = 0; i < 8; i++){
			playerPanel[i] = new JPanel(new BorderLayout());
			playerPanel[i].setBackground(null);
			playerPanel[i].setBorder(new LineBorder(Color.GRAY));
			playIPLabel[i] = new JLabel("Open");
			playIPLabel[i].setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 20));
			playerPanel[i].add(playIPLabel[i], BorderLayout.CENTER);
			p.add(playerPanel[i]);
		}
		this.add(p, BorderLayout.CENTER);
		messageField.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 20));
		start_game.setContentAreaFilled(false);
		start_game.setOpaque(false);
		start_game.setBorderPainted(false);
		leave_lobby_button.setContentAreaFilled(false);
		leave_lobby_button.setOpaque(false);
		leave_lobby_button.setBorderPainted(false);
		send.setContentAreaFilled(false);
		send.setOpaque(false);
		send.setBorderPainted(false);
		
		
		JPanel p1 = new JPanel();
		p1.setBackground(null);
		p1.add(start_game);
		p1.add(leave_lobby_button);
		p1.add(send);
		p1.add(messageField);
		this.add(p1,BorderLayout.SOUTH);
		lobby_chat.setEditable(false);
		lobby_chat.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 11));
		lobby_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//lobby_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(lobby_scroll, BorderLayout.EAST);
		this.setBackground(null);
		
		addActionListeners();
	}
	
	public void update(){
		System.out.println("update lobby pane");
		if(Monopoly.gameHandler.mn.is_host){
			String string = "";
			try{
				string = InetAddress.getLocalHost().toString().split("/")[1];
			}catch(UnknownHostException uhe){}
			catch(ArrayIndexOutOfBoundsException aiooe){}
			catch(NullPointerException npe){}
			host_IP.setText("Host IP: " + string);
			host_port.setText("Host port: " + Monopoly.gameHandler.mn.serverPort);
			playIPLabel[0].setText(Monopoly.gameHandler.mn.name + "(you)");
			for(int i = 0; i < Monopoly.gameHandler.mn.clientsNum; i++){
				playIPLabel[i+1].setText(Monopoly.gameHandler.mn.clients[i].client_name + "(" + Monopoly.gameHandler.mn.clients[i].clientSocket.getLocalAddress().toString() + ")");
			}
		}else{
			host_IP.setText("Host IP: " + Monopoly.gameHandler.mn.clients[0].clientSocket.getLocalAddress().toString().substring(1));
			host_port.setText("Host port: " + Monopoly.gameHandler.mn.clients[0].clientSocket.getPort());
			playIPLabel[0].setText(Monopoly.gameHandler.mn.clients[0].client_name + "(host)");
			for(int i = 0; i < Monopoly.gameHandler.your_index; i++){
				playIPLabel[i+1].setText(Monopoly.gameHandler.mn.clients[i].client_name + ", " + Monopoly.gameHandler.mn.clients[i].clientSocket.getLocalAddress().toString());
			}
			playIPLabel[Monopoly.gameHandler.your_index].setText(Monopoly.gameHandler.mn.name + "(you)");
			for(int i = Monopoly.gameHandler.your_index; i < Monopoly.gameHandler.mn.clientsNum; i++){
				playIPLabel[i+1].setText(Monopoly.gameHandler.mn.clients[i].client_name + ", " + Monopoly.gameHandler.mn.clients[i].clientSocket.getLocalAddress().toString());
			}
		}
		for(int i = Monopoly.gameHandler.mn.clientsNum; i < 7; i++){
			playIPLabel[i+1].setText("Open");
		}
	}
	
	public void addActionListeners(){
		start_game.addMouseListener(
			new MouseAdapter(){
				public void mouseEntered(MouseEvent e){
					start_game.setIcon(startGameIcons[1]);
					Monopoly.frame.revalidate();
				}
				public void mouseExited(MouseEvent e){
					start_game.setIcon(startGameIcons[0]);
					Monopoly.frame.revalidate();
				}
				
				public void mousePressed(MouseEvent e){
					start_game.setIcon(startGameIcons[2]);
					Monopoly.frame.revalidate();
				}
				
				public void mouseReleased(MouseEvent e){
					start_game.setIcon(startGameIcons[0]);
					Monopoly.frame.revalidate();
				}
			}
		);
		
		leave_lobby_button.addMouseListener(
			new MouseAdapter(){
				public void mouseEntered(MouseEvent e){
					leave_lobby_button.setIcon(leaveLobbyIcons[1]);
					Monopoly.frame.revalidate();
				}
				public void mouseExited(MouseEvent e){
					leave_lobby_button.setIcon(leaveLobbyIcons[0]);
					Monopoly.frame.revalidate();
				}
				
				public void mousePressed(MouseEvent e){
					leave_lobby_button.setIcon(leaveLobbyIcons[2]);
					Monopoly.frame.revalidate();
				}
				
				public void mouseReleased(MouseEvent e){
					leave_lobby_button.setIcon(leaveLobbyIcons[0]);
					Monopoly.frame.revalidate();
				}
			}
		);
		
		send.addMouseListener(
			new MouseAdapter(){
				public void mouseEntered(MouseEvent e){
					send.setIcon(lobbySendIcons[1]);
					Monopoly.frame.revalidate();
				}
				public void mouseExited(MouseEvent e){
					send.setIcon(lobbySendIcons[0]);
					Monopoly.frame.revalidate();
				}
				
				public void mousePressed(MouseEvent e){
					send.setIcon(lobbySendIcons[2]);
					Monopoly.frame.revalidate();
				}
				
				public void mouseReleased(MouseEvent e){
					send.setIcon(lobbySendIcons[0]);
					Monopoly.frame.revalidate();
				}
			}
		);
	}
}