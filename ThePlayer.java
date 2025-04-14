/*CSC 1054 Spring 2025 Project
The player class that controls the main player in the game
Blake Morgan 13-Apr-2025
*/

import javafx.scene.paint.*;
import javafx.scene.canvas.*;

public class ThePlayer extends DrawableObject
{
   private float forceX = 0;
   private float forceY = 0;
   
   //takes in its position
   public ThePlayer(float x, float y)
   {
      super(x, y);
   }

   //draws itself at the passed in x and y
   public void drawMe(float x, float y, GraphicsContext gc)
   {
      gc.setFill(Color.BLACK);
      gc.fillOval(x-14, y-14, 35, 35);
      gc.setFill(Color.GRAY);
      gc.fillOval(x-13, y-13, 33, 33);
   }
   
   public void applyForceX(float value) 
   {
      forceX += value;
      if (forceX > 5) forceX = 5;
      if (forceX < -5) forceX = -5;
   }
   
   public void applyForceY(float value) 
   {
      forceY += value;
      if (forceY > 5) forceY = 5;
      if (forceY < -5) forceY = -5;
   }
   
   public void applyDecayX() 
   {
      if (Math.abs(forceX) < 0.25f)
      {
         forceX = 0;
      }
      else if (forceX > 0)
      {
         forceX -= 0.025f;
      }
      else if (forceX < 0)
      {
         forceX += 0.025f;
      }
  }
   
   public void applyDecayY() 
   {
      if (Math.abs(forceY) < 0.25f)
      {
         forceY = 0;
      }
      else if (forceY > 0)
      {
         forceY -= 0.025f;
      }
      else if (forceY < 0)
      {
         forceY += 0.025f;
      }
   }
   
   public void updatePosition() 
   {
      setX(getX() + forceX);
      setY(getY() + forceY);
   }   
}