import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.text.DecimalFormat;

public class ForexStatusPanel extends JPanel{
	MonopolyGameHandler handler;
	JPanel tablePanel = new JPanel();
	JLabel[] currency_names = new JLabel[4];
	JLabel[] currency_to_dollar = new JLabel[4];
	JPanel[] currency_row_panel = new JPanel[4];
	float[][] history = new float[4][10];
	JPanel graphPanel = new JPanel();
	int history_header = 0;
	JLabel pos_max_change = new JLabel("");
	JLabel neg_max_change = new JLabel("");
	Forex forex;
	JPanel lineGraph = new JPanel(){
		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D)g;
			int interval = (int)(this.getWidth()/10.0f);
			Color[] currency_color ={Color.RED, Color.GREEN, Color.BLUE, Color.BLACK};
			float max_changed = 0f;
			for(int i = 0; i < 10; i++){
				int index = (history_header-i+10)%10;
				for(int j = 0; j < 4; j++){
					float change = (history[j][index]-Forex.currencies_ratios[j])/Forex.currencies_ratios[j];
					change = change<0?change*-1:change;
					//System.out.println(i + ": " + change);
					if(max_changed < change && change < .25){
						max_changed = change;
					}
				}
			}
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(1);
			pos_max_change.setText("+"+df.format(max_changed*100f)+"%");
			neg_max_change.setText("-"+df.format(max_changed*100f)+"%");
			//System.out.println("Max change = " + max_changed);
			for(int i = 0; i < 10; i++){
				g2.setColor(Color.GRAY);
				g2.drawLine(i*interval, 0, i*interval, this.getHeight());
				int index = (history_header-i+10)%10;
				for(int j = 0; j < 4; j++){
					int data_y = (int)(this.getHeight()/2.0)-(int)((((history[j][index==0?9:index-1]-Forex.currencies_ratios[j])/Forex.currencies_ratios[j])/max_changed)*(this.getHeight()/2.0));
					int data_y2 = (int)(this.getHeight()/2.0)-(int)((((history[j][index]-Forex.currencies_ratios[j])/Forex.currencies_ratios[j])/max_changed)*(this.getHeight()/2.0));
					g2.setColor(currency_color[j]);
					g2.drawLine(i==0?(i+1)*interval:(i)*interval, i==0?data_y:data_y2, (i+1)*interval, data_y);
					g2.fillArc(((i+1)*interval)-3, data_y-3, 6, 6, 0, 360);
					//Ellipse2D.Double circle = new Ellipse2D.Double((i+1)*interval, data_y, 5, 5);
					//g2.fill(circle);
				}
			}
		}
	};
	
	boolean is_host;
	public ForexStatusPanel(MonopolyGameHandler handler, boolean is_host){
		super();
		this.is_host = is_host;
		this.handler = handler;
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		this.setLayout(new BorderLayout());
		forex = new Forex(is_host, this);
		tablePanel.setLayout(new GridLayout(5,1));
		JPanel header = new JPanel();
		header.setLayout(new GridLayout(1,1));
		header.add(new JLabel("Currency per USD"));
		tablePanel.add(header);
		Color[] currency_color ={Color.RED, Color.GREEN, Color.BLUE, Color.BLACK};
		for(int i = 0; i < 4; i++){
			currency_row_panel[i] = new JPanel();
			currency_row_panel[i].setLayout(new GridLayout(1,2));
			currency_names[i] = new JLabel(forex.currency_names[i]);
			currency_names[i].setForeground(currency_color[i]);
			currency_to_dollar[i] = new JLabel("" + forex.currency_to_dollar_ratio[i]);
			currency_to_dollar[i].setForeground(currency_color[i]);
			
			currency_names[i].setBorder(new LineBorder(Color.GRAY));
			currency_to_dollar[i].setBorder(new LineBorder(Color.GRAY));
			currency_row_panel[i].add(currency_names[i]);
			currency_row_panel[i].add(currency_to_dollar[i]);
			tablePanel.add(currency_row_panel[i]);
		}
		graphPanel.setLayout(new BorderLayout());
		graphPanel.add(lineGraph, BorderLayout.CENTER);
		JPanel buff = new JPanel(new BorderLayout());
		buff.add(new JLabel("Most recent"), BorderLayout.WEST);
		buff.add(new JLabel("Least recent"), BorderLayout.EAST);
		JPanel buff2 = new JPanel(new BorderLayout());
		pos_max_change.setVerticalTextPosition(JLabel.TOP);
		neg_max_change.setVerticalTextPosition(JLabel.BOTTOM);
		buff2.add(pos_max_change, BorderLayout.NORTH);
		buff2.add(neg_max_change, BorderLayout.SOUTH);
		graphPanel.add(buff, BorderLayout.SOUTH);
		graphPanel.add(buff2, BorderLayout.WEST);
		this.add(tablePanel, BorderLayout.WEST);
		this.add(graphPanel, BorderLayout.CENTER);
		//forex.runForex();
	}
	
	public void update(){
		for(int i = 0; i < 4; i++){
			history[i][history_header] = forex.currency_to_dollar_ratio[i];
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			currency_names[i].setText(forex.currency_names[i]);
			currency_to_dollar[i].setText("" + df.format(forex.currency_to_dollar_ratio[i]));
			tablePanel.remove(currency_row_panel[i]);
			repaint();
			revalidate();
		}
		int[] rank = forex.getOptimalRatios();
		for(int i = 0; i < 4; i++){
			//System.out.println(rank[i] + " " + forex.currency_to_dollar_ratio[rank[i]]);
			
			tablePanel.add(currency_row_panel[rank[i]]);
			repaint();
			revalidate();
		}
		/*System.out.println(history_header+"---------------");
		for(int i = 0; i < 10; i++){
			int index = (history_header-i+10)%10;
			System.out.print("index: " + index);
			for(int j = 0; j< 4; j++){
				System.out.print("|" + history[j][index] + "|");
			}
			System.out.println("");
		}
		System.out.println("---------------");*/
		lineGraph.repaint();
		history_header++;
		history_header = history_header%10;
	}
}
