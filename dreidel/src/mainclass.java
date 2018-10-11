/*
 * Digital Dreidel - A JavaFX self-study project by Vasil Zhigilei
 */

import javafx.scene.control.*;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.converter.DoubleStringConverter;

public class mainclass extends Application{
	
	Random rn = new Random();
	int resultInt = (rn.nextInt(3 - 0 + 1) + 0); 
	boolean finishedSpin = true;
	boolean debug = false;
	boolean autospin = false;
	int players = 2;
	int chipsPerPlayer = 1;
	double worthPerChip = 1;
	int radius = 150;
	
	int pot = 0;
	
	int onPlayer = 0;
	
	int notPlaying = 0;
	
	int winner = 0; //Temporary variable used in loop to keep track of last player still in the game 
	
	static TextArea debugField = new TextArea();
	
	static Button debugBtn = new Button("Debug");
	
	double playersPerSide = 1;
	boolean extraPlayer = false;
	
	public static void main(String[] args) {
		/**
		 * Launches the game
		 */
		launch(args);
	}
	
	Stage stage;
	@Override
    public void start(Stage primaryStage) throws Exception {
		/**
		 * Sets up the game and makes sure that if the main window closes, the whole program closes including
		 * the debugging menu.
		 */
		debugField.setEditable(false);
		debugField.setWrapText(true);
		debugBtn.setVisible(false);
		
        stage = primaryStage;
        
        Scene scene = menuScene();
        primaryStage.setScene(scene);
        Image applicationIcon = new Image(getClass().getResourceAsStream("dreidel_icon.png"));
        primaryStage.getIcons().add(applicationIcon);
        primaryStage.setTitle("Dreidel");
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	Platform.exit();
      		    System.exit(0);
            }
        });  
        
    }
	
	public Scene menuScene() throws IOException{
		/**
		 * The first scene the user sees. This scene allows the user to input the number of players, chips per player,
		 * and the worth (in $) of each chip. It also allows for the enabling of the debugging window (for standalone debugging),
		 * as well as an auto-spin option.
		 * 
		 * @return The menu scene with all the added components and nodes
		 */
		StackPane root = new StackPane();
		root.setId("pane");
		Scene scene = new Scene(root, 900, 460);
		scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
		
		Label title = new Label("Dreidel: Digital Edition");
		
		Label playersLabel = new Label("         Players: ");
		Label chipsLabel = new Label("Chips per player:  ");
		Label worthLabel = new Label("  Each chip worth: ");
		
		Label errorLabel = new Label("Error: fill all three text fields correctly");
		errorLabel.setStyle("-fx-text-fill: #ff0033; -fx-font: 14 Ariel;");
		errorLabel.setTranslateY(110);
		
		playersLabel.setTranslateX(-210);
		playersLabel.setTranslateY(-20);
		playersLabel.setStyle("-fx-font: 14 Ariel;");
		Tooltip playersTool = new Tooltip("The number of players in your game (no less than two)");
		playersLabel.setTooltip(playersTool);
		
		chipsLabel.setTranslateX(-220);
		chipsLabel.setTranslateY(30);
		chipsLabel.setStyle("-fx-font: 14 Ariel;");
		Tooltip chipstool = new Tooltip("The number of chips each player starts with");
		chipsLabel.setTooltip(chipstool);
		
		worthLabel.setTranslateX(-224);
		worthLabel.setTranslateY(80);
		worthLabel.setStyle("-fx-font: 14 Ariel;");
		Tooltip worthTool = new Tooltip("The amount each chip is worth in real money (to enter under 1$ write '0.xx')");
		worthLabel.setTooltip(worthTool);
		
		TextField playersField = new TextField();
		playersField.setPromptText("#");
		playersField.setMaxWidth(100);
		
		TextField chipsField = new TextField();
		chipsField.setPromptText("#");
		chipsField.setMaxWidth(100);
		
		TextField worthField = new TextField();
		worthField.setPromptText("#");
	    worthField.setMaxWidth(100);
		
		playersField.setTranslateX(-100);
		playersField.setTranslateY(-20);
		
		chipsField.setTranslateX(-100);
		chipsField.setTranslateY(30);
		
		worthField.setTranslateX(-100);
		worthField.setTranslateY(80);
		
		CheckBox debugBox = new CheckBox("Debug Menu");
		debugBox.setTranslateX(180);
		debugBox.setTranslateY(-20);
		Tooltip debugTool = new Tooltip("For trouble shooting or development");
		debugBox.setTooltip(debugTool);
		
		CheckBox spinBox = new CheckBox("Auto-Spin");
		spinBox.setTranslateX(172);
		spinBox.setTranslateY(30);
		Tooltip spinTool = new Tooltip("Auto-Spins the dreidel on finish turn");
		spinBox.setTooltip(spinTool);
		
		Rectangle border = new Rectangle(0, 0, 600, 200);
		border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.GRAY);
        border.setStrokeWidth(3);
        border.setTranslateY(30);
		
		title.setStyle("-fx-font: 30 Ariel;");
		title.setTranslateY(-130);
		
		Button playBtn = new Button("Start");
		playBtn.getStyleClass().add("button"); 
		playBtn.setTranslateX(269);
		playBtn.setTranslateY(110);
		
		// force the field to be numeric only
	    playersField.textProperty().addListener(new ChangeListener<String>() {
	        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	            if (!newValue.matches("\\d*")) {
	                playersField.setText(newValue.replaceAll("[^\\d]", ""));
	            }
	        }
	    });
		
	    chipsField.textProperty().addListener(new ChangeListener<String>() {
	        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	            if (!newValue.matches("\\d*")) {
	                chipsField.setText(newValue.replaceAll("[^\\d]", ""));
	            }
	        }
	    });
	    
	    Pattern validDoubleText = Pattern.compile("-?((\\d*)|(\\d+\\.\\d*))");

        TextFormatter<Double> textFormatter = new TextFormatter<Double>(new DoubleStringConverter(), null, 
            change -> {
                String newText = change.getControlNewText() ;
                if (validDoubleText.matcher(newText).matches()) {
                    return change ;
                } else return null ;
            });

        worthField.setTextFormatter(textFormatter);
	    
	    
		playBtn.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent t){
                  try {
                	  
                	  if(playersField.getText().isEmpty() || chipsField.getText().isEmpty() 
                			  || worthField.getText().isEmpty() 
                			  || Integer.parseInt(playersField.getText()) < 2){
                		  if(!root.getChildren().contains(errorLabel)){
                			  root.getChildren().add(errorLabel);
                		  }
                	  }else{
                		  debug = debugBox.isSelected();
                    	  autospin = spinBox.isSelected();
                    	  
                    	  if(debugBox.isSelected()){
                    		  new mainclass().debug();
                    	  }
                    	  
                		  players = Integer.parseInt(playersField.getText());
                    	  chipsPerPlayer = Integer.parseInt(chipsField.getText());
                    	  worthPerChip = Double.parseDouble(worthField.getText());
                    	  
                    	  debugField.appendText("---------------------------\n");
                    	  debugField.appendText("# of players: " + players + "\n");
                    	  debugField.appendText("# of chips: " + chipsPerPlayer + "\n");
                    	  debugField.appendText("chip worth: " + worthPerChip + "\n");
                    	  debugField.appendText("debug: " + debug + "\n");
                    	  debugField.appendText("autospin: " + autospin + "\n");
                    	  debugField.appendText("---------------------------\n");
                    	  

						  stage.setScene(dreidelScene());
						  
                	  }

				} catch (IOException e) {
					  e.printStackTrace();
				}
            }
        });
		
		
		root.getChildren().addAll(border, title, playersLabel, chipsLabel, 
				worthLabel, playersField, chipsField, worthField, playBtn, 
				debugBox, spinBox);
		return scene;
	}

	public Scene dreidelScene() throws IOException{
		/**
		 * The main scene for the game. This is where all the nodes are generated, configured, and then used.
		 * A large part of this method is also setting the on click MouseEvents for the geometric shapes.
		 * 
		 * @return the game scene
		 */
	      StackPane root = new StackPane();
	      root.setId("pane");
	      Scene scene = new Scene(root, 900, 460);
	      scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
	      
	      Rectangle finalRect = new Rectangle(0,75,900,460);
	      finalRect.setFill(Color.rgb(200, 200, 200, .5));
	      
	      Label finalTitle = new Label("error didnt set name finalTitle");
	      finalTitle.setStyle("-fx-font: 30 Ariel;");
	      finalTitle.setTranslateY(-130);
	      
	      Label finalResults = new Label("You defeated " + (players-1) + " players, and won " 
	    		  + chipsPerPlayer*players + " chips ( $" + chipsPerPlayer*players*worthPerChip + " )");
	      
	      finalResults.setStyle("-fx-font: 20 Ariel;");
	      finalResults.setTranslateY(-100);
	      
	      Image dieImage1 = new Image(getClass().getResourceAsStream("tancolor.jpg"));
	      
	      Image dieImage2 = new Image(getClass().getResourceAsStream("tancolorQuestion.jpg"));
	        
	      if(debug){
	    	  debugBtn.getStyleClass().add("button"); 
	    	  debugBtn.setStyle("-fx-font: 10 Ariel;");
	  		  debugBtn.setTranslateX(378);
	  		  debugBtn.setTranslateY(187);
	    	  root.getChildren().add(debugBtn);
	      }
	      
	      debugBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	  public void handle(ActionEvent event) {
	    		  new mainclass().debug();
	    		  debugBtn.setVisible(false);
	    		  debugField.appendText("--debug menu opened--\n");  
	    	  }
	      });
	      
	      PhongMaterial materialTan = new PhongMaterial();
	      materialTan.setDiffuseMap(dieImage1);
	      materialTan.setSpecularColor(Color.RED);
	      materialTan.setSpecularPower(60.0);
	      
	      PhongMaterial materialTanQuest = new PhongMaterial();
	      materialTanQuest.setDiffuseMap(dieImage2);
	      materialTanQuest.setSpecularColor(Color.RED);
	      materialTanQuest.setSpecularPower(60.0);
	      
	      PhongMaterial materialTanBox2 = new PhongMaterial();
	      materialTanBox2.setDiffuseMap(dieImage1);
	      materialTanBox2.setSpecularColor(Color.RED);
	      materialTanBox2.setSpecularPower(50.0);
	      
	      TriangleMesh pyramidMesh = new TriangleMesh();
	      pyramidMesh.getTexCoords().addAll(0,0);
	      
	      float h = 40;                    // Height
	      float s = 56;                    // Side
	      pyramidMesh.getPoints().addAll(
	    	        0,    0,    0,            // Point 0 - Top
	    	        0,    h,    -s,         // Point 1 - Front
	    	        -s, h,    0,            // Point 2 - Left
	    	        s,  h,    0,            // Point 3 - Back
	    	        0,    h,    s           // Point 4 - Right
	    	    );
	      
	      pyramidMesh.getFaces().addAll(
	    	        0,0,  2,0,  1,0,          // Front left face
	    	        0,0,  1,0,  3,0,          // Front right face
	    	        0,0,  3,0,  4,0,          // Back right face
	    	        0,0,  4,0,  2,0,          // Back left face
	    	        4,0,  1,0,  2,0,          // Bottom rear face
	    	        4,0,  3,0,  1,0           // Bottom front face
	    	    ); 
	      
	      MeshView pyramid = new MeshView(pyramidMesh);
	      pyramid.setDrawMode(DrawMode.FILL);
	      pyramid.setMaterial(materialTan);
	      //pyramid.setTranslateX(200);
	      pyramid.setTranslateY(50);
	      //pyramid.setTranslateZ(200);
	      
	      Box box = new Box(80, 80, 80);
	      //box.setTranslateX(150);
	      box.setTranslateY(-50);
	      //box.setTranslateZ(0);
	      
	      Box box2 = new Box(20, 40, 20);
	      box2.setTranslateY(-110);
	      box2.getTransforms().add(new Rotate(0,0,0));
	      //image setting
	      
	      //dreidel sides
	      Image gimmelImage = new Image(getClass().getResourceAsStream("gimmel.gif"), 50, 50, false, false);
	      Image heImage = new Image(getClass().getResourceAsStream("he.gif"), 50, 50, false, false);
	      Image nunImage = new Image(getClass().getResourceAsStream("nun.gif"), 50, 50, false, false);
	      Image shiynImage = new Image(getClass().getResourceAsStream("shiyn.gif"), 50, 50, false, false);
	      
	      ImageView gimmelImv = new ImageView();
	      ImageView heImv = new ImageView();
	      ImageView nunImv = new ImageView();
	      ImageView shiynImv = new ImageView();
	      
	      gimmelImv.setImage(gimmelImage);
	      heImv.setImage(heImage);
	      nunImv.setImage(nunImage);
	      shiynImv.setImage(shiynImage);
	      
	      //bag image setup start
	      Image bagImage = new Image(getClass().getResourceAsStream("bag.png"), 100, 100, false, false);
	      //bag image setup stop
	      
	      gimmelImv.setTranslateY(-50);
	      heImv.setTranslateY(-50);
	      nunImv.setTranslateY(-50);
	      shiynImv.setTranslateY(-50);
	      
	      box.setMaterial(materialTanQuest);
	      box2.setMaterial(materialTanBox2);

	      Rectangle tableFelt = new Rectangle(0,0,600,300);
	      
	      tableFelt.setArcHeight(300);
	      tableFelt.setArcWidth(300);

	      RadialGradient gradient1 = new RadialGradient(0, 0, 0.5, 0.5, 0.8, true, 
	    	        CycleMethod.NO_CYCLE, new Stop(0, Color.web("#268F38")), 
	    	        new Stop(1, Color.web("#194012")));
	      
	      RadialGradient gradient2 = new RadialGradient(0, 0, 0.5, 0.5, 0.8, true, 
	    	        CycleMethod.NO_CYCLE, new Stop(0, Color.web("#6F4E15")), 
	    	        new Stop(1, Color.web("#ab7821")));
	      
	      tableFelt.setFill(gradient1);
	      tableFelt.setStroke(Color.web("#6F4E15"));
	      tableFelt.setStrokeWidth(18);
	      ColorAdjust brightLight = new ColorAdjust(0, .75, .25, 0.25); 
	      ColorAdjust brightLightLabel = new ColorAdjust(0, .18, .10, 0.18); 
	      Lighting tableLight = new Lighting();
	      // chain in lighting effect.
	      brightLight.setInput(tableLight);
	      
	      tableFelt.setEffect(brightLight);
	      
	      Rectangle potBox = new Rectangle(0, 0, 140, 47);
	      potBox.setTranslateY(68);
	      potBox.setFill(gradient2);
	      potBox.setStroke(Color.web("#402d0c"));
	      potBox.setStrokeWidth(2);
	      
	      Label potLabel = new Label(""+pot);
	      
	      potLabel.setTranslateX(0);
		  potLabel.setTranslateY(68);
		  potLabel.setStyle("-fx-text-fill: #C9AD6D; -fx-font: 17 Ariel;");
		  Tooltip potTool = new Tooltip("Bets collected from all players (One chip per round from each player)");
		  potLabel.setTooltip(potTool);
	      
	      root.getChildren().addAll(tableFelt, potBox, potLabel);
	      
	      // Use the material for a shape
	      //box.getTransforms().add(new Rotate(40, 0, 0, 0, Rotate.Y_AXIS));
	      
	      RotateTransition rt1 = new RotateTransition(Duration.millis(3000), box);
	      rt1.setAxis(Rotate.Y_AXIS);
	      int angleVar = (rn.nextInt(3 - 1 + 1) + 1) * 360; 
	      rt1.setByAngle(angleVar);
	      rt1.setAutoReverse(true);
	      
	      RotateTransition rt2 = new RotateTransition(Duration.millis(3000), box2);
	      rt2.setAxis(Rotate.Y_AXIS);
	      rt2.setAutoReverse(true);
	      rt2.setByAngle(angleVar);
	      
	      
	      RotateTransition rt3 = new RotateTransition(Duration.millis(3000), pyramid);
	      rt3.setAxis(Rotate.Y_AXIS);
	      rt3.setAutoReverse(true);
	      rt3.setByAngle(angleVar);
	      pyramid.getTransforms().add(new Rotate(180, 0, 0, 0, Rotate.Z_AXIS));
	      pyramid.getTransforms().add(new Rotate(45, 0, 0, 0, Rotate.Y_AXIS));
	      
	      
	      playersPerSide = players/2;
	      if(players % 2 != 0){
	    	  playersPerSide = (int)playersPerSide;
	    	  extraPlayer = true;
	    	  debugField.setText("Odd number of players\n");
	      }  
	      
	      
	      
	      playerObj[] player = new playerObj[players];
	      
	      debugField.appendText("Player object initialized\n");
	      
    	  int x = 0;
    	  int y = 0;
    	  onPlayer = 0;
    	  for(int i = 0; i < playersPerSide; i++, onPlayer++){
    		  
    		  x = (int)(-(radius*Math.cos(Math.toRadians(270 + i*(180/playersPerSide) 
  	  				+ (180/(2*playersPerSide)))))) - radius;
    		  y = (int)(radius*Math.sin(Math.toRadians(270 + i*(180/playersPerSide) 
    	  				+ (180/(2*playersPerSide)))));
    		  
    		  player[onPlayer] = new playerObj("Player " + onPlayer, chipsPerPlayer, 
    				  chipsPerPlayer*worthPerChip, true, false, 
    				  new Label("Player"+onPlayer+" "+chipsPerPlayer),
    				  new Label("$"+worthPerChip*chipsPerPlayer), x, y, 0, new ImageView(bagImage));
    		 
    		  player[onPlayer].getDisplayPlayer().setStyle("-fx-border-color: #0038A8; "
    		  		+ "-fx-border-width: 1px; -fx-background-color: "
    		  		+ "radial-gradient(center 50% 50%, radius 100%, #b98946, #C9AD6D); "
    		  		+ "-fx-padding: 2 2 2 2; -fx-text-fill: #0038A8; -fx-font: 15 Courier;");
    		 
    		  player[onPlayer].getBag().setEffect(new Reflection());
    		  
    		  player[onPlayer].getDisplayPlayer().setEffect(brightLightLabel);
    		  root.getChildren().addAll(player[onPlayer].getDisplayPlayer(),
    				  player[onPlayer].getBag(), player[onPlayer].getDisplayCash());
    	  }
    	  
    	  if(extraPlayer){
    		  player[onPlayer] = new playerObj("Player " + onPlayer, chipsPerPlayer, 
    				  chipsPerPlayer*worthPerChip, true, false, 
    				  new Label("Player"+onPlayer+" "+chipsPerPlayer),
    				  new Label("$"+worthPerChip*chipsPerPlayer), 0, radius, 2, new ImageView(bagImage));
    		  
    		  player[onPlayer].getDisplayPlayer().setStyle("-fx-border-color: #0038A8; "
      		  		+ "-fx-border-width: 1px; -fx-background-color: "
      		  		+ "radial-gradient(center 50% 50%, radius 100%, #b98946, #C9AD6D); "
      		  		+ "-fx-padding: 2 2 2 2; -fx-text-fill: #0038A8; -fx-font: 15 Courier;");
      		 
    		  player[onPlayer].getBag().setEffect(new Reflection());
    		  
      		  player[onPlayer].getDisplayPlayer().setEffect(brightLightLabel);
    		  root.getChildren().addAll(player[onPlayer].getDisplayPlayer(),
    				  player[onPlayer].getBag(), player[onPlayer].getDisplayCash());
    		  onPlayer++;
    	  } //sideOn = 2 despite this being 2nd out of 3 due to onPlayer++ system (0,1,*2*)
    	  
    	  for(int i = 0; i < playersPerSide; i++, onPlayer++){
    		  
    		  x = (int)(radius*Math.cos(Math.toRadians(270 + i*(180/playersPerSide) 
  	  				+ (180/(2*playersPerSide))))) + radius;
    		  y = (int)(-(radius*Math.sin(Math.toRadians(270 + i*(180/playersPerSide) 
    	  				+ (180/(2*playersPerSide))))));
    		  
    		  player[onPlayer] = new playerObj("Player " + onPlayer, chipsPerPlayer, 
    				  chipsPerPlayer*worthPerChip, true, false, 
    				  new Label("Player"+onPlayer+" "+chipsPerPlayer),
    				  new Label("$"+worthPerChip*chipsPerPlayer), x, y, 1, new ImageView(bagImage));
    		  
    		  player[onPlayer].getDisplayPlayer().setStyle("-fx-border-color: #0038A8; "
      		  		+ "-fx-border-width: 1px; -fx-background-color: "
      		  		+ "radial-gradient(center 50% 50%, radius 100%, #b98946, #C9AD6D); "
      		  		+ "-fx-padding: 2 2 2 2; -fx-text-fill: #0038A8; -fx-font: 15 Courier;");
      		 
    		  player[onPlayer].getBag().setEffect(new Reflection());
    		  
      		  player[onPlayer].getDisplayPlayer().setEffect(brightLightLabel);
    		  
    		  root.getChildren().addAll(player[onPlayer].getDisplayPlayer(),
    				  player[onPlayer].getBag(), player[onPlayer].getDisplayCash());
    	  }
    	  
    	  onPlayer = 0;
    	  
    	  player[onPlayer].getDisplayPlayer().setStyle("-fx-border-color: #0038A8; "
    		  		+ "-fx-border-width: 1px; -fx-background-color: "
    		  		+ "radial-gradient(center 50% 50%, radius 100%, #eae0c7, #ba9745); "
    		  		+ "-fx-padding: 2 2 2 2; -fx-text-fill: #0038A8; -fx-font: 15 Courier;");
    	  
    	  debugField.appendText("***First player highlight\n");
    	  
    	  debugField.appendText("Player variables set\n");
    	  
    	  potLabel.setText(""+pot);
    	  
    	  box.setOnMouseClicked(new EventHandler<MouseEvent>(){
              @Override
              public void handle(MouseEvent t) {
            	  if(finishedSpin == true){
	    			  box.setMaterial(materialTanQuest);
	    			  for(int i = 0; i < players; i++){
		      	    	  if(player[i].getPlaying()){
		      	    		  player[i].setChips(player[i].getChips()-1);
		      	    		  pot++;
		      	    		  player[i].getDisplayPlayer().setText(player[i].getName()+" "+player[i].getChips());
		      	    		  player[i].setMoney(player[i].getChips()*worthPerChip);
		      	    		  player[i].getDisplayCash().setText("$"+player[i].getMoney());

		      	    	  }
		      	      }
	    			  potLabel.setText(""+pot);
		      	      
	    			  switch (resultInt) {
	    			  	case 0: 
	    			  		root.getChildren().remove(gimmelImv);
	    			  		debugField.appendText("Case remove: gimmel (0)\n");
	    			  		debugField.appendText("-----------------\n");
	    			  		break;
	    			  	case 1:
	    			  		root.getChildren().remove(heImv);
	    			  		debugField.appendText("Case remove: he (1)\n");
	    			  		debugField.appendText("------------------\n");
	    			  		break;
	    			  	case 2:
	    			  		root.getChildren().remove(nunImv);
	    			  		debugField.appendText("Case remove: nun (2)\n");
	    			  		debugField.appendText("------------------\n");
	    			  		break;
	    			  	case 3:
	    			  		root.getChildren().remove(shiynImv);
	    			  		debugField.appendText("Case remove: shiyn (3)\n");
	    			  		debugField.appendText("------------------\n");
	    			  		break;
	    			  	default:
	    			  		debugField.appendText("Case remove: default (error)\n");
	    			  		debugField.appendText("------------------\n");
	    			  }
	    		  
	    			  int angleVar = (rn.nextInt(3 - 1 + 1) + 1) * 360; 
	    			  debugField.appendText("angleVar = " + angleVar + "\n");
	    			  rt1.setByAngle(angleVar);
	    			  rt1.play();
	    			  rt2.setByAngle(angleVar);
	    			  rt2.play();
	    			  rt3.setByAngle(angleVar);
	    			  rt3.play();
	    			  finishedSpin = false;
	    	  }else if(finishedSpin == false){
	    		  debugField.appendText("error: can't spin, still spinning\n");
	    	  }else{
	    		  debugField.appendText("error: [in btnSpin] finishedSpin =/= 0 || 1\n");
	    	  }
              }
          });
    	  
    	  box2.setOnMouseClicked(new EventHandler<MouseEvent>(){
              @Override
              public void handle(MouseEvent t) {
            	  if(finishedSpin == true){
	    			  box.setMaterial(materialTanQuest);
	    			  for(int i = 0; i < players; i++){
		      	    	  if(player[i].getPlaying()){
		      	    		  player[i].setChips(player[i].getChips()-1);
		      	    		  pot++;
		      	    		  player[i].getDisplayPlayer().setText(player[i].getName()+" "+player[i].getChips());
		      	    		  player[i].setMoney(player[i].getChips()*worthPerChip);
		      	    		  player[i].getDisplayCash().setText("$"+player[i].getMoney());

		      	    	  }
		      	      }
		      	      
	    			  potLabel.setText(""+pot);

	    			  switch (resultInt) {
	    			  	case 0: 
	    			  		root.getChildren().remove(gimmelImv);
	    			  		debugField.appendText("Case remove: gimmel (0)\n");
	    			  		debugField.appendText("-----------------\n");
	    			  		break;
	    			  	case 1:
	    			  		root.getChildren().remove(heImv);
	    			  		debugField.appendText("Case remove: he (1)\n");
	    			  		debugField.appendText("------------------\n");
	    			  		break;
	    			  	case 2:
	    			  		root.getChildren().remove(nunImv);
	    			  		debugField.appendText("Case remove: nun (2)\n");
	    			  		debugField.appendText("------------------\n");
	    			  		break;
	    			  	case 3:
	    			  		root.getChildren().remove(shiynImv);
	    			  		debugField.appendText("Case remove: shiyn (3)\n");
	    			  		debugField.appendText("------------------\n");
	    			  		break;
	    			  	default:
	    			  		debugField.appendText("Case remove: default (error)\n");
	    			  		debugField.appendText("------------------\n");
	    			  }
	    		  
	    			  int angleVar = (rn.nextInt(3 - 1 + 1) + 1) * 360; 
	    			  debugField.appendText("angleVar = " + angleVar + "\n");
	    			  rt1.setByAngle(angleVar);
	    			  rt1.play();
	    			  rt2.setByAngle(angleVar);
	    			  rt2.play();
	    			  rt3.setByAngle(angleVar);
	    			  rt3.play();
	    			  finishedSpin = false;
	    	  }else if(finishedSpin == false){
	    		  debugField.appendText("error: can't spin, still spinning\n");
	    	  }else{
	    		  debugField.appendText("error: [in btnSpin] finishedSpin =/= 0 || 1\n");
	    	  }
              }
          });
    	  
    	  pyramid.setOnMouseClicked(new EventHandler<MouseEvent>(){
              @Override
              public void handle(MouseEvent t) {
            	  if(finishedSpin == true){
	    			  box.setMaterial(materialTanQuest);
	    			  for(int i = 0; i < players; i++){
		      	    	  if(player[i].getPlaying()){
		      	    		  player[i].setChips(player[i].getChips()-1);
		      	    		  pot++;
		      	    		  player[i].getDisplayPlayer().setText(player[i].getName()+" "+player[i].getChips());
		      	    		  player[i].setMoney(player[i].getChips()*worthPerChip);
		      	    		  player[i].getDisplayCash().setText("$"+player[i].getMoney());

		      	    	  }
		      	      }
		      	      
	    			  potLabel.setText(""+pot);
	    			  
	    			  switch (resultInt) {
	    			  	case 0: 
	    			  		root.getChildren().remove(gimmelImv);
	    			  		debugField.appendText("Case remove: gimmel (0)\n");
	    			  		debugField.appendText("-----------------\n");
	    			  		break;
	    			  	case 1:
	    			  		root.getChildren().remove(heImv);
	    			  		debugField.appendText("Case remove: he (1)\n");
	    			  		debugField.appendText("------------------\n");
	    			  		break;
	    			  	case 2:
	    			  		root.getChildren().remove(nunImv);
	    			  		debugField.appendText("Case remove: nun (2)\n");
	    			  		debugField.appendText("------------------\n");
	    			  		break;
	    			  	case 3:
	    			  		root.getChildren().remove(shiynImv);
	    			  		debugField.appendText("Case remove: shiyn (3)\n");
	    			  		debugField.appendText("------------------\n");
	    			  		break;
	    			  	default:
	    			  		debugField.appendText("Case remove: default (error)\n");
	    			  		debugField.appendText("------------------\n");
	    			  }
	    		  
	    			  int angleVar = (rn.nextInt(3 - 1 + 1) + 1) * 360; 
	    			  debugField.appendText("angleVar = " + angleVar + "\n");
	    			  rt1.setByAngle(angleVar);
	    			  rt1.play();
	    			  rt2.setByAngle(angleVar);
	    			  rt2.play();
	    			  rt3.setByAngle(angleVar);
	    			  rt3.play();
	    			  finishedSpin = false;
	    	  }else if(finishedSpin == false){
	    		  debugField.appendText("error: can't spin, still spinning\n");
	    	  }else{
	    		  debugField.appendText("error: [in btnSpin] finishedSpin =/= 0 || 1\n");
	    	  }
              }
          });
    	  
    	  //big block spin click dreidel start
	      
	      
	      rt1.setOnFinished(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent ae) {
	                
	      	        box.setMaterial(materialTan);
				  
	      	        resultInt = (rn.nextInt(3 - 0 + 1) + 0); 
	      	        switch (resultInt) {
	      	        	case 0: 
	      	        		root.getChildren().add(gimmelImv);
	      	        		debugField.appendText("Case add: gimmel (0)\n");
	      	        		player[onPlayer].setChips(player[onPlayer].getChips()+pot);
	    	      	        player[onPlayer].getDisplayPlayer().setText(player[onPlayer].getName()+" "+player[onPlayer].getChips());
	    	      	        player[onPlayer].setMoney(player[onPlayer].getChips()*worthPerChip);
	    	      	        player[onPlayer].getDisplayCash().setText("$"+player[onPlayer].getMoney());
	      	        		pot = 0;
	      	        		potLabel.setText(""+pot);
	    	      	        break;
	      	        	case 1:
	      	        		root.getChildren().add(heImv);
	      	        		debugField.appendText("Case add: he (1)\n");
	      	        		player[onPlayer].setChips(player[onPlayer].getChips()+(int)Math.floor((pot/2)));
	    	      	        player[onPlayer].getDisplayPlayer().setText(player[onPlayer].getName()+" "+player[onPlayer].getChips());
	    	      	        player[onPlayer].setMoney(player[onPlayer].getChips()*worthPerChip);
	    	      	        player[onPlayer].getDisplayCash().setText("$"+player[onPlayer].getMoney());
	    	      	        pot = (int) Math.ceil(pot/2.0); // divide by float to get float value
	    	      	        potLabel.setText(""+pot);
	      	        		break;
	      	        	case 2:
	      	        		root.getChildren().add(nunImv);
	      	        		debugField.appendText("Case add: nun (2)\n");
	      	        		potLabel.setText(""+pot);
	      	        		break;
	      	        	case 3:
	      	        		root.getChildren().add(shiynImv);
	      	        		debugField.appendText("Case add: shiyn (3)\n");
	      	        		player[onPlayer].setChips(player[onPlayer].getChips()-3);
	    	      	        player[onPlayer].getDisplayPlayer().setText(player[onPlayer].getName()+" "+player[onPlayer].getChips());
	    	      	        player[onPlayer].setMoney(player[onPlayer].getChips()*worthPerChip);
	    	      	        player[onPlayer].getDisplayCash().setText("$"+player[onPlayer].getMoney());
	    	      	        if(player[onPlayer].getChips()<1){
	    	      	        	pot = pot + (3 + player[onPlayer].getChips());
	    	      	        	player[onPlayer].setChips(0);
	    	      	        	player[onPlayer].getDisplayPlayer().setText(player[onPlayer].getName()+" "+player[onPlayer].getChips());
	    	      	        	player[onPlayer].setMoney(0);
	    	      	        	player[onPlayer].getDisplayCash().setText("$"+player[onPlayer].getMoney());
	    	      	        }else{
	    	      	        	pot = pot + 3;
	    	      	        }
	    	      	        potLabel.setText(""+pot);
	      	        		break;
	      	        	default:
	      	        		debugField.appendText("Case add: default (error)\n");
	      	        }
	      	        
	      	        winner = onPlayer; //maybe? Or should player who was last i win? - 
	      	        //no, since winner is always last, not fair, onPlayer should win
	      	        for(int i = 0; i < players; i++){ // CHECKS FOR LOST PLAYERS
	      	        	if(player[i].getPlaying() == true && player[i].getChips() < 1){
	      	        		player[i].setPlaying(false);
	      	        		notPlaying++;
	      	        		player[i].getDisplayPlayer().setStyle("-fx-border-color: #0038A8; "
	      	        				+ "-fx-border-width: 1px; -fx-background-color: "
	      	        				+ "radial-gradient(center 50% 50%, radius 100%, #946e38, #a8883e); "
	      	        				+ "-fx-padding: 2 2 2 2; -fx-text-fill: #0038A8; -fx-font: 15 Courier;");
	      	        	}else if(player[i].getPlaying()){
	      	        		winner = i;
	      	        	}
	      	        }
	      	        
	      	        if(notPlaying >= players-1){ //If only one standing (or all "lose"), winner wins
	      	        	finalTitle.setText("Congratulations " + player[winner].getName());
	      	        	player[onPlayer].getDisplayPlayer().setStyle("-fx-border-color: #0038A8; "
	      	    		  		+ "-fx-border-width: 1px; -fx-background-color: "
	      	    		  		+ "radial-gradient(center 50% 50%, radius 100%, #eae0c7, #ba9745); "
	      	    		  		+ "-fx-padding: 2 2 2 2; -fx-text-fill: #0038A8; -fx-font: 15 Courier;");
		      	    	root.getChildren().addAll(finalRect, finalTitle, finalResults);  
		      	    	
		      	    	debugField.appendText("Winner = " + player[winner].getName());
	      	        }else{
	      	        	player[onPlayer].getDisplayPlayer().setStyle("-fx-border-color: #0038A8; "
	      	      		  		+ "-fx-border-width: 1px; -fx-background-color: "
	      	      		  		+ "radial-gradient(center 50% 50%, radius 100%, #b98946, #C9AD6D); "
	      	      		  		+ "-fx-padding: 2 2 2 2; -fx-text-fill: #0038A8; -fx-font: 15 Courier;");

	      	        	for(int i = 0; i < players-1; i++){
	      	        		if(onPlayer+1>=players){
	      	        			onPlayer=0;
	      	        		}else{
	      	        			onPlayer++;
	      	        		}
	      	        		if(player[onPlayer].getPlaying()){
	      	        			break;
	      	        		}
	      	        	}
	      	        	player[onPlayer].getDisplayPlayer().setStyle("-fx-border-color: #0038A8; "
	      	    		  		+ "-fx-border-width: 1px; -fx-background-color: "
	      	    		  		+ "radial-gradient(center 50% 50%, radius 100%, #eae0c7, #ba9745); "
	      	    		  		+ "-fx-padding: 2 2 2 2; -fx-text-fill: #0038A8; -fx-font: 15 Courier;");
	      	        }
	      	        
	      	        
	      	        
	      	        finishedSpin = true; //ba9745 bright, a8883e darkest, 
	            	
	      	        if(autospin){
	      	        	System.out.println("autospin if");
	      	        	pyramid.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
	      	        		   0, 0, 0, 0, MouseButton.PRIMARY, 1,
	      	        		   true, true, true, true, true, true, true, true, true, true, null));
	      	        }
	            }
	        });
	      
	      root.getChildren().addAll(box, box2, pyramid);
		  return scene;
	}
	
	public void debug(){
		/**
		 * Debugging menu that can be used if the game is a standalone executable, since there
		 * would be no access to the IDE console.
		 * This method takes in no arguments and does not return anything. It solely exists
		 * to create the Stage and Scene for the debugging menu.
		 * 
		 * In order to push a comment to the debug menu, the following code may used:
		 * debugField.appendText("Your String here!");
		 */
		Stage debugStage = new Stage();
		StackPane root = new StackPane();
	    root.setId("pane");
	    Scene scene = new Scene(root, 215, 460);
	   
	    debugStage.setScene(scene);

        Image applicationIcon = new Image(getClass().getResourceAsStream("dreidel_icon.png"));
        debugStage.getIcons().add(applicationIcon);
        debugStage.setTitle("Dreidel ~ Debug Menu");
        debugStage.show();
        
        debugStage.setX(debugStage.getX()+585); //was + 565
        
        debugStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	debugBtn.setVisible(true);
            	debugField.appendText("--debug menu closed--\n");
            }
        });        
          
        
	    scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
       
	    root.getChildren().addAll(debugField);
	   		
	}
	
	
	
}


