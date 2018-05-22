import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;


public class CurrencyExchangePanel extends JPanel{
	ImageIcon[] dropDownButtonIcons = {new ImageIcon("ui/DropDownButton.png"),new ImageIcon("ui/DropDownButton_hover.png"),new ImageIcon("ui/DropDownButton.png")};
	ImageIcon[] exchangeCurrencyIcons = {new ImageIcon("ui/ExchangeCurrencyButton.png"),new ImageIcon("ui/ExchangeCurrencyButton_hover.png"),new ImageIcon("ui/ExchangeCurrencyButton.png")};
	JTextField amount_from = new JTextField(10);
	JComboBox<String> currency_from = new JComboBox<>(Forex.currencies);
	JComboBox<String> currency_to = new JComboBox<>(Forex.currencies);
	
	JButton exchange_currency_button = new JButton(exchangeCurrencyIcons[0]);
	
	public CurrencyExchangePanel(){
		super();
		this.setLayout(new GridBagLayout());
		this.setBackground(null);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 8;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		JLabel buf = new JLabel("Exchange Currency: ");
		buf.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 12));
		this.add(buf, constraints);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 8;
		constraints.anchor = GridBagConstraints.LAST_LINE_END;
		this.add(exchange_currency_button, constraints);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		buf = new JLabel("from: ");
		buf.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 12));
		this.add(buf, constraints);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		add(currency_from, constraints);
		constraints.gridx = 3;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		amount_from.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 12));
		add(amount_from, constraints);
		constraints.gridx = 5;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		buf = new JLabel("to: ");
		buf.setFont(new Font("@Kozuka Gothic Pr6N H", Font.BOLD, 12));
		add(buf, constraints);
		constraints.gridx = 6;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		add(currency_to, constraints);
		
		exchange_currency_button.setContentAreaFilled(false);
		exchange_currency_button.setOpaque(false);
		exchange_currency_button.setBorderPainted(false);
		exchange_currency_button.addMouseListener(
						new MouseAdapter(){
							public void mouseEntered(MouseEvent e){
								exchange_currency_button.setIcon(exchangeCurrencyIcons[1]);
								Monopoly.frame.revalidate();
							}
							public void mouseExited(MouseEvent e){
								exchange_currency_button.setIcon(exchangeCurrencyIcons[0]);
								Monopoly.frame.revalidate();
							}
							
							public void mousePressed(MouseEvent e){
								exchange_currency_button.setIcon(exchangeCurrencyIcons[2]);
								Monopoly.frame.revalidate();
							}
							
							public void mouseReleased(MouseEvent e){
								exchange_currency_button.setIcon(exchangeCurrencyIcons[0]);
								Monopoly.frame.revalidate();
							}
						}
					);
		
		currency_from.setUI(
			new BasicComboBoxUI(){
				protected JButton createArrowButton(){
					JButton b = new JButton(new ImageIcon("ui/DropDownButton.png"));
					b.setContentAreaFilled(false);
					b.setOpaque(false);
					b.setBorderPainted(false);
					b.addMouseListener(
						new MouseAdapter(){
							public void mouseEntered(MouseEvent e){
								b.setIcon(dropDownButtonIcons[1]);
								Monopoly.frame.revalidate();
							}
							public void mouseExited(MouseEvent e){
								b.setIcon(dropDownButtonIcons[0]);
								Monopoly.frame.revalidate();
							}
							
							public void mousePressed(MouseEvent e){
								b.setIcon(dropDownButtonIcons[2]);
								Monopoly.frame.revalidate();
							}
							
							public void mouseReleased(MouseEvent e){
								b.setIcon(dropDownButtonIcons[0]);
								Monopoly.frame.revalidate();
							}
						}
					);
					return b;
				}
			}
		);
		currency_to.setUI(
			new BasicComboBoxUI(){
				protected JButton createArrowButton(){
					JButton b = new JButton(dropDownButtonIcons[0]);
					b.setContentAreaFilled(false);
					b.setOpaque(false);
					b.setBorderPainted(false);
					b.addMouseListener(
						new MouseAdapter(){
							public void mouseEntered(MouseEvent e){
								b.setIcon(dropDownButtonIcons[1]);
								Monopoly.frame.revalidate();
							}
							public void mouseExited(MouseEvent e){
								b.setIcon(dropDownButtonIcons[0]);
								Monopoly.frame.revalidate();
							}
							
							public void mousePressed(MouseEvent e){
								b.setIcon(dropDownButtonIcons[2]);
								Monopoly.frame.revalidate();
							}
							
							public void mouseReleased(MouseEvent e){
								b.setIcon(dropDownButtonIcons[0]);
								Monopoly.frame.revalidate();
							}
						}
					);
					return b;
				}
			}
		);
	}
}