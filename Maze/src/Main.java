import java.util.random.*;
import java.util.Random;
import java.util.Scanner;

import java.io.FileWriter;
import java.io.IOException;


public class Main {
	//=====================
	// MazeTile Class
	//=====================

	// a tile is just a cell or square in the grid (maze).
	public static class MazeTile{
		// Where this tile is in the maze , coordinates of the tile
		int x;
		int y; 
		char type;  // What kind of tile it is : 'E', 'W', 'T', 'P', 'G'
		boolean hasAgent; // Whether an agent (player) is standing on it or not: True if an agent is on this tile
		
		/* 'E' for Empty, 'W' for Wall, 'T' for Trap, 'P' for Power-up, 'G' for Goal*/
		
		public MazeTile(int x, int y, char type) {
			this.x=x;
			this.y = y;
			this.type=type;
			this.hasAgent=false;
		}
		
		//helps you check if a player can walk on this tile.I ftile's type is not W, then it ids traversable 
		public boolean isTraversable() {
			return type!='W';
		}
		
		//helps you print the tile in the console (like when showing the maze map).
		public String toString() {
	        if (hasAgent) return "A"; // Symbol for agent
	        return String.valueOf(type);
	    }	
	}
	
	//=====================
	// Agent Class
	//=====================

	//Every player in the game is a agent
	public static class Agent{
		int id;
		// current coordinates of agent's
		int initialX, initialY;
		int currentX, currentY; 
		// we keep track every agent's moves in a stack
		Stack moveHistory; 
		boolean hasReachedGoal;
		// total moves for specific agent
		int totalMoves;
		// backtracks for specific agent
		int backtracks;
	
		// I changed this: collected power ups
		public int CollectedPowerUps;
		// how many time an agent traps
		public int trapsTriggered;
		
		public int powerUpsUsed;
		
		public Agent(int id, int startX, int startY) {
			this.id=id;
			this.currentX=startX;
			this.currentY=startY;
			this.initialX = startX;
			this.initialY= startY;
			this.moveHistory= new Stack();
			this.hasReachedGoal=false;
			this.totalMoves=0;
			this.backtracks=0;
			this.CollectedPowerUps=0;
			this.trapsTriggered = 0;
			this.powerUpsUsed = 0;
			// we initialize move history based on random x,y positions
			// this.moveHistory.push(startX, startY);

		}
		
		// due to the direction UPDATE the AGENT'S CURRENT POSITION FIELD 
		public void move(String direction) {
			switch(direction.toUpperCase()) {
				case "UP":
					currentX--;
					break;
				case "DOWN":
					currentX++;
					break;
				case "LEFT":
					currentY--;
					break;
				case "RIGHT":
					currentY++;
					break;
				default:
					System.out.println("Please enter a valid direction");
					return;
			}
			totalMoves++;
			recordMove(currentX, currentY);
		}
		
		// UODATE THE MOVE STACK
		public void recordMove(int x, int y) {
			moveHistory.push(x,y);
		}
		
		// POP FROM MOVE STACK 
		// UPDATE the AGENT'S CURRENT POSITION FIELD 
		// here i do not check if tile is empty or not, i also don't update the hasAgent field, also i do not return the popped element
		public void backtrack() {
			if(moveHistory.peek()!=null) {
				moveHistory.pop();
				if(moveHistory.peek()!=null) {
					String[] currentPos = moveHistory.peek().split(",");
					currentX = Integer.parseInt(currentPos[0]);			
					currentY = Integer.parseInt(currentPos[1]);
					backtracks++;
				}else {
					currentX = initialX;			
					currentY = initialY;
					System.out.println("After moving back player"+ this.id +" returns at its initial state");
				}
			}else {
				System.out.println("Player"+ this.id +" is alraedy at its initial state can not move back");
			}
			
		}
		
		
		public void applyPowerUp() {
			if(CollectedPowerUps != 0) {
				CollectedPowerUps--;
				powerUpsUsed++;
				System.out.println("Agent " + id + " used a power-up!");
			}
			
		}
		
		public String getMoveHistoryAsString(){
			return moveHistory.toString();
		}
		
		
		public int getMaxStackDepth() {
		    return moveHistory.getMaxDepth();
		}
	}
	
	
	
	//=====================
	// MazeManager Class
	//=====================
	/*    y     0      1    
	 * x  ------------------ 
	 *   |        |       |
	 * 0 |  "x,y" |       |
	 *   |--------|-------|
	 * 1 |        |       |
	 *   |        |       |
	 *   |--------|-------|
	*/
	public static class MazeManager{
		
		public MazeTile[][] grid;
		// width represent y axis
		// height represents x axis
		public int width, height;
		// create an array to store every agent, so type of array is Agent
		public Agent[] agents;
		// ?
		public CircularList rotatingRows;
		
		public MazeManager(int num) {
			this.width=6;
			this.height=6;
			this.grid= new MazeTile[height][width];
			this.agents= new Agent[num];
			this.rotatingRows = new CircularList();
			
			generateMaze();
		}
		
		public void generateMaze() {
			Random rand= new Random();
			for (int i= 0; i<height; i++) {
				//rotatingRows.add(i);
				for (int j=0; j<width; j++) {
					char type;
					
					//Simple random tile logic 
					int r= rand.nextInt(10);
					if (r<2) type ='W'; //20% WALL
					else if (r==2) type ='T'; // 10% TRAP
					else if (r==3) type ='P'; // 10% POWER-UP
					else type= 'E'; //60% EMPTY
					
					grid[i][j]= new MazeTile(i, j, type);
				}
			}
			
			//Place a goal
			int gx,gy;
			do {
				gx= rand.nextInt(height);
				gy= rand.nextInt(width);
			}while (grid[gx][gy].type!='E');
			grid[gx][gy].type='G';
		}
		
		
		
		// Shift all tiles in row circularly
		// corridors rotate every few turns to:
		public void rotateCorridor(int rowId) {
			CircularList old_list= new CircularList();
			
			for(int i=0; i<width; i++) {
				old_list.add(grid[rowId][i].type);
			}
			char[] newArray= old_list.rotate();
			
			for(int i=0;i<width; i++) {
				grid[rowId][i].type= newArray[i];
			}
			
		}
		
		public int isValidMove(int fromX, int fromY, String direction) {
			int currX= fromX;
			int currY= fromY;
			boolean isValid=true;
			
			switch(direction.toUpperCase()) {
				case "UP":
					if(currX==0) {
						isValid=false;
						break;
					}
					currX--;
					break;
				case "DOWN":
					if(currX==5) {
						isValid=false;
						break;
					}
					currX++;
					break;
				case "LEFT":
					if(currY==0) {
						isValid=false;
						break;
					}
					currY--;
					break;
				case "RIGHT":
					if(currY==5) {
						isValid=false;
						break;
					}
					currY++;
					break;
				default:
					System.out.println("Enter one of these 4 instruction: up, down,right, left");
					return 5;
			}
			MazeTile tile = grid[currX][currY];
			if (!isValid) {
				return 1;
			}else if (!tile.isTraversable()) {
				return 2;
			}else if (tile.hasAgent){
				return 3;
			}else {
				return 4;
			}
			
		}
		
		public MazeTile getTile(int x, int y) {
			return grid[x][y];
		}
		
		
		public void updateAgentLocation(Agent a, int oldX, int oldY) {
			MazeTile oldUpdated= getTile(oldX, oldY);
			oldUpdated.hasAgent = false;
			
			MazeTile newUpdated= getTile(a.currentX, a.currentY);
			newUpdated.hasAgent= true;
		}
		
		public void printMazeSnapshot() {
			for(int i=0;i<this.height;i++) {
				for(int j=0;j<this.width;j++) {
					if(getTile(i,j).type != 'E') {
						System.out.print(getTile(i,j).type);
						System.out.print("|");
					}else {
						System.out.print(" ");	
						System.out.print("|");
					}
				}
				System.out.println("");
			}
		}
		
	}
	
	//=====================
	// TurnManagger Class
	//=====================
	public static class TurnManager{
		public CircularQueue agentQueue;
		public int currentRound;
		
		public TurnManager(Agent[] agents) {
			agentQueue = new CircularQueue(agents.length);
			for(int i=0; i<agents.length; i++) {
				agentQueue.enqueue(agents[i]);
			}
			currentRound=1;
 		}
		
		public void advanceTurn() {
			Agent a= agentQueue.dequeue();
			agentQueue.enqueue(a); // put back at the end
			currentRound++;
		}
		
		public Agent getCurrentAgent() {
			return agentQueue.peek();
		}
		
		public boolean allAgentsFinished() {
			for (int i=0;i<agentQueue.size;i++){
				Agent a= agentQueue.queue[i];
				if(!a.hasReachedGoal) {
					return false;
				}
			}
			return true;
		}
		public void printTurnsList() {
			agentQueue.printQueue();
		}
		public void logTurnSummary() {
			for (int i =0; i <agentQueue.size; i++) {
				System.out.println("Agent " + agentQueue.queue[i].id + " at (" + agentQueue.queue[i].currentX + "," + agentQueue.queue[i].currentY + ")");
				if (agentQueue.queue[i].moveHistory.peek() != null) {
					System.out.println("Move history of Player"+ agentQueue.queue[i].id + "'s" + ": " + agentQueue.queue[i].getMoveHistoryAsString());
				}
				else {
					System.out.println("Move history stack of Player" + agentQueue.queue[i].id + " is empty.");
				}
			}
		}
	}
	
	//=====================
	// GameController Class
	//=====================
		
	public static class GameController{
		public MazeManager maze;
		public TurnManager turns;
		int maxTurns;
		int turnCount;
		int rowToRotate = 0;
		
		
		public GameController() {
			this.maxTurns=0;
			this.turnCount=0;
		}
		
		public void initializeGame(int numAgents) {
			Random rand= new Random();
			maze = new MazeManager(numAgents);
			
			// creating positions for every agent
			for(int i=0; i < numAgents; i++) {
				int gx,gy;
				do {
					gx= rand.nextInt(maze.height);
					gy= rand.nextInt(maze.width);
				}while (maze.grid[gx][gy].type!='E');
				
				maze.getTile(gx, gy).hasAgent=true;
				 
				Agent agent =new Agent(i, gx, gy);
				maze.agents[i]= agent;
				System.out.println("In the initial state position of " + i + ".th player is ("+gx + ", " + gy + ")" );				// adding agents in the maze
				char c = (char) ('0' + i);
				maze.grid[gx][gy].type=(char)c;
			}
			
			turns= new TurnManager(maze.agents);
			maxTurns= 100;
			turnCount= 0;
			maze.printMazeSnapshot();
		}
		
		public void runSimulation() {
			while(turnCount < maxTurns && !turns.allAgentsFinished()) {
				System.out.println("Round" + turnCount+ ":");
				turns.printTurnsList(); 
				Agent current= turns.getCurrentAgent();
				processAgentAction(current);
				System.out.println("After action: ");
				turns.logTurnSummary();
				turns.advanceTurn();
				
				Random rand = new Random();
				int rowToRotate = rand.nextInt(maze.height); // pick a row between 0 and height-1
				maze.rotateCorridor(rowToRotate);
				System.out.println("Round" + turnCount+ " is finished ");
				System.out.println("After round Rotated row id :" + rowToRotate + "\n");
				maze.printMazeSnapshot();
		        turnCount++;
			}
		    printFinalStatistics();
		}
		
		public void processAgentAction(Agent a) {
			String[] actions= {"MOVE","UNDO","USEPOWERUP"};
			Random rand = new Random();
			String action= actions[rand.nextInt(3)];
			
			if(action.equals("MOVE")) {
				String[] directions= {"UP", "DOWN","LEFT","RIGHT"};
				String direction= directions[rand.nextInt(4)];
				
				int oldX= a.currentX;
				int oldY= a.currentY;
				int flag = maze.isValidMove(oldX, oldY, direction);
				if(flag == 4) {
					a.move(direction);
					maze.updateAgentLocation(a, oldX, oldY);
					MazeTile newTile= maze.getTile(a.currentX, a.currentY);
					System.out.println("Player" + a.id +" tried to move " + direction);
					checkTileEffect(a, newTile);
					if(newTile.type=='G') {
						a.hasReachedGoal=true;
			            System.out.println("Agent " + a.id + " has reached the goal!");
					}
				}else if (flag==1){
			        System.out.println("Invalid Move: Player " + a.id + " tried to move to "+direction+" so out of the grid.");
				}else if (flag==2){
			        System.out.println("Invalid Move: Player " + a.id + " tried to move to "+direction+ " so wall tile.");
				}else if (flag==3){
			        System.out.println("Invalid Move: Player " + a.id + " tried to move to "+direction+" so there is an agent tile.");
				}else if (flag==5){
			        System.out.println("Invalid Move: Player " + a.id + " entered invalid instruction.");
				}
				
			}
			else if(action.equals("UNDO")){
				int oldX= a.currentX;
				int oldY= a.currentY;
				System.out.println("Player" + a.id+" tries to undo");
				a.backtrack();
				maze.updateAgentLocation(a, oldX, oldY);
				MazeTile newTile= maze.getTile(a.currentX, a.currentY);
				checkTileEffect(a, newTile);
			}
			else if(action.equals("USEPOWERUP")) {
				if( a.CollectedPowerUps !=0 ) {
					a.applyPowerUp();
					a.powerUpsUsed++;
					System.out.println("Player" + a.id+" used power up");
				}
				System.out.println("Player" + a.id+" tries to use power ups but it has not any power ups");
			}
			
		}
		
		public void checkTileEffect(Agent a, MazeTile tile) {
			if(tile.type=='T') {
				System.out.println("Agent " + a.id + " triggered a trap!");
				int oldX= a.currentX;
				int oldY= a.currentY;
				if (a.moveHistory.size() >= 2) {
				    a.backtrack();
				    a.backtrack();
				} else {
				    // maybe just one or none
				    a.backtrack();
				}
				a.trapsTriggered++;
				maze.updateAgentLocation(a, oldX, oldY);
		        
		        
			}else if(tile.type=='P') {
				a.CollectedPowerUps++;
		        System.out.println("Agent " + a.id + " picked up a power-up!");
			}else if (tile.type=='E') {
				System.out.println("Agent " + a.id + " is at an empty tile");
			}
		}
		

		public void printFinalStatistics() {
		    System.out.println("\n=== Simulation Ended ===");
		    System.out.println("Total Turns: " + turnCount);

		    int winnerId = -1;
		    int earliestGoal = Integer.MAX_VALUE;

		    for (Agent a : maze.agents) {
		        System.out.println("Agent " + a.id + ":");
		        System.out.println(" - Total Moves: " + a.totalMoves);
		        System.out.println(" - Backtracks: " + a.backtracks);
		        System.out.println(" - Traps Triggered: " + a.trapsTriggered);
		        System.out.println(" - Power-Ups Used: " + a.powerUpsUsed);
		        System.out.println(" - Max Stack Depth: " + a.getMaxStackDepth());
		        System.out.println(" - Reached Goal: " + a.hasReachedGoal);
		        System.out.println();

		        if (a.hasReachedGoal && a.totalMoves < earliestGoal) {
		            earliestGoal = a.totalMoves;
		            winnerId = a.id;
		        }
		    }

		    if (winnerId != -1) {
		        System.out.println("🎉 Agent " + winnerId + " is the WINNER!");
		    } else {
		        System.out.println("No agent reached the goal.");
		    }
		    logGameSummaryToFile("game_summary.txt");

		}

		public void logGameSummaryToFile(String filename) {
		    try (FileWriter writer = new FileWriter(filename)) {
		        writer.write("=== Final Game Summary ===\n");
		        writer.write("Total Turns: " + turnCount + "\n\n");

		        for (Agent a : maze.agents) {
		            writer.write("Agent " + a.id + ":\n");
		            writer.write(" - Total Moves: " + a.totalMoves + "\n");
		            writer.write(" - Backtracks: " + a.backtracks + "\n");
		            writer.write(" - Traps Triggered: " + a.trapsTriggered + "\n");
		            writer.write(" - Power-Ups Used: " + a.powerUpsUsed + "\n");
		            writer.write(" - Max Stack Depth: " + a.getMaxStackDepth() + "\n");
		            writer.write(" - Reached Goal: " + a.hasReachedGoal + "\n");
		            writer.write(" - Move History:\n" + a.getMoveHistoryAsString() + "\n\n");
		        }

		        writer.write("End of simulation.\n");
		    } catch (IOException e) {
		        System.out.println("Error writing file: " + e.getMessage());
		    }
		}

		


	}
	

	
	
	//=====================
	// MAIN Class
	//=====================
	public static void main(String[] args) {
	    GameController game = new GameController();
	    game.initializeGame(2);
	    game.runSimulation();
	}

	
	
	
}

















