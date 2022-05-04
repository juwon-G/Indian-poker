package IndianPoker;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

enum status_type { play, AllIn, fold, retire }

public abstract class Player {
	
	protected String name;
	protected int holding_chip;
	protected int betting_chip;
	protected status_type status;
	protected boolean human_check;
	public static ArrayList<Player> players = new ArrayList<Player>();
	static Scanner sc = new Scanner(System.in);
	Random rand = new Random();
	
	Player(){
		name = Integer.toString(rand.nextInt(1000));
		holding_chip = rand.nextInt(10)*50+500;
		betting_chip = 0;
		status = status_type.play;
		
		players.add(this);
	}
	
	Player(String str, int n){
		name = str;
		holding_chip = n;
		betting_chip = 0;
		status = status_type.play;
		
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
	
	void clear(Dealer D) {
		
		if(status != status_type.retire) {
			betting_chip = D.Get_ante();
			holding_chip -= betting_chip;
			status = status_type.play;
			
			D.Plus_betting_stack(D.Get_ante());
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
		
		System.out.println(name + " bet");
		return true;
		
	}
	
	boolean raise(int rasing_chips, Dealer D) {

		if(holding_chip < (D.Get_betting_chip() + rasing_chips - betting_chip)) {
			call(D);
		}
		else if(holding_chip == (D.Get_betting_chip() + rasing_chips - betting_chip)) {
			status = status_type.AllIn;
			System.out.println(name + " AllIn");
		}
		else {
			D.Set_betting_chip(D.Get_betting_chip() + rasing_chips);
			holding_chip -= D.Get_betting_chip() - betting_chip;
			D.Plus_betting_stack(D.Get_betting_chip() - betting_chip);
			betting_chip = D.Get_betting_chip();
			System.out.println(name + " raise");
		}
		return true;
	}
	
	boolean call(Dealer D) {

		if(holding_chip <= (D.Get_betting_chip() - betting_chip)) {
			status = status_type.AllIn;
			betting_chip += holding_chip;
			D.Plus_betting_stack(holding_chip);
			holding_chip = 0;
			System.out.println(name + " AllIn");
		}
		else {
			holding_chip -= D.Get_betting_chip()-betting_chip;
			D.Plus_betting_stack(D.Get_betting_chip()-betting_chip);
			betting_chip = D.Get_betting_chip();
			System.out.println(name + " call");
		}
		
		return true;
	}
	
	boolean check() {
		System.out.println(name + " check");
		return true;
	}
	
	boolean fold() {
		status = status_type.fold;
		System.out.println(name + " fold");
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

		/*
		int []showing_card = D.Get_showing_card(this);
		int sum=0;
		
		for(int c : showing_card) sum += c;
		
		double avg = (double)sum/D.Get_playing_num();
		double p = rand.nextDouble()*10;
		int Case = 0;
		if(p>avg) Case = 1;
		else if(p<avg)
		*/
		
		switch(rand.nextInt(100)%7) {
		case 0:
		case 1:
		case 2:
			if(!D.Get_bet_check()) bet(D.Get_ante(), D);
			raise(D.Get_ante(), D);
			break;
		
		case 3:
		case 4:
		case 5:
			if(action.equals("check")) check();
			else call(D);
			break;
		
		case 6:
			fold();
			break;
		}
		
		return chip;
	}
}