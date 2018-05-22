//add time to each player 1 min 15 sec
//player chooses which property to sell if not enough money (cards part not yet done)

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.Random;

public class MonopolyGameHandler extends MouseAdapter implements ActionListener{
	SoundThread sound;
	Property[] properties = new Property[40];
	MonopolyBoard board;
	MonopolyNetwork mn;
	PlayerData[] pData = new PlayerData[8];
	PlayerStatusPanel playerStatusPanel;
	ForexStatusPanel forexStatusPanel;
	CurrencyExchangePanel currencyExchangePanel;
	Cards cards;
	ImageIcon[] dice_icons = {new ImageIcon(new ImageIcon("ui/Die_0.png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)),
							  new ImageIcon(new ImageIcon("ui/Die_1.png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)),
							  new ImageIcon(new ImageIcon("ui/Die_2.png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)),
							  new ImageIcon(new ImageIcon("ui/Die_3.png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)),
							  new ImageIcon(new ImageIcon("ui/Die_4.png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)),
							  new ImageIcon(new ImageIcon("ui/Die_5.png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)),
							  new ImageIcon(new ImageIcon("ui/Die_6.png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT))};
	boolean gamepane_open = false;
	
	JLabel current_turn_label = new JLabel("Current Turn: ");
	ImageIcon[] roll_dice_button_icons = {new ImageIcon(new ImageIcon("ui/RollDiceButton.png").getImage().getScaledInstance(150, 50, Image.SCALE_DEFAULT)),
										  new ImageIcon(new ImageIcon("ui/RollDiceButton_hover.png").getImage().getScaledInstance(150, 50, Image.SCALE_DEFAULT)),
										  new ImageIcon(new ImageIcon("ui/RollDiceButton_mouse_down.png").getImage().getScaledInstance(150, 50, Image.SCALE_DEFAULT))};
	JButton roll_dice_button = new JButton(roll_dice_button_icons[0]);
	JLabel[] dice_icon_label = {new JLabel(dice_icons[0]), new JLabel(dice_icons[0])};
	
	boolean is_host = false;
	int your_index = 0;
	
	int current_turn = 0;
	int num_of_players;
	boolean turn_responded = false;
	boolean moved = false;
	boolean rolled_dice = false;
	boolean lose = false;
	
	TurnTimerThread ttt = new TurnTimerThread(this);
	
	public MonopolyGameHandler(boolean is_host){
		this.is_host = is_host;
		
		board = new MonopolyBoard(this);
		//String[] sss = {"Bob", "Vin", "John", "May", "Carl", "April", "Buck", "Lucy"};
		for(int i = 0; i < 8; i++){
			pData[i] = new PlayerData("blank", null, board.tokens[i]);
		}
		playerStatusPanel = new PlayerStatusPanel(pData, 8, this);
		forexStatusPanel = new ForexStatusPanel(this, is_host);
		currencyExchangePanel = new CurrencyExchangePanel();
		cards = new Cards(this);
		currencyExchangePanel.exchange_currency_button.addActionListener(this);
		roll_dice_button.addActionListener(this);
		roll_dice_button.setEnabled(false);
		playerStatusPanel.sell_button.addActionListener(this);
		playerStatusPanel.upgrade_button.addActionListener(this);
		mn = new MonopolyNetwork(this, is_host);
		
		roll_dice_button.setContentAreaFilled(false);
		roll_dice_button.setOpaque(false);
		roll_dice_button.setBorderPainted(false);
		roll_dice_button.addMouseListener(
			new MouseAdapter(){
				public void mouseEntered(MouseEvent e){
					roll_dice_button.setIcon(roll_dice_button_icons[1]);
					Monopoly.frame.revalidate();
				}
				public void mouseExited(MouseEvent e){
					roll_dice_button.setIcon(roll_dice_button_icons[0]);
					Monopoly.frame.revalidate();
				}
				
				public void mousePressed(MouseEvent e){
					roll_dice_button.setIcon(roll_dice_button_icons[2]);
					Monopoly.frame.revalidate();
				}
				
				public void mouseReleased(MouseEvent e){
					roll_dice_button.setIcon(roll_dice_button_icons[0]);
					Monopoly.frame.revalidate();
				}
			}
		);
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == currencyExchangePanel.exchange_currency_button){
			String currency_from = currencyExchangePanel.currency_from.getSelectedItem().toString();
			String currency_to = currencyExchangePanel.currency_to.getSelectedItem().toString();
			float amount_from = 0.0f;
			try{
				amount_from = Float.parseFloat(currencyExchangePanel.amount_from.getText());
			}catch(NumberFormatException nfe){}
			changeCurrency(currency_from, currency_to, amount_from, your_index);
		}else if(e.getSource() == playerStatusPanel.sell_button){
			try{
				String prop1 = playerStatusPanel.playerPropertiesListSelector[your_index].getSelectedValue().toString();
				String[] prop = prop1.split(":");
				int index = Integer.parseInt(prop[0])-1;
				Property p = pData[your_index].properties.get(index);
				new SoundThread("sounds/popup.wav", false);
				int n = JOptionPane.showConfirmDialog(
					null,
					"Are you sure you want to sell property for " + (((p.price*(p.level+1))+(p.price*.15f*p.level))/10f) + " " + Forex.currencies[p.currency],
					"Sell property?",
					JOptionPane.YES_NO_OPTION);
				if(n == JOptionPane.YES_OPTION){
					pData[your_index].properties.remove(p);
					pData[your_index].balance[p.currency] += (((p.price*(p.level+1))+(p.price*.15f*p.level))/10f);
					updateBalance(your_index, pData[your_index].balance[p.currency], p.currency);
					p.owner = -1;
					p.level = 0;
					playerStatusPanel.updatePropertiesPanel(your_index);
					mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+your_index+";"+pData[your_index].balance[p.currency]+";"+p.currency+";");
					mn.sendToAll(MonopolyNetwork.P_PROPERTIES+";0;"+your_index+";"+index+";");
				}
			}catch(NullPointerException npe){}
		}else if(e.getSource() == playerStatusPanel.upgrade_button){
			try{
				String prop1 = playerStatusPanel.playerPropertiesListSelector[your_index].getSelectedValue().toString();
				String[] prop = prop1.split(":");
				int index = Integer.parseInt(prop[0])-1;
				Property p = pData[your_index].properties.get(index);
				if(!p.upgradeable){
					new SoundThread("sounds/popup.wav", false);
					JOptionPane.showMessageDialog(null, "Property is not cannot be upgraded!");
					return;
				}else if(!p.monopolized){
					new SoundThread("sounds/popup.wav", false);
					JOptionPane.showMessageDialog(null, "Property must be monopolized!");
					return;
				}
				new SoundThread("sounds/popup.wav", false);
				int n = JOptionPane.showConfirmDialog(
					null,
					"Are you sure you want to upgrade property for " + (p.price+(p.price*.15f*(p.level+1))) + " " + Forex.currencies[p.currency],
					"Sell property?",
					JOptionPane.YES_NO_OPTION);
				if(n == JOptionPane.YES_OPTION){
					if(pay(your_index, p.price+(p.price*.15f*(p.level+1)), p.currency)){
						p.level++;
						playerStatusPanel.updatePropertiesPanel(your_index);
						mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+your_index+";"+pData[your_index].balance[p.currency]+";"+p.currency+";");
						mn.sendToAll(MonopolyNetwork.P_PROPERTIES+";2;"+your_index+";"+index+";");
					}else{
						new SoundThread("sounds/popup.wav", false);
						JOptionPane.showMessageDialog(null, "Insufficient money for upgrade!");
					}
				}
			}catch(NullPointerException npe){}
		}else if(e.getSource() == roll_dice_button){
			rolled_dice = true;
			lose = false;
			roll_dice_button.setEnabled(false);
			new SoundThread("sounds/roll_dice.wav", false);
			int toAdd1 =  1+(new Random().nextInt(6));
			int toAdd2 =  1+(new Random().nextInt(6));
			dice_icon_label[0].setIcon(dice_icons[toAdd1]);
			dice_icon_label[1].setIcon(dice_icons[toAdd2]);
			mn.sendToAll(MonopolyNetwork.P_CHANGE_DICE_ICON + ";"+toAdd1+";"+toAdd2);
			int destination = (pData[your_index].curTileNum+toAdd1+toAdd2)%40;//2+(new Random().nextInt(10));
			/*int toAdd = 1;
			try{
				toAdd =Integer.parseInt(asdf.getText());
			}catch(NumberFormatException nfe){}
			int destination = (pData[your_index].curTileNum + toAdd)%40;*/
			board.moveToken(your_index, destination);
			if(destination < pData[your_index].curTileNum){
				updateBalance(your_index, pData[your_index].balance[0]+100, 0);
				mn.sendToAll(mn.P_BALANCE+";"+your_index+";"+pData[your_index].balance[0]+";"+0+";");
			}
			mn.sendToAll(mn.P_MOVE+";"+your_index+";"+destination+";");
			try{
				Thread.sleep(100);
			}catch(InterruptedException ie){}
			new Thread(){
				public void run(){
					while(board.token_busy[your_index]){System.out.print("");}
					//System.out.println("Pos: " + pData[your_index].curTileNum);
					if(properties[pData[your_index].curTileNum].owner == -1 && !lose && properties[pData[your_index].curTileNum].buyable){
						//System.out.println("1");
						new SoundThread("sounds/popup.wav", false);
						int n = JOptionPane.showConfirmDialog(
							null,
							"Would you like to buy property for " + properties[pData[your_index].curTileNum].price + " " + Forex.currencies[properties[pData[your_index].curTileNum].currency] + "? Tile no: " + pData[your_index].curTileNum,
							"Buy property?",
							JOptionPane.YES_NO_OPTION);
						if(n == JOptionPane.YES_OPTION){
							//buy property
							buyProperty(your_index, pData[your_index].curTileNum);
							mn.sendToAll(mn.P_PROPERTIES + ";" + 1 + ";" + your_index + ";" + pData[your_index].curTileNum + ";");
						}
					}else if(!lose && properties[pData[your_index].curTileNum].buyable && properties[pData[your_index].curTileNum].owner != your_index){
						//System.out.println("2");
						int ownerIndex = properties[pData[your_index].curTileNum].owner;
						float payable = properties[pData[your_index].curTileNum].fine+(properties[pData[your_index].curTileNum].level*0.05f);
						int currencyToPay = properties[pData[your_index].curTileNum].currency;
						if(properties[pData[your_index].curTileNum].monopolized){
							payable = payable*2f;
						}
						if(pay(your_index, payable, currencyToPay)){
							mn.sendToAll(mn.P_BALANCE+";"+your_index+";"+pData[your_index].balance[currencyToPay]+";"+currencyToPay+";");
							updateBalance(ownerIndex, (pData[ownerIndex].balance[currencyToPay]+payable), currencyToPay);
							mn.sendToAll(mn.P_BALANCE+";"+ownerIndex+";"+(pData[ownerIndex].balance[currencyToPay])+";"+currencyToPay+";");
						}else{
							if(pData[your_index].properties.size()>0){
								sellPropertyJOption(your_index, "You need to pay " + payable + " " + Forex.currencies[currencyToPay] + ". Choose a property to sell", payable, currencyToPay);
								if(pay(your_index, payable, currencyToPay)){
									mn.sendToAll(mn.P_BALANCE+";"+your_index+";"+pData[your_index].balance[currencyToPay]+";"+currencyToPay+";");
									updateBalance(ownerIndex, (pData[ownerIndex].balance[currencyToPay]+payable), currencyToPay);
									mn.sendToAll(mn.P_BALANCE+";"+ownerIndex+";"+(pData[ownerIndex].balance[currencyToPay])+";"+currencyToPay+";");
								}else{
									lose = true;
								}
							}else{
								lose = true;
							}
						}
					}else if(!lose && pData[your_index].curTileNum == 30){
						//System.out.println("3");
						if(pData[your_index].has_bail_chance){
							new SoundThread("sounds/popup.wav", false);
							int n = JOptionPane.showConfirmDialog(
								null,
								"You are going to jail. Would you like to use your free pass?",
								"Jail",
								JOptionPane.YES_NO_OPTION);
								if(n == JOptionPane.YES_OPTION){
									pData[your_index].has_bail_chance = false;
									mn.sendToAll(MonopolyNetwork.P_STATUS+";"+your_index+";1;");
								}else{
									mn.sendToAll(MonopolyNetwork.P_STATUS+";"+your_index+";"+"In Jail");
									board.moveToken(your_index, 10);
									mn.sendToAll(mn.P_MOVE+";"+your_index+";"+10+";");
								}
						}else{
							mn.sendToAll(MonopolyNetwork.P_STATUS+";"+your_index+";"+"In Jail");
							board.moveToken(your_index, 10);
							mn.sendToAll(mn.P_MOVE+";"+your_index+";"+10+";");
						}
					}else if(pData[your_index].curTileNum == 4){
						//System.out.println("4");
						//income tax
						if(!pay(your_index, 200f, 0)){
							if(pData[your_index].properties.size()>0){
								sellPropertyJOption(your_index, "You need to pay " + 200f + " " + Forex.currencies[0] + ". Choose a property to sell", 200f, 0);
								if(pay(your_index, 200f, 0)){
									mn.sendToAll(mn.P_BALANCE+";"+your_index+";"+pData[your_index].balance[0]+";"+0+";");
								}else{
									lose = true;
								}
							}else{
								lose = true;
							}
						}
					}else if(pData[your_index].curTileNum == 38){
						//System.out.println("5");
						//super tax
						if(!pay(your_index, 42000f, 3)){
							if(!pay(your_index, 42000f, 3)){
								if(pData[your_index].properties.size()>0){
									sellPropertyJOption(your_index, "You need to pay " + 42000f + " " + Forex.currencies[3] + ". Choose a property to sell", 42000f, 3);
									if(pay(your_index, 42000f, 3)){
										mn.sendToAll(mn.P_BALANCE+";"+your_index+";"+pData[your_index].balance[3]+";"+3+";");
									}else{
										lose = true;
									}
								}else{
									lose = true;
								}
							}
						}
						mn.sendToAll(mn.P_BALANCE+";"+your_index+";"+pData[your_index].balance[3]+";"+3+";");
					}else if(pData[your_index].curTileNum == 2 || pData[your_index].curTileNum == 17 || pData[your_index].curTileNum == 33){
						//System.out.println("6");
						if(is_host){
							getCard(1, your_index);
						}else{
							mn.clients[0].out.println(MonopolyNetwork.P_CARD_REQUEST+";1;"+your_index+";");
						}
					}else if(pData[your_index].curTileNum == 7 || pData[your_index].curTileNum == 22 || pData[your_index].curTileNum == 36){
						//System.out.println("8");
						if(is_host){
							getCard(2, your_index);
						}else{
							mn.clients[0].out.println(MonopolyNetwork.P_CARD_REQUEST+";2;"+your_index+";");
						}
					}
					if(is_host){
						current_turn+=1;
						current_turn = current_turn%num_of_players;
					}else{
						mn.clients[0].out.println(MonopolyNetwork.H_TURN_DONE+";"+(your_index)+";");
					}
					if(lose){
						mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + your_index + ";");
						updateStatus(your_index, "Lost");
						System.out.println("lost");
					}else{
						ttt.timer.stop();
						updateStatus(your_index, "Playing");
						mn.sendToAll(MonopolyNetwork.P_STATUS+";"+your_index+";"+"Playing");
					}
					moved = true;
					rolled_dice = false;
				}
			}.start();
		}
	}
	
	public void changeCurrency(String currency_from, String currency_to, float amount_from, int pNum){
		System.out.println("Exchange from " + currency_from + " to " + currency_to + " by " + amount_from);
		int from_index = Forex.getIndex(currency_from);
		int to_index = Forex.getIndex(currency_to);
		if(pData[pNum].balance[from_index]-amount_from < 0.0){
			//System.out.println(pData[pNum].balance[from_index]+"<"+amount_from);
			return;
		}
		float from_bal = pData[pNum].balance[from_index]-amount_from;
		updateBalance(pNum, from_bal, from_index);
		mn.sendToAll(mn.P_BALANCE+";"+pNum+";"+from_bal+";"+from_index+";");
		float exchange_ratio = forexStatusPanel.forex.currency_to_dollar_ratio[to_index]/forexStatusPanel.forex.currency_to_dollar_ratio[from_index];
		float to_bal = pData[pNum].balance[to_index]+(amount_from*exchange_ratio);
		updateBalance(pNum, to_bal,to_index);
		mn.sendToAll(mn.P_BALANCE+";"+your_index+";"+to_bal+";"+to_index+";");
	}
	
	public void updateBalance(int pNum, float balance, int currency){
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		pData[pNum].balance[currency] = balance;
		playerStatusPanel.balance[pNum][currency].setText("" + df.format(pData[pNum].balance[currency]));
	}
	
	public void updateStatus(int pNum, String status){
		pData[pNum].status = status;
		playerStatusPanel.status[pNum].setText("Player Status: " + status);
	}
	
	public boolean pay(int pNum, float amount, int currency){
		if(pData[pNum].balance[currency]-amount<0){
			//System.out.println("hereeee");
			boolean can_change = false;
			int[] ranked_currency = forexStatusPanel.forex.getOptimalRatios();
			int index = 0;
			while(pData[pNum].balance[currency] < amount && index < 4){
				float exchange_ratio = forexStatusPanel.forex.currency_to_dollar_ratio[ranked_currency[index]]/forexStatusPanel.forex.currency_to_dollar_ratio[currency];
				if(ranked_currency[index] == currency){
				}else if(pData[pNum].balance[ranked_currency[index]]-((amount-pData[pNum].balance[currency])*exchange_ratio)>=0){
					//System.out.println("here1");
					//System.out.println(pData[pNum].balance[1]);
					//System.out.println(pData[pNum].balance[0]);
					changeCurrency(forexStatusPanel.forex.currencies[ranked_currency[index]], forexStatusPanel.forex.currencies[currency], ((amount-pData[pNum].balance[currency])*(exchange_ratio)), pNum);
				}else if(pData[pNum].balance[ranked_currency[index]] > 0){
					changeCurrency(forexStatusPanel.forex.currencies[ranked_currency[index]], forexStatusPanel.forex.currencies[currency], pData[pNum].balance[ranked_currency[index]], pNum);
				}
				index++;
			}
			if(amount- pData[pNum].balance[currency] < .1f){
				pData[pNum].balance[currency] = amount;
			}
			if(pData[pNum].balance[currency] < amount){
				can_change = false;
			}else{
				can_change = true;
			}
			if(!can_change){
				return false;
			}
		}
		updateBalance(pNum, pData[pNum].balance[currency]-amount, currency);
		return true;
	}
	
	public void buyProperty(int pNum, int location){
		for(int i = 0; i < properties.length; i++){
			if(properties[i].location == location){
				if(properties[i].owner != -1){return;}
				if(pay(pNum, properties[i].price, properties[i].currency)){
					properties[i].owner = pNum;//free_properties.remove(buf);
					pData[pNum].properties.add(properties[i]);
					playerStatusPanel.updatePropertiesPanel(pNum);
				}else{
					new SoundThread("sounds/popup.wav", false);
					JOptionPane.showMessageDialog(null, "Cannot buy property!");
				}
				break;
			}
		}
	}
	
	public void startGameManagerThread(){
		new Thread(){
			public void run(){
				while(true){
					int winner = -1;
					if((winner = checkWinner()) != -1){
						declareWinner(winner);
						mn.sendToAll(MonopolyNetwork.LOBBY_FOUND_WINNER+";"+winner+";");
						nullify();
						Monopoly.showMenu();
						break;
					}
					System.out.print("");
					if(pData[current_turn].status.equals("Lost")){
						current_turn++;
						current_turn = current_turn%num_of_players;
						continue;
					}else if(current_turn == 0){
						System.out.println("host turn");
						mn.sendToAll(MonopolyNetwork.P_STATUS+";"+current_turn+";"+"Turn");
						updateStatus(current_turn, "Turn");
						current_turn_label.setText("Current Turn: Player 1("+ pData[1].name + ")");
						current_turn_label.setSize(current_turn_label.getPreferredSize());
						ttt.startTurn();
						roll_dice_button.setEnabled(true);
						moved = false;
						while(!moved && !pData[current_turn].status.equals("Lost")){System.out.print("");}
						mn.sendToAll(MonopolyNetwork.LOBBY_FORCE_CLOSE_JDIALOGS+"");
					}else{
						try{
							System.out.println(current_turn + ", " + mn.clients[current_turn-1].clientSocket.getLocalAddress().toString());
							mn.clients[current_turn-1].out.println(MonopolyNetwork.P_YOUR_TURN + ";");
							mn.sendToAll(MonopolyNetwork.P_STATUS+";"+current_turn+";"+"Turn");
							updateStatus(current_turn, "Turn");
							turn_responded = false;
							while(!turn_responded && !pData[current_turn].status.equals("Lost") && !pData[current_turn].status.equals("Lost")){
								System.out.print("");
							}
							mn.sendToAll(MonopolyNetwork.P_STATUS+";"+current_turn+";"+"Playing");
							mn.sendToAll(MonopolyNetwork.LOBBY_FORCE_CLOSE_JDIALOGS+"");
							current_turn+=1;
							current_turn = current_turn%num_of_players;
						}catch(ArrayIndexOutOfBoundsException aioobe){aioobe.printStackTrace();}
					}
				}
			}
		}.start();
	}
	
	public void forceCloseJDialogs(){
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			if (window instanceof JDialog) {
				JDialog dialog = (JDialog) window;
				if (dialog.getContentPane().getComponentCount() == 1
					&& dialog.getContentPane().getComponent(0) instanceof JOptionPane){
					dialog.dispose();
				}
			}
		}
	}
	
	public boolean sellPropertyJOption(int pNum, String message, float payable, int currency){
		JLabel amountsold = new JLabel("Amount sold: 0");
		DefaultListModel propertiesList = new DefaultListModel();
		for(int i = 0; i < pData[pNum].properties.size(); i++){
			String s = (i+1)+":" + pData[pNum].properties.get(i).toString();
			propertiesList.addElement(s);
		}
		JList playerPropertiesListSelector = new JList(propertiesList);
		playerPropertiesListSelector.setSelectionModel(new DefaultListSelectionModel() {
			@Override
			public void setSelectionInterval(int index0, int index1) {
				if(super.isSelectedIndex(index0)) {
					super.removeSelectionInterval(index0, index1);
				}
				else {
					super.addSelectionInterval(index0, index1);
				}
			}
		});
		playerPropertiesListSelector.addListSelectionListener(
			new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent lse){
					Object[] values = playerPropertiesListSelector.getSelectedValues();
					float[] totalPrice = {0f,0f,0f,0f};
					float totalNet = 0f;
					for(int i = 0; i < values.length; i++){
						String[] prop = ((String)values[i]).split(":");
						int index = Integer.parseInt(prop[0])-1;
						Property p = pData[pNum].properties.get(index);
						totalPrice[p.currency] +=(((p.price*(p.level+1))+(p.price*.15f*p.level))/10f);
					}
					String text = "";
					for(int i = 0; i < 4; i++){
						if(totalPrice[i] > 0f){
							text += totalPrice[i] + Forex.currencies[i] + " ";
							if(i == 0){
								totalNet += totalPrice[i];
							}else{
								float exchange_ratio = forexStatusPanel.forex.currency_to_dollar_ratio[0]/forexStatusPanel.forex.currency_to_dollar_ratio[i];
								totalNet += totalPrice[i]*exchange_ratio;
							}
						}
					}
					float exchange_ratio1 = forexStatusPanel.forex.currency_to_dollar_ratio[currency]/forexStatusPanel.forex.currency_to_dollar_ratio[0];
					totalNet *= exchange_ratio1;
					amountsold.setText("Amount sold: " + text);
					if(totalNet >= payable){
						amountsold.setForeground(Color.GREEN);
					}else{
						amountsold.setForeground(Color.RED);
					}
				}
		});
		
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel(message), BorderLayout.NORTH);
		p.add(playerPropertiesListSelector, BorderLayout.CENTER);
		p.add(amountsold, BorderLayout.SOUTH);
		
		new SoundThread("sounds/popup.wav", false);
		JOptionPane.showMessageDialog(null,p,"Choose property to sell",JOptionPane.INFORMATION_MESSAGE);
		Object[] values = playerPropertiesListSelector.getSelectedValues();
		float[] totalPrice = {0f,0f,0f,0f};
		float totalNet = 0f;
		for(int i = 0; i < values.length; i++){
			String[] prop = ((String)values[i]).split(":");
			int index = Integer.parseInt(prop[0])-1;
			Property p1 = pData[pNum].properties.get(index);
			totalPrice[p1.currency] +=(((p1.price*(p1.level+1))+(p1.price*.15f*p1.level))/10f);
		}
		String text = "";
		for(int i = 0; i < 4; i++){
			if(totalPrice[i] > 0f){
				text += totalPrice[i] + Forex.currencies[i] + " ";
				if(i == 0){
					totalNet += totalPrice[i];
				}else{
					float exchange_ratio = forexStatusPanel.forex.currency_to_dollar_ratio[0]/forexStatusPanel.forex.currency_to_dollar_ratio[i];
					totalNet += totalPrice[i]*exchange_ratio;
				}
			}
		}
		float exchange_ratio1 = forexStatusPanel.forex.currency_to_dollar_ratio[currency]/forexStatusPanel.forex.currency_to_dollar_ratio[0];
		totalNet *= exchange_ratio1;
		Property[] to_sell = new Property[values.length];
		for(int i = 0; i < values.length; i++){
			String[] prop = ((String)values[i]).split(":");
			int index = Integer.parseInt(prop[0])-1;
			to_sell[i] = pData[pNum].properties.get(index);
		}
		for(int i = 0; i < values.length; i++){
			String[] prop = ((String)values[i]).split(":");
			int index = Integer.parseInt(prop[0])-1;
			Property p2 = to_sell[i];
			pData[pNum].properties.remove(p2);
			pData[pNum].balance[p2.currency] += (((p2.price*(p2.level+1))+(p2.price*.15f*p2.level))/10f);
			updateBalance(pNum, pData[pNum].balance[p2.currency], p2.currency);
			p2.owner = -1;
			p2.level = 0;
			playerStatusPanel.updatePropertiesPanel(pNum);
			mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+pData[pNum].balance[p2.currency]+";"+p2.currency+";");
			mn.sendToAll(MonopolyNetwork.P_PROPERTIES+";0;"+pNum+";"+index+";");
		}
		if(totalNet >= payable){
			//do nothing
		}else{
			while(totalNet < payable && pData[pNum].properties.size() > 0){
				Property p3 = pData[pNum].properties.get(0);
				pData[pNum].properties.remove(p3);
				pData[pNum].balance[p3.currency] += (((p3.price*(p3.level+1))+(p3.price*.15f*p3.level))/10f);
				updateBalance(pNum, pData[pNum].balance[p3.currency], p3.currency);
				float exchange_ratio2 = forexStatusPanel.forex.currency_to_dollar_ratio[currency]/forexStatusPanel.forex.currency_to_dollar_ratio[p3.currency];
				totalNet += (p3.price)*exchange_ratio2;
				p3.owner = -1;
				p3.level = 0;
				playerStatusPanel.updatePropertiesPanel(pNum);
				mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+pData[pNum].balance[p3.currency]+";"+p3.currency+";");
				mn.sendToAll(MonopolyNetwork.P_PROPERTIES+";0;"+pNum+";"+0+";");
			}
		}
		if(totalNet >= payable){
			return true;
		}else{
			return false;
		}
	}
	
	public void startGame(){
		if(!is_host){
			return;
		}
		forexStatusPanel.forex.runForex();
		num_of_players = mn.clientsNum+1;
		startGameManagerThread();
	}
	
	public void nullify(){
		try{
			mn.serverSocket.close();
			mn.serverSocket = null;
		}catch(NullPointerException npe){}catch(IOException io_ex){}
		for(int i = 0; i < 7; i++){
			try{
				mn.clients[i].out.println("exit");
				mn.clients[i].clientSocket.close();
			}catch(NullPointerException npe){}catch(IOException io_ex){}
		}
		mn = null;
		forexStatusPanel.forex.timer.stop();
		forexStatusPanel = null;
		playerStatusPanel = null;
		board = null;
		currencyExchangePanel = null;
	}
	
	public void registerNames(){
		//System.out.println("changing names");
		if(mn.is_host){
			pData[0].name = mn.name;
			for(int i = 0; i < Monopoly.gameHandler.mn.clientsNum; i++){
				pData[i+1].name = mn.clients[i].client_name;
			}
		}else{
			pData[0].name = mn.clients[0].client_name;
			for(int i = 0; i < Monopoly.gameHandler.your_index; i++){
				pData[i+1].name = mn.clients[i].client_name;
			}
			pData[your_index].name = mn.name;
			for(int i = Monopoly.gameHandler.your_index; i < Monopoly.gameHandler.mn.clientsNum; i++){
				pData[i+1].name = mn.clients[i].client_name;
			}
		}
		//System.out.println("changing names done");
	}
	
	public void initFreeProperties(){
		float[] init_prices = {0f, 80f, 0f, 110f, 0f, 400f, 170f, 0f, 260f, 350f,
							   0f, 4000f, 15000f, 5000f, 5500f, 20000f, 8500f, 0f, 13000f, 17500f,
							   0f, 64f, 0f, 88f, 90f, 320f, 136f, 208f, 240f, 280f,
							   0f, 8400f, 11550f, 0f, 17850f, 42000f, 0f, 27300f, 0f, 37500f};
		int[] init_currency = {0,0,0,0,0,0,0,0,0,0, //USD 1USD=1USD
							   1,1,1,1,1,1,1,1,1,1, //Pesos 1USD = 50Pesos
							   2,2,2,2,2,2,2,2,2,2, //Euro 1USD = 0.8Euro
							   3,3,3,3,3,3,3,3,3,3};//Yen 1USD = 105Yen
		for(int i = 0; i < 40; i++){
			properties[i] = new Property(i, init_prices[i], init_currency[i], (init_prices[i]==0.0f?false:true));
		}
	}
	
	public void getCard(int cardType, int pNum){
		if(!is_host){return;}
		Card c;
		if(cardType == 1){
			c = cards.getCommunityChestCard();
			mn.sendToAll(MonopolyNetwork.P_USE_CARD+";1;"+pNum+";"+c.text+";");
			//System.out.println("Player " +(pNum+1) + "used cc card: " + c.text);
			cards.useCommunityChestCard(c.use_number, pNum);
			new SoundThread("sounds/popup.wav", false);
			JOptionPane.showMessageDialog(null, "Player " + (pNum+1) + " used community chest card: " + c.text);
		}else if(cardType == 2){
			c = cards.getChanceCard();
			mn.sendToAll(MonopolyNetwork.P_USE_CARD+";2;"+pNum+";"+c.text+";");
			//System.out.println("Player " +(pNum+1) + "used c card: " + c.text);
			cards.useChanceCard(c.use_number, pNum);
			new SoundThread("sounds/popup.wav", false);
			JOptionPane.showMessageDialog(null, "Player " + (pNum+1) + " used chance card: " + c.text);
		}
		
	}
	
	public void declareWinner(int pNum){
		new SoundThread("sounds/popup.wav", false);
		JOptionPane.showMessageDialog(null, "Player " + pData[pNum].name + " won!");
		System.out.println("Player " + pNum + " won!");
	}
	
	public int checkWinner(){
		int remaining_players = num_of_players;
		int index = 0;
		for(int i = 0; i < num_of_players; i++){
			if(pData[i].status.equals("Lost")){
				remaining_players--;
			}else{
				index = i;
			}
		}
		if(remaining_players == 1){
			return index;
		}else{
			return -1;
		}
	}
}