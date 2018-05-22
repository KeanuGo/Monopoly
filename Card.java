public class Card{
	static String comm_chest_text[]= {
							"Get out of Jail, Free. This card may be kept until needed.",
							"You have won second prize in a beauty contest. Collect $10.",
							"From sale of stock, you get $50.",
							"Life insurance matures. Collect $100.",
							"Income tax refund. Collect $20.",
							"Holiday fund matures. Receive $100.",
							"You inherit $100.",
							"Receive $25 consultancy fee.",
							"Pay hospital fees of $100.",
							"Bank error in your favor. Collect $200.",
							"Pay school fees of $50.",
							"Doctor's fee. Pay $50.",
							"It is your birthday. Collect $10 from every player.",
							"Advance to \"GO\" (Collect $200).",
							"You are assessed for street repairs. $30 per property",
							"Go to Jail. Go directly to Jail. Do not pass \"GO\". Do not collect $200."
							};
	static String chance_text[]= {
							"GET OUT OF JAIL FREE. This card may be kept until needed or traded.",
							"Make General Repairs on All Your Property. For each property pay $20.",
							"Speeding fine $15.",
							"You have been elected chairman of the board. Pay each player $40.",
							"Go back three spaces.",
							"ADVANCE TO THE NEAREST UTILITY. IF OWNED, pay owner twice the rental to which they are otherwise entitled.",
							"Bank pays you dividend of $50.",
							"ADVANCE TO THE NEAREST RAILROAD. If OWNED, pay owner twice the rental to which they are otherwise entitled.",
							"Pay poor tax of $15.",
							"Take a trip to Reading Rail Road. If you pass \"GO\" collect $200.", // tile 5
							"ADVANCE to Boardwalk.",// 39
							"ADVANCE to Illinois Avenue. If you pass \"GO\" collect $200.", //24
							"Your building loan matures. Collect $150.",
							"ADVANCE TO THE NEAREST RAILROAD. If UNOWNED, you may buy it from the Bank. If OWNED, pay owner twice the rental to which they are otherwise entitled.",
							"ADVANCE to St. Charles Place. If you pass \"GO\" collect $200.", //11
							"Go to Jail. Go Directly to Jail. Do not pass \"GO\". Do not collect $200."
							};
	String text;
	int use_number;
	public Card(String text, int use_number){
		this.text = text;
		this.use_number = use_number;
	}
}