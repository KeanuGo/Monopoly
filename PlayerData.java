import java.awt.image.*;
import javax.swing.JLabel;
import java.util.ArrayList;

public class PlayerData{
	String name = "";
	String status = "";
	float balance[] = {332f, 0f, 0f, 0f};
	int curTileNum = 0;
	BufferedImage icon;
	JLabel token = null;
	ArrayList<Property> properties = new ArrayList<>();
	boolean has_bail_chance = false;
	int turns_with_debt = 0;
	
	public PlayerData(String name, BufferedImage icon, JLabel token){
		this.name = name;
		this. icon = icon;
		this.token = token;
		token.setToolTipText(this.name);
	}
	
	public boolean isBankrupt(){
		for(int i = 0; i < 4; i++){
			if(balance[i]>0){
				return false;
			}
		}
		return true;
	}
	
	public String toString(){
		return name+"~"+status+"~"+balance+"~"+curTileNum+"~";
	}
	
	public boolean checkData(String data){
		String[] vars = data.split("~");
		boolean result = true;
		if(!(vars[0].equals(this.name))){
			result = false;
		}
		if(!(vars[1].equals(this.status))){
			result = false;
		}
		/*if(Integer.parseInt(vars[2]) != this.balance){
			result = false;
		}
		if(Integer.parseInt(vars[3]) != this.balance){
			result = false;
		}*/
		return result;
	}
}