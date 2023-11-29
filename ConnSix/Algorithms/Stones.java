
public class Stones {
	Stone oneStone;
	Stone twoStone;

	public Stones(){
		this.oneStone = new Stone();
		this.twoStone = new Stone();
	}

	public Stones(int x1, int y1, int x2, int y2){
		this.oneStone = new Stone();
		this.twoStone = new Stone();

		setStone(x1, y1, x2, y2);
	}

	public Stones(Stone oneStone, Stone twoStone){
		this.oneStone = oneStone;
		this.twoStone = twoStone;
	}
	
	public Stone getOneStone(){
		return oneStone;
	}

	public void setOneStone(int x, int y){
		this.oneStone.setStone(x, y);
	}

	public Stone getTwoStone(){
		return twoStone;
	}

	public void setTwoStone(int x, int y){
		this.twoStone.setStone(x, y);
	}
	
	public void setStone(int x1, int y1, int x2, int y2) {
		setOneStone(x1, y1);
		setTwoStone(x2, y2);
	}
}
