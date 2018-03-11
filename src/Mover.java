/* Both Player and Ghost inherit Mover. It has generic functions relevant to both*/
public class Mover {
  /* Framecount is used to count animation frames. */
  int frameCount=0;
  /* State contains the game level map. When wall->false, when free to pass->true. */
  boolean[][] state; 
  /* gridSize is the size of one square in the game. (24)
     increment is the speed at which the object moves and it must be multiple of gridSize
     1 increment per move() call (see Pacman.class) */
  int gridSize=Data.squareSize;
  int increment;
  
  /* When 60% of cookies eaten, increase Player's speed by 30%. */
  boolean speedPelletsIncreased=false;
  
  /* When handling speed according to Scared Ghosts or Not */
  boolean speedScaredIncr=false, gotINIncr=false;
  boolean speedScaredDecr=false, gotINDecr=false;
  boolean gotIN=false; 
  
  /* Generic constructor */
  protected Mover() {
    gridSize=Data.squareSize; // 24 
    increment = gridSize/8;   // the speed
    state = new boolean[Data.mapWidthInSquares][Data.mapHeightInSquares]; // [19][22] (see Data.class)
    for(int i =0;i<Data.mapWidthInSquares;i++)
      for(int j=0;j<Data.mapHeightInSquares;j++)   
        state[i][j] = false;    
  }

  /* Updates the state information */
  protected void updateState(boolean[][] state){
    for(int i=0;i<Data.mapWidthInSquares;i++)
      for(int j=0;j<Data.mapHeightInSquares;j++)
        this.state[i][j] = state[i][j];
  }

  /* Determines if a set of coordinates is a valid destination.*/
  protected boolean isValidDest(int x, int y) {
    /* The first statements check that the x and y are inbounds.  The last statement checks the map to
       see if it's a valid location. */
	  if ((((x)%Data.squareSize==0) || ((y)%Data.squareSize)==0) 
			    && 0<=x && x<Data.width
	    		&& 0<=y && y<Data.Center_height 
	    		&& state[x/Data.squareSize][y/Data.squareSize] )
		  return true; // 24x24 pixels   
	  return false;
  }
}