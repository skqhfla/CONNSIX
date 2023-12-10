
public class Stone {
	int x;
	int y;
	
	Stone(){
		x = -1;
		y = -1;
	}

	Stone(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void setStone(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String getPosition() {
		return String.format("%c%02d", (char) ((y < 8) ? (y + 'A') : (y + 'A' + 1)), 19 - x);
	}
}
