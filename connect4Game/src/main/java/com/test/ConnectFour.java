package com.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * object to represent grid square values
 */
enum Color {
	EMPTY,
	RED,
	GREEN;
	
	public char getCode() {
		switch (this) {
		case RED:
			return 'R';
		case GREEN:
			return 'G';
		default:
			return ' ';
		}
	}
}

/*
 * connect four game class
 */
public class ConnectFour {
	// constraint values
	private static final byte NO_ROWS = 6;
	private static final byte NO_COLS = 7;
	private static final byte NO_DIRS = 4;
	
	// array of incremental values for each direction
	private static byte directions[][] = {
			// x, y
			{ -1, 1 }, // NW
			{  0, 1 }, // N
			{  1, 1 }, // NE
			{  1, 0 }  // E
	};
	
	// 2d array to represent board
	private Color board[][];
	
	// possible directions of finding a match at each grid square
	private List<byte[]> possibleDirs[][];
	
	// scanner for console io
	private Scanner scanner;
	
	// constructor
	public ConnectFour(Scanner scanner) {
		this.scanner = scanner;
	}
	
	// initializes the board and possible direction arrays
	@SuppressWarnings("unchecked")
	public void init() {
		// instantiate arrays
		board = new Color[NO_ROWS][NO_COLS];
		possibleDirs = new List[NO_ROWS][NO_COLS];
		
		// iterate through each spot
		for (byte r = 0; r < NO_ROWS; r++) {
			for (byte c = 0; c < NO_COLS; c++) {
				// set space as empty on board
				board[r][c] = Color.EMPTY;
				
				// setup possible directions
				possibleDirs[r][c] = new ArrayList<byte[]>();
				for (byte i = 0; i < NO_DIRS; i++) {
					// determine if space to have 4 consecutive pieces (3 further)
					if (
							c + 3 * directions[i][0] >= 0 &&
							c + 3 * directions[i][0] <= 6 &&
							r + 3 * directions[i][1] >= 0 &&
							r + 3 * directions[i][1] <= 5
							) {
						possibleDirs[r][c].add(directions[i]);
					}
						
				}
			}
		}
	}
	
	// prints out the board
	public void printBoard() {
		
		for (byte r = NO_ROWS - 1; r >= 0; r--) {
			System.out.print("|");
			for (byte c = 0; c < NO_COLS; c++) {
				System.out.print(board[r][c].getCode() + "|");
			}
			System.out.println();
		}
		
	}
	
	// goes through the game
	public void play() {
		byte turn = 0;
		boolean isRunning = true;
		int currentPlayer =0;
		
		// cannot exceed 42 turns because then board is full
		while (turn < NO_ROWS * NO_COLS && isRunning) {
			// determine current color based on odd/even turn
			Color currentColor = (turn & 1) == 1 ? Color.GREEN : Color.RED;
			if(currentColor.getCode() == 'R') {
				currentPlayer = 1;
			}
			else {
				currentPlayer = 2;
			}
			byte col = 0;
		
			// insert piece
			while (true) {
				// get input
				System.out.print("\nPlayer " +currentPlayer+"[" +currentColor +"]"+" - choose column (1-7): ");
				String in = scanner.next();
				
				// parse value
				try {
					col = Byte.parseByte(in);
				} catch (Exception e) {
					System.out.println("Please input a valid digit (1-7).");
					continue;
				}
				
				if (insert(currentColor, (byte)(col - 1))) {
					break;
				}
				
				System.out.println("Please input a valid number (1-7).");
			}
			
			printBoard();
			
			// check for winner
			boolean foundWinner = false;
			for (byte r = 0; r < NO_ROWS; r++) {
				for (byte c = 0; c < NO_COLS; c++) {
					// determine if spot should be checked
					// don't check empty or other color because cannot win if not its turn
					if (board[r][c] == currentColor) {
						// iterate through each possible direction
						for (byte i = 0; i < possibleDirs[r][c].size(); i++) {
							// determine if connection of >= 4 pieces
							if (countConsecutive(r, c, possibleDirs[r][c].get(i), currentColor) >= 4) {
								foundWinner = true;
								break;
							}
						}
					}
				}
				
				if (foundWinner)  {
					break;
				}
			}
			
			// stop loop
			if (foundWinner) {
				System.out.println("Player " +currentPlayer+"[" +currentColor +"]"+" wins!");
				System.out.println("````");
				isRunning = false;
				break;
			}
			
			// increment turn
			turn++;
		}
		
		if (isRunning) {
			// winner not found
			System.out.println("Nobody Won SO Draw Happened");
		}
	}
	
	// inserts a piece of the color into the column
	public boolean insert(Color color, byte col) {
		if (col < 0 || col >= NO_COLS) {
			// out of bounds
			return false;
		}
		
		byte r;
		for (r = NO_ROWS - 1; r >= 0; r--) {
			if (board[r][col] != Color.EMPTY) {
				// insert above
				r++;
				break;
			}
		}
		
		if (r == NO_ROWS) {
			// piece present in top of column
			return false;
		}
		else if (r == -1) {
			// no pieces in column, insert at bottom
			r++;
		}
		
		// set value
		board[r][col] = color;
		
		return true;
	}
	
	// counts consecutive pieces starting at the spot
	public byte countConsecutive(byte r, byte c, byte dir[], Color color) {
		// out of bounds check
		if (r < 0 || r >= NO_ROWS || c < 0 || c >= NO_COLS) {
			return 0;
		}
		
		// color match check
		if (board[r][c] != color) {
			return 0;
		}
		
		// match found, add to string
		return (byte)(1 + countConsecutive(
				(byte)(r + dir[1]), // increment row
				(byte)(c + dir[0]), // increment column
				dir, // pass in same direction
				color));
	}
	
	// main method
	public static void main(String[] args) {
		System.out.println("'''' Text");
		
		// instantiate scanner
		Scanner scanner = new Scanner(System.in);
		
		// instantiate game
		ConnectFour connectFour = new ConnectFour(scanner);
		connectFour.init();
		connectFour.printBoard();
		connectFour.play();
		
		// close scanner
		scanner.close();
	}
}