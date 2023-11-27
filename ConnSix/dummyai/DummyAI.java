import java.util.Scanner;

public class DummyAI {
	static int Ai;
	static int opponent;
	static int red = 3;
	
	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Input the ip address > ");
		String ip = scanner.nextLine();
		System.out.print("Input the port number > ");
		int port = Integer.parseInt(scanner.nextLine());
		System.out.print("Input the color > ");
		String color = scanner.nextLine();
		
		ConnectSix conSix = new ConnectSix(ip, port, color);
		System.out.println("Red Stone positions are " + conSix.redStones);

		if (color.toLowerCase().compareTo("black") == 0) {
			Ai = 1;
			opponent = 2;
			String first = conSix.drawAndRead("K10");
		} else if (color.toLowerCase().compareTo("white") == 0) {
			Ai = 2;
			opponent = 1;
			String first = conSix.drawAndRead("");
		}
		
		while (true) {
			
			String draw = Connect6.returnStringCoor(conSix);
			
			String read = conSix.drawAndRead(draw);
			
			if(read.compareTo("WIN") == 0 || read.compareTo("LOSE") == 0 || read.compareTo("EVEN") == 0) {
				 break;
			}
		}

	}
	
		public static int getAIColor() {
			return Ai;
		}

		public static int getPlayerColor() {
				return opponent;
		}	
		
			
}
