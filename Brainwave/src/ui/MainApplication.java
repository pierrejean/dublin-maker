package ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import main.process.Mindwave;

import static java.lang.Math.random;

import java.util.concurrent.atomic.AtomicInteger;

import com.sun.javafx.tk.FontMetrics;

public class MainApplication extends Application {

	// private MindwaveMobile mindwaveMobile = null;
	private Thread mindwaveThread = null;
	private Timeline timeline = null;
	private int time = 50;
	private Text counterText = new Text();
	private Button goButton = new Button();
	private final AtomicInteger data = new AtomicInteger(0);
	
	public static int WIDTH=1600;
	public static int HEIGHT=825;
	public static int CIRCLES=25;
	
	// private CubicCurve cubic = new CubicCurve();

	public static void main(String[] args) {
		launch(args);
	}

	public MainApplication() {
		// mindwaveMobile = new MindwaveMobile( );
		timeline = new Timeline();
	}
	
//	private void drawLine(Group root, float x0, float y0, float x1, float y1 ){
//		Line line = new Line();
//		line.setStartX( x0 );
//		line.setStartY( y0 );
//		line.setEndX( x1 );
//		line.setEndY( y1);
//		line.setStrokeWidth(2f);
//		line.setStroke(Color.ALICEBLUE);
//		
//		root.getChildren().add(line);s
//	}
	

//	private void moveCurveOnX( float minuX){
//		cubic.setStartX( cubic.getStartX() - minuX);
//		// cubic.setStartY( cubic.getStartY() - minuX);
//		cubic.setControlX1( cubic.getControlX1() - minuX);
//		//cubic.setControlY1( cubic.getControlY1() - minuX);
//		cubic.setControlX2( cubic.getControlX2() - minuX);
//		//cubic.setControlY2( cubic.getControlY2() - minuX);
//		cubic.setEndX( cubic.getEndX() - minuX );
//		// cubic.setEndY( cubic.getEndY() - minuX );
//	}
	
	
//	private void drawCurve(Group root, float x0, float y0, float x1, float y1 ){
//
//		
//		cubic.setStartX(x0);
//		cubic.setStartY(y0);
//		cubic.setControlX1(25.0f);
//		cubic.setControlY1(0.0f);
//		cubic.setControlX2(25.0f);
//		cubic.setControlY2(100.0f);
//		cubic.setEndX(x1);
//		cubic.setEndY(y1);
//		cubic.setStrokeWidth(15f);
//		cubic.setStroke(Color.ALICEBLUE);
//		cubic.setStyle("-fx-smooth: true");
//		
//		root.getChildren().add(cubic);
//	}

	@Override
	public void start(Stage primaryStage) {
		Group root = new Group();
		Scene scene = new Scene(root, MainApplication.WIDTH , MainApplication.HEIGHT, Color.WHITE);
		primaryStage.setScene(scene);
		scene.getStylesheets().add(MainApplication.class.getResource("style.css").toExternalForm());
		
		createCircles(root, scene);

		root.getChildren().add( createButtonGo() );
		
		// drawCurve(root , 0 , 0 , 100 , 200);
		root.getChildren().add(  createCounterText() );

		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
            	if ( mindwaveThread != null ){
            		mindwaveThread.interrupt();	
            	}
                Platform.exit();
                System.exit(0);
            }
        });
		
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
		
		
		
		

	}

}