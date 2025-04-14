/*CSC 1054 Spring 2025 Project
Game that tracks a player in outer space. The object is to travel as far away
from the start position as possible while avoiding the mines
Blake Morgan 13-Apr-2025
*/

import java.io.*;
import java.lang.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class Main extends Application
{
   StackPane root;
   
   Canvas theCanvas = new Canvas(600,600);
   ThePlayer thePlayer = new ThePlayer(300, 300);
   StartPosition startPosition = new StartPosition(300, 300);
   Label score = new Label("");
   Label highscore = new Label("\n\n    High Score is: 0");
   private Set<KeyCode> keysHeld = new HashSet<>();

   public void start(Stage stage)
   {
      root = new StackPane();
      root.getChildren().addAll(theCanvas, score, highscore);
      StackPane.setAlignment(score, Pos.TOP_LEFT);
      StackPane.setAlignment(highscore, Pos.TOP_LEFT);
      gc = theCanvas.getGraphicsContext2D();
      drawBackground(300, 300, gc);
      
      score.setStyle("-fx-text-fill: white;");
      highscore.setStyle("-fx-text-fill: white;");
         
      thePlayer.draw(300, 300, gc, true);
      
      root.setOnKeyPressed(new KeyListenerDown());
      root.setOnKeyReleased(new KeyListenerUp());
           
      Scene scene = new Scene(root, 600, 600);
      stage.setScene(scene);
      stage.setTitle("Project :)");
      stage.show();
      
      AnimationHandler ah = new AnimationHandler();
      ah.start();
      
      root.requestFocus();  
   }
   
   GraphicsContext gc;
   
   Image background = new Image("stars.png");
   Image overlay = new Image("starsoverlay.png");
   Random backgroundRand = new Random();
   
   //this piece of code doesn't need to be modified
   public void drawBackground(float playerx, float playery, GraphicsContext gc)
   {
	   //re-scale player position to make the background move slower. 
      playerx*=.1;
      playery*=.1;
   
	   //figuring out the tile's position.
      float x = (playerx) / 400;
      float y = (playery) / 400;
      
      int xi = (int) x;
      int yi = (int) y;
      
	   //draw a certain amount of the tiled images
      for(int i=xi-3;i<xi+3;i++)
      {
         for(int j=yi-3;j<yi+3;j++)
         {
            gc.drawImage(background,-playerx+i*400,-playery+j*400);
         }
      }
      
	   //below repeats with an overlay image
      playerx*=2f;
      playery*=2f;
   
      x = (playerx) / 400;
      y = (playery) / 400;
      
      xi = (int) x;
      yi = (int) y;
      
      for(int i=xi-3;i<xi+3;i++)
      {
         for(int j=yi-3;j<yi+3;j++)
         {
            gc.drawImage(overlay,-playerx+i*400,-playery+j*400);
         }
      }
   }
     
   public class AnimationHandler extends AnimationTimer
   {
      public void handle(long currentTimeInNanoSeconds) 
      {  
         //increase force
         if (keysHeld.contains(KeyCode.W))
         {
            thePlayer.applyForceY(-0.1f);
         }
         if (keysHeld.contains(KeyCode.S))
         {
            thePlayer.applyForceY(0.1f);
         }
         if (keysHeld.contains(KeyCode.A))
         {
            thePlayer.applyForceX(-0.1f);
         }
         if (keysHeld.contains(KeyCode.D))
         {
            thePlayer.applyForceX(0.1f);
         }

         //decrease force
         if (!keysHeld.contains(KeyCode.W) && !keysHeld.contains(KeyCode.S))
         {
             thePlayer.applyDecayY();
         }
         if (!keysHeld.contains(KeyCode.A) && !keysHeld.contains(KeyCode.D))
         {
             thePlayer.applyDecayX();
         }
         
         //update players position
         thePlayer.updatePosition();         
                 
         //clear the previous drawing
         gc.clearRect(0,0,600,600);
         //draw the background
         drawBackground(thePlayer.getX(), thePlayer.getY(), gc);
         //draw the player
         thePlayer.draw(300,300,gc,true);
         //update the label (display as an int)
         score.setText("    Score is: " + (int) thePlayer.distance(startPosition));
         
             
 
         //example call of a draw where m is a non-player object. Note that you are passing the player's position in and not m's position.
         //m.draw(thePlayer.getX(),thePlayer.getY(),gc,false);
         
      }
   }

   public static void main(String[] args)
   {
      launch(args);
   }
   
   public class KeyListenerDown implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      {
         keysHeld.add(event.getCode());                           
      }
   }
   public class KeyListenerUp implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      {
         keysHeld.remove(event.getCode());  
      }
   }   
}