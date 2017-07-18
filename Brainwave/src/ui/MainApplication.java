package ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import main.process.Mindwave;
import static java.lang.Math.random;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


import main.rxtx.PortCommunication;

public class MainApplication extends Application {

	// private MindwaveMobile mindwaveMobile = null;
	private Thread mindwaveThread = null;
	private Timeline timeline = null;
	private int time = 50;
	private Text counterText = new Text();
	private Button goButton = new Button();
	private final AtomicInteger data = new AtomicInteger(0);
	
	private PortCommunication portCommunication = new PortCommunication("COM14");
		
	private PathTransition pathTransitionSinus = new PathTransition();
	
	
	private Text calculationText = new Text();
	private PathTransition pathTransitionCalculationText = new PathTransition();
	
	private MediaPlayer mediaPlayer = null;
	
	public static int WIDTH=1600;
	public static int HEIGHT=825;
	public static int CIRCLES=25;
	
	private int multiplcatorFactor = 1;

	public static void main(String[] args) {
		launch(args);
	}

	public MainApplication() {
		// mindwaveMobile = new MindwaveMobile( );
		timeline = new Timeline();

		try {
			// portCommunication.connect();
		} catch (Exception e) {
			System.out.println("Error connection port COM");
			e.printStackTrace();
			this.exit();
		}
	}
	

	
	private	 Group createCurve(Group g){
		Path path = new Path();
		path.getElements().add(new MoveTo(100,90));
		path.getElements().add(new LineTo( MainApplication.WIDTH,90));
	
		for(int i = 0; i < 34; i ++){
			CubicCurve cubicCurve = new CubicCurve();
			cubicCurve.setStartX(0+i*90);
			cubicCurve.setStartY(150);
			cubicCurve.setControlX1(30+i*90);
			cubicCurve.setControlY1(50);
			cubicCurve.setControlX2(80+i*90);
			cubicCurve.setControlY2(250);
			cubicCurve.setEndX(90+i*90);
			cubicCurve.setEndY(150);
			if ( i == 0 )
			cubicCurve.setFill( Color.ANTIQUEWHITE );
			else 
				cubicCurve.setFill( Color.ANTIQUEWHITE );
			
			// cubicCurveList.add( cubicCurve );
			g.getChildren().add(cubicCurve);
		}
		
		
		pathTransitionSinus.setDuration(Duration.millis(4000));
		pathTransitionSinus.setPath(path);
		pathTransitionSinus.setNode(g);
		pathTransitionSinus.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
		pathTransitionSinus.setCycleCount(Timeline.INDEFINITE);
		pathTransitionSinus.setAutoReverse(true);
		
		return g;
	}


	@Override
	public void start(Stage primaryStage) {
		Group root = new Group();
		Scene scene = new Scene(root, MainApplication.WIDTH , MainApplication.HEIGHT, Color.WHITE);
		scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {

		      if ( time >= 0 ){
		    	  String message = key.getCode().toString();
		    	  char character = message.charAt( message.length()-1);
		    	  // DEBUG Feature to add .... 
		    	  if ( ( character >= '0' ) && ( character <='9') ){
		    		  multiplcatorFactor = character - '0';
		    		  System.out.println("Multiplactof Factor : " + multiplcatorFactor);
		    	  }
		    	  
		    	  portCommunication.sendMessage( character );  
		    	  
		      }
		      
		      
		});
		primaryStage.setScene(scene);
		scene.getStylesheets().add(MainApplication.class.getResource("style.css").toExternalForm());
		
		createCircles(root, scene);
		
		root.getChildren().add( createCurve( new Group() ) );

		root.getChildren().add( createButtonGo() );
		
		// drawCurve(root , 0 , 0 , 100 , 200);
		root.getChildren().add(  createCounterText() );
		
		root.getChildren().add( createCalculationText( ) );

		primaryStage.show();
		pathTransitionSinus.play();
		pathTransitionCalculationText.play();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
            	exit();
            }

        });
		playMusic();
	}
	
	
	private Text createCalculationText(){
		
		this.calculationText.setText( "" );
		this.calculationText.setTextAlignment(TextAlignment.RIGHT);
		this.calculationText.setId("calculationtext");
		this.calculationText.setFill(Color.RED);
		this.calculationText.setX(MainApplication.WIDTH *0.18);
		this.calculationText.setY( MainApplication.HEIGHT *0.8);
		this.calculationText.setText("");
		
		Path path = new Path();
		path.getElements().add(new MoveTo( MainApplication.HEIGHT ,-400));
		path.getElements().add(new LineTo( MainApplication.HEIGHT, MainApplication.WIDTH*0.6  ) );
		
		this.pathTransitionCalculationText.setDuration(Duration.millis(4000));
		this.pathTransitionCalculationText.setPath( path );
		this.pathTransitionCalculationText.setNode( this.calculationText );
		this.pathTransitionCalculationText.setOrientation(PathTransition.OrientationType.NONE);
		this.pathTransitionCalculationText.setCycleCount(Timeline.INDEFINITE);
		this.pathTransitionCalculationText.setAutoReverse(true);
		
		return this.calculationText;
	}

	
	
	private Text createCounterText(){
		
		this.counterText.setText( "" );
		this.counterText.setTextAlignment(TextAlignment.RIGHT);
		this.counterText.setId("fancytext");
		this.counterText.setFill(Color.RED);
		this.counterText.setX(MainApplication.WIDTH *0.78);
		this.counterText.setY(250);
		return this.counterText;
	}

	private void createCircles(Group root, Scene scene) {
		Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(),
				new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE,
						new Stop[] { new Stop(0, Color.web("#f8bd55")), new Stop(0.14, Color.web("#c0fe56")),
								new Stop(0.28, Color.web("#5dfbc1")), new Stop(0.43, Color.web("#64c2f8")),
								new Stop(0.57, Color.web("#be4af7")), new Stop(0.71, Color.web("#ed5fc2")),
								new Stop(0.85, Color.web("#ef504c")), new Stop(1, Color.web("#f2660f")), }));
		colors.widthProperty().bind(scene.widthProperty());
		colors.heightProperty().bind(scene.heightProperty());

		Group circles = new Group();
		for (int i = 0; i < MainApplication.CIRCLES; i++) {
			Circle circle = new Circle(150, Color.web("darkblue", 0.75));
			
			circle.setStrokeType(StrokeType.OUTSIDE);
			circle.setStroke(Color.web("darkblue", 0.86));
			circle.setStrokeWidth(4);
			circles.setEffect(new BoxBlur(10, 10, 3));
			circles.getChildren().add(circle);
		}

		Group blendModeGroup = new Group(
				new Group(new Rectangle(scene.getWidth(), scene.getHeight(), Color.GREENYELLOW), circles), colors);
		colors.setBlendMode(BlendMode.OVERLAY);
		root.getChildren().add(blendModeGroup);

		for (Node circle : circles.getChildren()) {
			timeline.getKeyFrames()
					.addAll(new KeyFrame(Duration.ZERO, new KeyValue(circle.translateXProperty(), random() * MainApplication.WIDTH),
							new KeyValue(circle.translateYProperty(), random() * 600 + 300)),
							new KeyFrame(new Duration(40000), new KeyValue(circle.translateXProperty(), random() * MainApplication.WIDTH),
									new KeyValue(circle.translateYProperty(), random() * 60)));
		}
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	private BorderPane createButtonGo() {
		BorderPane borderPane = new BorderPane();
		
		
		goButton.setText("GO");
		goButton.setId("shiny-orange");
		
		goButton.setAlignment(Pos.CENTER);
		goButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mindwaveThread = new Thread(new Mindwave(data));
				mindwaveThread.start();
				goButton.setVisible(false);
				changeCalculationText();
				
				PauseTransition wait = new PauseTransition(Duration.seconds(5));
				wait.setOnFinished((e) -> {
					changeAnimation();
					wait.playFromStart();
				});
				wait.play();
				
				
				PauseTransition counter = new PauseTransition(Duration.seconds(1));
				counter.setOnFinished((e) -> {
					counterText.setText(""+time);
					time = time - 1;
					if ( time >= 0 ){
						counter.playFromStart();	
					}else{
						goButton.setVisible(true);
						time = 50;						
					}
					
					
					
				});
				counter.play();

			}
		});
		borderPane.setPadding(new Insets(50));
		borderPane.setBottom(goButton);
		return borderPane;
	}

	public void changeAnimation() {
		double rate = data.get();
		if (rate != 0) {		
			rate = rate / 100 + 1;
			timeline.setRate(rate);
			System.out.println("changeAnimation: " + rate);
			timeline.play();					
		}

		
		changeCalculationText();
	}

	
	private void exit() {
		
    	if ( mindwaveThread != null ){
    		mindwaveThread.interrupt();	
    	}
    	portCommunication.close();
        Platform.exit();
        System.exit(0);
	}
	
	
	private void changeCalculationText(){
		Random random = new Random();
		int randomNumber1 = random.nextInt(10) ;
		int randomNumber2 = random.nextInt(10) ;
		int randomSymbole = random.nextInt(5);
		String symbol = "+";
		if ( randomSymbole > 2) {
			symbol = "-";
		}
		if ( randomSymbole > 4) {
			symbol = "/";
		}		
		this.calculationText.setText("" + randomNumber1 + symbol + randomNumber2);
	}
	
	
	private void playMusic(){
		
		
		
		
		Media media = new Media( new File("2_Andante.mp3").toURI().toString() );
		this.mediaPlayer = new MediaPlayer(media); 
		this.mediaPlayer.setOnEndOfMedia(new Runnable() {
	        @Override public void run() {
	        	mediaPlayer.stop();
	            
	            Media media = new Media( new File("amclassical_piano_sonata_k_310_mvt_1.mp3").toURI().toString() );       
	            MediaPlayer player1 = new MediaPlayer(media); 
	            player1.play();
	          }
	        });
		mediaPlayer.play();
		  
	}
	
}