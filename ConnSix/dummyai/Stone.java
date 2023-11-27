
public class Stone {
	int x;
	int y;
	
	Stone(){
		x = -1;
		y = -1;
	}
	
	public void setStone(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String getPosition() {
		return String.format("%c%02d", (char) ((x < 8) ? (x + 'A') : (x + 'A' + 1)), Connect6.COL - y);
	}
}
