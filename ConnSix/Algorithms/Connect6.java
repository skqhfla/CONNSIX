
public class Connect6 {

	static int ROW = 19;
	static int COL = 19;
	
	static int Empty = 0;
	static int red = 3;
	static int Ai = DummyAI.getAIColor();
	static int opponent = DummyAI.getPlayerColor();	
	
	// Four direction: horizon, vertical, right-down diagonal, right-up diagonal.
	static int[] dx = {1, 0, 1, 1};
	static int[] dy = {0, 1, 1, -1};
	
	private static int[][] CopyBoard(int [][] originBoard) {
		int[][] board = new int[ROW][COL];
		
		for(int x = 0; x < ROW; x++) {
			for(int y = 0; y < COL; y++) {
				board[x][y] = originBoard[x][y];
			}
		}
		
		return board;
	}
	
	private static void printBoard(int [][] board) {
		for(int y = 0; y < COL; y++) {
			for(int x = 0; x < ROW; x++) {
				System.out.printf("[%3d]", board[x][y]);
			}
			System.out.println();
		}
	}
	
	private static String Result(Stone[] stones) {		
		return stones[0].getPosition() + ":" + stones[1].getPosition();
	}
	
	private static Boolean IsOutOfBounds(int x, int y) {
		return x < 0 || y < 0 || x >= ROW || y >= COL;
	}
	
	private static Boolean isImpossibleConn7(int [][] board, int d, int x, int y, int player) {
		return (IsOutOfBounds(x - dx[d], y - dy[d]) || board[x - dx[d]][y - dy[d]] != player) && (IsOutOfBounds(x + 6 * dx[d], y + 6 * dy[d]) || board[x + 6 * dx[d]][y + 6 * dy[d]] != player);
	}

	// integer 형태의 이상적 좌표를 형식에 맞춘 String으로 바꿔 리턴(다음에 놓을 그거임. 스톤 하나하나 기준.)
	public static String returnStringCoor(ConnectSix consix) {
		Stone[] stones = new Stone[2];
		int[][] playBoard = new int[ROW][COL];

		for (int Y = 0; Y < COL; Y++) {
			System.out.printf("%2d ", COL - Y);
			for (int X = 0; X < ROW; X++) {
				String stone = String.format("%c%02d", (char) ((X < 8) ? (X + 'A') : (X + 'A' + 1)), COL - Y);
				String temp = consix.getStoneAt(stone);
				if(temp.equals("EMPTY") == true)
					playBoard[X][Y] = 0;
				else if(temp.equals("WHITE") == true)
					playBoard[X][Y] = 2;
				else if(temp.equals("BLACK") == true)
					playBoard[X][Y] = 1;
				else if(temp.equals("RED") == true)
					playBoard[X][Y] = 3;
				
				System.out.printf("[%3d]", playBoard[X][Y]);
			}
			System.out.println("");
		}
		

		// Check whether it is possible connect 6 stones
		stones = isPossibleConn6(CopyBoard(playBoard), Ai);
		if(stones != null)
			return Result(stones);
		
		// Check whether there are some cases that the opposite could connect 6 stones
		stones = isPossibleConn6(CopyBoard(playBoard), opponent);
		if(stones != null)
			return Result(stones);
		
		
		System.out.println("No Stones -> Randomly");
		
		stones = new Stone[2];
		
		for(int i = 0; i < 2; i++)
			stones[i] = new Stone();
		
		for(int i = 0; i < 2; i++) {
			do {
				stones[i].setStone((int) (Math.random() * 19), (int) (Math.random() * 19));			
			} while(consix.getStoneAt(stones[i].getPosition()).equals("EMPTY") != true);
		}
	


		return Result(stones);

	}
	
	private static Stone FindOneStone(int [][] board, int player, int d, int x, int y) {

		Stone stone = new Stone();

		for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (board[j][i] != Empty || (i == x - dx[d] && j == y - dy[d]) || (i == x + 6 * dx[d] && j == y + 6 * dx[d]))
                    continue;
                stone.setStone(j, i);
                return stone;
            }
        }
		
		return stone;
	}
	
	private static Stone[] isPossibleConn6(int [][] board, int player) {
		System.out.println("player = " + player);
		Stone[] stones = new Stone[2];
		for(int i = 0; i < 2; i++)
			stones[i] = new Stone();
		
		for(int d = 0; d < 4; d++) {
			for(int y = 0; y < ROW - 5 ; y++) {
				for(int x = 0; x < COL - 5 ; x++) {
					
					if(IsOutOfBounds(x + 5 * dx[d], y + 5 * dy[d])){
						continue;
					}
						

					int playerStone = 0;

					for(int i = 0; i < 6; i++) {

						if(board[x + i * dx[d]][y + i * dy[d]] == player){
							playerStone++;
						}
						else if(board[x + i * dx[d]][y + i * dy[d]] != Empty) {
							playerStone = 0;
							break;
						}
					}
										
					if(playerStone >= 4 && isImpossibleConn7(board, d, x, y, player)) {

						int putcnt = 0;
						for (int i = 0; i < 6; i++) {
	                        if (board[x + i*dx[d]][y + i*dy[d]] == Empty) {
	                        	board[x + i*dx[d]][y + i*dy[d]] = player;
	                        	stones[putcnt++].setStone(x + i*dx[d], y + i*dy[d]);
	                        }
	                    }
						
						if(putcnt == 2)
							return stones;
						
						stones[putcnt] = FindOneStone(board, player, d, x, y);
						

						// Two case: 1. There is no possible connect6. 2. There is no possible to loaction for FindOneStone
						if(stones[putcnt].x == -1) {
							System.out.println("no more location to locate additional stone.");
							// Since the combination of two stones in stones is impossible, so make the location of stone[0] as empty and go to the next loop.
							board[stones[0].x][stones[0].y] = Empty; 
							stones[0].x = -1;
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

}