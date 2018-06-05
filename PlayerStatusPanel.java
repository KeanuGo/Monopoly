import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.text.DecimalFormat;

public class PlayerStatusPanel extends JPanel{
	MonopolyGameHandler handler;
	JPanel[] playerPanel = new JPanel[8];
	JLabel[] status = new JLabel[8];
	JLabel[][] balance = new JLabel[8][4];
	JPanel[] playerProperties = new JPanel[8];
	JList[] playerPropertiesListSelector = new JList[8];
	DefaultListModel[] playerPropertiesList = new DefaultListModel[8];
	JButton sell_button = new JButton("Sell");
	JButton upgrade_button = new JButton("Upgrade");
	//Color[] sss = {Color.MAGENTA, Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED, Color.WHITE, Color.ORANGE, Color.CYAN};
	
	public PlayerStatusPanel(PlayerData[] data, int numOfPlayers, MonopolyGameHandler handler){
		super();
		this.setBackground(Color.GREEN);
		this.handler = handler;
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		this.setLayout(new GridLayout(4, 2));
		for(int i = 0; i < numOfPlayers; i++){
			playerPanel[i] = new JPanel();
			playerPanel[i].setBackground(null);
			playerPanel[i].setLayout(new BorderLayout());
			status[i] = new JLabel("Player Status: " + data[i].status);
			status[i].setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 12));
			playerPanel[i].add(status[i], BorderLayout.NORTH);
			JPanel buf = new JPanel();
			buf.setLayout(new GridLayout(4,1));
			buf.setBackground(null);
			balance[i] = new JLabel[4];
			
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.setBackground(null);
			
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			for(int j = 0; j < 4; j++){
				JPanel buf2 = new JPanel();
				buf2.setBackground(null);
				buf2.setLayout(new GridLayout(1,2));
				balance[i][j] = new JLabel("" + df.format(data[i].balance[j]));
				balance[i][j].setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 12));
				JLabel buf1 = new JLabel(Forex.currencies[j] + " : ");
				buf1.setHorizontalAlignment(JLabel.RIGHT);
				buf1.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 12));
				buf2.add(buf1);
				buf2.add(balance[i][j]);
				buf.add(buf2);
				tabbedPane.addTab("Balance", null, buf, "Balance");
			}
			
			
			playerProperties[i] = new JPanel();
			playerProperties[i].setLayout(new BorderLayout());
			playerPropertiesList[i] = new DefaultListModel();
			playerPropertiesListSelector[i] = new JList(playerPropertiesList[i]);
			playerPropertiesListSelector[i].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			playerPropertiesListSelector[i].setLayoutOrientation(JList.VERTICAL);
			playerPropertiesListSelector[i].setVisibleRowCount(1);
			JScrollPane jsp = new JScrollPane(playerPropertiesListSelector[i]);
			playerProperties[i].add(jsp, BorderLayout.CENTER);
			playerProperties[i].setBackground(null);
			tabbedPane.addTab("Properties", null, playerProperties[i], "Properties");
			
			playerPanel[i].add(tabbedPane, BorderLayout.CENTER);
			//balance[i] = new JLabel("Balance: ");
			//playerPanel[i].add(balance[i], BorderLayout.SOUTH);
			this.add(playerPanel[i]);
			//playerPanel[i].setBackground(sss[i]);
			
			TitledBorder border = new TitledBorder(data[i].name);
			if(handler.your_index == i){
				border.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
			}else{
				border.setBorder(new LineBorder(Color.GRAY));
			}
			playerPanel[i].setBorder(border);
			//System.out.println(playerPanel[i].getPreferredSize());
		}
	}
	
	public void reBorder(){
		this.removeAll();
		for(int i = 0; i < /*8*/handler.mn.clientsNum+1; i++){
			TitledBorder border = new TitledBorder("P" + (i+1) + ":" + handler.pData[i].name);
			border.setTitleFont(new Font("@Kozuka Gothic Pr6N H", Font.PLAIN, 15));
			if(handler.your_index == i){
				border.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
			}else{
				border.setBorder(new LineBorder(Color.GRAY));
			}
			playerPanel[i].setBorder(border);
			this.add(playerPanel[i]);
		}
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1,2));
		p.add(upgrade_button);
		p.add(sell_button);
		playerProperties[handler.your_index].add(p, BorderLayout.SOUTH);
	}
	
	public void updatePropertiesPanel(int pNum){
		checkGroups(pNum);
		playerPropertiesList[pNum].removeAllElements();
		for(int i = 0; i < handler.pData[pNum].properties.size(); i++){
			String s = (i+1)+":" + handler.pData[pNum].properties.get(i).toString();
			playerPropertiesList[pNum].addElement(s);
		}
		playerPanel[pNum].repaint();
		playerPanel[pNum].revalidate();
	}
	
	public void checkGroups(int pNum){
		if(handler.properties[1].owner == pNum && handler.properties[3].owner == pNum){
			//JOptionPane.showMessageDialog(null, "Player" + (pNum+1) + "owned group 1");
			handler.properties[1].monopolized = true;
			handler.properties[3].monopolized = true;
		}else{
			handler.properties[1].monopolized = false;
			handler.properties[3].monopolized = false;
		}
		
		if(handler.properties[6].owner == pNum && handler.properties[8].owner == pNum && handler.properties[9].owner == pNum){
			//JOptionPane.showMessageDialog(null, "Player" + (pNum+1) + "owned group 2");
			handler.properties[6].monopolized = true;
			handler.properties[8].monopolized = true;
			handler.properties[9].monopolized = true;
		}else{
			handler.properties[6].monopolized = false;
			handler.properties[8].monopolized = false;
			handler.properties[9].monopolized = false;
		}
		
		if(handler.properties[11].owner == pNum && handler.properties[13].owner == pNum && handler.properties[14].owner == pNum){
			//JOptionPane.showMessageDialog(null, "Player" + (pNum+1) + "owned group 3");
			handler.properties[11].monopolized = true;
			handler.properties[13].monopolized = true;
			handler.properties[14].monopolized = true;
		}else{
			handler.properties[11].monopolized = false;
			handler.properties[13].monopolized = false;
			handler.properties[14].monopolized = false;
		}
		
		if(handler.properties[16].owner == pNum && handler.properties[18].owner == pNum && handler.properties[19].owner == pNum){
			//JOptionPane.showMessageDialog(null, "Player" + (pNum+1) + "owned group 4");
			handler.properties[16].monopolized = true;
			handler.properties[18].monopolized = true;
			handler.properties[19].monopolized = true;
		}else{
			handler.properties[16].monopolized = false;
			handler.properties[18].monopolized = false;
			handler.properties[19].monopolized = false;
		}
		
		if(handler.properties[21].owner == pNum && handler.properties[23].owner == pNum && handler.properties[24].owner == pNum){
			//JOptionPane.showMessageDialog(null, "Player" + (pNum+1) + "owned group 5");
			handler.properties[21].monopolized = true;
			handler.properties[23].monopolized = true;
			handler.properties[24].monopolized = true;
		}else{
			handler.properties[21].monopolized = false;
			handler.properties[23].monopolized = false;
			handler.properties[24].monopolized = false;
		}
		
		if(handler.properties[26].owner == pNum && handler.properties[27].owner == pNum && handler.properties[29].owner == pNum){
			//JOptionPane.showMessageDialog(null, "Player" + (pNum+1) + "owned group 6");
			handler.properties[26].monopolized = true;
			handler.properties[27].monopolized = true;
			handler.properties[29].monopolized = true;
		}else{
			handler.properties[26].monopolized = false;
			handler.properties[27].monopolized = false;
			handler.properties[29].monopolized = false;
		}
		
		if(handler.properties[31].owner == pNum && handler.properties[32].owner == pNum && handler.properties[34].owner == pNum){
			//JOptionPane.showMessageDialog(null, "Player" + (pNum+1) + "owned group 7");
			handler.properties[31].monopolized = true;
			handler.properties[32].monopolized = true;
			handler.properties[34].monopolized = true;
		}else{
			handler.properties[31].monopolized = false;
			handler.properties[32].monopolized = false;
			handler.properties[34].monopolized = false;
		}
		
		if(handler.properties[37].owner == pNum && handler.properties[39].owner == pNum){
			//JOptionPane.showMessageDialog(null, "Player" + (pNum+1) + "owned group 8");
			handler.properties[37].monopolized = true;
			handler.properties[39].monopolized = true;
		}else{
			handler.properties[37].monopolized = false;
			handler.properties[39].monopolized = false;
		}
	}
}
