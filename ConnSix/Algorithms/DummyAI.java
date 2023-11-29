import java.util.Scanner;

public class DummyAI {
	
	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Input the ip address > ");
		String ip = scanner.nextLine();
		System.out.print("Input the port number > ");
		int port = Integer.parseInt(scanner.nextLine());
		System.out.print("Input the color > ");
		String color = scanner.nextLine();
		
		ConnectSix conSix = new ConnectSix(ip, port, color);
		Connect6 con = new Connect6();
		System.out.println("Red Stone positions are " + conSix.redStones);

		if (color.toLowerCase().compareTo("black") == 0) {
			con.Ai = 1;
			String first = conSix.drawAndRead("K10");
		} else if (color.toLowerCase().compareTo("white") == 0) {
			con.Ai = 2;
			String first = conSix.drawAndRead("");
		}

		con.opponent = 3 - con.Ai;
		con.Red = con.Ai + con.opponent;
		
		while (true) {
			
			String draw = con.returnStringCoor(conSix);
			
			String read = conSix.drawAndRead(draw);
			
			if(read.compareTo("WIN") == 0 || read.compareTo("LOSE") == 0 || read.compareTo("EVEN") == 0) {
				 break;
			}
		}

	}			
}
