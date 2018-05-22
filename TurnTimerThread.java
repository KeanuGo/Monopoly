import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class TurnTimerThread implements ActionListener{
	MonopolyGameHandler handler;
	Timer timer = new Timer(1000, this);
	
	int time = 0;
	int max_time = 5;
	boolean ask_to_sell_replied = false;
	public TurnTimerThread(MonopolyGameHandler handler){
		this.handler = handler;
	}
	
	public void startTurn(){
		time = 0;
		timer.start();
	}
	
	public void actionPerformed(ActionEvent e){
		if(time > max_time){
			if(!handler.rolled_dice){
				if(handler.pay(handler.your_index, 50f, 0)){
					handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+handler.your_index+";"+handler.pData[handler.your_index].balance[0]+";0;");
				}else{
					if(handler.is_host && handler.your_index == 0){
						handler.sellPropertyJOption(handler.your_index, "You need to pay " + 50f + " " + Forex.currencies[0] + ". Choose a property to sell", 50f, 0);
						if(handler.pay(handler.your_index, 50f, 0)){
							handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+handler.your_index+";"+handler.pData[handler.your_index].balance[0]+";0;");
						}else{
							handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + handler.your_index + ";");
							handler.updateStatus(handler.your_index, "Lost");
						}
					}else{
						handler.mn.clients[handler.your_index-1].out.println(MonopolyNetwork.P_ASK_TO_SELL+";100;0;");
						System.out.println("host wait sell");
						new Thread(){
							public void run(){
								while(!ask_to_sell_replied){System.out.print("");}
								if(handler.pay(handler.your_index, 50f, 0)){
									handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+handler.your_index+";"+handler.pData[handler.your_index].balance[0]+";0;");
								}else{
									handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + handler.your_index + ";");
									handler.updateStatus(handler.your_index, "Lost");
								}
							}
						}.start();
						System.out.println("host wait sell done");
					}
				}
			}
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
			if(handler.is_host){
				handler.current_turn+=1;
				handler.current_turn = handler.current_turn%handler.num_of_players;
				handler.moved = true;
				handler.mn.sendToAll(MonopolyNetwork.P_STATUS+";"+handler.your_index+";"+"Playing");
				handler.updateStatus(handler.your_index, "Playing");
				handler.roll_dice_button.setEnabled(false);
			}else{
				handler.mn.clients[0].out.println(MonopolyNetwork.H_TURN_DONE+";"+(handler.your_index)+";");
				handler.mn.sendToAll(MonopolyNetwork.P_STATUS+";"+handler.your_index+";"+"Playing");
				handler.updateStatus(handler.your_index, "Playing");
				handler.roll_dice_button.setEnabled(false);
			}
			timer.stop();
			handler.rolled_dice = false;
		}else{
			handler.mn.sendToAll(MonopolyNetwork.P_STATUS+";"+handler.your_index+";"+"Turn("+(max_time-time)+")");
			handler.updateStatus(handler.your_index, "Turn("+(max_time-time)+")");
			time++;
		}
	}
}