/*CSC 1054 Spring 2025 Project
DrawableObject class. Both the player and the mines inherit from this class
Blake Morgan 18-Apr-2025
*/

import javafx.scene.paint.*;
import javafx.scene.canvas.*;

public abstract class DrawableObject
{
   public DrawableObject(float x, float y)
   {
      this.x = x;
      this.y = y;
   }

   //positions
   protected float x;
   protected float y;
   
   //takes the position of the player and calls draw me with appropriate positions
   public void draw(float playerx, float playery, GraphicsContext gc, boolean isPlayer)
   {  
      if(isPlayer)
         drawMe(playerx,playery,gc);
      else
         drawMe(-playerx+300+x,-playery+300+y,gc);
   }
   
   //NOTE: DO NOT CALL DRAWME YOURSELF. Let the the "draw" method do it for you. I take care of the math in that method for a reason.
   public abstract void drawMe(float x, float y, GraphicsContext gc);
   public void act()
   {
   }
   
   public float getX(){return x;}
   public float getY(){return y;}
   public void setX(float x_){x = x_;}
   public void setY(float y_){y = y_;}
   
   public double distance(DrawableObject other)
   {
      return (Math.sqrt((other.x-x)*(other.x-x) + (other.y-y)*(other.y-y)));
   }
   
   //added a distance to point method so I wouldn't need to create extra dummy objects in the main program
   //when measuring and calculating the grid for spawning mines
   public double distanceToPoint(float x, float y)
   {
       float dx = this.getX() - x;
       float dy = this.getY() - y;
       return Math.sqrt(dx * dx + dy * dy);
   }   
}