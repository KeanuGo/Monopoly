import java.util.Random;
import javax.swing.Timer;
import java.awt.event.*;

public class Forex implements ActionListener{
	ForexStatusPanel forexStatusPanel;
	Timer timer = new Timer(1000, this);
	Random rand = new Random();
	static String[] currencies = {"USD", "Peso", "Euro", "Yen"};
	static float[] currencies_ratios = {1.0f, 50.0f, 0.8f, 105.0f};
	String[] currency_names = {"USD", "Peso", "Euro", "Yen"};
	float[] currency_to_dollar_ratio = {1.0f, 50.0f, 0.8f, 105.0f};
	
	
	float maximum_change_from_init_value = 0.25f;
	float maximum_change_per_iteration = 0.02f;
	
	boolean if_running = false;
	boolean if_server = false;
	public Forex(boolean if_server){
		this.if_running = false;
		this.if_server = if_server;
	}
	
	public Forex(boolean if_server, ForexStatusPanel fsp){
		this(if_server);
		this.forexStatusPanel = fsp;
	}
	
	public void runForex(){
		if(if_server){
			if_running = true;
			timer.start();
			if(forexStatusPanel == null){
				while(true){
					
				}
			}
		}
	}
	
	public void stopForex(){
		if_running = false;
		timer.stop();
	}
	
	public void setCurrencies(String[] names, float[] to_dollar_ratio){
		if(if_running || names.length != to_dollar_ratio.length){
			return;
		}
		currencies = null;
		currencies_ratios = null;
		currencies = new String[names.length+1];
		currencies_ratios = new float[names.length+1];
		currencies[0] = "Dollar";
		currencies_ratios[0] = 1.0f;
		for(int i = 0; i < names.length; i++){
			currencies[i+1] = names[i];
			currencies_ratios[i+1] = to_dollar_ratio[i];
		}
		
		currency_names = null;
		currency_to_dollar_ratio = null;
		currency_names = new String[currencies.length];
		currency_to_dollar_ratio = new float[currencies_ratios.length];
		for(int i = 0 ; i < currency_names.length; i++){
			currency_names[i] = currencies[i];
			currency_to_dollar_ratio[i] = currencies_ratios[i];
		}
	}
	
	public float getRelativeChange(int currency){
		if(currency >= 4){
			return 0f;
		}
		return ((currency_to_dollar_ratio[currency]-currencies_ratios[currency])/currencies_ratios[currency]);
	}
	
	public int[] getOptimalRatios(){
		int[] ranking = {0,1,2,3};
		float[] values = new float[4];
		for(int i = 0; i < 4; i++){
			values[i] = getRelativeChange(i);
		}
		
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < i; j++){
				if(values[j]>values[j+1]){
					int buff = ranking[j];
					ranking[j] = ranking[j+1];
					ranking[j+1] = buff;
					float buff2 = values[j];
					values[j] = values[j+1];
					values[j+1] = buff2;
				}
			}
		}
		return ranking;
	}
	
	public void actionPerformed(ActionEvent e){
		//System.out.println("Currencies: ");
		for(int i = 0; i < currency_to_dollar_ratio.length; i++){
			float to_add = rand.nextFloat();
			if(!rand.nextBoolean()){
				to_add = to_add>0?-1*to_add:to_add;
			}
			if(currency_to_dollar_ratio[i] - currencies_ratios[i] > currencies_ratios[i]*maximum_change_from_init_value){
				to_add = to_add>0?-1*to_add:to_add;
			}
			if(currencies_ratios[i] - currency_to_dollar_ratio[i] > currencies_ratios[i]*maximum_change_from_init_value){
				to_add = to_add>0?to_add:-1*to_add;
			}
			if(i == 0){
				to_add = 0.0f;
			}
			currency_to_dollar_ratio[i] += to_add*(maximum_change_per_iteration*currency_to_dollar_ratio[i]);
			//System.out.println(currency_names[i] + ": " + currency_to_dollar_ratio[i] + ", " + (currency_to_dollar_ratio[i]/currencies_ratios[i]*100) + "%" + ", " +(to_add>0?"UP":"DOWN"));
		}
		if(!(forexStatusPanel == null)){
			forexStatusPanel.update();
		}
		if(if_server){
			String message = 9+";";
			for(int i = 0; i < 4; i++){
				message += currency_to_dollar_ratio[i] + ";";
			}
			forexStatusPanel.handler.mn.sendToAll(message);
		}
	}
	
	public static int getIndex(String currency){
		for(int i = 0; i < 4; i++){
			if(currencies[i].equals(currency)){
				return i;
			}
		}
		return -1;
	}
	
	public static void main(String[] args){
		new Forex(true).runForex();
	}
}