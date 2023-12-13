import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractMap;
import java.util.Random;

public class Connect6 {

	int ROW = 19;
	int COL = 19;
	
	int MAX_DEPTH = 2;
	int EMPTY = 0;
	int BLACK = 1;
	int WHITE = 2;
	int Candidate = 5;
	public int Red = 3;
	public int Ai;
	public int opponent;
	
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

		putStones(redStones, Red);
	}

	private void printBoard(int [][] board){
		System.out.println();
		for(int i = 0; i < ROW; i++){
			for(int j = 0; j < COL; j++){
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
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
		putStones(stones.getPosition(), Ai);	
		return stones.getPosition();
	}
	
	private Boolean IsOutOfBounds(int x, int y) {
		return x < 0 || y < 0 || x >= ROW || y >= COL;
	}

	private Stone getRandomStone(int[][] playBoard){
		Stone stone = new Stone();
			do {
				stone.setStone((int) (Math.random() * 19), (int) (Math.random() * 19));			
			} while(playBoard[stone.x][stone.y] != EMPTY);

		return stone;
	}

	public void putStones(String stonesArr, int color){
		if(stonesArr == null)
			return;

		String[] stones = stonesArr.split(":");

		for(String stone : stones){
			char charValue = stone.charAt(0);
			int numericValue = Integer.parseInt(stone.substring(1));

			int C = (int) ((charValue < 'I') ? (charValue - 'A') : (charValue - 'A' - 1));
			int R = ROW - numericValue;

			System.out.println("C: " + C + ", R: " + R);

			playBoard[R][C] = color;
		}
	}

	public String returnStringCoor(String opponentStone) {
		putStones(opponentStone, opponent);

		int[][] board = CopyBoard(playBoard);

		Stones stones;
		

		for(int i = 0 ; i < 2 ; i++){
			if(i % 2 == 0)
				stones = isPossibleConn6(board, Ai); // Check whether it is possible connect 6 stones
			else
				stones = isPossibleConn6Opponent(board, opponent); // Check whether there are some cases that the opposite could connect 6 stones
			
			if(stones != null){
				if(stones.getSecondStone() != null)
					return Result(stones);
				else{
					board[stones.getFirstStone().x][stones.getFirstStone().y] = Ai;
					stones.setSecondStone(getRandomStone(board));
					return Result(stones);
				}
			}
		}
		
		System.out.println("End of checking possibilites");
		stones = findBestStones(board, Ai, 5);
		System.out.println("End of findBestStones");

		return Result(stones);

	}
	
	private Stone FindOneStone(int [][] board, int player) {
		for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (board[i][j] != EMPTY)
                    continue;

				Stone stone = new Stone();
                stone.setStone(i, j);
                return stone;
            }
        }
		
		return null;
	}
	
	private Stones isPossibleConn6(int [][] board, int player) {
		Stones stones = new Stones();

		System.out.println("player = " + player);
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

			Stones candiate = new Stones(stoneList.get(0).getFirstStone(), null);
			stoneCandidateList.add(candiate);

			candiate = new Stones(stoneList.get(0).getSecondStone(), null);
			stoneCandidateList.add(candiate);
		}
		else if(stoneList.size() == 2){
			Stones[] candiateList = new Stones[4];

			candiateList[0] = new Stones(stoneList.get(0).getFirstStone(), stoneList.get(1).getFirstStone());
			candiateList[1] = new Stones(stoneList.get(0).getSecondStone(), stoneList.get(1).getFirstStone());
			candiateList[2] = new Stones(stoneList.get(0).getFirstStone(), stoneList.get(1).getSecondStone());
			candiateList[3] = new Stones(stoneList.get(0).getSecondStone(), stoneList.get(1).getSecondStone());

			for(int i = 0; i < 4; i++){
				stoneCandidateList.add(candiateList[i]);
			}
		}

		stoneCandidateList = getPossibleDefenceStones(board, 3 - opponent, stoneCandidateList);

		if(stoneCandidateList.size() > 0){
			Random random = new Random();
			int randomIndex = random.nextInt(stoneCandidateList.size());

			return stoneCandidateList.get(randomIndex);
		}

		return null;
		
	}

	private Stones isPossibleConn6Opponent(int [][] board, int opponent) {
		int[][] playboard = CopyBoard(board);

		ArrayList<Stones> stoneList = new ArrayList<>();
		
		System.out.println("opponent = " + opponent);
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
	                        	playboard[R + i*dx[d]][C + i*dy[d]] = Candidate;

								stones[putcnt] = new Stone();
	                        	stones[putcnt++].setStone(R + i*dx[d], C + i*dy[d]);
								
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
        System.out.println("Start of findBestStones!");
        Stones bestStones = new Stones();
        int bestValue = (player == BLACK) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

		List<Map.Entry<Stones, Integer>> legalStonesArr = getLegalStones(board, player, size);

		for (Map.Entry<Stones, Integer> legalStones : legalStonesArr) {
            System.out.println("Stones in LegalStones: " + legalStones.getKey().getPosition());
				applyStones(board, legalStones.getKey(), player);
				int boardValue = alphabeta(board, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, player, 20, null);
				undoStones(board, legalStones.getKey());
				if ((player == BLACK && boardValue > bestValue) || (player == WHITE && boardValue < bestValue)) {
					bestStones = legalStones.getKey();
					bestValue = boardValue;
				}
        }
		
		return bestStones;
    }

	private int alphabeta(int[][] board, int depth, int alpha, int beta, int player, int size, Stones putStones) {
        if (depth == 0 || isTerminal(board)) {
			System.out.println("done alphabeta " + putStones.getPosition());
			return scoreofStones(board, putStones, 3 - player);
        }

        if (player == BLACK) {
            int value = Integer.MIN_VALUE;
			List<Map.Entry<Stones, Integer>> legalStonesArr = getLegalStones(board, BLACK, size);
            for (Map.Entry<Stones, Integer> legalStones : legalStonesArr) {
				System.out.println("alphabeta Stones in LegalStones: " + legalStones.getKey().getPosition());
                applyStones(board, legalStones.getKey(), BLACK);
                value = Math.max(value, alphabeta(board, depth - 1, alpha, beta, 3 - player, size, legalStones.getKey()));
                undoStones(board, legalStones.getKey());
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break; // beta cut-off
                }
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            List<Map.Entry<Stones, Integer>> legalStonesArr = getLegalStones(board, WHITE, size);
            for (Map.Entry<Stones, Integer> legalStones : legalStonesArr) {
                applyStones(board, legalStones.getKey(), WHITE);
                value = Math.min(value, alphabeta(board, depth - 1, alpha, beta, player, size, legalStones.getKey()));
                undoStones(board, legalStones.getKey());
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break; // alpha cut-off
                }
            }
            return value;
        }
	}

	private List<Map.Entry<Stone, Integer>> getLegalStone(int[][] board, int player, int size){
		int [][] temp = CopyBoard(board);

		int[] dx = {1, 0, 1, 1, 0, -1, -1, -1};
		int[] dy = {0, 1, 1, -1, -1, -1, 0, 1};

		Map<Stone, Integer> stoneMap = new HashMap<Stone, Integer>();

		for(int R = 0; R < ROW; R++){
			for(int C = 0; C < COL; C++){

				if(temp[R][C] != Ai && temp[R][C] != opponent)
					continue;

				for(int d = 0; d < 8; d++){
					for(int i = 0; i < 6; i++){
						if(IsOutOfBounds(R + i * dx[d], C + i * dy[d])){
							break;
						}

						if(temp[R + i * dx[d]][C + i * dy[d]] == EMPTY){
							Stone stone = new Stone(R + i * dx[d], C + i * dy[d]);
							stoneMap.put(stone, evaluate(board, stone, player));
							temp[R + i * dx[d]][C + i * dy[d]] = Candidate;
						}
					}
				}
			}
		}

		List<Map.Entry<Stone, Integer>> sortedEntries = new ArrayList<>(stoneMap.entrySet());
        sortedEntries.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

		return sortedEntries.subList(0, size - 1);
	}

	private List<Map.Entry<Stones, Integer>> getLegalStones(int[][] board, int player, int size) {
		Map<Stones, Integer> stonesMap = new HashMap<Stones, Integer>();
		List<Map.Entry<Stone, Integer>> legalStoneArr = getLegalStone(board, player, size);
		
		for(int i = 0; i < legalStoneArr.size(); i++){
			for(int j = i + 1; j < legalStoneArr.size(); j++){
				Stones stones = new Stones(legalStoneArr.get(i).getKey(), legalStoneArr.get(j).getKey());
				stonesMap.put(stones, scoreofStones(board, stones, player));
			}
		}

        List<Map.Entry<Stones, Integer>> legalStonesArr = new ArrayList<>(stonesMap.entrySet());
        legalStonesArr.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

		return legalStonesArr.subList(0, size - 1);
    }

	private int scoreofStones(int[][] board, Stones stones, int player){
		if(stones == null)
			return -1;

		int value = 0;
		value += evaluate(board, stones.getFirstStone(), player);
		applyStone(board, stones.getFirstStone(), player);
		value += evaluate(board, stones.getSecondStone(), player);
		undoStone(board, stones.getFirstStone());
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

	private int evaluate(int[][] board, Stone currStone, int player) {
		System.out.println("Player: " + player);


		int[] stoneValue = {1, 3, 6, 12, 30, 100};
		int totalValue = 0;
		int opponent = 3 - player;

		int[][] tempBoard = CopyBoard(board);
		tempBoard[currStone.x][currStone.y] = player;

		printBoard(tempBoard);

		for(int d = 0 ; d < 4 ; d++){
			for(int i = 0 ; i < 6 ; i ++){
				int startingX = currStone.x - i * dx[d];
				int startingY = currStone.y - i * dy[d];
				if(IsOutOfBounds(startingX, startingY) || IsOutOfBounds(startingX + 5 * dx[d], startingY + 5 * dy[d])){
					continue;
				}
				int playerStone = 0;
				int opponentStone = 0;
				boolean isPossibleConn6 = true; // in terms of player
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
					else if(tempBoard[currX][currY] == Red){
						break; // It is not possible for being conn6 for both of player and opponent.
					}

					if(isPossibleConn6 && playerStone >= 4){
						totalValue += 5000;
					}
					if(playerStone == 1){ 
						//System.out.println("playerStone이 1"); 
						//System.out.println("OpponentStone:" + opponentStone); 
						totalValue += stoneValue[opponentStone];}
					if(opponentStone == 0){ 
						//System.out.println("opponentStone이 0"); 
						//System.out.println("PlayerStone:" + playerStone);
						totalValue += stoneValue[playerStone];}
				}
			}
		}
		return totalValue;
    }

}