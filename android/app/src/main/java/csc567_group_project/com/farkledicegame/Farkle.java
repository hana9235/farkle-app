package csc567_group_project.com.farkledicegame;

import android.os.Parcelable;

import java.io.*;
import java.util.*;


public class Farkle {
    
    int num_players;
    private static ArrayList<Player> p_list;
    // each player gets their own dice list on Player() init
    // so don't need to init a dice list here
    
    public static void main(String[] args) {
        System.out.println("-={ Farkle }=-");

        // ask for number of players, cast to int
        System.out.print("Enter the number of players: ");
        Scanner sc = new Scanner(System.in);
        int num_players = sc.nextInt();
        
        System.out.print("Enter the number of AIs: ");
        int num_AI = sc.nextInt();
        
        int num_humans = num_players - num_AI;
        
        
        // init and fill ArrayList
        p_list = new ArrayList<Player>();
        
        for(int i = 1; i <= num_players; i++) {
            Player p = new Player();
            
            // check if p needs to be an AI
            // if i > num_humans, then p is an AI
            if (i > num_humans) { 
                p.set_to_ai();
            }
            p_list.add(p);
        }
        
                
        // set player names to default "Player 1"
        for (int i = 1; i <= p_list.size(); i++) {
            Player renamed = p_list.get(i-1);
            renamed.set_name("Player " + i);
            if (renamed.get_ai() ) {
                renamed.set_name(renamed.get_name() + " (AI)");
            }
            p_list.set(i-1, renamed);
        }
        
        System.out.println("\n\nPLAYERS NAMES");
        for (int j = 0; j < p_list.size(); j++) {
            System.out.println(p_list.get(j).get_name());
        }
        
                        
        // create that list, pass it to Game();
        System.out.println("Let's play!");
        Game farkle = new Game(p_list);
        farkle.play();
    }
}



class Game {
    
    boolean game_won;
    ArrayList<Player> player_list;
    int current_player;
    static int GAME_LIMIT = 10000;
    
    public Game(ArrayList<Player> p_list) {
        this.player_list = p_list;
        
        this.game_won = false;
        
        // an index to be used for the player_list
        this.current_player = 0;     

    }
    

    public void play() {
        while (!this.game_won) {
            System.out.println("\nPlayer " + (this.current_player + 1) + "'s turn");
            Player p = this.player_list.get(this.current_player);
            System.out.println("============");
            take_turn(p);
            next_player();
        }
        System.out.println("Game over, congratulations Player " + (this.current_player + 1) + "!");
        return;
    }
    
    public void take_turn(Player p) {
        boolean go_again, is_rolling = true;
        int turn_total = 0;
        while (is_rolling) {
            p.roll_dice();
            ArrayList<Integer> roll_results = calculate_roll_value(p);
            int roll_score = roll_results.get(0);
            int scoring_dice = roll_results.get(1);

            // UPDATE UI WITH DICE ROLL RESULTS
            // IF BUST, DISABLE ROLL AGAIN BUTTON, hide it?

            // check for no points
            if (roll_score == 0) {
                // bust
                is_rolling = false;
                System.out.println("Bust!");
                p.get_score();
                this.show_score(p);
                p.reset_dice();
                break;
            }
            
            if (p.get_ai()) {
                go_again = ai_decision(p, roll_score, scoring_dice);
            }
            else {
                go_again = human_decision(p, roll_score, scoring_dice);
            }
            
            
            if (go_again) {
                // total up things, check dice, start while again
                // you have to make sure to update the score so the user only gets credit
                // for the dice that they held
                ArrayList<Integer> updated_results = calculate_roll_value(p);
                roll_score = roll_score - updated_results.get(0);

                turn_total += roll_score;
            }
            else { // player said no to roll again
                turn_total += roll_score;
                if (p.is_on_board(turn_total)) {
                    // only add points if player is on board
                    // requires 1000+ in one roll
                    p.add_to_score(turn_total);
                }
                
                p.reset_dice();
                if (p.get_score() >= GAME_LIMIT) {
                    this.game_won = true;
                }
                this.show_score(p);
                break;
            }
        }
        // end of function, no return because it's void
    }


    public ArrayList<Integer> calculate_roll_value(Player p) {
        ArrayList<Integer> results = new ArrayList<Integer>();
        // result will have two values, the point value and the number of scoring dice
        
        int sum = 0;
        int scoring_dice = 0;
        ArrayList<Die> rolled_dice = p.get_rolled_dice();
        
        // count occurrences
        Map<Integer, Integer> counted = new HashMap<Integer, Integer>();
        for (int i = 0; i < rolled_dice.size(); i++) {
            int die_face_val = rolled_dice.get(i).get_value();
            if(!counted.containsKey(die_face_val)) {
                // value is not yet in the map, add it
                counted.put(die_face_val, 1);
            }
            else { // value exists, increase number of occurrences
                int old_count = (int) counted.get(die_face_val);
                counted.put(die_face_val, old_count + 1);
            }
        }
        
        // counted should contain something like:
        // {1=3, 4=2, 6=1}
        
        // rules vary for scoring if at least 3 of a value has been rolled
        ArrayList<Integer> at_least_three = new ArrayList<Integer>();
        ArrayList<Integer> less_than_three = new ArrayList<Integer>();
        
        for( Map.Entry<Integer, Integer> entry : counted.entrySet()) {
            int face_val = entry.getKey();
            int occurrences = entry.getValue();
            
            if (occurrences < 3) {
                less_than_three.add(face_val);
            }
            else {
                at_least_three.add(face_val);
            }
        }
        // the roll values are properly sorted between the lists
        
        // first take care of special cases
        if (less_than_three.size() == 6) {
            // rolled a straight
            sum = 1500;
            scoring_dice = 6;
            results.add(sum);
            results.add(scoring_dice);
            System.out.println("Straight");
            return results;
        }

        if (less_than_three.size() == 3 && at_least_three.size() == 0
            && rolled_dice.size() == 6) {
            // possibly rolled three pairs
            boolean is_three_pair = true;
            for(int i = 0; i < 3; i++) {
                int key = less_than_three.get(i);
                int occurrences = counted.get(key);
                if (occurrences != 2) {
                    // if one isn't a pair, three pair fails
                    is_three_pair = false;
                }
            }
            if(is_three_pair) {
                // if it got to here, then it must be three pair
                sum = 600;
                scoring_dice = 6;
                System.out.println("Three pairs");
                results.add(sum);
                results.add(scoring_dice);
                return results;
            }
        }
        
        // special cases didn't happen, calculate point values of roll
        // three or more of a number
        for(int val : at_least_three) {
            int occurrences = counted.get(val);
            if(occurrences > 3) {
                if (val == 1) {
                    // special case, 3 1's == 1000
                    sum += 1000 * (2 * (occurrences - 3));
                }
                else { // not 1's, no special case
                    sum += (val * 100) * (2 * (occurrences - 3));   
                }
                scoring_dice += occurrences;
            }
            else { // exactly 3 of the same value rolled
                if (val == 1) {
                    sum += 1000;
                }
                else {
                    sum += (val * 100);
                }
                scoring_dice += occurrences;
            }
        }
        
        // for 1 or 2 of a number, only 1's and 5's count for points
        for (int val : less_than_three) {
            int occurrences = counted.get(val);
            if (val == 1) {
                sum += 100 * occurrences;
                scoring_dice += occurrences;
            }
            else if (val == 5) {
                sum += 50 * occurrences;
                scoring_dice += occurrences;
            }
        }
        
        System.out.println("score: " + sum);
        
        results.add(sum);
        results.add(scoring_dice);
        return results;
    }
    
    public void next_player() {
        // get next player from list, or loop back to player 1
        if(this.current_player < this.player_list.size() - 1) {
            this.current_player++;
        }
        else {
            this.current_player = 0;
        }

        // UPDATE UI WITH CURRENT PLAYER'S INFO, either all blank dice or roll them immediately
    }
    
    public void show_score(Player p) {
        System.out.print("Your current score is: ");
        System.out.println(p.get_score());
    }
    
    public Map<Integer, Integer> get_scoring_dice(Player p) {
        // used in calculate_roll_value and ai_decision
        // represents the number of dice that potentially score
        // ai uses this to determine which dice to hold
        ArrayList<Die> rolled_dice = p.get_rolled_dice();
        
        // count occurrences
        Map<Integer, Integer> counted = new HashMap<Integer, Integer>();
        for (int i = 0; i < rolled_dice.size(); i++) {
            int die_face_val = rolled_dice.get(i).get_value();
            if(!counted.containsKey(die_face_val)) {
                // value is not yet in the map, add it
                counted.put(die_face_val, 1);
            }
            else { // value exists, increase number of occurrences
                int old_count = (int) counted.get(die_face_val);
                counted.put(die_face_val, old_count + 1);
            }
        }
        
        return counted;
    }
    
    public boolean human_decision(Player p, int roll_score, int scoring_dice) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Roll again? (y/n): ");
        String go_again = sc.next().toLowerCase();
        int y_or_n = go_again.compareTo("y");
        
        if(y_or_n == 0) { // player said yes
            //turn_total += roll_score;
            if(scoring_dice == 6) {
                // all dice score some value, reset and reroll all
                System.out.println("resetting dice...");
                p.reset_dice();
                return true;
            }
            else {
                // at least one didn't score, player must hold at least one to roll again
                System.out.println();
                // show dice and ask player to choose which
                ArrayList<Die> rolled = p.get_rolled_dice();
                for(int i = 0; i < rolled.size(); i++) {
                    System.out.print("D" + (i + 1) + " ");
                }
                
                System.out.println();
                for(Die d : rolled) {
                    System.out.print(" " + d.get_value() + " ");
                }
                System.out.println();
                
                // choose which - POP UP TOAST?
                System.out.println("\nWhich dice do you want to hold?");
                System.out.print("Enter the numbers (D1 = 1) separated only by commas: ");
                // ^^^ JUST CLICK ON THE DICE ON THE UI AND CALCULATE HOLD ON RETURN FROM UI
                
                ArrayList<Integer> dice_to_hold = new ArrayList<Integer>();
                String dice_indexes = sc.next();
                
                for (int i = 0; i < dice_indexes.length(); i++) {
                    String die_num = Character.toString(dice_indexes.charAt(i));
                    int is_comma = die_num.compareTo(",");
                    if (is_comma == 0) {
                        continue;
                    }
                    else {
                        // add the values to the hold list, adjusted for 0-index
                        dice_to_hold.add(Integer.parseInt(die_num) - 1);
                    }
                }
                    p.hold(dice_to_hold);
                    // UPDATE UI, move held dice from play screen to the hold area
                return true;
            }
        }
        return false;  // not rolling again    
    }
    
    
    public boolean ai_decision(Player p, int roll_score, int scoring_dice) { // list or int of scoring dice?  need to know which ones 
        System.out.println("Current player is an AI, decide what to do with your roll.");
        ArrayList<Integer> dice_to_hold = new ArrayList<Integer>();

        // AI doesn't have to pipe input into y/n questions, because this will be a separate if-branch, so make the decisions here */
        if (roll_score > 1000) {
            // decide not to roll again, because that's a good score and don't want to risk losing it
            // this could go higher depending on how often computer hits it
            return false;
        }
        else {  // worry about everything else  -- IS NOT ON BOARD?? 
            if (scoring_dice == 6) {
                p.reset_dice();
                return true;
                // turn_total += roll_score; is done in the take_turn code?
            }
            
            else if (scoring_dice > 3 && scoring_dice < 6) {
                // only one or two dice to roll, odds are not good for scoring
                // do NOT go again
                return false;
            }
            
            else { // at least 3 dice to roll
                // going to hold any?
                if (roll_score <= 200) {
                    if (scoring_dice == 2) {
                        // then these are 1s or 5s, so take the 1s
                        for(int i = 0; i < p.dice_list.size(); i++) {
                            Die d = p.dice_list.get(i);
                            if (d.get_value() == 1) {
                                // hold
                                dice_to_hold.add(i);
                            }
                        }
                        p.hold(dice_to_hold);

                        // UPDATE UI
                        if (p.dice_list.size() == 0) {
                            // all 6 dice are held
                            p.reset_dice();
                        }
                        System.out.println("AI held dice, rolling again...");
                        return true;
                    }
                }
                else {
                    Map<Integer, Integer> count_faces = get_scoring_dice(p);
                    System.out.println("Counted = " + count_faces);
                    ArrayList<Integer> faces_to_hold = new ArrayList<Integer>();
                    
                    if (scoring_dice >= 3) {
                        // now go through the counted list, if value > 2, hold the dice of that value
                        for (Map.Entry<Integer, Integer> entry : count_faces.entrySet()) {
                            System.out.println(entry.getKey() + "/" + entry.getValue());
                            if (entry.getValue() > 2) {
                                faces_to_hold.add(entry.getKey());
                            }
                        }
                        
                        // find the matching faces in the rolled list
                        for(int i = 0; i < p.dice_list.size(); i++) {
                            int this_face = p.dice_list.get(i).get_value();
                            if(faces_to_hold.contains(this_face)) {
                                // if this is one of the matching dice, add that index to the dice to hold list
                                dice_to_hold.add(i);
                            }
                        }
                        // do the actual holding
                        p.hold(dice_to_hold);
                        if (p.dice_list.size() == 0) {
                            // all 6 dice are held
                            p.reset_dice();
                        }
                        if (dice_to_hold.size() > 0) {
                            System.out.println("AI held dice, rolling again...");
                        }
                        return true;
                    }
                }
                
                System.out.println("AI did not roll at least 1000\n\n");
                System.out.println("AI is rolling again...");
                return true;
            }
        }
    }
}




class Player {
    String playerName;
    int total_score;
    boolean on_board, is_ai;
    ArrayList<Die> dice_list, held_dice;

    public Player() {
        // constructor for the player class
        this.total_score = 0;
        this.on_board = false;
        this.is_ai = false;
        dice_list = new ArrayList<Die>();
        held_dice = new ArrayList<Die>();

        for( int i = 0; i < 6; i++) {
            // populate dice list
            Die d = new Die();
            this.dice_list.add(d);
        }
    }

    public void set_name(String s) {
        this.playerName = s;
    }

    public String get_name() {
        return this.playerName;
    }

    public void set_to_ai() {
        this.is_ai = true;
    }

    public boolean get_ai() {
        return this.is_ai;
    }

    public int get_score() {
        return this.total_score;
    }

    public void add_to_score(int points) {
        this.total_score += points;
    }

    public void roll_dice() {
        Die d;
        for( int i = 0; i < this.dice_list.size(); i++ ){
            d = this.dice_list.get(i);
            d.roll();
        }
    }

    public void hold(ArrayList<Integer> dice_to_hold) {
        // move the selected dice from the dice_list to
        // the held_dice list
        for( int i = 0; i < dice_to_hold.size(); i++) {
            int index = dice_to_hold.get(i);
            this.held_dice.add(this.dice_list.get(index));
        }
        System.out.println("Player's held list: ");
        for(int i = 0; i < this.held_dice.size(); i++) {
            System.out.print(this.held_dice.get(i).get_value() + " ");
        }

        System.out.println("\n");

        // now remove the held_dice from the dice_list
        // go backward so there are no index errors
        for(int i = dice_to_hold.size(); i > 0; i--) {
            int index = dice_to_hold.get(i-1);
            this.dice_list.remove(index);
        }

    }

    public void reset_dice() {
        this.dice_list.clear();
        for( int i = 0; i < 6; i++) {
            // populate dice list
            Die d = new Die();
            this.dice_list.add(d);
        }
        this.held_dice.clear();
    }

    public boolean is_on_board(int turn_points) {
        if (!this.on_board) {
            if (turn_points < 1000) {
                // didn't hit threshold
                System.out.println("Not enough to get on the board. Score = 0");
                return false;
            }
            else { // score at 1000+
                this.on_board = true;
                return true;
            }
        }
        else { // already on board
            return true;
        }
    }

    public ArrayList<Die> get_rolled_dice(){
        return this.dice_list;
    }


    public static void main(String[] args) {
        Player p = new Player();
        System.out.println(p.get_score());

        for(int i = 0; i < 4; i++) {
            p.roll_dice();
            System.out.println();
        }

    }


}



class Die {

    int value;
    Random random;

    public Die() {
        this.value = 1;
        random = new Random();

    }

    public void roll() { // probably change return type
        int new_value = random.nextInt(6) + 1;
        this.value = new_value;
        System.out.print(this.value + " ");
    }

    public int get_value() {
        return this.value;
    }

    public static void main(String[] args) {
        Die d = new Die();
        for (int i = 0; i < 6; i++) {
            d.roll();
        }
    }
}
