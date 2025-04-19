/*CSC 1054 Spring 2025 Project
Game that tracks a player in outer space. The object is to travel as far away
from the start position as possible while avoiding the mines
Blake Morgan 18-Apr-2025
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
   //create a stackpane to stack the different elements on top of each other
   StackPane root;
   Canvas theCanvas = new Canvas(600,600);
   
   //add the player and a start position object
   ThePlayer thePlayer = new ThePlayer(300, 300);
   StartPosition startPosition = new StartPosition(300, 300);
   
   //create two labels for the current score and the highscore
   Label score = new Label("");
   Label highscore = new Label("\n\n    High Score is: 0");
   
   //create ArrayLists for which keys are being held down,
   //which grids have been generated,
   //and which mines are active in the game
   List<KeyCode> keysHeld = new ArrayList<>();
   List<String> generatedGrids = new ArrayList<>();
   List<Mine> allMines = new ArrayList<>();
   
   //add variables to keep track of if the game ended,
   //the high score value,
   //and which mine was the one that the player collided with
   private boolean gameOver = false;
   private double highScoreValue = 0;
   Mine collidedMine = null;
   
   GraphicsContext gc;
   
   //setup background
   Image background = new Image("stars.png");
   Image overlay = new Image("starsoverlay.png");
   Random backgroundRand = new Random(); 

   public void start(Stage stage)
   {
      //add the canvas, the score and the highscore to the stackpane
      //set both labels to the top left
      root = new StackPane();
      root.getChildren().addAll(theCanvas, score, highscore);
      StackPane.setAlignment(score, Pos.TOP_LEFT);
      StackPane.setAlignment(highscore, Pos.TOP_LEFT);
      gc = theCanvas.getGraphicsContext2D();
      drawBackground(300, 300, gc);
      
      //color the labels white
      score.setStyle("-fx-text-fill: white;");
      highscore.setStyle("-fx-text-fill: white;");
            
      //draw the player  
      thePlayer.draw(300, 300, gc, true);
      
      //setup key listeners
      root.setOnKeyPressed(new KeyListenerDown());
      root.setOnKeyReleased(new KeyListenerUp());
      
      //load the previous highscore
      loadHighScore();
           
      Scene scene = new Scene(root, 600, 600);
      stage.setScene(scene);
      stage.setTitle("Project :)");
      stage.show();
      
      //setup animation handler
      AnimationHandler ah = new AnimationHandler();
      ah.start();
      
      //request focus to the root
      root.requestFocus();  
   }
   
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
         if (!gameOver)
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
            
            //get players current grid
            int cgridx = ((int)thePlayer.getX()) / 100;
            int cgridy = ((int)thePlayer.getY()) / 100;
            
            // Store already generated grids
            String gridKey = cgridx + "," + cgridy;
            if (!generatedGrids.contains(gridKey))
            {
                generatedGrids.add(gridKey);
            
                for (int dx = -4; dx <= 5; dx++)
                {
                    for (int dy = -4; dy <= 5; dy++)
                    {
                        int adx = Math.abs(dx);
                        int ady = Math.abs(dy);
            
                        // Only generate mines in grids 3 or 4 away
                        boolean isFarEnough = (adx >= 3 || ady >= 3) && (adx <= 4 && ady <= 4);
            
                        if (isFarEnough)
                        {
                            int gridx = cgridx + dx;
                            int gridy = cgridy + dy;
            
                            float gx = gridx * 100;
                            float gy = gridy * 100;
            
                            //call distance to point method we created in drawable object
                            //to prevent us from having to create dummy drawable objects
                            double dist = thePlayer.distanceToPoint(gx, gy);
                            
                            //calculate the number of mine spawn attempts we will make
                            int N = Math.max(1, (int)(dist / 1000));
                            for (int i = 0; i < N; i++)
                            {
                                //30% chance of spawning a mine
                                //add it to our allMines ArrayList
                                if (Math.random() < 0.3)
                                {
                                    float offsetX = (float)(Math.random() * 100);
                                    float offsetY = (float)(Math.random() * 100);
                                    allMines.add(new Mine(gx + offsetX, gy + offsetY));
                                }
                            }
                        }
                    }
                }
            }
                   
            //clear the previous drawing
            gc.clearRect(0,0,600,600);
            //draw the background
            drawBackground(thePlayer.getX(), thePlayer.getY(), gc);
            //draw the player
            thePlayer.draw(300,300,gc,true);
            //update the score label (display as an int)
            score.setText("    Score is: " + (int) thePlayer.distance(startPosition));
            
            //if collision happens, set collidedMine to the one that caused the collision
            if (!gameOver)
            {
               for (Mine m : allMines)
               {
                  if (thePlayer.distance(m) <= 26)
                  {
                     gameOver = true;
                     collidedMine = m;
   
                     // Check for new high score
                     double currentScore = thePlayer.distance(startPosition);
                     if (currentScore > highScoreValue)
                     {
                        highScoreValue = currentScore;
                        highscore.setText("\n\n    High Score is: " + (int)highScoreValue);
                        saveHighScore(currentScore);
                     }
   
                     break;
                  }
               }
            }
            
            //draw and remove mines
            //iterate through the mines list in reverse order to remove elements without causing errors
            //or skipping items due to the list changing while we’re iterating over it 
            for (int i = allMines.size() - 1; i >= 0; i--)
            {
                Mine m = allMines.get(i);
                
                if (m == collidedMine)
                {
                    continue; // Don't draw the mine that collided with the player
                }
                
                //remove mines that are greater than 800 distance away from the player
                if (thePlayer.distance(m) > 800)
                {
                    allMines.remove(i);
                }
                else
                {
                    m.draw(thePlayer.getX(), thePlayer.getY(), gc, false);
                }
            }
         }
         else
         {
            // When game is over, still draw background, just don’t draw player
            gc.clearRect(0,0,600,600);
            drawBackground(thePlayer.getX(), thePlayer.getY(), gc);
            score.setText("    Score is: " + (int) thePlayer.distance(startPosition));
            for (int i = allMines.size() - 1; i >= 0; i--)
            {
                Mine m = allMines.get(i);
                
                if (m == collidedMine)
                {
                    continue; // Don't draw the mine that collided with the player
                }
                
                if (thePlayer.distance(m) > 800)
                {
                    allMines.remove(i);
                }
                else
                {
                    m.draw(thePlayer.getX(), thePlayer.getY(), gc, false);
                }
            }                  
         }  
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
         if (!keysHeld.contains(event.getCode()))
         {
            keysHeld.add(event.getCode());
         }                           
      }
   }
   public class KeyListenerUp implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      {
         keysHeld.remove(event.getCode());  
      }
   } 
   
   public void loadHighScore()
   {
      File file = new File("highscore.txt");
      if (file.exists())
      {
         try (Scanner scanner = new Scanner(file))
         {
            if (scanner.hasNextDouble())
            {
               highScoreValue = scanner.nextDouble();
               highscore.setText("\n\n    High Score is: " + (int)highScoreValue);
            }
         }
         catch (IOException e)
         {
            System.out.println("Failed to load highscore.");
         }
      }
   }
   
   public void saveHighScore(double score)
   {
      try (PrintWriter out = new PrintWriter("highscore.txt"))
      {
         out.println(score);
      }
      catch (IOException e)
      {
         System.out.println("Failed to save highscore.");
      }
   }     
}