
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class playerObj {

	private String name;
	private int chips;
	private double money;
	private boolean playing;
	private boolean cheating;
	private Label displayP;
	private Label displayC;
	private int x;
	private int y;
	private int sideOn;
	private ImageView bag;
	//private String lastResult;
	
	int shiftBag = 75;
	
	public playerObj(String namePlayer, int chipsPlayer, double moneyPlayer, boolean playingPlayer,
			boolean cheatingPlayer, Label displayPlayer, Label displayCash, int xPlayer, 
			int yPlayer, int sideOnPlayer, ImageView bagOfMoney) {
		
		name = namePlayer;
		chips = chipsPlayer;
		money = moneyPlayer;
		playing = playingPlayer;
		cheating = cheatingPlayer;
		displayP = displayPlayer;
		x = xPlayer;
		y = yPlayer;
		sideOn = sideOnPlayer;
		displayC = displayCash;
		bag = bagOfMoney;
	
		bag.setFitHeight(48);
		bag.setFitWidth(42);
		
		displayP.setTranslateX(x);
		displayP.setTranslateY(y);
		
		if(sideOn == 0){ //left side xy
			displayC.setTranslateX(x-shiftBag);
			displayC.setTranslateY(y);
			bag.setTranslateX(x-shiftBag);
			bag.setTranslateY(y-6);
		}else if(sideOn == 1){ //right side xy
			displayC.setTranslateX(x+shiftBag);
			displayC.setTranslateY(y);
			bag.setTranslateX(x+shiftBag);
			bag.setTranslateY(y-6);
		}else if(sideOn == 2){ //center xy
			displayC.setTranslateX(x);
			displayC.setTranslateY(y+shiftBag-25);
			bag.setTranslateX(x);
			bag.setTranslateY(y+shiftBag-25-6);
		}else{ //neither - error xy
			System.out.println("error playerObj sideOn =/= 0,1,2 xy");
		}
		// TODO Auto-generated constructor stub	
	}

	//getters & setters section - also the inside () might cause conflict with above constructor
	
	//name
	public void setName(String namePlayer){
		this.name = namePlayer;	
	}
	public String getName(){
		return name;
	}
	
	//chips
	public void setChips(int chipsPlayer){
		this.chips = chipsPlayer;	
	}
	public int getChips(){
		return chips;
	}
	
	//money
	public void setMoney(double moneyPlayer){
		this.money = moneyPlayer;	
	}
	public double getMoney(){
		return money;
	}
	
	//playing
	public void setPlaying(boolean playingPlayer){
		this.playing = playingPlayer;	
	}
	public boolean getPlaying(){
		return playing;
	}
	
	//cheating
	public void setCheating(boolean cheatingPlayer){
		this.cheating = cheatingPlayer;	
	}
	public boolean getCheating(){
		return cheating;
	}
	
	//display player with name
	public void setDisplayPlayer(Label displayPlayer){
		this.displayP = displayPlayer;	
	}
	public Label getDisplayPlayer(){
		return displayP;
	}
	
	//display chips on bag
	public void setDisplayCash(Label displayCash){
		this.displayC = displayCash;	
	}
	public Label getDisplayCash(){
		return displayC;
	}
	
	//x
	public void setX(int xPlayer){
		this.x = xPlayer;	
		displayP.setTranslateX(x);
		
		if(sideOn == 0){ //left side x
			displayC.setTranslateX(x-shiftBag);
			bag.setTranslateX(x-shiftBag);
		}else if(sideOn == 1){ //right side x
			displayC.setTranslateX(x+shiftBag);
			bag.setTranslateX(x+shiftBag);
		}else if(sideOn == 2){ //center x
			displayC.setTranslateX(x);
			bag.setTranslateX(x);
		}else{ //neither - error x
			System.out.println("error playerObj sideOn =/= 0,1,2 x");
		}
		
	}
	public int getX(){
		return x;
	}
	
	//y
	public void setY(int yPlayer){
		this.y = yPlayer;	
		displayP.setTranslateY(y);
		
		if(sideOn == 0){ //left side y
			displayC.setTranslateY(y);
			bag.setTranslateY(y);
		}else if(sideOn == 1){ //right side y
			displayC.setTranslateY(y);
			bag.setTranslateY(y);
		}else if(sideOn == 2){ //center y
			displayC.setTranslateY(y+shiftBag-25);
			bag.setTranslateY(y+shiftBag-25);
		}else{ //neither - error y
			System.out.println("error playerObj sideOn =/= 0,1,2 y");
		}
	}
	public int getY(){
		return y;
	}
	
	public void setSideOn(int sideOnPlayer){
		this.sideOn = sideOnPlayer;
	}
	public int getSideOn(){
		return sideOn;
	}
	
	public void setBag(ImageView bagOfMoney){
		this.bag = bagOfMoney;
	}
	public ImageView getBag(){
		return bag;
	}
}
