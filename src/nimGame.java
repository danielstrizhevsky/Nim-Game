import java.awt.Point;
import java.util.Scanner;


public class nimGame {

	private static final int stackMaxBits = 4;
	private static final int maxStacks = 10;
	private static Scanner scan;

	public static void main(String[] args) {
		int numStacks = 3 + (int) ((maxStacks - 2) * Math.random()); //min stacks = 3
		int[] stacks = new int[numStacks];
		int turnChoice = 0;
		for (int i = 0; i < numStacks; i++) {
			stacks[i] = 1 + (int) ((Math.pow(2, stackMaxBits) - 1) * Math.random());
		}
		printGameBoard(stacks);
		System.out.print("Would you like to go first (1), second (2), or let "
				+ "NimBot choose (3)? ");
		scan = new Scanner(System.in);
		while (!scan.hasNextInt()) {
			System.out.println("Please enter 1, 2, or 3: ");
			scan.next();
		}
		turnChoice = scan.nextInt();
		if (turnChoice == 1)
			playGame(stacks, true);
		else if (turnChoice == 2)
			playGame(stacks, false);
		else if (turnChoice == 3)
			playGame(stacks, chooseOrder(convertToBinary(stacks)));
	}

	public static boolean[][] convertToBinary(int[] stacks) {
		//array of arrays of bits (array of numbers of things in each pile)
		boolean[][] binaryStacks = new boolean[stacks.length][stackMaxBits];
		for (int i = 0; i < stacks.length; i++) {
			int quotient = stacks[i];
			for (int j = 0; quotient != 0; j++) {
				binaryStacks[i][j] = (quotient % 2) == 1; //binary in reverse order
				quotient /= 2;
			}
		}
		return binaryStacks;
	}

	public static Point moveToZero(boolean[][] stacks) {
		int selectedBit = 0;
		int selectedRow = 0;
		boolean rowChosen = false;
		int newAmount = 0;
		//iterates through every bit from 2^n (i = start) to 2^0 (i = 0)
		for (int i = (stackMaxBits - 1); i >= 0; i--) {
			boolean bitNimsum = false;
			//iterates through every pile for the current bit
			for (int j = 0; j < stacks.length; j++) {
				bitNimsum ^= stacks[j][i];
			}
			if (rowChosen) { //if row has been chosen, changes it to the zero position
				if (bitNimsum && stacks[selectedRow][i]) {
					stacks[selectedRow][i] = false; //sets 1 to 0 to set bit column XOR to 0
				}
				else if (bitNimsum && !stacks[selectedRow][i]) {
					stacks[selectedRow][i] = true; //sets 0 to 1 to set bit column XOR to 0
				}
			}
			else { //chooses the row to work with on this turn
				if (bitNimsum) {
					selectedBit = i;
					for (int k = 0; k < stacks.length; k++) {
						if (stacks[k][selectedBit]) {
							selectedRow = k;
							rowChosen = true;
							i++;
							break;
						}
					}
				}
			}
		}
		for (int i = 0; i < stackMaxBits; i++) {
			if (stacks[selectedRow][i]) {
				newAmount += Math.pow(2, i);
			}
		}
		return new Point(selectedRow,newAmount);
	}
	
	//returns true if player should go first, false if NimBot should go first
	public static boolean chooseOrder(boolean[][] stacks) {
		for (int i = (stackMaxBits - 1); i >= 0; i--) {
			boolean bitNimsum = false;
			//iterates through every pile for the current bit
			for (int j = 0; j < stacks.length; j++) {
				bitNimsum ^= stacks[j][i];
			}
			if (bitNimsum)
				return false;
		}
		return true;
	}
	
	public static void printGameBoard(int[] stacks) {
		System.out.print("Current game board: ");
		for (int i = 0; i < stacks.length; i++) {
			System.out.print("\t" + stacks[i]);
		}
		System.out.print("\n-----------------");
		for (int i = 0; i < stacks.length; i++) {
			System.out.print("--------");
		}
		System.out.println();
	}

	public static void playGame(int[] stacks, boolean playerTurn) {
		boolean gameOver = false;
		int itemsLeft = 0;
		for (int i = 0; i < stacks.length; i++) {
			itemsLeft += stacks[i];
		}
		if (itemsLeft == 0) //game is over if nothing is left
			gameOver = true;
		if (!gameOver) { //checks if game is over
			//game board is printed no matter whose turn it is
			printGameBoard(stacks);
			if (playerTurn) { //does player turn stuff
				int chosenStack = 0;
				int amountRemoved = 0;
				scan = new Scanner(System.in);
				System.out.print("Remove from which stack? ");
				while (!scan.hasNextInt()) {
					System.out.print("Please enter an integer: ");
					scan.next();
				}
				chosenStack = scan.nextInt() - 1;
				while (chosenStack > stacks.length - 1 || chosenStack < 0
						|| stacks[chosenStack] == 0) {
					System.out.print("Please choose a stack that has at least "
							+ "1 item in it: ");
					while (!scan.hasNextInt()) {
						System.out.print("Please enter an integer: ");
						scan.next();
					}
					chosenStack = scan.nextInt() - 1;
				}
				System.out.print("Remove how many from stack " + (chosenStack + 1) + "? ");
				while (!scan.hasNextInt()) {
					System.out.print("Please enter a positive integer no greater than: "
							+ stacks[chosenStack] + ": ");
					scan.next();
				}
				amountRemoved = scan.nextInt();
				while (amountRemoved < 1 || amountRemoved > stacks[chosenStack]) {
					System.out.print("Please enter a positive integer no greater than "
							+ stacks[chosenStack] + ": ");
					while (!scan.hasNextInt()) {
						System.out.print("Please enter a positive integer no greater than: "
								+ stacks[chosenStack] + ": ");
						scan.next();
					}
					amountRemoved = scan.nextInt();
				}
				stacks[chosenStack] -= amountRemoved;
				playGame(stacks, false);
			}
			else { //does computer turn stuff
				boolean[][] binaryStacks = convertToBinary(stacks);
				Point update = moveToZero(binaryStacks);
				System.out.println("Nimbot removes " + (stacks[update.x] - update.y) +
						" items from stack " + (update.x + 1) + ".");
				stacks[update.x] = update.y;
				playGame(stacks, true);
			}
		}
		else { //game is over, the person who had moved previously is the winner
			if (playerTurn) {
				System.out.println("Nimbot won! Better luck next time!");
			}
			else {
				System.out.println("You won! Good job!");
			}
		}
	}
}
