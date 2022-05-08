package IndianPoker;

import java.util.Arrays;
import java.util.Random;

public class Dealer {

	private int player_num;
	private int playing_num;
	private int betting_stack;
	private int betting_chip;
	private int ante;
	private int card_count;
	private boolean[] cards;
	private int[] playing_card;
	private boolean bet_check;
	private Random rand = new Random();
	
	Dealer(int num, int playing_ante) {
		
		player_num = num;
		playing_num = num;
		ante = playing_ante;
		card_count=40;
		
		betting_stack = 0;
		betting_chip = 0;
		
		cards = new boolean[card_count];
		playing_card = new int[player_num];
		Arrays.fill(cards, true);
	}
	
	void Plus_betting_stack(int betting) {
		betting_stack += betting;
	}
	void Set_betting_chip(int betting) {
		betting_chip = betting;
	}
	void Set_bet_check(boolean check) {
		bet_check = check;
	}
	int Get_playing_num() {
		return playing_num;
	}
	int Get_betting_stack() {
		return betting_stack;
	}
	int Get_betting_chip() {
		return betting_chip;
	}
	int Get_ante() {
		return ante;
	}
	int[] Get_showing_card(Player p) {
		int[] showing_card = new int[player_num];
		for(int i=0;i<player_num;i++) {
			if(Player.players.get(i).Get_name().equals(p.Get_name()))
				showing_card[i] = 0;
			else
				showing_card[i] = playing_card[i];
		}
		return showing_card;
	}
	void Show_cards(Player p) {
		
		int[] showing_card = Get_showing_card(p);
		
		for(int i=0;i<player_num;i++)
			System.out.printf("%d ",showing_card[i]);
		System.out.println("");
	}
	boolean Get_bet_check() {
		return bet_check;
	}
	
	void deal() {

		bet_check = false;
		betting_chip = ante;
		betting_stack = 0;
		int card;
		
		for(int i=0;i<player_num;i++) {
			Player.players.get(i).clear(this);
			
			while( (card=rand.nextInt(40)) != -1 ) {
				if(cards[card]) {
					playing_card[i]=card%10+1;
					cards[card] = false;
					card_count--;
					break;
				}
			}
		}
	}
	
	int tie_deal(int[] winner, int winner_count) {
		
		int card;
		int[] tie_winner = new int[4];
		int tie_winner_count = 0;
		int winner_card = 0;
		
		for(int i=0;i<player_num;i++) {
			
			while( (card=rand.nextInt(40)) != -1 ) {
				
				if(cards[card]) {
					playing_card[i]=card%10+1;
					break;
				}
			}
		}

		for(int i=0;i<=winner_count;i++) {
			if(playing_card[winner[i]]>winner_card) {
				Arrays.fill(tie_winner, -1);
				tie_winner_count = 0;
				tie_winner[tie_winner_count]=winner[i];
				winner_card = playing_card[winner[i]];
			}
			else if(playing_card[winner[i]] == winner_card) tie_winner[++tie_winner_count]=winner[i];
		}
		
		System.out.println("\ntie winnercard= " +winner_card);
		for(int c : playing_card)
			System.out.printf("%d ", c);
		
		if(tie_winner_count>0) return tie_deal(tie_winner, tie_winner_count);
		else return tie_winner[0];
	}
	
	void player_shift() {
		
		Player tmp = Player.players.get(0);
		Player.players.remove(0);
		Player.players.add(tmp);
	
	}

	boolean betting() {
		
		String action = "fold";
		int chip=0;
		
		for(Player p : Player.players) {
			show_game_info();
			show_players_info();
			if(p.human_check) Show_cards(p);
			while(true) {
				try {
					if( (p.Get_status() == status_type.retire) || (p.Get_status() == status_type.fold) ) ;
					else if (p.Get_status() == status_type.AllIn) p.call(this);
					else if(p.human_check) {
						if( p.Get_status() == status_type.retire || p.Get_status() == status_type.fold ) ;
						else if (p.Get_status() == status_type.AllIn) p.call(this);
						else chip = p.betting(action, chip, this);
					}
					else chip = p.betting(action, chip, this);
					break;
				}catch(Exception e) {
					System.out.println(e);
				}	
			}
		}
		
		int l=0, m=0, n=0;
		for(Player p : Player.players) {
			if(p.Get_status() == status_type.play) {
				n = p.Get_betting_chip();
				l += n;
				m++;
			}
		}
		
		if ( m == 0 || ( n == (l/m) ) ) {
			return false;
		}
		
		return true;
	}
	
	void show_game_info() {
		System.out.println("===============================================");
		System.out.printf("Betting stack : %d, Betting chip : %d\n", betting_stack, betting_chip);
		System.out.println("===============================================");
	}
	
	void show_players_info() {
		System.out.println("===============================================");
		System.out.printf("Name	chips	betting chip	status	play\n");
		for(Player p : Player.players) {
			System.out.printf("%s	%d	%d		%s	%s\n", p.Get_name(), p.Get_holding_chip(), p.Get_betting_chip(), p.Get_status(), p.Get_p_status());
		}
		System.out.println("===============================================");
	}
	
	boolean round_result() {
		
		int[] winner = new int[4];
		int winner_count = 0;
		int winner_card = 0;
		int round_winner;
		int distribute_betting = 0;
		Arrays.fill(winner, -1);
		
		for(int i=0;i<player_num;i++) {
			if( (Player.players.get(i).Get_status() == status_type.play) || (Player.players.get(i).Get_status() == status_type.AllIn) ) {
				if(playing_card[i] > winner_card) {
					Arrays.fill(winner, -1);
					winner_count = 0;
					winner[winner_count]=i;
					winner_card = playing_card[i];
				}
				else if(playing_card[i] == winner_card)	winner[++winner_count]=i;
			}
		}
		
		System.out.println("winnercard= " +winner_card);
		for(int c : playing_card)
			System.out.printf("%d ", c);
		
		if(winner_count>0) round_winner = tie_deal(winner, winner_count);
		else round_winner = winner[winner_count];
		System.out.println("\nwinner = "+Player.players.get(round_winner).Get_name());
		
		if(Player.players.get(round_winner).Get_status() == status_type.AllIn) {
			
			distribute_betting =Player.players.get(round_winner).Get_betting_chip() * playing_num;
			Player.players.get(round_winner).Plus_holding_chip(distribute_betting);
			
			betting_stack -= distribute_betting;
			
			for(Player p : Player.players) {
				if( (p.Get_status() != status_type.AllIn) || (p.Get_status() != status_type.retire) )
					p.Plus_holding_chip(betting_stack/(playing_num-1));
			}
		}
		else Player.players.get(round_winner).Plus_holding_chip(betting_stack);
		System.out.println("win stack : "+betting_stack);
		
		for(Player p : Player.players) {
			if( (p.Get_holding_chip() < ante) && (p.Get_status() != status_type.retire) ) {
				p.Set_status_type(status_type.retire);
				playing_num--;
			}
		}
		
		if( ( card_count/player_num == 0 ) || ( playing_num == 1 ) ) {
			System.out.println("\ngame finish");
			
			int win = 0;
			for(int i=0;i<player_num-1;i++)
				if(Player.players.get(win).Get_holding_chip()<Player.players.get(i+1).Get_holding_chip()) win=i+1;
				
			System.out.println("winner : " + Player.players.get(win).Get_name());
			
			for(Player p : Player.players)
				 p.Set_betting_chip(0);
			
			show_players_info();
			return false;
		}
		
		return true;
	}
	
}