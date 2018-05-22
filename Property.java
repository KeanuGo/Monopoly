public class Property{
	int location;
	float price;
	int currency;
	int owner = -1;
	int level = 0;
	boolean buyable = false;
	boolean upgradeable = false;
	boolean monopolized = false;
	float fine = 0f;
	
	public Property(int loc, float p, int c, boolean buyable){
		this.location = loc;
		this.price = p;
		this.currency = c;
		this.buyable = buyable;
		if(loc%10 == 5 || loc == 12 || loc == 28){
			upgradeable = false;
		}else{
			upgradeable = true;
		}
		fine = this.price*0.05f;
	}
	
	public String toString(){
		return MonopolyBoard.toolTip[location] + ":" + (level+1);
	}
}