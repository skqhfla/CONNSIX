
public class Stones {
	Stone first;
	Stone second;

	public Stones(){
		this.first = new Stone();
		this.second = new Stone();
	}

	public Stones(int x1, int y1, int x2, int y2){
		this.first = new Stone();
		this.second = new Stone();

		setStone(x1, y1, x2, y2);
	}

	public Stones(Stone first, Stone second){
		this.first = first;
		this.second = second;
	}
	
	public Stone getFirstStone(){
		return first;
	}

	public void setFirstStone(int x, int y){
		this.first.setStone(x, y);
	}

	public void setFirstStone(Stone first){
		this.first = first;
	}

	public Stone getSecondStone(){
		return second;
	}

	public void setSecondStone(int x, int y){
		this.second.setStone(x, y);
	}

	public void setSecondStone(Stone second){
		this.second = second;
	}
	
	public void setStone(int x1, int y1, int x2, int y2) {
		setFirstStone(x1, y1);
		setSecondStone(x2, y2);
	}

	public String getPosition(){
		return first.getPosition() + ":" + second.getPosition();
	}
}
