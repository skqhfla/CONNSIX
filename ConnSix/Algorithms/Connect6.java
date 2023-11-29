import java.util.ArrayList;

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
	
	private void printBoard(int [][] board) {
		for(int R = 0; R < ROW; R++) {
			for(int C = 0; C < COL; C++) {
				System.out.printf("[%3d]", board[R][C]);
			}
			System.out.println();
		}
	}
	
	private String Result(Stone[] stones) {		
		return stones[0].getPosition() + ":" + stones[1].getPosition();
	}
	
	private Boolean IsOutOfBounds(int x, int y) {
		return x < 0 || y < 0 || x >= ROW || y >= COL;
	}

	public String returnStringCoor(ConnectSix consix) {
		Stone[] stones;
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
		

		// Check whether it is possible connect 6 stones
		stones = isPossibleConn6(CopyBoard(playBoard), Ai);
		if(stones != null)
			return Result(stones);
		
		// Check whether there are some cases that the opposite could connect 6 stones
		// ArrayList<Stones> stoneList = isPossibleConn6Opponent(CopyBoard(playBoard), opponent);
		// if(stoneList != null){
		// 	Stones stonesSet = stoneList.get(0);
		// 	stones = new Stone[2];
		// 	stones[0] = stonesSet.getOneStone();
		// 	stones[1] = stonesSet.getTwoStone();
		// 	return Result(stones);
		// }
			
		
		
		System.out.println("No Stones -> Randomly");
		
		stones = new Stone[2];
		
		for(int i = 0; i < 2; i++)
			stones[i] = new Stone();
		
		for(int i = 0; i < 2; i++) {
			do {
				stones[i].setStone((int) (Math.random() * 19), (int) (Math.random() * 19));			
			} while(consix.getStoneAt(stones[i].getPosition()).equals("EMPTY") != true && playBoard[stones[i].x][stones[i].y] != Empty);
			playBoard[stones[i].x][stones[i].y] = Ai;
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
	
	private Stone[] isPossibleConn6(int [][] board, int player) {
		Stone[] stones = new Stone[2];
		System.out.println("player = " + player);
		for(int d = 0; d < 4; d++) {
			for(int R = 0; R < ROW - 5 ; R++) {
				for(int C = 0; C < COL - 5 ; C++) {
					
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
						System.out.println("Stone = " + playerStone);
						int putcnt = 0;
						for (int i = 0; i < 6; i++) {
	                        if (board[R + i*dx[d]][C + i*dy[d]] == Empty) {
	                        	board[R + i*dx[d]][C + i*dy[d]] = player;
								System.out.println("putcnt = " + putcnt);
								stones[putcnt] = new Stone();
	                        	stones[putcnt++].setStone(R + i*dx[d], C + i*dy[d]);
								
	                        }
	                    }
						
						if(putcnt == 2)
							return stones;
						
						stones[putcnt] = FindOneStone(board, player);
						

						// Two case: 1. There is no possible connect6. 2. There is no possible to loaction for FindOneStone
						if(stones[putcnt] == null) {
							System.out.println("no more location to locate additional stone.");
							// Since the combination of two stones in stones is impossible, so make the location of stone[0] as empty and go to the next loop.
							board[stones[0].x][stones[0].y] = Empty; 
							stones[0] = null;
						}
						else {
							return stones;
						}
					}
				}
			}
		}
		
		return null;
	}

	private ArrayList<Stones> isPossibleConn6Opponent(int [][] board, int opponent) {
		ArrayList<Stones> stoneList = new ArrayList<>();
		
		System.out.println("opponent = " + opponent);
		for(int d = 0; d < 4; d++) {
			for(int R = 0; R < ROW - 5 ; R++) {
				for(int C = 0; C < COL - 5 ; C++) {
					
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
						
						Stone[] stones = new Stone[2];
						int putcnt = 0;

						for (int i = 0; i < 6; i++) {
	                        if (board[R + i*dx[d]][C + i*dy[d]] == Empty) {
	                        	board[R + i*dx[d]][C + i*dy[d]] = Candidate;

								System.out.println("putcnt = " + putcnt);
								stones[putcnt] = new Stone();
	                        	stones[putcnt++].setStone(R + i*dx[d], C + i*dy[d]);
								
	                        }
	                    }

						Stones stoneSet = new Stones(stones[0], stones[1]);

						stoneList.add(stoneSet);
					}
				}
			}
		}

		// if(stoneList.size() == 0)
		// 	return null;
		// else if(stoneList.size() == 1){
		// 	Stone stone = stoneList.get(0).getOneStone();
		// }

		return null;
		
	}

}