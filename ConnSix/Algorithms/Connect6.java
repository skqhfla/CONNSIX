import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractMap;
import java.time.Duration;
import java.time.Instant;

public class Connect6 {

	int ROW = 19;
	int COL = 19;
	
	int MAX_DEPTH = 4;
	int EMPTY = 0;
	int BLACK = 1;
	int WHITE = 2;
	int RED = 3;
	int CANDIDATE = 4;
	
	public int AI;
	public int opponent;

	int setOfStones = 5;
	int numOfBestStone = 20;
	int checktimeout = 26;

	Instant startTime;
	boolean isTimeout = false;
	
	// Four direction: horizon, vertical, right-down diagonal, right-up diagonal.
	int[] dx = {1, 0, 1, 1};
	int[] dy = {0, 1, 1, -1};
	
	int[][] playBoard = new int[ROW][COL];

	public Connect6(String redStones){
		for(int i = 0; i < 19; i++){
			for(int j = 0; j < 19; j++){
				playBoard[i][j] = EMPTY;
			}
		}

		putStones(redStones, RED);
	}

	private void printBoard(int [][] board){
		System.out.println();
		for(int i = 0; i < ROW; i++){
			System.out.printf("%2d ", 19 - i);
			for(int j = 0; j < COL; j++){
				System.out.printf("[%1d]", board[i][j]);
			}
			System.out.println();
		}
		System.out.print("   ");
		for (int o = 0; o < 20; o++) {
			if(o == 8) o++;
			System.out.printf(" %c ", 65 + o);
		}
		System.out.println("\n");

	}
	
	private int[][] CopyBoard(int [][] originBoard) {
		int[][] board = new int[ROW][COL];
		
		for(int x = 0; x < ROW; x++) {
			for(int y = 0; y < COL; y++) {
				board[x][y] = originBoard[x][y];
			}
		}
		
		return board;
	}
	
	private String Result(Stones stones) {	
		putStones(stones.getPosition(), AI);	
		return stones.getPosition();
	}
	
	private Boolean IsOutOfBounds(int x, int y) {
		return x < 0 || y < 0 || x >= ROW || y >= COL;
	}

	public void putStones(String stonesArr, int color){
		if(stonesArr == null)
			return;

		String[] stones = stonesArr.split(":");

		for(String stone : stones){
			try{
				char charValue = stone.charAt(0);
				int numericValue = Integer.parseInt(stone.substring(1));

				int C = (int) ((charValue < 'I') ? (charValue - 'A') : (charValue - 'A' - 1));
				int R = ROW - numericValue;

				//System.out.println("C: " + C + ", R: " + R);

				playBoard[R][C] = color;
			}
			catch (Exception e){
				continue;
			}
			
		}
	}

	private void checkTime(){
		Instant currentTime = Instant.now();
        Duration elapsedDuration = Duration.between(startTime, currentTime);
        long elapsedSeconds = elapsedDuration.getSeconds();
        if (elapsedSeconds >= checktimeout){
			System.out.println("----timeout: " + elapsedSeconds + " ----");
			isTimeout = true;
		}
	}

	public String returnStringCoor(String opponentStone) {
		isTimeout = false;
		startTime = Instant.now();
		putStones(opponentStone, opponent);

		int[][] board = CopyBoard(playBoard);

		Stones stones;
		

		for(int i = 0 ; i < 2 ; i++){
			if(i % 2 == 0)
				stones = isPossibleConn6(board, AI); // Check whether it is possible connect 6 stones
			else
				stones = isPossibleConn6Opponent(board, opponent); // Check whether there are some cases that the opposite could connect 6 stones
			
			if(stones != null){
				if(stones.getSecondStone() != null)
					return Result(stones);
				else{
					board[stones.getFirstStone().x][stones.getFirstStone().y] = AI;
					stones.setSecondStone(getLegalStone(board, AI, 1).get(0).getKey());
					return Result(stones);
				}
			}
		}
		
		// System.out.println("End of checking possibilites");
		stones = findBestStones(board, AI, setOfStones);
		// System.out.println("End of findBestStones");

		Instant currentTime = Instant.now();
        Duration elapsedDuration = Duration.between(startTime, currentTime);
        long elapsedSeconds = elapsedDuration.getSeconds();

		System.out.println("------ end time " + elapsedSeconds + "-----");

		return Result(stones);

	}
	
	private Stones isPossibleConn6(int [][] board, int player) {
		Stones stones = new Stones();

		// System.out.println("player = " + player);
		for(int d = 0; d < 4; d++) {
			for(int R = 0; R < ROW; R++) {
				for(int C = 0; C < COL; C++) {
					
					if(IsOutOfBounds(R + 5 * dx[d], C + 5 * dy[d])){
						continue;
					}

					int playerStone = 0;

					for(int i = 0; i < 6; i++) {

						if(board[R + i * dx[d]][C + i * dy[d]] == player){
							playerStone++;
						}
						else if(board[R + i * dx[d]][C + i * dy[d]] != EMPTY) {
							playerStone = 0;
							break;
						}
					}
										
					if(playerStone >= 4) {
						int putcnt = 0;
						for (int i = 0; i < 6; i++) {
	                        if (board[R + i*dx[d]][C + i*dy[d]] == EMPTY) {
	                        	board[R + i*dx[d]][C + i*dy[d]] = player;

								if (putcnt++ == 0)
									stones.setFirstStone(R + i*dx[d], C + i*dy[d]);
								else
									stones.setSecondStone(R + i*dx[d], C + i*dy[d]);								
	                        }
	                    }
						
						if(putcnt != 0)
							return stones;
					}
				}
			}
		}
		
		return null;
	}

	private ArrayList<Stones> getPossibleDefenceStones(int [][] board, int player, ArrayList<Stones> stoneList){
		int opponent = 3 - player;
		boolean canDefence = true;

		for(int idx = 0; idx < stoneList.size(); idx++){
			Stone first = stoneList.get(idx).getFirstStone();
			Stone second = stoneList.get(idx).getSecondStone();

			if(first == null)
				continue;

			board[first.x][first.y] = player;
			if(second != null)
				board[second.x][second.y] = player;

			canDefence = true;

			for(int d = 0; d < 4; d++) {
				for(int R = 0; R < ROW; R++) {
					for(int C = 0; C < COL; C++) {
						
						if(IsOutOfBounds(R + 5 * dx[d], C + 5 * dy[d])){
							continue;
						}

						int opponentStone = 0;

						for(int i = 0; i < 6; i++) {

							if(board[R + i * dx[d]][C + i * dy[d]] == opponent){
								opponentStone++;
							}
							else if(board[R + i * dx[d]][C + i * dy[d]] != EMPTY) {
								opponentStone = 0;
								break;
							}
						}
											
						if(opponentStone >= 4) {
							canDefence = false;
							break;
						}
					}

					if(!canDefence)
						break;
				}
				
				if(!canDefence){
					stoneList.remove(idx);
					idx--;
					break;
				}
			}

			board[first.x][first.y] = EMPTY;
			if(second != null)
				board[second.x][second.y] = EMPTY;
		}

		return stoneList;
	}

	private Stones ChooseFromCandidate(int [][] board, int opponent, ArrayList<Stones> stoneList){
		ArrayList<Stones> stoneCandidateList = new ArrayList<>();

		if(stoneList.size() == 1){

			Stones candidate;
			
			if(stoneList.get(0).getFirstStone() != null){
				candidate = new Stones(stoneList.get(0).getFirstStone(), null);
				stoneCandidateList.add(candidate);
			}				

			if(stoneList.get(0).getSecondStone() != null){
				candidate = new Stones(stoneList.get(0).getSecondStone(), null);
				stoneCandidateList.add(candidate);
			}
			
			
		}
		else if(stoneList.size() == 2){
			Stones[] candidateList = new Stones[4];

			candidateList[0] = new Stones(stoneList.get(0).getFirstStone(), stoneList.get(1).getFirstStone());
			candidateList[1] = new Stones(stoneList.get(0).getSecondStone(), stoneList.get(1).getFirstStone());
			candidateList[2] = new Stones(stoneList.get(0).getFirstStone(), stoneList.get(1).getSecondStone());
			candidateList[3] = new Stones(stoneList.get(0).getSecondStone(), stoneList.get(1).getSecondStone());

			for(int i = 0; i < 4; i++){
				stoneCandidateList.add(candidateList[i]);
			}
		}

		stoneCandidateList = getPossibleDefenceStones(board, 3 - opponent, stoneCandidateList);

		double bestValue = 0;
		Stones bestStones = null;

		if(stoneCandidateList.size() == 1)
			return stoneCandidateList.get(0);

		for(Stones stones : stoneCandidateList){
			if(stones.getSecondStone() != null){
				applyStones(board, stones, 3 - opponent);
				double boardValue = alphabeta(board, MAX_DEPTH, Double.MIN_VALUE, Double.MAX_VALUE, 3 - opponent, numOfBestStone, null);
				undoStones(board, stones);
				if (boardValue > bestValue) {
					bestStones = stones;
					bestValue = boardValue;
				}

				checkTime();

				if(isTimeout)
					break;
			}
			else{
				double boardValue = evaluate(board, stones.getFirstStone(), 3 - opponent);
				if (boardValue > bestValue) {
					bestStones = stones;
					bestValue = boardValue;
				}
			}
			
		}

		return bestStones;
		
	}

	private Stones isPossibleConn6Opponent(int [][] board, int opponent) {
		int[][] playboard = CopyBoard(board);

		ArrayList<Stones> stoneList = new ArrayList<>();
		
		// System.out.println("opponent = " + opponent);
		for(int d = 0; d < 4; d++) {
			for(int R = 0; R < ROW; R++) {
				for(int C = 0; C < COL; C++) {
					
					if(IsOutOfBounds(R + 5 * dx[d], C + 5 * dy[d])){
						continue;
					}

					int opponentStone = 0;

					for(int i = 0; i < 6; i++) {

						if(playboard[R + i * dx[d]][C + i * dy[d]] == opponent){
							opponentStone++;
						}
						else if(playboard[R + i * dx[d]][C + i * dy[d]] != EMPTY) {
							opponentStone = 0;
							break;
						}
					}
										
					if(opponentStone >= 4) {
						
						Stone[] stones = new Stone[2];
						int putcnt = 0;

						for (int i = 0; i < 6; i++) {
	                        if (playboard[R + i*dx[d]][C + i*dy[d]] == EMPTY) {
	                        	playboard[R + i*dx[d]][C + i*dy[d]] = CANDIDATE;

								stones[putcnt] = new Stone();
	                        	stones[putcnt++].setStone(R + i*dx[d], C + i*dy[d]);
								
								System.out.println("Find threat (" + (R + i*dx[d]) + ", " + (C + i*dy[d]) + ")");
	                        }
	                    }

						Stones stoneSet = new Stones(stones[0], stones[1]);

						stoneList.add(stoneSet);

						if(stoneList.size() == 2)
							break;
					}
				}
			}
		}

		if(stoneList.size() != 0)
			return ChooseFromCandidate(board, opponent, stoneList);

		return null;
		
	}

	private Stones findBestStones(int [][] board, int player, int size){
        // System.out.println("Start of findBestStones!");
        Stones bestStones = new Stones();
        double bestValue = (player == BLACK) ? Double.MIN_VALUE : Double.MAX_VALUE;

		List<Map.Entry<Stones, Double>> legalStonesArr = getLegalStones(board, player, size);

		for (Map.Entry<Stones, Double> legalStones : legalStonesArr) {
            // System.out.println("Stones in LegalStones: " + legalStones.getKey().getPosition());
				applyStones(board, legalStones.getKey(), player);
				double boardValue = alphabeta(board, MAX_DEPTH, Double.MIN_VALUE, Double.MAX_VALUE, player, numOfBestStone, null);
				undoStones(board, legalStones.getKey());
				if ((player == BLACK && boardValue > bestValue) || (player == WHITE && boardValue < bestValue)) {
					bestStones = legalStones.getKey();
					bestValue = boardValue;
				}

				checkTime();

				if(isTimeout)
					break;
        }
		
		return bestStones;
    }

	private double alphabeta(int[][] board, int depth, double alpha, double beta, int player, int size, Stones putStones) {
        if (depth == 0 || isTerminal(board)) {
			return scoreofStones(board, putStones, 3 - player);
        }

        if (player == BLACK) {
            double value = Double.MIN_VALUE;
			List<Map.Entry<Stones, Double>> legalStonesArr = getLegalStones(board, BLACK, size);
            for (Map.Entry<Stones, Double> legalStones : legalStonesArr) {
				// System.out.println("alphabeta Stones in LegalStones: " + legalStones.getKey().getPosition());
                applyStones(board, legalStones.getKey(), BLACK);
                value = Math.max(value, alphabeta(board, depth - 1, alpha, beta, 3 - player, size, legalStones.getKey()));
                undoStones(board, legalStones.getKey());
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break; // beta cut-off
                }

				checkTime();

				if(isTimeout)
					break;
            }
            return value;
        } else {
            double value = Double.MAX_VALUE;
            List<Map.Entry<Stones, Double>> legalStonesArr = getLegalStones(board, WHITE, size);
            for (Map.Entry<Stones, Double> legalStones : legalStonesArr) {
                applyStones(board, legalStones.getKey(), WHITE);
                value = Math.min(value, alphabeta(board, depth - 1, alpha, beta, player, size, legalStones.getKey()));
                undoStones(board, legalStones.getKey());
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break; // alpha cut-off
                }

				checkTime();

				if(isTimeout)
					break;
            }
            return value;
        }
	}

	private List<Map.Entry<Stone, Double>> getLegalStone(int[][] board, int player, int size){
		int [][] temp = CopyBoard(board);

		int[] dx = {1, 0, 1, 1, 0, -1, -1, -1};
		int[] dy = {0, 1, 1, -1, -1, -1, 0, 1};

		Map<Stone, Double> stoneMap = new HashMap<Stone, Double>();

		for(int R = 0; R < ROW; R++){
			for(int C = 0; C < COL; C++){

				/*
				if(temp[R][C] == EMPTY){
						Stone stone = new Stone(R, C);
						stoneMap.put(stone, evaluate(board, stone, player));
						temp[R][C] = CANDIDATE;
				} */
				
				for(int d = 0; d < 8; d++){
					for(int i = 0; i < 6; i++){
						if(IsOutOfBounds(R + i * dx[d], C + i * dy[d])){
							break;
						}

						if(temp[R + i * dx[d]][C + i * dy[d]] == EMPTY){
							Stone stone = new Stone(R + i * dx[d], C + i * dy[d]);
							stoneMap.put(stone, evaluate(board, stone, player));
							temp[R + i * dx[d]][C + i * dy[d]] = CANDIDATE;
						}
					}
				}
			}
		}

		List<Map.Entry<Stone, Double>> sortedEntries = new ArrayList<>(stoneMap.entrySet());
        sortedEntries.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

		return sortedEntries.subList(0, size);
	}

	private List<Map.Entry<Stones, Double>> getLegalStones(int[][] board, int player, int size) {
		Map<Stones, Double> stonesMap = new HashMap<Stones, Double>();
		List<Map.Entry<Stone, Double>> legalStoneArr = getLegalStone(board, player, size);
		
		for(int i = 0; i < legalStoneArr.size(); i++){
			for(int j = i + 1; j < legalStoneArr.size(); j++){
				Stones stones = new Stones(legalStoneArr.get(i).getKey(), legalStoneArr.get(j).getKey());
				stonesMap.put(stones, scoreofStones(board, stones, player));
			}
		}

        List<Map.Entry<Stones, Double>> legalStonesArr = new ArrayList<>(stonesMap.entrySet());
        legalStonesArr.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

		return legalStonesArr.subList(0, size);
    }

	private double scoreofStones(int[][] board, Stones stones, int player){
		if(stones == null)
			return -1;

		double value = 0;
		// System.out.println("---------------------------");
		value += evaluate(board, stones.getFirstStone(), player);
		applyStone(board, stones.getFirstStone(), player);
		value += evaluate(board, stones.getSecondStone(), player);
		undoStone(board, stones.getFirstStone());
		// System.out.println(stones.getPosition() + " = " + value);
		// System.out.println("---------------------------");
		return value;
	}

	private void applyStone(int[][] board, Stone stone, int player) {
		board[stone.x][stone.y] = player;
    }

	private void applyStones(int[][] board, Stones stones, int player) {
		Stone firstStone = stones.getFirstStone();
		Stone secondStone = stones.getSecondStone();

		board[firstStone.x][firstStone.y] = player;
		board[secondStone.x][secondStone.y] = player;
    }

	private void undoStone(int[][] board, Stone stone) {
		board[stone.x][stone.y] = EMPTY;
    }

    private void undoStones(int[][] board, Stones stones) {
		Stone firstStone = stones.getFirstStone();
		Stone secondStone = stones.getSecondStone();

		board[firstStone.x][firstStone.y] = EMPTY;
		board[secondStone.x][secondStone.y] = EMPTY;
    }

	private boolean isTerminal(int[][] board) {
		// 흑돌과 백돌 모두에 대해 승리 조건을 검사
		return checkLines(board, BLACK) || checkLines(board, WHITE);
	}
	
	private boolean checkLines(int[][] board, int player) {
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				if (board[i][j] == player) {
					for(int d = 0; d < 4; d++){
						if(checkExactStones(board, i, j, dx[d], dy[d], player))
							return true;
					}
				}
			}
		}

		return false;
    }

	private boolean checkExactStones(int[][] board, int x, int y, int dx, int dy, int player) {
        int count = 0;
        while (x >= 0 && x < ROW && y >= 0 && y < COL && board[x][y] == player) {
            count++;
            x += dx;
            y += dy;
        }
		
        return count > 5;
    }

	private double evaluate(int[][] board, Stone currStone, int player) {
		// System.out.println("Player: " + player);


		int[] stoneValue = {0, 1, 3, 10, 20, 50, 100};
		double weight = 1.7;
		double totalValue = 0;
		int opponent = 3 - player;

		int[][] tempBoard = CopyBoard(board);
		tempBoard[currStone.x][currStone.y] = player;

		//System.out.println("----------------------------------------");
		// System.out.println("[Current Position] " + currStone.getPosition() + "\n");
		//printBoard(tempBoard);

		for(int d = 0 ; d < 4 ; d++){
			//System.out.println("[Currect Direction] " + d);
			for(int i = 0 ; i < 6 ; i ++){
				int startingX = currStone.x - i * dx[d];
				int startingY = currStone.y - i * dy[d];
				if(IsOutOfBounds(startingX, startingY) || IsOutOfBounds(startingX + 5 * dx[d], startingY + 5 * dy[d])){
					continue;
				}
				int playerStone = 0;
				int opponentStone = 0;
				boolean isPossibleConn6 = true; // in terms of player
				Stone baseStone = new Stone(startingX, startingY);
				// System.out.println("[Current Base Position]: " + baseStone.getPosition());

				for(int j = 0 ; j < 6 ; j++){
					int currX = startingX + j * dx[d];
					int currY = startingY + j * dy[d];
					
					if(tempBoard[currX][currY] == player){
						playerStone++;
					}
					else if(tempBoard[currX][currY] == opponent){
						opponentStone++;
						isPossibleConn6 = false;
					}
					else if(tempBoard[currX][currY] == RED){
						playerStone = 0;
						opponentStone = 0;
						isPossibleConn6 = false;
						break; // It is not possible for being conn6 for both of player and opponent.
					}
					else if(tempBoard[currX][currY] == CANDIDATE){
						isPossibleConn6 = false; // because the possiblity of conn6 is already considered.
					}
				}


				if(isPossibleConn6 && (playerStone == 4)){
					totalValue += 10000;
					// System.out.println("PlayerStone can be Connect6! Add 10,000. | Total Value: " + totalValue);
					for(int k = 0 ; k < 6 ; k++){
						int currX = startingX + k * dx[d];
						int currY = startingY + k * dy[d];
						if(tempBoard[currX][currY] == EMPTY){
							tempBoard[currX][currY] = CANDIDATE;
						}
					}
				}
				if(playerStone == 1){ 
					// System.out.println("playerStone Count: 1 | OpponentStone Count: " + opponentStone); 
					totalValue += stoneValue[opponentStone] * weight;}
				if(opponentStone == 0){ 
					// System.out.println("opponentStone Count: 0 | playerStone Count: " + playerStone); 
					totalValue += stoneValue[playerStone];}
			}
		}
		// System.out.println("The Score: " + totalValue);
		return totalValue;
    }

}