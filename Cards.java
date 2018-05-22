import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Cards{
	MonopolyGameHandler handler;
	String text;
	int action;
	ArrayList<Card> communityChestCards_InDeck = new ArrayList<>();
	ArrayList<Card> communityChestCards_Used = new ArrayList<>();
	ArrayList<Card> chanceCards_InDeck = new ArrayList<>();
	ArrayList<Card> chanceCards_Used = new ArrayList<>();
	
	Random rand = new Random();
	boolean ask_to_sell_replied = false;
	int inner_thread_index;
	public Cards(MonopolyGameHandler handler){
		this.handler = handler;
		initCards();
	}
	
	public void initCards(){
		for(int i=0; i<Card.comm_chest_text.length; i++){
			if(i == 1){
				communityChestCards_InDeck.add(new Card(Card.comm_chest_text[i], i));
				chanceCards_InDeck.add(new Card(Card.chance_text[i], i));
			}
		}
	}
	
	public Card getCommunityChestCard(){
		Card ret = communityChestCards_InDeck.get(rand.nextInt(communityChestCards_InDeck.size()));
		communityChestCards_Used.add(ret);
		if(communityChestCards_InDeck.size() == 0){
			while(communityChestCards_Used.size() > 0){
				communityChestCards_InDeck.add(communityChestCards_Used.get(0));
			}
		}
		return ret;
	}
	
	public Card getChanceCard(){
		Card ret = chanceCards_InDeck.get(rand.nextInt(chanceCards_InDeck.size()));
		chanceCards_Used.add(ret);
		if(chanceCards_InDeck.size() == 0){
			while(chanceCards_Used.size() > 0){
				chanceCards_InDeck.add(chanceCards_Used.get(0));
			}
		}
		return ret;
	}
	
	public void useCommunityChestCard(int use_number, int pNum){
		//0,1,2,3,4,5,6,7,8,9,10,11,12,13
		//mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + your_index + ";");
		//updateStatus(your_index, "Lost");
		if(use_number == 0){
			handler.pData[pNum].has_bail_chance = true;
			handler.mn.sendToAll(MonopolyNetwork.P_STATUS+";"+pNum+";0;");
		}else if(use_number == 1){
			handler.updateBalance(pNum, handler.pData[pNum].balance[0]+10f, 0);
			handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
		}else if(use_number == 2){
			handler.updateBalance(pNum, handler.pData[pNum].balance[0]+50f, 0);
			handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
		}else if(use_number == 3 || use_number == 5 || use_number == 6){
			handler.updateBalance(pNum, handler.pData[pNum].balance[0]+100f, 0);
			handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
		}else if(use_number == 4){
			handler.updateBalance(pNum, handler.pData[pNum].balance[0]+20f, 0);
			handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
		}else if(use_number == 7){
			handler.updateBalance(pNum, handler.pData[pNum].balance[0]+25f, 0);
			handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
		}else if(use_number == 8){
			if(handler.pay(pNum, 100f, 0)){
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
			}else{
				if(handler.is_host && pNum == 0){
					handler.sellPropertyJOption(handler.your_index, "You need to pay " + 100f + " " + Forex.currencies[0] + ". Choose a property to sell", 100f, 0);
					if(handler.pay(pNum, 100f, 0)){
						handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
					}else{
						handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + pNum + ";");
						handler.updateStatus(pNum, "Lost");
					}
				}else{
					handler.mn.clients[pNum-1].out.println(MonopolyNetwork.P_ASK_TO_SELL+";100;0;");
					System.out.println("host wait sell");
					new Thread(){
						public void run(){
							while(!ask_to_sell_replied){System.out.print("");}
							if(handler.pay(pNum, 100f, 0)){
								handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
							}else{
								handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + pNum + ";");
								handler.updateStatus(pNum, "Lost");
							}
						}
					}.start();
					System.out.println("host wait sell done");
				}
			}
		}else if(use_number == 9){
			handler.updateBalance(pNum, handler.pData[pNum].balance[0]+200f, 0);
			handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
		}else if(use_number == 10 || use_number == 11){
			if(handler.pay(pNum, 50f, 0)){
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
			}else{
				if(handler.is_host && pNum == 0){
					handler.sellPropertyJOption(handler.your_index, "You need to pay " + 50f + " " + Forex.currencies[0] + ". Choose a property to sell", 50f, 0);
					if(handler.pay(pNum, 50f, 0)){
						handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
					}else{
						handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + pNum + ";");
						handler.updateStatus(pNum, "Lost");
					}
				}else{
					handler.mn.clients[pNum-1].out.println(MonopolyNetwork.P_ASK_TO_SELL+";50;0;");
					System.out.println("host wait sell");
					new Thread(){
						public void run(){
							while(!ask_to_sell_replied){System.out.print("");}
							if(handler.pay(pNum, 50f, 0)){
								handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
							}else{
								handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + pNum + ";");
								handler.updateStatus(pNum, "Lost");
							}
						}
					}.start();
					System.out.println("host wait sell done");
				}
			}
		}else if(use_number == 12){
			for(int i = 0; i < handler.num_of_players; i++){
				if(handler.pData[i].status.equals("Lost") || i == pNum){
					continue;
				}
				if(handler.pay(i, 10f, 0)){
					handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+i+";"+handler.pData[i].balance[0]+";0;");
					handler.updateBalance(pNum, handler.pData[pNum].balance[0]+10f, 0);
					handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
				}else{
					if(handler.is_host && i == 0){
						handler.sellPropertyJOption(handler.your_index, "You need to pay " + 10f + " " + Forex.currencies[0] + ". Choose a property to sell", 10f, 0);
						if(handler.pay(i, 10f, 0)){
							handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+i+";"+handler.pData[i].balance[0]+";0;");
							handler.updateBalance(pNum, handler.pData[pNum].balance[0]+10f, 0);
							handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
						}else{
							handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + i + ";");
							handler.updateStatus(i, "Lost");
						}
					}else{
						handler.mn.clients[i-1].out.println(MonopolyNetwork.P_ASK_TO_SELL+";10;0;");
						System.out.println("host wait sell");
						inner_thread_index = i;
						new Thread(){
							public void run(){
								while(!ask_to_sell_replied){System.out.print("");}
								if(handler.pay(inner_thread_index, 10f, 0)){
									handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+inner_thread_index+";"+handler.pData[inner_thread_index].balance[0]+";0;");
									handler.updateBalance(pNum, handler.pData[pNum].balance[0]+10f, 0);
									handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
								}else{
									handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + inner_thread_index + ";");
									handler.updateStatus(inner_thread_index, "Lost");
								}
							}
						}.start();
						System.out.println("host wait sell done");
					}
				}
			}
		}else if(use_number == 13){
			handler.board.moveToken(pNum, 0);
			handler.mn.sendToAll(handler.mn.P_MOVE+";"+pNum+";0;");
			handler.updateBalance(pNum, handler.pData[pNum].balance[0]+200f, 0);
			handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
		}else if(use_number == 14){
			for(int i = 0; i < handler.pData[pNum].properties.size(); i++){
				if(handler.pay(pNum, 30f, 0)){
					handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
				}else{
					if(handler.is_host && pNum == 0){
						handler.sellPropertyJOption(handler.your_index, "You need to pay " + 30f + " " + Forex.currencies[0] + ". Choose a property to sell", 30f, 0);
						if(handler.pay(pNum, 30f, 0)){
							handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
						}else{
							handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + pNum + ";");
							handler.updateStatus(pNum, "Lost");
						}
					}else{
						handler.mn.clients[pNum-1].out.println(MonopolyNetwork.P_ASK_TO_SELL+";30;0;");
						System.out.println("host wait sell");
						inner_thread_index = pNum;
						new Thread(){
							public void run(){
								while(!ask_to_sell_replied){System.out.print("");}
								if(handler.pay(inner_thread_index, 30f, 0)){
									handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+inner_thread_index+";"+handler.pData[inner_thread_index].balance[0]+";0;");
								}else{
									handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + inner_thread_index + ";");
									handler.updateStatus(inner_thread_index, "Lost");
								}
							}
						}.start();
						System.out.println("host wait sell done");
					}
				}
			}
		}else if(use_number == 15){
			if(handler.pData[pNum].has_bail_chance){
				int n = JOptionPane.showConfirmDialog(
					null,
					"You are going to jail. Would you like to use your free pass?",
					"Jail",
					JOptionPane.YES_NO_OPTION);
					if(n == JOptionPane.YES_OPTION){
						handler.pData[pNum].has_bail_chance = false;
					}else{
						handler.board.moveToken(pNum, 10);
						handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+10+";");
					}
			}else{
				handler.board.moveToken(pNum, 10);
				handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+10+";");
			}
		}
	}
	
	public void useChanceCard(int use_number, int pNum){
		//0,1,2,3,4,5,6,7,8,9,10,11,12,14,15
		if(use_number == 0){
			handler.pData[pNum].has_bail_chance = true;
			handler.mn.sendToAll(MonopolyNetwork.P_STATUS+";"+pNum+";0;");
		}else if(use_number == 1){
			for(int i = 0; i < handler.pData[pNum].properties.size(); i++){
				if(handler.pay(pNum, 20f, 0)){
					handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
				}else{
					if(handler.is_host && pNum == 0){
						handler.sellPropertyJOption(handler.your_index, "You need to pay " + 20f + " " + Forex.currencies[0] + ". Choose a property to sell", 20f, 0);
						if(handler.pay(pNum, 20f, 0)){
							handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
						}else{
							handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + pNum + ";");
							handler.updateStatus(pNum, "Lost");
						}
					}else{
						handler.mn.clients[pNum-1].out.println(MonopolyNetwork.P_ASK_TO_SELL+";20;0;");
						System.out.println("host wait sell");
						inner_thread_index = pNum;
						new Thread(){
							public void run(){
								while(!ask_to_sell_replied){System.out.print("");}
								if(handler.pay(inner_thread_index, 20f, 0)){
									handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+inner_thread_index+";"+handler.pData[inner_thread_index].balance[0]+";0;");
								}else{
									handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + inner_thread_index + ";");
									handler.updateStatus(inner_thread_index, "Lost");
								}
							}
						}.start();
						System.out.println("host wait sell done");
					}
				}
			}
		}else if(use_number == 2){
			if(handler.pay(pNum, 15f, 0)){
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
			}else{
				if(handler.is_host && pNum == 0){
					handler.sellPropertyJOption(handler.your_index, "You need to pay " + 15f + " " + Forex.currencies[0] + ". Choose a property to sell", 15f, 0);
					if(handler.pay(pNum, 15f, 0)){
						handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
					}else{
						handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + pNum + ";");
						handler.updateStatus(pNum, "Lost");
					}
				}else{
					handler.mn.clients[pNum-1].out.println(MonopolyNetwork.P_ASK_TO_SELL+";15;0;");
					System.out.println("host wait sell");
					new Thread(){
						public void run(){
							while(!ask_to_sell_replied){System.out.print("");}
							if(handler.pay(pNum, 15f, 0)){
								handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
							}else{
								handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + pNum + ";");
								handler.updateStatus(pNum, "Lost");
							}
						}
					}.start();
					System.out.println("host wait sell done");
				}
			}
		}else if(use_number == 3){
			for(int i = 0; i < handler.num_of_players; i++){
				if(handler.pData[i].status.equals("Lost") || i == pNum){
					continue;
				}
				if(handler.pay(i, 40f, 0)){
					handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+i+";"+handler.pData[i].balance[0]+";0;");
					handler.updateBalance(pNum, handler.pData[pNum].balance[0]+40f, 0);
					handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
				}else{
					if(handler.is_host && i == 0){
						handler.sellPropertyJOption(handler.your_index, "You need to pay " + 40f + " " + Forex.currencies[0] + ". Choose a property to sell", 10f, 0);
						if(handler.pay(i, 40f, 0)){
							handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+i+";"+handler.pData[i].balance[0]+";0;");
							handler.updateBalance(pNum, handler.pData[pNum].balance[0]+40f, 0);
							handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
						}else{
							handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + i + ";");
							handler.updateStatus(i, "Lost");
						}
					}else{
						handler.mn.clients[i-1].out.println(MonopolyNetwork.P_ASK_TO_SELL+";40;0;");
						System.out.println("host wait sell");
						inner_thread_index = i;
						new Thread(){
							public void run(){
								while(!ask_to_sell_replied){System.out.print("");}
								if(handler.pay(inner_thread_index, 40f, 0)){
									handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+inner_thread_index+";"+handler.pData[inner_thread_index].balance[0]+";0;");
									handler.updateBalance(pNum, handler.pData[pNum].balance[0]+40f, 0);
									handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
								}else{
									handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + inner_thread_index + ";");
									handler.updateStatus(inner_thread_index, "Lost");
								}
							}
						}.start();
						System.out.println("host wait sell done");
					}
				}
			}
		}else if(use_number == 4){
			handler.board.moveToken(pNum, (handler.pData[pNum].curTileNum-3));
			handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+handler.pData[pNum].curTileNum+";");
			//add necessary actions on land
		}else if(use_number == 5){
			//"ADVANCE TO THE NEAREST UTILITY. IF UNOWNED, you may buy it from the Bank. IF OWNED, throw dice and pay owner a total ten times the amount thrown."
			int landplace = 0;
			if(handler.pData[pNum].curTileNum == 7){
				handler.board.moveToken(pNum, 12);
				handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+12+";");
				landplace = 12;
			}else if(handler.pData[pNum].curTileNum == 22){
				handler.board.moveToken(pNum, 25);
				handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+25+";");
				landplace = 25;
			}else if(handler.pData[pNum].curTileNum == 36){
				handler.updateBalance(pNum, handler.pData[pNum].balance[0]+200f, 0);
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
				handler.board.moveToken(pNum, 5);
				handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+5+";");
				landplace = 5;
			}
			if(handler.properties[landplace].owner != -1){
				int ownerIndex = handler.properties[landplace].owner;
				float payable = (handler.properties[landplace].fine+(handler.properties[landplace].level*0.05f))*2;
				int currencyToPay = handler.properties[landplace].currency;
				handler.pay(pNum, payable, currencyToPay);
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[currencyToPay]+";"+currencyToPay+";");
				handler.updateBalance(ownerIndex, (handler.pData[ownerIndex].balance[currencyToPay]+payable), currencyToPay);
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+ownerIndex+";"+(handler.pData[ownerIndex].balance[currencyToPay])+";"+currencyToPay+";");
				System.out.println(pNum + ": " +handler.pData[pNum].balance[currencyToPay]);
				System.out.println(ownerIndex + ": " + handler.pData[ownerIndex].balance[currencyToPay]);
			}
		}else if(use_number == 6){
			handler.updateBalance(pNum, handler.pData[pNum].balance[0]+50f, 0);
			handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
		}else if(use_number == 7){
			//"ADVANCE TO THE NEAREST RAILROAD. If UNOWNED, you may buy it from the Bank. If OWNED, pay owner twice the rental to which they are otherwise entitled."
			int landplace = 0;
			if(handler.pData[pNum].curTileNum == 7){
				handler.board.moveToken(pNum, 15);
				handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+15+";");
				landplace = 15;
			}else if(handler.pData[pNum].curTileNum == 22){
				handler.board.moveToken(pNum, 25);
				handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+25+";");
				landplace = 25;
			}else if(handler.pData[pNum].curTileNum == 36){
				handler.updateBalance(pNum, handler.pData[pNum].balance[0]+200f, 0);
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
				handler.board.moveToken(pNum, 5);
				handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+5+";");
				landplace = 5;
			}
			if(handler.properties[landplace].owner != -1){
				int ownerIndex = handler.properties[landplace].owner;
				float payable = (handler.properties[landplace].fine+(handler.properties[landplace].level*0.05f))*2;
				int currencyToPay = handler.properties[landplace].currency;
				handler.pay(pNum, payable, currencyToPay);
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[currencyToPay]+";"+currencyToPay+";");
				handler.updateBalance(ownerIndex, (handler.pData[ownerIndex].balance[currencyToPay]+payable), currencyToPay);
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+ownerIndex+";"+(handler.pData[ownerIndex].balance[currencyToPay])+";"+currencyToPay+";");
			}
		}else if(use_number == 8){
			if(handler.pay(pNum, 10f, 0)){
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
			}else{
				if(handler.is_host && pNum == 0){
					handler.sellPropertyJOption(handler.your_index, "You need to pay " + 10f + " " + Forex.currencies[0] + ". Choose a property to sell", 10f, 0);
					if(handler.pay(pNum, 10f, 0)){
						handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
					}else{
						handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + pNum + ";");
						handler.updateStatus(pNum, "Lost");
					}
				}else{
					handler.mn.clients[pNum-1].out.println(MonopolyNetwork.P_ASK_TO_SELL+";10;0;");
					System.out.println("host wait sell");
					new Thread(){
						public void run(){
							while(!ask_to_sell_replied){System.out.print("");}
							if(handler.pay(pNum, 10f, 0)){
								handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
							}else{
								handler.mn.sendToAll(MonopolyNetwork.P_LOSE + ";" + pNum + ";");
								handler.updateStatus(pNum, "Lost");
							}
						}
					}.start();
					System.out.println("host wait sell done");
				}
			}
		}else if(use_number == 9){
			//"Take a trip to Reading Rail Road. If you pass \"GO\" collect $200.", // tile 5
			if(handler.pData[pNum].curTileNum > 5){
				handler.updateBalance(pNum, handler.pData[pNum].balance[0]+200f, 0);
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
			}
			handler.board.moveToken(pNum, 5);
			handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+5+";");
		}else if(use_number == 10){
			//"ADVANCE to Boardwalk.",// 39
			handler.board.moveToken(pNum, 39);
			handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+39+";");
		}else if(use_number == 11){
			//"ADVANCE to Illinois Avenue. If you pass \"GO\" collect $200.", //24
			if(handler.pData[pNum].curTileNum > 24){
				handler.updateBalance(pNum, handler.pData[pNum].balance[0]+200f, 0);
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
			}
			handler.board.moveToken(pNum, 24);
			handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+24+";");
		}else if(use_number == 12){
			handler.updateBalance(pNum, handler.pData[pNum].balance[0]+150f, 0);
			handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
		}else if(use_number == 14){
			//"ADVANCE to St. Charles Place. If you pass \"GO\" collect $200.", //11
			if(handler.pData[pNum].curTileNum > 11){
				handler.updateBalance(pNum, handler.pData[pNum].balance[0]+200f, 0);
				handler.mn.sendToAll(MonopolyNetwork.P_BALANCE+";"+pNum+";"+handler.pData[pNum].balance[0]+";0;");
			}
			handler.board.moveToken(pNum, 11);
			handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+11+";");
		}else if(use_number == 15){
			if(handler.pData[pNum].has_bail_chance){
				int n = JOptionPane.showConfirmDialog(
					null,
					"You are going to jail. Would you like to use your free pass?",
					"Jail",
					JOptionPane.YES_NO_OPTION);
					if(n == JOptionPane.YES_OPTION){
						handler.pData[pNum].has_bail_chance = false;
					}else{
						handler.board.moveToken(pNum, 10);
						handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+10+";");
					}
			}else{
				handler.board.moveToken(pNum, 10);
				handler.mn.sendToAll(MonopolyNetwork.P_MOVE+";"+pNum+";"+10+";");
			}
		}
	}
}