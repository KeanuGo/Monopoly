import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class MenuPanel extends JPanel{
	//ImageIcon[] createLobbyIcons = {new ImageIcon("ui/CreateLobbyButton.png"), new ImageIcon("ui/CreateLobbyButton_hover.png"), new ImageIcon("ui/CreateLobbyButton_mouse_down.png")};
	ImageIcon[] createLobbyIcons = {new ImageIcon(new ImageIcon("ui/CreateLobbyButton.png").getImage().getScaledInstance(201, 67, Image.SCALE_DEFAULT)),
									new ImageIcon(new ImageIcon("ui/CreateLobbyButton_hover.png").getImage().getScaledInstance(201, 67, Image.SCALE_DEFAULT)),
									new ImageIcon(new ImageIcon("ui/CreateLobbyButton_mouse_down.png").getImage().getScaledInstance(201, 67, Image.SCALE_DEFAULT))};
	ImageIcon[] joinLobbyIcons = {new ImageIcon("ui/JoinLobbyButton.png"), new ImageIcon("ui/JoinLobbyButton_hover.png"), new ImageIcon("ui/JoinLobbyButton_mouse_down.png")};
	ImageIcon[] exitGameIcons = {new ImageIcon("ui/ExitGameButton.png"), new ImageIcon("ui/ExitGameButton_hover.png"), new ImageIcon("ui/ExitGameButton_mouse_down.png")};
	JButton createLobbyButton = new JButton(createLobbyIcons[0]);
	//JButton createLobbyButton = new JButton("Create Lobby",createLobbyIcons[0]);
	JButton joinLobbyButton = new JButton(joinLobbyIcons[0]);
	JButton exitGameButton = new JButton(exitGameIcons[0]);
	JLabel menu_header = new JLabel(new ImageIcon(new ImageIcon("ui/monopoly_logo.png").getImage().getScaledInstance(603, 219, Image.SCALE_DEFAULT)));
	JLabel ign_label = new JLabel(new ImageIcon("ui/IgnLabel.png"));
	JTextField ign_field = new JTextField(15);
	boolean is_open = false;
	
	public MenuPanel(){
		super();
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		this.add(menu_header, constraints);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.LINE_END;
		this.add(ign_label, constraints);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.LINE_START;
		this.add(ign_field, constraints);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.LINE_START;
		//createLobbyButton.setIcon(new ImageIcon("ui/Arrow_red.png"));
		createLobbyButton.setContentAreaFilled(false);
		createLobbyButton.setOpaque(false);
		createLobbyButton.setBorderPainted(false);
		//createLobbyButton.setHorizontalTextPosition(JButton.CENTER);
		//createLobbyButton.setVerticalTextPosition(JButton.CENTER);
		//createLobbyButton.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 25));
		this.add(createLobbyButton, constraints);
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.LINE_START;
		joinLobbyButton.setContentAreaFilled(false);
		joinLobbyButton.setOpaque(false);
		joinLobbyButton.setBorderPainted(false);
		this.add(joinLobbyButton, constraints);
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.LINE_START;
		exitGameButton.setContentAreaFilled(false);
		exitGameButton.setOpaque(false);
		exitGameButton.setBorderPainted(false);
		this.add(exitGameButton, constraints);
		this.setBackground(null);
		
		ign_field.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 20));
		
		addActionListeners();
	}
	
	public void addActionListeners(){
		createLobbyButton.addMouseListener(
			new MouseAdapter(){
				public void mouseExited(MouseEvent e){
					createLobbyButton.setIcon(createLobbyIcons[0]);
					Monopoly.frame.revalidate();
				}
				
				public void mouseEntered(MouseEvent e){
					createLobbyButton.setIcon(createLobbyIcons[1]);
					Monopoly.frame.revalidate();
				}
				
				public void mousePressed(MouseEvent e){
					createLobbyButton.setIcon(createLobbyIcons[2]);
					Monopoly.frame.revalidate();
				}
				
				public void mouseReleased(MouseEvent e){
					createLobbyButton.setIcon(createLobbyIcons[0]);
					Monopoly.frame.revalidate();
				}
			}
		);
		
		joinLobbyButton.addMouseListener(
			new MouseAdapter(){
				public void mouseEntered(MouseEvent e){
					joinLobbyButton.setIcon(joinLobbyIcons[1]);
					Monopoly.frame.revalidate();
				}
				public void mouseExited(MouseEvent e){
					joinLobbyButton.setIcon(joinLobbyIcons[0]);
					Monopoly.frame.revalidate();
				}
				
				public void mousePressed(MouseEvent e){
					joinLobbyButton.setIcon(joinLobbyIcons[2]);
					Monopoly.frame.revalidate();
				}
				
				public void mouseReleased(MouseEvent e){
					joinLobbyButton.setIcon(joinLobbyIcons[0]);
					Monopoly.frame.revalidate();
				}
			}
		);
		
		exitGameButton.addMouseListener(
			new MouseAdapter(){
				public void mouseEntered(MouseEvent e){
					exitGameButton.setIcon(exitGameIcons[1]);
					Monopoly.frame.revalidate();
				}
				public void mouseExited(MouseEvent e){
					exitGameButton.setIcon(exitGameIcons[0]);
					Monopoly.frame.revalidate();
				}
				
				public void mousePressed(MouseEvent e){
					exitGameButton.setIcon(exitGameIcons[2]);
					Monopoly.frame.revalidate();
				}
				
				public void mouseReleased(MouseEvent e){
					exitGameButton.setIcon(exitGameIcons[0]);
					Monopoly.frame.revalidate();
				}
			}
		);
	}
	
	
}