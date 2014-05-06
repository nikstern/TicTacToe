import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nicksirock
 * Date: 11/22/13
 * Time: 12:41 AM
 * Uses a minimax algorithm for a user to play tic tac toe against the program
 */
public class Board implements ActionListener{
    private final int NUM_OF_BOXES = 9;
    private final char BLANK_BOX = '#';
    
    private JFrame window = new JFrame("Tic-Tac-Toe"); // Creates a Frame with the title "Tac-Tac-Toe"
    private JButton buttons[] = new JButton[NUM_OF_BOXES]; // Creates an array of 9 buttons that serve as our input for player and output for both computer and player
    private String p1;  // Represents whichever X or O the player will be using
    private String AI;  // Represents whichever X or O the computer will be using
    private char cp1;   // Represents whichever X or O the player will be using as a character to save a lot of converting
    private char cAI;   // You get the idea.
    Board(String letter, boolean first) // Creates the initial frame with empty elements, sets which user is using what, and either waits for user input or
    {                                   // processes the computers first move based on who goes first
        if (letter.equals("X"))     // setting up who uses what token
        {
            this.p1 = "X";
            this.cp1 = 'X';
            this.AI = "O";
            this.cAI = 'O';
        }
        else
        {
            this.p1 = "O";
            this.cp1 = 'O';
            this.AI = "X";
            this.cAI = 'X';
        }
        
        window.setSize(300,300);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new GridLayout(3,3));       // Creating the grid seen in the frame
        
        /*Add Buttons To The Window*/
        for(int i=0; i<NUM_OF_BOXES; i++){
            buttons[i] = new JButton();
            window.add(buttons[i]);
            buttons[i].addActionListener(this);     // creating the buttons and adding Listeners to watch for mouse input
        }
        
        /*Make The Window Visible*/
        window.setVisible(true);        // allows the window to be seen
        if (!first)           // either processes the computer move or waits for input based on who is going first
        {
            window.setEnabled(false);  // stops user from clicking out of their turn
            int result = determine(boardState());  // this is the index that that has been computed to be the best possible move
            buttons[result].setText(AI); // Sets the button at that index to display the computers token
            buttons[result].setEnabled(false);   // Disables that button from being clicked by the player
            checkWin();
            window.setEnabled(true); // allows user to click
        }
    }
    public char[] boardState() // converts the strings of the button texts to characters for easy processing by the computer
    {
        char[] result = new char[NUM_OF_BOXES];
        for (int i = 0; i < NUM_OF_BOXES; i++)
        {
            if (buttons[i].getText().equals(AI))
                result[i] = cAI;
            else if (buttons[i].getText().equals(p1))
                result[i] = cp1;
            else
                result[i] = BLANK_BOX; // stores any empty values as # symbols
        }
        return result;
    }
    public void actionPerformed(ActionEvent a)     // in the event of the player clicking a button
    {
        JButton pressedButton = (JButton)a.getSource(); // sets the text of the button that was clicked to the player's token
        pressedButton.setText(p1);
        pressedButton.setEnabled(false);  // disables the button from being clicked
        window.setEnabled(false); // prevents user from clicking really fast and cheating (I hope)
        checkWin();    // checks to see if the game has ended
        int result = determine(boardState()); // this is the index that that has been computed to be the best possible move
        buttons[result].setText(AI); // sets the text of the button at that index to the computer's token
        buttons[result].setEnabled(false); // disables that button from being clicked
        checkWin();      // checks if game has ended
        window.setEnabled(true);  // allows user to click again
    }
    public void checkWin()
    {
        char[] board = boardState();
        int result = getGameState(board);
        switch (result)
        {
            case 2: // Game is still going
                break;
            case 1:    // Computer won
                JOptionPane.showMessageDialog(null,"Computer won the game!");
                break;
            case -1:    // Player won
                JOptionPane.showMessageDialog(null,"You won the game!");
                break;
            case 0:     // Tie
                JOptionPane.showMessageDialog(null,"The game was a tie!");
                break;
        }
    }
    public int getGameState(char[] current) // returns 1 for win by computer, -1 for win by player, 0 for tie, and 2 for unfinished
    {
        if (computecol(cAI,0,current) || computecol(cAI,1,current) || computecol(cAI,2,current)) // checks all columns for win by computer
            return 1;
        if (computerow(cAI,0,current) || computerow(cAI,1,current) || computerow(cAI,2,current)) // checks all rows for win by computer
            return 1;
        if (computediag(cAI,current) || computediag2(cAI, current))  // checks either diagonal for a win by computer
            return 1;
        if (computecol(cp1,0,current) || computecol(cp1,1,current) || computecol(cp1,2,current))  // checks if player won by columns
            return -1;
        if (computerow(cp1,0,current) || computerow(cp1,1,current) || computerow(cp1,2,current)) // checks if player won by rows
            return -1;
        if (computediag(cp1,current) || computediag2(cp1,current)) // checks if player won by diagonals
            return -1;
        if (legal(current).isEmpty())  // checks for stalemate
            return 0;
        return 2;   // else game is still going
    }
    public int determine(char[] current) // returns the index of the best possible move by the computer
    {
        
        ArrayList<Integer> moves = legal(current); // gets all possible moves where there is an empty space
        int CBS = Integer.MIN_VALUE; // Current Best Score
        int CBM = 100; // Current Best Value
        for (int move : moves)
        {
            current[move] = this.cAI; // Set the grid to a possible move made by computer
            int score = min(current); // Finds the score of the next worst for the computer move made by the player
            current[move] = BLANK_BOX; // Resets the grid so that it does not consider possible move in future
            if (score > CBS)  // if there is a better score
            {
                CBM = move; // update the index to move to and the new highest score
                CBS = score;
            }
        }
        return CBM;
    }
    public int min(char[] current)  // Returns the score of the best possible move made by the player given a map of the grid
    {
        int v = getGameState(current);  // gets whether game is won, game is in tie or game is continuing
        if (v != 2)     // if game is anything but continuing the min value is the game state
            return v;
        ArrayList<Integer> moves = legal(current); // finds all possible moves where there is a blank space
        int CBS = Integer.MAX_VALUE; // Current Best Score (best for player worst for computer)
        for (int move : moves)
        {
            current[move] = cp1;    // Makes a simulated move by the player
            int score = max(current); // Finds the score of the next best move made by the computer
            current[move] = BLANK_BOX; // Resets the grid for next simulated move by the player
            if (score < CBS) // if there is something worse for the computer set new worst
                CBS = score;
        }
        return CBS;
    }
    public int max(char[] current) // Returns the score of the best possible move made by computer given a map of the grid
    {
        int v = getGameState(current);  // gets whether game is over or game is in tie or game is continuing
        if (v != 2)  // if game is anything but continuing the max value is the game state
            return v;
        ArrayList<Integer> moves = legal(current);  // all possible moves meaning there is a blank space
        int CBS = Integer.MIN_VALUE;   // Current Best Score meaning best for computer
        for (int move : moves)
        {
            current[move] = cAI;  // Makes a simulated move by the computer
            int score = min(current); // Finds the score of the resulting move by the player that is worst for the player
            current[move] = BLANK_BOX; // resets the grid
            if (score > CBS)        // if there is something better for the computer set new best
                CBS = score;
        }
        return CBS;
    }
    private ArrayList<Integer> legal(char[] current) // Returns an ArrayList of all the indexes of legal moves
    {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < NUM_OF_BOXES; i++)
        {
            if (current[i] == BLANK_BOX)    // checks if the space is empty
            {
                result.add(i);
            }
        }
        return result;
    }
    public boolean computerow(char s ,int y, char[] current) // Returns whether the row is a winning one
    {
        return (current[3*y] == s && current[3*y+1] == s && current[3*y+2] == s);
    }
    public boolean computecol(char s ,int x, char[] current)    // Returns whether the column is a winning one
    {
        return (current[x] == s && current[x+3] == s && current[x+6] ==s);
    }
    public boolean computediag(char s, char[] current)  // Returns whether the diagonal from the top left to the bottom right is a winning one
    {
        return current[0] == s && current[4] == s && current[8] == s;
    }
    public boolean computediag2(char s, char[] current) // Returns whether the diagonal from the top right to the bottom left is a winning one
    {
        return current[2] == s && current[4] == s && current[6] == s;
    }
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        String input = "";
        while (!(input.equals("X") || input.equals("O")))
        {
            System.out.println("Enter X or O:");
            input = scan.next();
            input = input.toUpperCase();
        }
        Random ran = new Random();
        boolean first = ran.nextBoolean();
        if (first)
            System.out.println("You will be going first");
        else
            System.out.println("The Computer will be going first");
        new Board(input,first);
    }
}
