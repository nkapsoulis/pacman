/* Ghost class controls the ghost. */
import java.util.Random;

public class Ghost extends Mover {
  /* Direction ghost is heading */
  char direction='L';

  /* Last ghost location. It's coordinates. */
  int lastX, lastY;

  /* Current ghost location. It's coordinates. */
  int x, y;
  
  /* Pacman's coordinates when ghost can see it in <= 3 blocks distance. */
  int closeX=0, closeY=0; 

  /* The pellet the ghost IS on top of. 'pellets[][]' boolean array indices. */
  int pelletX,pelletY;

  /* The pellet the ghost WAS last on top of. 'pellets[][]' boolean array indices. */
  int lastPelletX,lastPelletY;
  
  /* When 60% of cookies eaten, increase each ghost's speed by 30%. */
  boolean speedPelletsIncreased=false;
  
  /* Scared! */
  boolean scared=false;  
  
  /* Constructor places ghost and updates states. */
  protected Ghost(int x, int y) {
    if ((x<gridSize) && (y<gridSize)){
		x=x*gridSize;
		y=y*gridSize;
	}
    direction='L';
    pelletX=x/gridSize; // _/gridSize: talking about indices, not pixels!
    pelletY=y/gridSize; 
    lastPelletX=pelletX;
    lastPelletY=pelletY;
    lastX = x; 			  
    lastY = y;			  
    this.x = x;
    this.y = y;
  }

  /* Update pellet status */
  protected void updatePellet() {
    int tempX,tempY;
    tempX = x/gridSize;
    tempY = y/gridSize;
    if (tempX != pelletX || tempY != pelletY) {
      lastPelletX = pelletX;
      lastPelletY = pelletY;
      pelletX=tempX;
      pelletY=tempY;
    }     
  } 
 
  /* Determines if the location is one where the ghost has to make a decision*/
  private boolean isChoiceDest() {
    if (x%gridSize==0&& y%gridSize==0) {
    	if (speedScaredIncr || speedScaredDecr || speedPelletsIncreased) {
      	  if (speedPelletsIncreased && !gotIN) {
      		  // for animation purposes
      		  increment= (int) Math.round(increment*1.3);
      		  gotIN=true;
      	  }
      	  
      	  if (speedScaredDecr && !gotINDecr) {
      		  increment= (int) Math.round(increment*0.9);
      		  gotINDecr=true;
      	  }
      	  else if (speedScaredIncr) {
      		  increment= (int) Math.round(increment/0.9);
          	  speedScaredIncr=false;
      		  gotINDecr=false;
      		  scared=false;
      	  }
        }
    	
    	return true;      
    }
    return false;
  }

  private char newDirection(char[] odd) { 

	char backwards='_';
    switch(direction) {
      case 'L':
         backwards='R'; break;  
      case 'R':
         backwards='L'; break;     
      case 'U':
         backwards='D'; break;     
      case 'D':
         backwards='U'; break;     
    }
	
	char dirs[]=new char[]{};
    switch(odd.length) {
		case 0:
			boolean tempDir[] = new boolean[]{false,false,false,false}; // L,R,U,D
			if ( isValidDest(x-increment,y)) tempDir[0]=true; //L
			if ( isValidDest(x+gridSize,y)) tempDir[1]=true; //R
			if ( isValidDest(x,y-increment)) tempDir[2]=true; //U
			if ( isValidDest(x,y+gridSize)) tempDir[3]=true; //D
			int countTrue=0;
			for(int i=0;i<tempDir.length;i++)
				if (tempDir[i]) countTrue++;
			if (countTrue==1) { // only 1 direction possible! ONLY BACKWARDS 
				int position;
				for (position=0;position<tempDir.length;position++)
					if (tempDir[position]) break;
				switch(position){
					case 0: return 'L';
					case 1: return 'R';
					case 2: return 'U';
					case 3: return 'D';
				}
			}
			else if (countTrue>1) { // countTrue>1
				switch(direction) {
			      case 'L':
			         tempDir[1]=false; break; // R, cause it's backwards
			      case 'R':
			    	 tempDir[0]=false; break; // L, cause it's backwards
			      case 'U':
			    	 tempDir[3]=false; break; // D, cause it's backwards 
			      case 'D':
			    	 tempDir[2]=false; break; // U, cause it's backwards  
			    }
				countTrue=0;
				for(int i=0;i<tempDir.length;i++)
					if (tempDir[i]) countTrue++; 
				dirs = new char[countTrue];
				int count=0;
				if (tempDir[0]) {
					dirs[count]='L';
					count++;
				}
				if (tempDir[1]) {
					dirs[count]='R';
					count++;
				}
				if (tempDir[2]) {
					dirs[count]='U';
					count++;
				}
				if (tempDir[3]) {
					dirs[count]='D';
					count++;
				}
			}
			break;
		case 2:
			if (odd[0]=='L' || odd[0]=='R') 
				switch(backwards) {
			      /*case 'U':
			    	 System.out.println("It's impossible! U");
					 return 'D';
			      case 'D':
				     System.out.println("It's impossible! D");
					 return 'U';*/
			      default:
				     dirs = new char[]{'U', 'D'};
				     break;
			    }
			else 
			if (odd[0]=='U' || odd[0]=='D') 
				switch(backwards) {
			      /*case 'R':
			    	 System.out.println("It's impossible! R");
					 return 'R';
			      case 'L':
				     System.out.println("It's impossible! L");
					 return 'L';*/
			      default:
					 dirs = new char[]{'R', 'L'};
				     break;
			    }
			break;
     	default: // unecessary
			System.out.println("Never call with odd.length ==1 OR >=3!"); dirs = new char[]{'R', 'L', 'U'};
			break;
	}
		
    /* Now dirs (=all possible directions so far) is a char[] with length 2 OR 3 */
    int rnd = new Random().nextInt(dirs.length);
    return dirs[rnd];    
  }

  /* Random move function */
  protected void move() {
    lastX=x;
    lastY=y;
    if (isChoiceDest()) {
	     boolean gotIN=false;
	     if ((closeX == x && Math.abs(closeY-y) <= 3*gridSize)||
	    	 (closeY == y && Math.abs(closeX-x) <= 3*gridSize)){
	    	if (closeX == x && Math.abs(closeY-y) <= 3*gridSize){
	    		if (isValidDest(x,(closeY+y)/2)          && state[x/gridSize][(closeY+y)/2/gridSize] &&
	    			isValidDest(x,(closeY+y)/2-gridSize) && state[x/gridSize][(closeY+y)/2/gridSize-1] &&
	    			isValidDest(x,(closeY+y)/2+gridSize) && state[x/gridSize][(closeY+y)/2/gridSize+1]) {
	    			if(closeY>y)
	    				if (!scared) direction='D';         // Go TOWARDS Pacman
	    				else  {								// If Scared, avoid Pacman.
	    					if ( isValidDest(x,y-increment))
	    						direction='U';
	    					else 
	    						direction = newDirection(new char[]{'U', 'D'}); // exclude 'U','D'
	    				}
	    			else
	    				if (!scared) direction='U'; 		// Go TOWARDS Pacman
	    				else { 								// If Scared, avoid Pacman.
	    					if ( isValidDest(x,y+gridSize)) 
	    						direction='D';
	    					else
	    						direction = newDirection(new char[]{'D', 'U'}); // exclude 'D','U'
	    				}
	    			gotIN=true;
	    	    	//System.out.println("Is close! Y-axis: "+direction);
	    		}
	    	}
	    	else if (closeY == y && Math.abs(closeX-x) <= 3*gridSize) {
	    		if (isValidDest((closeX+x)/2,y) && state[(closeX+x)/2/gridSize][y/gridSize] &&
	    			isValidDest((closeX+x)/2-gridSize,y) && state[(closeX+x)/2/gridSize-1][y/gridSize] &&
	    			isValidDest((closeX+x)/2+gridSize,y) && state[(closeX+x)/2/gridSize+1][y/gridSize]) {
	    			if(closeX>x) 
	    				if (!scared) direction='R'; 		// Go TOWARDS Pacman
	    				else { 								// If Scared, avoid Pacman.
	    					if ( isValidDest(x-increment,y)) 
	    						direction='L';
	    					else
	    						direction = newDirection(new char[]{'L', 'R'}); // exclude 'L','R'
	    				}
	    			else 
	    				if (!scared) direction='L'; 		// Go TOWARDS Pacman
	    				else { 								// If Scared, avoid Pacman.
	    					if ( isValidDest(x+gridSize,y))
	    						direction='R';
	    					else 
	    						direction = newDirection(new char[]{'R', 'L'}); // exclude 'R','L'
	    				}
	    			gotIN=true;
	    	    	//System.out.println("Is close! X-axis: "+direction);
	    		}
	    	}   	  	
	    }
	    if (!gotIN) 
	        direction = newDirection(new char[]{});
    }
    
    /* If that direction is valid, move that way */
    switch(direction) {
      case 'L':
         if ( isValidDest(x-increment,y))
           x -= increment;
         break;     
      case 'R':
         if ( isValidDest(x+gridSize,y))
           x+= increment;
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
}
