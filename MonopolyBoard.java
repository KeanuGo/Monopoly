import java.awt.*;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.border.Border;

public class MonopolyBoard extends JPanel{
	final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
	private MonopolyGameHandler game_handler;
	private JPanel board = new JPanel();
	JLayeredPane lp = new JLayeredPane();
	JPanel glassPanel = new JPanel();
	JLabel squares[]= new JLabel[40];
	JLabel[] tokens = new JLabel[8];
	static String[] toolTip = {"GO, Collect $200 Salary as you pass",
							"Mediterranean Avenue, $80",
							"Community Chest",
							"Baltic Avenue, $110",
							"Income Tax",
							"Reading Railroad, $400", 
							"Oriental Avenue, $170",
							"Chance",
							"Vermont Avenue, $260",
							"Connecticut Avenue, $350",
							"Jail",
							"St. Francis Square, ₱4000",
							"Electric Company, ₱15000",
							"Commonwealth Avenue, ₱5000",
							"Recto Avenue, ₱5500",
							"MRT Station, ₱20000", 
							"Escolta Street, ₱8500",
							"Community Chest",
							"Taft Avenue, ₱13000",
							"Rizal Avenue, ₱17500",
							"Free Parking",
							"Strand, £64",
							"Chance",
							"Fleet Street, £88",
							"Trafalgar Square, £90",
							"Fenchurch St. Station, £320",
							"Leicester Square, £136",
							"Coventry Street, £208",
							"Water Works, £240",
							"Piccadilly, £280",
							"Go to jail",
							"Chuo-dori Street, ¥8400",
							"Omote-Sando Avenue, ¥11550",
							"Community Chest",
							"Takeshita Street, ¥17850",
							"Shibuya Station, ¥42000",
							"Chance",
							"Asakusa, ¥27300",
							"Luxury Tax",
							"Ginza, ¥37500"};
	
	boolean[] token_busy = {false, false, false, false, false, false, false, false};
	public MonopolyBoard(MonopolyGameHandler handler){
		this();
		this.game_handler = handler;
	}
	
	public MonopolyBoard(){
		super();
		if (RIGHT_TO_LEFT) {
            board.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		board.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		if (shouldFill) {
		//natural height, maximum width
		c.fill = GridBagConstraints.HORIZONTAL;
		}
		if (shouldWeightX) {
		c.weightx = 0.5;
		}
		String imgLabel[]= {"images/go_200_sal.jpg",
							"images/mediterranean.jpg",
							"images/comm_chest2.jpg",
							"images/baltic.jpg",
							"images/income_tax.jpg",
							"images/reading_rr.jpg", 
							"images/oriental.jpg",
							"images/chance2.jpg",
							"images/vermont.jpg",
							"images/connecticut.jpg",
							"images/in_jail.jpg",
							"images/stfrancis_sq.jpg",
							"images/electric_cny.jpg",
							"images/commonwealth_ave.jpg",
							"images/recto_ave.jpg",
							"images/mrt_stn.jpg", 
							"images/escolta_st.jpg",
							"images/comm_chest.jpg",
							"images/taft_ave.jpg",
							"images/rizal_ave.jpg",
							"images/free_parking.jpg",
							"images/strand.jpg",
							"images/chance.jpg",
							"images/fleet_street.jpg",
							"images/trafalgar_square.jpg",
							"images/fenchurch_st.jpg",
							"images/leicester_square.jpg",
							"images/coventry_st.jpg",
							"images/water_works.jpg",
							"images/piccadilly.jpg",
							"images/go_to_jail.jpg",
							"images/chuodori_st.jpg",
							"images/omotesando_ave.jpg",
							"images/comm_chest1.jpg",
							"images/takeshita_st.jpg",
							"images/shibuya_stn.jpg",
							"images/chance1.jpg",
							"images/asakusa.jpg",
							"images/luxury_tax.jpg",
							"images/ginza.jpg"
							};
		int w= 89, h = 89;
		for(int i=0; i<10; i++){
			squares[i]= new JLabel(new ImageIcon(new ImageIcon(imgLabel[i]).getImage().getScaledInstance(w, h, Image.SCALE_DEFAULT)));
			squares[i].setToolTipText(toolTip[i]);
			w=56;
		}
		w= 89; h=89;
		for(int i=10; i<20; i++){
			squares[i]= new JLabel(new ImageIcon(new ImageIcon(imgLabel[i]).getImage().getScaledInstance(w, h, Image.SCALE_DEFAULT)));
			squares[i].setToolTipText(toolTip[i]);
			h=56;
		}
		w= 89; h=89;
		for(int i=20; i<30; i++){
			squares[i]= new JLabel(new ImageIcon(new ImageIcon(imgLabel[i]).getImage().getScaledInstance(w, h, Image.SCALE_DEFAULT)));
			squares[i].setToolTipText(toolTip[i]);
			w=56;
		}
		w= 89; h=89;
		for(int i=30; i<40; i++){
			squares[i]= new JLabel(new ImageIcon(new ImageIcon(imgLabel[i]).getImage().getScaledInstance(w, h, Image.SCALE_DEFAULT)));
			squares[i].setToolTipText(toolTip[i]);
			h=56;
		}
		Border border = BorderFactory.createLineBorder(Color.GRAY, 1);
		for(int i=0; i<40; i++){
			squares[i].setBorder(border);
		}

		for(int i=0; i<11; i++){
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = i;
			c.gridy = 0;
			board.add(squares[i+20], c);
		}
		
		for(int i=0; i<9;i++){		
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weighty = 0.0;
			c.gridx = 0;
			c.gridy = i+1;
			board.add(squares[19-i], c);
		}
		
		for(int i=0; i<9;i++){
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weighty = 0.0;
			c.gridx = 10;
			c.gridy = i+1;
			board.add(squares[31+i], c);	
		}
		for(int i=0; i<11; i++){		
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = i;
			c.gridy = 10;
			board.add(squares[10-i], c);
		}
		
		JLabel logo= new JLabel(new ImageIcon(new ImageIcon("images/monopoly_logo.png").getImage().getScaledInstance(250, 100, Image.SCALE_DEFAULT)));
		c.fill = GridBagConstraints.BOTH;//make this component tall
		c.weightx = 0.0;
		c.gridwidth = 9;
		c.gridx = 1;
		c.gridheight = 9;
		c.gridy = 1;
		board.add(logo, c);
		
		String[] tokensrc = {"images/monopoly_token_barrow.png",
							 "images/monopoly_token_boot.png",
							 "images/monopoly_token_car.png",
							 "images/monopoly_token_dog.png",
							 "images/monopoly_token_hat.png",
							 "images/monopoly_token_iron.png",
							 "images/monopoly_token_ship.png",
							 "images/monopoly_token_thimble.png"};
		this.setLayout(new BorderLayout());
		this.add(lp, BorderLayout.CENTER);
		
		glassPanel.setLayout(null);
		board.setSize(board.getPreferredSize());
		lp.add(glassPanel, new Integer(2));
		lp.add(board, new Integer(1));
		for(int i = 0; i < 8 ; i++){
			tokens[i] = new JLabel(new ImageIcon(new ImageIcon(tokensrc[i]).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
			tokens[i].setBackground(Color.WHITE);
			glassPanel.add(tokens[i]);
			tokens[i].setSize(tokens[i].getPreferredSize());
		}
		glassPanel.setSize(board.getPreferredSize());
		glassPanel.setLocation(0,0);
		glassPanel.setOpaque(false);
		this.setSize(board.getPreferredSize());
	}
	
	public void refresh(){
		for(int i = game_handler.mn.clientsNum+1; i < 8; i++){
			glassPanel.remove(tokens[i]);
		}
	}
	
	public void moveToken(int tokenIndex, int dest){
		Point destination = squares[dest].getLocation();
		//System.out.print("Move:" + tokenIndex + "{from: (" + tokens[tokenIndex].getX() + ", " + tokens[tokenIndex].getY() + ")");
		//System.out.println(", to: (" + destination.x + ", " + destination.y + ")}");
		new Thread(){
			public void run(){
				while(token_busy[tokenIndex]){System.out.print("");}
				token_busy[tokenIndex] = true;
				//System.out.println(tokenIndex + " busy");
				while(game_handler.pData[tokenIndex].curTileNum != dest){
					int linear_dest = (java.lang.Math.abs(game_handler.pData[tokenIndex].curTileNum-dest)<10?dest:((((int)((float)game_handler.pData[tokenIndex].curTileNum/10.0))+1)*10)%40);
					int x_dest = (int)squares[linear_dest].getLocation().getX() + ((int)(squares[linear_dest].getSize().getWidth()/2.0))-25;
					int y_dest = (int)squares[linear_dest].getLocation().getY() + ((int)(squares[linear_dest].getSize().getHeight()/2.0))-25;
					Point destination = new Point(x_dest, y_dest);
					//Point destination = squares[linear_dest].getLocation();
					//System.out.println("Moving from " + game_handler.pData[tokenIndex].curTileNum + " to " + linear_dest);
					//System.out.println("Coords from: ("+ squares[game_handler.pData[tokenIndex].curTileNum].getLocation() + ") to (" + squares[linear_dest].getLocation() + ")");
					for(int x = tokens[tokenIndex].getX(), y = tokens[tokenIndex].getY(); (x != destination.x || y != destination.y); ){
						if( x > destination.x){
							x--;
						}else if( y > destination.y){
							y--;
						}else if(x < destination.x){
							x++;
						}else if(y < destination.y){
							y++;
						}
						try { Thread.sleep(2); } catch(InterruptedException ex){}
						game_handler.pData[tokenIndex].token.setLocation(x, y);
						repaint();
					}
					game_handler.pData[tokenIndex].curTileNum = linear_dest;
				}
				token_busy[tokenIndex] = false;
				//System.out.println(tokenIndex + " not busy");
			}
		}.start();
	}
}