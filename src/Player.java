/* This is the pacman object */
public class Player extends Mover {
  char currDirection, desiredDirection;

  /* Keeps track of pellets eaten to determine end of game */
  int pelletsEaten;

  /* Last location. It's pixel coordinates. */
  int lastX, lastY;
 
  /* Current location. It's pixel coordinates. */
  int x, y;
 
  /* Which pellet the pacman is on top of. They're array indices. */
  int pelletX, pelletY;

  /* teleporting is true when travelling through the teleport tunnels. 
   * All teleports are saved @ Data.class as protected static boolean teleport[][]; */ 
  boolean teleporting;
  
  /* Stopped is set when the pacman is not moving or has been killed. (see also lastX==x, etc) */
  boolean stopped = false;
  
  /* Constructor places pacman in initial location and orientation. */
  protected Player(int x, int y) {
	if ((x<gridSize) && (y<gridSize)){
		x=x*gridSize;
		y=y*gridSize; 
	}
    teleporting=false;
    pelletsEaten=0;
    pelletX = x/gridSize;
    pelletY = y/gridSize;
    this.lastX=x;
    this.lastY=y;
    this.x = x;
    this.y = y;
    currDirection='L';
    desiredDirection='L';
  }

  /* The move function moves the pacman for one frame */
  protected void move() {
    lastX=x;
    lastY=y; 
    
    /* Try to turn in the direction input by the user */
    /* Can only turn if in center of a square (24x24pixels) */
    if (x %gridSize==0 && y%gridSize==0 ||	
       /* Or if reversing*/
       (desiredDirection=='L' && currDirection=='R')  ||
       (desiredDirection=='R' && currDirection=='L')  ||
       (desiredDirection=='U' && currDirection=='D')  ||
       (desiredDirection=='D' && currDirection=='U')) {
      
      if ((x %gridSize==0 && y%gridSize==0) && (speedScaredIncr || speedScaredDecr || speedPelletsIncreased)) {
    	  if (speedPelletsIncreased && !gotIN) {
    		  increment= (int) Math.round(increment*1.3); if (increment==5) increment=6; // for animation purposes
    		  gotIN=true;
    	  }
    	  
    	  if (speedScaredIncr && !gotINIncr) {
    		  increment= (int) Math.round(increment*1.2); if (increment==5) increment=6; // for animation purposes
    		  gotINIncr=true;
    	  }
    	  else if (speedScaredDecr) {
    		  increment= (int) Math.round(increment/1.2); if (increment==5) increment=4; // for animation purposes 
        	  speedScaredDecr=false;
    		  gotINIncr=false;
    	  }
      }
      
      switch(desiredDirection) {
        case 'L':
           if ( isValidDest(x-increment,y)) // It's increment because each square is painted from up-left corner
             x -= increment;				
           break;     
        case 'R':
           if ( isValidDest(x+gridSize,y))  // 24x24pixels
             x+= increment;
           break;     
        case 'U':
           if ( isValidDest(x,y-increment)) // It's increment because each square is painted from up-left corner
             y-= increment;
           break;     
        case 'D':
           if ( isValidDest(x,y+gridSize))  // 24x24pixels
             y+= increment;
           break;     
      }
    }
    
    /* NEW_IF: If Pacman hasn't moved, then move in the direction Pacman was headed anyway 
    *  i 	  j<->x WIDTH
	* HEIGHT
    *  y */
    if (lastX==x && lastY==y) {
      switch(currDirection) {
        case 'L':
           if ( isValidDest(x-increment,y))
             x -= increment;
           else if (Data.teleport[x/gridSize][y/gridSize]) { // x-increment < 0
        	   teleporting=true;
        	   x = Data.width-gridSize;
           }
           break;     
        case 'R':
           if ( isValidDest(x+gridSize,y))
             x+= increment;
           else if (Data.teleport[x/gridSize][y/gridSize]) { // x+increment > Data.width
        	   teleporting=true;
        	   x = 0;
           }
           break;     
        case 'U':
           if ( isValidDest(x,y-increment))
             y-= increment;
           break;     
        case 'D':
           if ( isValidDest(x,y+gridSize))
             y+= increment;
           break;
      }
    }
    else currDirection=desiredDirection; /* If we did change direction, update currDirection to reflect that */
        
    /* NEW_IF: If still didn't move at all, set the stopped flag */    
    if (lastX == x && lastY==y)
      stopped=true;  
    /* Otherwise, clear the stopped flag and increase the frameCount for animation purposes. */
    else {
      stopped=false;
      frameCount++;
    }
  }

  /* Update ON WHAT pellet the pacman is on top of */
  protected void updatePellet() {  
    if (x%gridSize ==0 && y%gridSize == 0) {
    pelletX = x/gridSize;
    pelletY = y/gridSize;
    } // Just find which pellet for the 'pellets[][]' boolean array
  } 
}