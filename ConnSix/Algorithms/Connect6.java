import java.util.ArrayList;
import java.util.Random;

public class Connect6 {

	int ROW = 19;
	int COL = 19;
	
	static int Empty = 0;
	static int Candidate = -1;
	public int Red;
	public int Ai;
	public int opponent;	
	
	// Four direction: horizon, vertical, right-down diagonal, right-up diagonal.
	int[] dx = {1, 0, 1, 1};
	int[] dy = {0, 1, 1, -1};

	public Connect6(){
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
		return stones.getPosition();
	}
	
	private Boolean IsOutOfBounds(int x, int y) {
		return x < 0 || y < 0 || x >= ROW || y >= COL;
	}

	private Stone getRandomStone(int[][] playBoard){
		Stone stone = new Stone();
			do {
				stone.setStone((int) (Math.random() * 19), (int) (Math.random() * 19));			
			} while(playBoard[stone.x][stone.y] != Empty);

		return stone;
	}

	public String returnStringCoor(ConnectSix consix) {
		Stones stones;
		int[][] playBoard = new int[ROW][COL];

		for (int R = 0; R < ROW; R++) {
			System.out.printf("%2d ", ROW - R);
			for (int C = 0; C < COL; C++) {
				String stone = String.format("%c%02d", (char) ((C < 8) ? (C + 'A') : (C + 'A' + 1)), ROW - R);
				String temp = consix.getStoneAt(stone);
				if(temp.equals("EMPTY") == true)
					playBoard[R][C] = Empty;
				else if(temp.equals("WHITE") == true)
					playBoard[R][C] = 2;
				else if(temp.equals("BLACK") == true)
					playBoard[R][C] = 1;
				else if(temp.equals("RED") == true)
					playBoard[R][C] = Red;
				
				System.out.printf("[%3d]", playBoard[R][C]);
			}
			System.out.println("");
		}
		

		for(int i = 0 ; i < 2 ; i++){
			if(i % 2 == 0)
				stones = isPossibleConn6(CopyBoard(playBoard), Ai); // Check whether it is possible connect 6 stones
			else
				stones = isPossibleConn6Opponent(playBoard, opponent); // Check whether there are some cases that the opposite could connect 6 stones
			
			if(stones != null){
				if(stones.getSecondStone() != null)
					return Result(stones);
				else{
					playBoard[stones.getFirstStone().x][stones.getFirstStone().y] = Ai;
					stones.setSecondStone(getRandomStone(playBoard));
					return Result(stones);
				}
			}
		}
		
		System.out.println("No Stones -> Randomly");
		// Randomly Put
		stones = new Stones();
		
		for(int i = 0; i < 2; i++) {	
			if(i == 0){
				stones.setFirstStone(getRandomStone(playBoard));
				playBoard[stones.getFirstStone().x][stones.getFirstStone().y] = Ai;
			}
			else
				stones.setSecondStone(getRandomStone(playBoard));
		}

		return Result(stones);

	}
	
	private Stone FindOneStone(int [][] board, int player) {
		for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (board[i][j] != Empty)
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
						else if(board[R + i * dx[d]][C + i * dy[d]] != Empty) {
							playerStone = 0;
							break;
						}
					}
										
					if(playerStone >= 4) {
						int putcnt = 0;
						for (int i = 0; i < 6; i++) {
	                        if (board[R + i*dx[d]][C + i*dy[d]] == Empty) {
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
							else if(board[R + i * dx[d]][C + i * dy[d]] != Empty) {
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

			board[first.x][first.y] = Empty;
			if(second != null)
				board[second.x][second.y] = Empty;
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
						else if(playboard[R + i * dx[d]][C + i * dy[d]] != Empty) {
							opponentStone = 0;
							break;
						}
					}
										
					if(opponentStone >= 4) {
						
						Stone[] stones = new Stone[2];
						int putcnt = 0;

						for (int i = 0; i < 6; i++) {
	                        if (playboard[R + i*dx[d]][C + i*dy[d]] == Empty) {
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

}