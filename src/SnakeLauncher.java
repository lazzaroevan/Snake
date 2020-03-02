import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Random;
import javax.sound.sampled.*;

public class SnakeLauncher extends Application {
    String direction = "Right";
    int speed = 100;
    boolean gameover = false;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Random rand = new Random();
        Group root = new Group();
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 800, 510);
        primaryStage.setScene(scene);
        primaryStage.show();
        Rectangle firstApple = new Rectangle();
        firstApple.setX((20*(rand.nextInt((int)(scene.getWidth()/20)))));
        firstApple.setY(30+(20*(rand.nextInt((int)(scene.getHeight()/20)-1))));
        firstApple.setWidth(20);
        firstApple.setHeight(20);
        firstApple.setFill(Paint.valueOf("RED"));
        root.getChildren().add(firstApple);
        SnakeBody mainBody = new SnakeBody();
        root.getChildren().add(mainBody);
        Line line = new Line();
        Label scoreLabel = new Label("Score: 0");
        line.setStartX(0);
        line.setStartY(30);
        line.setEndX(scene.getWidth());
        line.setEndY(30);
        scoreLabel.setFont(Font.font(18));
        root.getChildren().add(line);
        Button speedUpButton = new Button("Speed Up");
        Button slowDownButton = new Button("Slow down");
        HBox scoreTrackHB = new HBox();
        Label speedLabel = new Label("Speed: 0");
        scoreTrackHB.getChildren().addAll(scoreLabel,speedLabel,speedUpButton,slowDownButton);
        scoreTrackHB.setSpacing(scene.getWidth()/(scoreTrackHB.getChildren().size()+1));
        root.getChildren().add(scoreTrackHB);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100),ae->move(direction,root,mainBody,primaryStage,scoreLabel)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        speedUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timeline.setRate(timeline.getCurrentRate()+1);
                speedLabel.setText("Speed: " + (-timeline.getCurrentRate()));
            }
        });
        speedUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timeline.setRate(timeline.getCurrentRate()-1);
                speedLabel.setText("Speed: " + (-timeline.getCurrentRate()));
            }
        });
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode() == KeyCode.W)
                {
                    direction = ("Up");
                }
                if(keyEvent.getCode() == KeyCode.A)
                {
                    direction =("Left");
                }
                if(keyEvent.getCode() == KeyCode.S)
                {
                    direction =("Down");
                }
                if(keyEvent.getCode() == KeyCode.D)
                {
                    direction =("Right");
                }
                if(keyEvent.getCode() ==KeyCode.SPACE)
                {

                }
            }
        });
    }
    public boolean move(String direction, Group root,SnakeBody mainHead,Stage primaryStage,Label score)
    {
        Random rand = new Random();
        ObservableList rootChildren =root.getChildren();
        SnakeBody snakebodyFirst = (SnakeBody)(root.getChildren().get(1));
        boolean appleEaten = false;
        double xval = snakebodyFirst.getX();
        double yval = snakebodyFirst.getY();
        boolean upBool = yval>=50 ;
        boolean leftBool = xval>=20;
        boolean downBool = yval+65-root.getScene().getWindow().getHeight()<=0;
        boolean rightBool = xval+40-root.getScene().getWindow().getWidth()<=0;
        if(snakebodyFirst.getX()==(((Rectangle)rootChildren.get(0)).getX())&snakebodyFirst.getY()==(((Rectangle)rootChildren.get(0)).getY()))
        {
            System.out.println("Apple Eaten");
            try
            {
                InputStream in = getClass().getResourceAsStream("eatAppleTone.wav");
                InputStream bufferedIn = new BufferedInputStream(in);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            appleEaten = true;
            ((Rectangle)rootChildren.get(0)).setX((20*(rand.nextInt((int)(root.getScene().getWidth()/20)))));
            ((Rectangle)rootChildren.get(0)).setY(30+(20*(rand.nextInt((int)(root.getScene().getHeight()/20)-1))));
            score.setText("Score: "+(Integer.parseInt(score.getText().substring(7))+1));
        }
        switch (direction)
        {
            case "Up": {if(upBool){snakebodyFirst.setY(yval-20);}break; }
            case "Down": {if(downBool){snakebodyFirst.setY(yval+20);}break;}
            case "Left": {if(leftBool) {snakebodyFirst.setX(xval-20);}break;}
            case "Right":{if(rightBool){snakebodyFirst.setX(xval+20);}break;}
        }
        for(int i = 0 ; i < rootChildren.size();i++)
        {
            try
            {
                SnakeBody snakeBodyTemp = (SnakeBody)rootChildren.get(i);
                double tempy = snakeBodyTemp.getY();
                double tempx = snakeBodyTemp.getX();
                snakeBodyTemp.setX(xval);
                snakeBodyTemp.setY(yval);
                if(mainHead.getX()==tempx & mainHead.getY()==tempy)
                {
                    if(!gameover)
                    try
                    {
                        InputStream in = getClass().getResourceAsStream("gameOver.wav");
                        InputStream bufferedIn = new BufferedInputStream(in);
                        AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioIn);
                        clip.start();
                        gameover = true;
                    }
                    catch (Exception e)
                    {
                        System.out.println(e);
                    }
                    Thread.sleep(2000);
                    primaryStage.close();
                }
                xval=tempx;
                yval=tempy;
            }
            catch(Exception e)
            {
                i++;
            }
        }
        if(appleEaten)
        {
            eatApple(root,xval,yval);
        }
        return false;
    }
    private void eatApple(Group root,double x,double y)
    {
        SnakeBody newBody = new SnakeBody(x,y);
        root.getChildren().add(newBody);
    }


    public static void main(String[] args) {
        launch(args);
    }
}

class SnakeBody extends Rectangle {
    public SnakeBody(double x, double y)
    {
        this.setHeight(20);
        this.setWidth(20);
        this.setFill(Paint.valueOf("Blue"));
        this.setX(x);
        this.setY(y);
        this.setStrokeType(StrokeType.INSIDE);
    }
    public SnakeBody()
    {
        this.setHeight(20);
        this.setWidth(20);
        this.setX(0);
        this.setY(30);
        this.setFill(Paint.valueOf("Green"));
        this.setStrokeType(StrokeType.INSIDE);
    }
    public Node getStyleableNode() {
        return null;
    }
}