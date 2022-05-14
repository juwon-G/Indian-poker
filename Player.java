package IndianPoker;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

enum status_type { play, AllIn, fold, retire }
enum play_status { not, bet, raise, call, check, fold }

public abstract class Player {
	
	protected String name;
	protected int holding_chip;
	protected int betting_chip;
	protected status_type status;
	protected play_status p_status;
	protected boolean human_check;
	public static ArrayList<Player> players = new ArrayList<Player>();
	public static Scanner sc = new Scanner(System.in);
	Random rand = new Random();
	
	Player(){
		name = Integer.toString(rand.nextInt(1000));
		holding_chip = 500;
		betting_chip = 0;
		status = status_type.play;
		p_status = play_status.not;
		
		players.add(this);
	}
	
	Player(String str, int n){
		name = str;
		holding_chip = n;
		betting_chip = 0;
		status = status_type.play;
		p_status = play_status.not;
		
		players.add(this);
	}
	
	void Plus_holding_chip(int chips) {
		holding_chip += chips;
	}
	void Set_betting_chip(int chips) {
		betting_chip = chips;
	}
	void Set_status_type(status_type stat) {
		status = stat;
	}
	String Get_name() {
		return name;
	}
	int Get_holding_chip() {
		return holding_chip;
	}
	int Get_betting_chip() {
		return betting_chip;
	}
	status_type Get_status() {
		return status;
	}
	void Set_p_status(play_status stat) {
		p_status = stat;
	}
	play_status Get_p_status() {
		return p_status;
	}
	
	void clear(Dealer D) {
		
		if(status != status_type.retire) {
			betting_chip = D.Get_ante();
			holding_chip -= betting_chip;
			status = status_type.play;
			p_status = play_status.not;
		}
	}
	
	boolean bet(int chip, Dealer D) {
		
		if(holding_chip < chip) {
			return false;
		}
		else if(holding_chip == chip) {
			status = status_type.AllIn;
			System.out.println(name + " AllIn");
		}
		
		holding_chip -= chip;
		betting_chip += chip;
		D.Plus_betting_stack(chip);
		D.Set_betting_chip(betting_chip);
		
		p_status = play_status.bet;
		D.Set_bet_check(true);
		return true;
		
	}
	
	boolean raise(int rasing_chips, Dealer D) {

		if(holding_chip < (D.Get_betting_chip() + rasing_chips - betting_chip)) {
			call(D);
			p_status = play_status.call;
		}
		else if(holding_chip == (D.Get_betting_chip() + rasing_chips - betting_chip)) {
			status = status_type.AllIn;
			p_status = play_status.raise;
		}
		else {
			D.Set_betting_chip(D.Get_betting_chip() + rasing_chips);
			holding_chip -= D.Get_betting_chip() - betting_chip;
			D.Plus_betting_stack(D.Get_betting_chip() - betting_chip);
			betting_chip = D.Get_betting_chip();
			p_status = play_status.raise;
		}
		return true;
	}
	
	boolean call(Dealer D) {

		if(holding_chip <= (D.Get_betting_chip() - betting_chip)) {
			status = status_type.AllIn;
			betting_chip += holding_chip;
			D.Plus_betting_stack(holding_chip);
			holding_chip = 0;
		}
		else {
			holding_chip -= D.Get_betting_chip()-betting_chip;
			D.Plus_betting_stack(D.Get_betting_chip()-betting_chip);
			betting_chip = D.Get_betting_chip();
		}
		p_status = play_status.call;
		return true;
	}
	
	boolean check() {
		p_status = play_status.check;
		return true;
	}
	
	boolean fold() {
		status = status_type.fold;
		p_status = play_status.fold;
		return true;
	}
	
	abstract int betting(String action, int chip, Dealer D) throws Exception;
}

class Human extends Player{
	
	public Human(String str, int n) {
		super(str, n);
		human_check = true;
	}
	
	int betting(String action, int chip, Dealer D) throws Exception {
		
		action = sc.next();
		switch(action) {
		case "bet":
			if(D.Get_bet_check()) throw new Exception("Can't bet.");
			chip = sc.nextInt();
			bet(chip, D);
			break;
			
		case "raise":
			if(!D.Get_bet_check()) throw new Exception("Can't raise.");
			chip = sc.nextInt();
			raise(chip, D);
			break;
			
		case "call":
			if(!D.Get_bet_check()) throw new Exception("Can't call");
			call(D);
			break;
			
		case "check":
			if(D.Get_bet_check()) throw new Exception("Can't check");
			check();
			break;
			
		case "fold":
			fold();
			break;
			
		default :
			throw new Exception("Wrong input");
		}
		
		return chip;
	}	
}

class Computer extends Player{
	
	public Computer() {
		super();
		human_check = false;
	}
	
	int betting(String action, int chip, Dealer D) {

		
		int []showing_card = D.Get_showing_card(this);
		int max=0;
		for(int i=0;i<D.Get_player_num();i++)
			if(players.get(i).Get_p_status() != play_status.fold) max=Math.max(max, showing_card[i]);
		
		int p = rand.nextInt(10);
		int Case = p-max;
		
		if(Case<-2) {
			if(!D.Get_bet_check()) check();
			else fold();
		}
		else if(Case<2) {
			if(!D.Get_bet_check()) check();
			else call(D);
		}
		else {
			if(!D.Get_bet_check()) bet(D.Get_ante()*Case, D);
			else raise(D.Get_ante()*Case, D);
		}
		
		return chip;
	}
}