/*CSC 1054 Spring 2025 Project
The Mine class that controls when and how the mines spawn into the game
Blake Morgan 13-Apr-2025
*/

import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import java.util.Random;

public class Mine extends DrawableObject
{  
   private final Random rand = new Random();
   //generate a random value as the start value for our green and blue variables
   private double green = rand.nextDouble();
   private double blue = green;
   //increase represents the direction the color of the mine is changing
   private boolean increase = true;
   
   //takes in its position
   public Mine(float x, float y)
   {
      super(x, y);
   }

   //draws itself at the passed in x and y
   public void drawMe(float x, float y, GraphicsContext gc)
   {
      gc.setFill(Color.BLACK);
      gc.fillOval(x-14, y-14, 16, 16);
      gc.setFill(Color.color(1, green, blue));
      gc.fillOval(x-13, y-13, 14, 14);
      
      //if the color of the mine gets to fully white or fully red, 
      //change the direction of the oscillation back to the other color
      if (green < .0015)
      {
         increase = true;
      }
      if  (green > .9985)
      {
         increase = false;
      }     
      if (increase)
      {
         green += .0015;
         blue += .0015;
      }
      else 
      {
         green -= .0015;
         blue -= .0015;      
      } 
   }  
}