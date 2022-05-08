package IndianPoker;

public class Game {
	public static void main(String[] args) {
		
		int num = 4;
		
		Dealer dealer = new Dealer(num, 5);
		
		new Human("abc",500);
		new Computer();
		new Computer();
		new Computer();
	
		int round_cnt=1;
		do {
			dealer.player_shift();
			dealer.deal();
			System.out.printf("\n\nRound %d\n", round_cnt);
			
			boolean round_result_check = true;
			while( round_result_check ) round_result_check = dealer.betting();
			
			round_cnt++;
		}while(dealer.round_result());
		
		Player.sc.close();
		
		return ;
		
	}
}
