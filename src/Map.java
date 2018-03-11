import java.awt.*;
import java.lang.Math;

import javax.swing.JPanel;
import java.util.*;
import java.io.*;
/* The Map class contains the player, ghosts, pellets, and most of the game logic. */

public class Map extends JPanel {
  private static final long serialVersionUID = 1L;
    
  /* Initialize the images*/
  Image ghostImage1 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/Ghost1.gif"));
  Image ghostImage2 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/Ghost2.gif"));
  Image pacmanImage = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PM0.gif"));
  Image pacmanUpImage1 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMup1.gif"));
  Image pacmanUpImage2 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMup2.gif"));
  Image pacmanUpImage3 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMup3.gif"));
  Image pacmanDownImage1 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMdown1.gif"));
  Image pacmanDownImage2 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMdown2.gif"));
  Image pacmanDownImage3 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMdown3.gif")); 
  Image pacmanLeftImage1 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMleft1.gif"));
  Image pacmanLeftImage2 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMleft2.gif"));
  Image pacmanLeftImage3 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMleft3.gif"));
  Image pacmanRightImage1=Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMright1.gif"));
  Image pacmanRightImage2=Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMright2.gif"));
  Image pacmanRightImage3=Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/PMright3.gif"));
  Image titleScreenImage=Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/titleScreen.jpg"));
  Image gameOverImage = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/gameOver.jpg"));
  Image winScreenImage = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/winScreen.jpg"));
  Image ghostScaredImg1 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/GhostScared1.gif"));
  Image ghostScaredImg2 = Toolkit.getDefaultToolkit().getImage(Pacman.class.getResource("img/GhostScared2.gif"));
  Image gImg10,gImg11,gImg20,gImg21,gImg30,gImg31,gImg40,gImg41, PMI, whichScreen;
  
  /* Initialize the player and ghosts */
  int level=0;
  Data data = new Data();
  //Data.LoadLevel l = data.new LoadLevel(level);
  char[][] LevelInCharArray = data.LoadLevel(level);
  FindPositions F = new FindPositions(LevelInCharArray);
  int[] positions = F.getPnGCoordinates();
  Player player = new Player(positions[0],positions[1]); 
  Ghost ghost1 = new Ghost(positions[2],positions[3]);
  Ghost ghost2 = new Ghost(positions[4],positions[5]);
  Ghost ghost3 = new Ghost(positions[6],positions[7]);
  Ghost ghost4 = new Ghost(positions[6],positions[7]);

  /* timer here is used for playing sound effects and animations */
  long timer = System.currentTimeMillis(); 

  /* dying is used to count the 4 frames of the dying animation. If dying<>0, Pacman is in the process of dying. */
  int dying=0;
 
  /* Score info */
  int currScore, highscore[] = new int[]{0,0,0,0,0}, highPos;
  String name[] = new String[]{"","","","",""}, submittedName="";
  
  boolean clearHighScores= false;

  int numLives=2;

  /* Contains the game map, passed to player and ghosts. */
  boolean[][] state;

  /* Contains the state of all pellets. */
  boolean[][] pellets, bigO; 
  int totalPellets;
  
  /* Game dimensions */
  int gridSize=Data.squareSize;

  /* State flags*/
  boolean stopped, titleScreen, winScreen = false, overScreen = false;
  int New;
  
  /* When 60% of cookies eaten, increase all Mover speed by 30%. */
  boolean speedPelletsIncreased=false; 
  
  /* When ghosts are scared, because Pacman ate bigO. */
  boolean ghostsAreScared=false;
  int ghostEatenReward=200;
  long scaredTimer;
  String whichGhost;
  
  /* Used to call sound effects */
  GameSounds sounds;

  int lastPelletEatenX=0;
  int lastPelletEatenY=0;

  /* HighScores file */
  String fileMeta = this.getClass().getClassLoader().getResource("HighScores.txt").toString().substring(5);
  
  /* To let the user choose whether to Exit Pacman Game or Start a new one. 
   * When true, gameplay is waiting on winScreen of the last level OR overScreen. */
  boolean endOfGame=false;

  /* Used IFF "Load Game" from the menu is pressed. */
  boolean loadedGame=false;
  
  /* Constructor initializes state flags etc.*/
  protected Map() {
	readHighScores();
    sounds = new GameSounds();
    currScore=0;
    numLives=2;
    stopped=false;
    gridSize=Data.squareSize;
    New=0;
    titleScreen = true;
  }

  /* Update Score-HighScore Top Bar */
  protected void updateScoreOnScreen(Graphics g) {
	  g.setColor(Color.RED);
      g.fillRect(0,0,Data.width,Data.North_height);
      g.setColor(Color.YELLOW);
      g.setFont(new Font("Tahoma",Font.BOLD, 12));
      g.drawString("Score: "+(currScore)+"      High Score: "+highscore[0],Data.width/5,Data.squareSize);      
  }
  
  /* Reads the high scores file and returns it in appropiate readable String format */
  protected String readHighScores() {
	String HighScorestoString="";
    Scanner sc=null;
    try {
    	sc = new Scanner(new File(fileMeta));	
    } catch (FileNotFoundException e) {e.printStackTrace();}
    highPos=0;
    while (sc.hasNext()) {
		   // if the next is a int, print found and the int
		   if (sc.hasNextInt()) {
			   int temp=sc.nextInt();
			  // System.out.println("Found :" + temp);	
			   highscore[highPos]=temp;
			   highPos++;
		   }
		   else { 
			   String temp=sc.next();
			  // System.out.println("Not Found :" + temp);
			   name[highPos]=temp;
		   }
	}	 
    for(int i=0;i<highPos;i++) {
    	String a=String.valueOf(name[i]),
    		   b=String.format("%1$"+String.valueOf(18-name[i].length())+"s",highscore[i]);
    	//System.out.println(a+b);
    	HighScorestoString+=a+b+"\n";
    }
    sc.close();
    return HighScorestoString;
  }
      
  protected void checkScore() {
	  /* First check, then update*/
	  for(highPos=0;highPos<5;highPos++) 
		if (currScore>highscore[highPos]) 
			break;
  }
  /* NOW, if newHighPos(=highPos) < 5 then updateScore()+request submit name */
  protected void updateScore() {
	int newHighPos=highPos;
		for(int i=4;i>0;i--) 
				if (i!=newHighPos) {
					name[i]=name[i-1];
					highscore[i]=highscore[i-1];				
				}
				else { // i==newHighPos
					name[i]=submittedName;
					highscore[i]=currScore;	
					break;
				}
		if (newHighPos==0) {
			name[0]=submittedName;
			highscore[0]=currScore;
		}	
	
    PrintWriter out=null;
    try {
		out = new PrintWriter(fileMeta);

	} catch (FileNotFoundException e) {e.printStackTrace();}
    int i=0;
    while(i<5 && name[i]!="") {
    	out.print(name[i]);    	
    	out.println(String.format("%1$"+String.valueOf(35-name[i].length())+"s",highscore[i]));
    	//System.out.println(name[i]+String.format("%1$"+String.valueOf(18-name[i].length())+"s",highscore[i]));
    	i++;
    }
    out.flush();
    out.close();
  }

  protected void clearHighScores() {
    PrintWriter out=null;
    try {
    	out = new PrintWriter(fileMeta);
        out.println("");
	} catch (FileNotFoundException e) {e.printStackTrace();}
    finally {
    	out.close();
    }
	name = new String[]{"","","","",""};
	highscore = new int []{0,0,0,0,0};
	highPos=0;
  }

  /* Reset occurs on a new game*/
  private void reset() {
    state = new boolean[Data.mapWidthInSquares][Data.mapHeightInSquares]; // 19x22, passed as variable!!
    pellets = new boolean[Data.mapWidthInSquares][Data.mapHeightInSquares]; // 19x22
    Data.teleport = new boolean[Data.mapWidthInSquares][Data.mapHeightInSquares]; // 19x22 
    bigO = new boolean[Data.mapWidthInSquares][Data.mapHeightInSquares]; // 19x22
    speedPelletsIncreased=false;
  }

  private void drawLevel(Graphics g) { // i 		j<->x WIDTH
	  								  //HEIGHT	state[j][i]
	  								  // y
	totalPellets=0;
	for (int i=0;i<LevelInCharArray.length;i++) {    // y
	  for(int j=0;j<LevelInCharArray[i].length;j++) {// x
		char temp = LevelInCharArray[i][j];          // yx
        switch (temp) {
            case '.':  // paint black square with yellow dot
            //  state[x][y] 
            	state[j][i]=true;
            	pellets[j][i]=true;
            	Data.teleport[j][i]=false;
            	bigO[j][i]=false;
            	g.setColor(Color.YELLOW);
                g.fillOval(j*gridSize+10,Data.North_height+i*gridSize+10,4,4); // 2*10+4=24, 10=blank, 24=total
                totalPellets++;
                break;
            case '#':  // paint blue square
            	state[j][i]=false;
            	pellets[j][i]=false;
            	Data.teleport[j][i]=false;
            	bigO[j][i]=false;
            	g.setColor(Color.BLUE);
                g.fillRect(j*gridSize,Data.North_height+i*gridSize,gridSize,gridSize);
      	        break;
            case 'O':  // paint yellow bold circle
            	state[j][i]=true;
            	pellets[j][i]=false;
            	Data.teleport[j][i]=false;
            	bigO[j][i]=true;
            	g.setColor(Color.WHITE);
            	g.fillOval(j*gridSize+4,Data.North_height+i*gridSize+4,16,16); // 2*4+16=24, 4=blank, 24=total
            	totalPellets++;
      	        break; 
            case 'P':  // put Pacman, new Player(.,.)
            	// save Pacman position & put it when game starts.	
            	state[j][i]=true;
            	pellets[j][i]=false;
            	Data.teleport[j][i]=false;
            	bigO[j][i]=false;            	
      	        break;
            case 'F':  // put Ghost, new Ghost(.,.)
			    state[j][i]=true;
            	pellets[j][i]=false;
            	Data.teleport[j][i]=false;
            	bigO[j][i]=false;
      	        break;				    
            case '-':  // paint ghost_box
            	state[j][i]=true;
            	pellets[j][i]=false;
            	Data.teleport[j][i]=false;
            	bigO[j][i]=false;
            	g.setColor(Color.ORANGE);
                g.fillRect(j*gridSize+1,Data.North_height+i*gridSize+8,gridSize,8); // (x, y, width, height)
      	        break;
            case ' ': 
            	state[j][i]=true;
            	pellets[j][i]=false;
            	if (i==0 || i==LevelInCharArray.length-1 || j==0 || j==LevelInCharArray[i].length-1)
            		Data.teleport[j][i]=true;
            	else
            		Data.teleport[j][i]=false;
            	bigO[j][i]=false;
	    } // end of switch
      } // end of for(j=0 
	} // end of for(i=0
  }
  
    /* Draws the appropriate NUMBER OF LIVES on the bottom left of the screen. 
   * Also draws the MENU */
  private void drawLivesMenu(Graphics g) {
	/*Clear the bottom bar*/
	g.setColor(Color.BLACK);
	g.fillRect(0,Data.North_height+Data.Center_height,Data.width,Data.South_height);
    g.setColor(Color.YELLOW);
    /*Draw each life */
	for(int i = 0;i<numLives;i++)
		g.drawImage(pacmanRightImage3,i*Data.squareSize,Data.North_height+Data.Center_height+5,Color.BLACK,null);
	
	/* Draw the MENU items */
	g.setColor(Color.YELLOW);
	g.setFont(new Font("Tahoma",Font.BOLD, 16));
	int w =Data.width/5, h=Data.North_height+Data.Center_height+Data.South_height/2+10;
	String 
	s="Start";		 g.drawString(s,w,h); 
	s="Load"; 		 g.drawString(s,w+Data.squareSize*3,h);
	s="High Scores"; g.drawString(s,w+Data.squareSize*6,h);
	s="Exit";		 g.drawString(s,w+Data.squareSize*12,h);

	g.setColor(Color.RED);
	g.drawString("~ Game ~",Data.width/3+Data.squareSize+7,h-18);
  }
      
  /* Draws one individual pellet. Used to redraw pellets that ghosts have run over. */
  private void fillPellet(int pelletX, int pelletY, Graphics g) {
    g.setColor(Color.YELLOW);
    g.fillOval(pelletX*gridSize+10,Data.North_height+pelletY*gridSize+10,4,4);
  }
  
  private void fillBigO(int pelletX, int pelletY, Graphics g) {
	    g.setColor(Color.WHITE);
	    g.fillOval(pelletX*gridSize+4,Data.North_height+pelletY*gridSize+4,16,16);
  }

  /* This is the main function that draws one entire frame of the game */
  public void paint(Graphics g) {
    /* If we're playing the dying animation, don't update the entire screen, just kill Pacman! */ 
    if (dying > 0) { // dying it's a metric to count the 4 frames of the dying animation
      /* Stop any pacman eating sounds_ */
      sounds.nomNomStop();

      /* Draw the pacman */
      g.drawImage(pacmanImage,player.x,player.y+Data.North_height,Color.BLACK,null);
      g.setColor(Color.BLACK);
      
      /* Kill the pacman */
      if (dying == 4)
        g.fillRect(player.x,player.y+Data.North_height,gridSize,gridSize/3);
      else if (dying == 3)
        g.fillRect(player.x,player.y+Data.North_height,gridSize,gridSize/2);
      else if (dying == 2)
        g.fillRect(player.x,player.y+Data.North_height,gridSize,gridSize); 
      else if (dying == 1) 
        g.fillRect(player.x,player.y+Data.North_height,gridSize,gridSize); 
      
     
      /* Take .1 seconds on each frame of death, and then take 1.125 seconds
       * for the final frame to allow for the sound effect to end */ 
      long currTime = System.currentTimeMillis();
      long temp;
      if (dying != 1)
        temp = 100;
      else
        temp = 1125;
      /* If it's time to draw a new death frame... */
      if (currTime-timer >= temp) {
        dying--;
        timer = currTime;
        /* If this was the last death frame...*/
        if (dying == 0) {
          sounds.death.stop(); // cause it's looping inside GameSounds.class
          if (numLives>=0) {
    	      /* Allow ghosts to exit ghost box for the next round, if any. */
    	      ghost1.state[positions[8]][positions[9]]=true;
    	      ghost2.state[positions[8]][positions[9]]=true;
    	      ghost3.state[positions[8]][positions[9]]=true;
    	      ghost4.state[positions[8]][positions[9]]=true;
          }
          else if (numLives==-1) {
              /* Game over for player. If relevant, update high score. Set gameOver flag. */
              updateScoreOnScreen(g);
              overScreen=true; 
              numLives=2;
          }        
        }
      }
      return;
    }

    if (titleScreen || winScreen || overScreen) {
	    if (titleScreen) whichScreen=titleScreenImage;
	    else if (winScreen) whichScreen=winScreenImage;
	    else if (overScreen) whichScreen=gameOverImage; 
	    g.setColor(Color.BLACK);
	    g.fillRect(0,0,Data.width,Data.height); // (0,0,width, height)
	    g.drawImage(whichScreen,Data.width/2-whichScreen.getWidth(null)/2,
	    					    Data.height/2-whichScreen.getHeight(null)/2,Color.BLACK,null);
	    /* Stop any pacman eating sounds_ */
	    sounds.nomNomStop();
	    if (overScreen || (winScreen && level==1))  // 1: Last Level, if n internal game levels: level==n	    
	    	endOfGame=true;
	    else if (titleScreen || winScreen) 
	    	New=1;	    
	    return;
    }

    /* If need to update the high scores, redraw the top bar (score and highscore). */
    if (clearHighScores) {
      clearHighScores();
      clearHighScores=false;
      updateScoreOnScreen(g);
    }
   
    /* oops is set to true when there is a collision (Pacman & some ghost),
     * where either numLives--; or ghostEatenReward*=2; */ 
    boolean oops=false;
    
    /* Game initialization */
    if (New==1) {
      g.setColor(Color.BLACK);
      g.fillRect(0,0,Data.width,Data.height);
      reset();
      player = new Player(positions[0],positions[1]);
      ghost1 = new Ghost(positions[2],positions[3]);  
      ghost2 = new Ghost(positions[4],positions[5]);
      ghost3 = new Ghost(positions[6],positions[7]);
      ghost4 = new Ghost(positions[6],positions[7]);
      drawLevel(g);
      drawLivesMenu(g); /* Draw 2 times in a row, necessary: */ drawLivesMenu(g);
      /* Send the game map to player and all ghosts */
      player.updateState(state);
      /* Don't let the player go in the ghost box*/
      player.state[positions[8]][positions[9]]=false;     // see FindPositions.class
      ghost1.updateState(state);
      ghost2.updateState(state);
      ghost3.updateState(state);
      ghost4.updateState(state);  

      g.drawImage(ghostImage1,ghost1.x,ghost1.y+Data.North_height,Color.BLACK,null);
      g.drawImage(ghostImage2,ghost2.x,ghost2.y+Data.North_height,Color.BLACK,null);
      g.drawImage(ghostImage1,ghost3.x,ghost3.y+Data.North_height,Color.BLACK,null);
      g.drawImage(ghostImage2,ghost4.x,ghost4.y+Data.North_height,Color.BLACK,null);
   
      updateScoreOnScreen(g);
      if (!loadedGame) {
    	  /* Wait until "New Game" pressed */
          New++;    	  
      }
      else {
          g.setColor(Color.RED);
      	  g.drawString("Press Start to start loaded level!",
      			  Data.width/3-2*Data.squareSize,Data.North_height+Data.Center_height-5);
      }
    }
    /* Second frame of new game */
    else if (New == 2) {
      New++;
      /* Play the newGame sound effect */
      sounds.newGame();
      timer = System.currentTimeMillis();
      return; 
    }
    /* Third frame of new game */
    else if (New == 3) {
      /* Wait until the newGame() sound effect is over */
      long currTime = System.currentTimeMillis();
      if (currTime - timer >= 5000)
        New=0;
      else
        return;
    }

    if (ghostsAreScared) {
    	if (scaredTimer == -1) 
    		scaredTimer = System.currentTimeMillis();
    	long currScaredTime = System.currentTimeMillis();
    	if (currScaredTime - scaredTimer >= 7000) {
    		ghostsAreScared=false;
    		player.speedScaredDecr=true;
    		player.speedScaredIncr=false; 
    		     // if new Ghost, that means eaten & waiting in ghost box until Scared is over. 
    		if(!ghost1.state[positions[8]][positions[9]]) 	
    			ghost1.state[positions[8]][positions[9]]=true;
    		else // if Ghost, not eaten 
    			ghost1.speedScaredIncr=true;    		
    		if(!ghost2.state[positions[8]][positions[9]])
    			ghost2.state[positions[8]][positions[9]]=true;
    		else 
    			ghost2.speedScaredIncr=true;
    		if(!ghost3.state[positions[8]][positions[9]])
    			ghost3.state[positions[8]][positions[9]]=true;
    		else 
    			ghost3.speedScaredIncr=true;
    		if(!ghost4.state[positions[8]][positions[9]])
    			ghost4.state[positions[8]][positions[9]]=true;
    		else
    			ghost4.speedScaredIncr=true;
    		ghost1.scared=false;
    		ghost2.scared=false;
    		ghost3.scared=false;
    		ghost4.scared=false;
    	}
    }
      // i 		j<->x WIDTH
	  //HEIGHT	state[j][i]
	  // y		positions[8]=j (x) , positions[9]=i (y)
    /* Ghosts must not enter ghost box once they are out. */
    if (ghost1.y != positions[9]*gridSize && ghost1.x != positions[8]*gridSize) // if not @ entrance
    	if (ghost1.y != (positions[9]+1)*gridSize)
    		if (ghost1.x != (positions[8]+1)*gridSize && ghost1.x != (positions[8]-1)*gridSize)  // neither inside
    			ghost1.state[positions[8]][positions[9]]=false; 
    if (ghost2.y != positions[9]*gridSize && ghost2.x != positions[8]*gridSize) // if not @ entrance
    	if (ghost2.y != (positions[9]+1)*gridSize)
    		if (ghost2.x != (positions[8]+1)*gridSize && ghost2.x != (positions[8]-1)*gridSize)  // neither inside
    			ghost2.state[positions[8]][positions[9]]=false;
    if (ghost3.y != positions[9]*gridSize && ghost3.x != positions[8]*gridSize) // if not @ entrance
    	if (ghost3.y != (positions[9]+1)*gridSize) 
    		if (ghost3.x != (positions[8]+1)*gridSize && ghost3.x != (positions[8]-1)*gridSize)  // neither inside
    			ghost3.state[positions[8]][positions[9]]=false;
    if (ghost4.y != positions[9]*gridSize && ghost4.x != positions[8]*gridSize) // if not @ entrance
    	if (ghost4.y != (positions[9]+1)*gridSize)
    		if (ghost4.x != (positions[8]+1)*gridSize && ghost4.x != (positions[8]-1)*gridSize)  // neither inside
    			ghost4.state[positions[8]][positions[9]]=false; 
    
    /* Detect collisions */
    if ((player.x == ghost1.x && Math.abs(player.y-ghost1.y) < 10)||
    		(player.y == ghost1.y && Math.abs(player.x-ghost1.x) < 10)) {
	    oops=true;
	    whichGhost="ghost1";
    }
    else if ((player.x == ghost2.x && Math.abs(player.y-ghost2.y) < 10)||
    		(player.y == ghost2.y && Math.abs(player.x-ghost2.x) < 10)) {
        oops=true;
	    whichGhost="ghost2";
    }
    else if ((player.x == ghost3.x && Math.abs(player.y-ghost3.y) < 10)||
    		(player.y == ghost3.y && Math.abs(player.x-ghost3.x) < 10)) {
        oops=true;
	    whichGhost="ghost3";
    }
    else if ((player.x == ghost4.x && Math.abs(player.y-ghost4.y) < 10)||
    		(player.y == ghost4.y && Math.abs(player.x-ghost4.x) < 10)) {
        oops=true;
	    whichGhost="ghost4";
    }

    /* When collision: */
    if(!ghostsAreScared) { 	// When ghosts are NOT scared
	    /* Kill the pacman */
	    if (oops && !stopped) {
	      /* 4 frames of death*/
	      dying=4;

	      /* Stop any pacman eating sounds */
	      sounds.nomNomStop();
	      /* Play death sound effect */
	      sounds.death();
	 
	      /* Decrement lives, update screen to reflect that.  And set appropriate flags and timers */
	      numLives--;
	      drawLivesMenu(g);
	      stopped=true;
	      timer = System.currentTimeMillis();
	      /* Allow ghosts to exit ghost box for the next round, if any. */
	      ghost1.state[positions[8]][positions[9]]=true;
	      ghost2.state[positions[8]][positions[9]]=true;
	      ghost3.state[positions[8]][positions[9]]=true;
	      ghost4.state[positions[8]][positions[9]]=true;
	    }
    }
    else { 		   // When ghostsAreScared
    	if(oops) {
    		currScore+=ghostEatenReward;
    		ghostEatenReward*=2;
    		switch (whichGhost) {
    		case "ghost1": 
    		    g.setColor(Color.BLACK);
    		    g.fillRect(ghost1.lastX,ghost1.lastY+Data.North_height,gridSize,gridSize);
                ghost1 = new Ghost(positions[2],positions[3]); 
                ghost1.updateState(state);
                ghost1.state[positions[8]][positions[9]]=false; // Wait in ghost box until NOT Scared anymore!
            	break;
    		case "ghost2":
    		    g.setColor(Color.BLACK);
    		    g.fillRect(ghost2.lastX,ghost2.lastY+Data.North_height,gridSize,gridSize);
                ghost2 = new Ghost(positions[4],positions[5]); 
                ghost2.updateState(state);
                ghost2.state[positions[8]][positions[9]]=false; // Wait in ghost box until NOT Scared anymore!
            	break;
    		case "ghost3": 
    		    g.setColor(Color.BLACK);
    		    g.fillRect(ghost3.lastX,ghost3.lastY+Data.North_height,gridSize,gridSize);
                ghost3 = new Ghost(positions[6],positions[7]); 
                ghost3.updateState(state);
                ghost3.state[positions[8]][positions[9]]=false; // Wait in ghost box until NOT Scared anymore!
            	break;
    		case "ghost4": 
    		    g.setColor(Color.BLACK);
    		    g.fillRect(ghost4.lastX,ghost4.lastY+Data.North_height,gridSize,gridSize);
                ghost4 = new Ghost(positions[6],positions[7]); 
                ghost4.updateState(state);
                ghost4.state[positions[8]][positions[9]]=false; // Wait in ghost box until NOT Scared anymore!
            	break;
    		} 
    		updateScoreOnScreen(g);    		    	      
    	}
    }
    
    /* Delete the players and ghosts */
    g.setColor(Color.BLACK);
    g.fillRect(player.lastX,player.lastY+Data.North_height,gridSize,gridSize);
    g.fillRect(ghost1.lastX,ghost1.lastY+Data.North_height,gridSize,gridSize);
    g.fillRect(ghost2.lastX,ghost2.lastY+Data.North_height,gridSize,gridSize);
    g.fillRect(ghost3.lastX,ghost3.lastY+Data.North_height,gridSize,gridSize);
    g.fillRect(ghost4.lastX,ghost4.lastY+Data.North_height,gridSize,gridSize);

    /* Eat pellets AND bigO */
    if ((pellets[player.pelletX][player.pelletY] && New!=2)||
        (bigO[player.pelletX][player.pelletY] && New!=2)) {
      lastPelletEatenX = player.pelletX;
      lastPelletEatenY = player.pelletY;
      /* Play eating sound */ 
      sounds.nomNom();
      
      /* Increment pellets eaten value to track for end game */
      player.pelletsEaten++;
      
      /* When 60% of cookies eaten, increase all Mover speed by 30%. */
      if((player.pelletsEaten>=totalPellets*0.6)&&(!speedPelletsIncreased)&&(!player.speedPelletsIncreased)) {
    	  player.speedPelletsIncreased=true;
    	  ghost1.speedPelletsIncreased=true; 
    	  ghost2.speedPelletsIncreased=true;
    	  ghost3.speedPelletsIncreased=true;
    	  ghost4.speedPelletsIncreased=true;
    	  /* Not to increase again! */
    	  speedPelletsIncreased=true;
      }
      
      /* Redraw all pellets every time, else some will become hidden 
       * 'hidden'=  pellet not drawn + pellets[][]=true 
       * :happens when Pacman towards it, mouth touches pellet, direction changed, no position interaction  
       *  pellets = new boolean[Data.mapWidthInSquares][Data.mapHeightInSquares]; 19x22 */
      g.setColor(Color.YELLOW);
      for(int j=0;j<Data.mapWidthInSquares;j++)
    	  for(int i=0;i<Data.mapHeightInSquares;i++)
    		if (pellets[j][i])
    		   g.fillOval(j*gridSize+10,Data.North_height+i*gridSize+10,4,4);
      
      /* Delete the pellet OR the bigO */
      if (pellets[player.pelletX][player.pelletY]){
    	  pellets[player.pelletX][player.pelletY]=false;
      	  currScore += 10;
      }
      else if (bigO[player.pelletX][player.pelletY]){
    	  bigO[player.pelletX][player.pelletY]=false;
      	  currScore += 50;
      	  /* if ghosts are NOT Scared so far (and they are about to be NOW */
      	  if(!ghostsAreScared) { 
          	  ghostEatenReward=200;
      	  }
      	  ghostsAreScared=true;
      	  scaredTimer=-1;
      	  player.speedScaredIncr=true;
      	  ghost1.speedScaredDecr=true;
      	  ghost2.speedScaredDecr=true;
      	  ghost3.speedScaredDecr=true;
      	  ghost4.speedScaredDecr=true;
      	  ghost1.scared=true;
      	  ghost2.scared=true;
      	  ghost3.scared=true;
      	  ghost4.scared=true;
      	  whichGhost="";
      }

      updateScoreOnScreen(g);       
      /* If this was the last pellet */
      if (player.pelletsEaten == totalPellets) {    	  	      
		  winScreen = true;  
		  return;
      }
    }
    /* If we moved to a location without pellets, stop the sounds_ */
    else if ((player.pelletX != lastPelletEatenX || player.pelletY != lastPelletEatenY ) || player.stopped) {
      /* Stop any pacman eating sounds_ */
      sounds.nomNomStop();
    }

    /* Replace pellets AND bigO that have been run over by ghosts */
    if ( pellets[ghost1.lastPelletX][ghost1.lastPelletY])
      fillPellet(ghost1.lastPelletX,ghost1.lastPelletY,g);
    if ( bigO[ghost1.lastPelletX][ghost1.lastPelletY])
        fillBigO(ghost1.lastPelletX,ghost1.lastPelletY,g);
    if ( pellets[ghost2.lastPelletX][ghost2.lastPelletY])
      fillPellet(ghost2.lastPelletX,ghost2.lastPelletY,g);
    if ( bigO[ghost2.lastPelletX][ghost2.lastPelletY])
    	fillBigO(ghost2.lastPelletX,ghost2.lastPelletY,g);
    if ( pellets[ghost3.lastPelletX][ghost3.lastPelletY])
      fillPellet(ghost3.lastPelletX,ghost3.lastPelletY,g);
    if ( bigO[ghost3.lastPelletX][ghost3.lastPelletY])
        fillBigO(ghost3.lastPelletX,ghost3.lastPelletY,g);
    if ( pellets[ghost4.lastPelletX][ghost4.lastPelletY])
      fillPellet(ghost4.lastPelletX,ghost4.lastPelletY,g);
    if ( bigO[ghost4.lastPelletX][ghost4.lastPelletY])
        fillBigO(ghost4.lastPelletX,ghost4.lastPelletY,g);

    /*
     * All the rest are about drawing the right image at the right time.
     *
     */
    
    /*Draw the ghosts */
    if(ghostsAreScared){
    	gImg10=ghostScaredImg1;
    	gImg20=ghostScaredImg2;
    	gImg30=ghostScaredImg1;
    	gImg40=ghostScaredImg2;
    	gImg11=ghostScaredImg2;
    	gImg21=ghostScaredImg1;
    	gImg31=ghostScaredImg2;
    	gImg41=ghostScaredImg1;

    	/* If ghosts' scared state is about to end, warn by blinking their images */
    	long currScaredTime = System.currentTimeMillis();
    	if (currScaredTime - scaredTimer >= 4000) {
	    	gImg11=ghostImage1; // Ghost1.gif
	    	gImg21=ghostImage1;
	    	gImg31=ghostImage1;
	    	gImg41=ghostImage1;
    	}
    }
    else {
    	gImg10=ghostImage1;
    	gImg20=ghostImage2;
    	gImg30=ghostImage1;
    	gImg40=ghostImage2; 
    	gImg11=ghostImage2;
    	gImg21=ghostImage1;
    	gImg31=ghostImage2;
    	gImg41=ghostImage1;   	
    }
    
    /* Switching ghosts images according to speed (Ghost1.gif & Ghost2.gif) OR respectively if scared. */
    int middle=10, high=20;  // 15,30 | 10,20 | 5,10
    if (ghost1.speedPelletsIncreased) 
    	if (!ghost1.speedScaredDecr) {  // fastest possible, if >60% cookies eaten
    		middle=2; high=5; 
    	}
    	else {							// if >60% cookies eaten & scared
    		middle=10; high=20;
    	}
    else 
    	if (!ghost1.speedScaredDecr) {  // normal, starting speed
    		middle=10; high=20;
    	}
    	else {
    		middle=18; high=36;			// if scared
    	}
    		
    if (ghost1.frameCount < middle) {
      /* Draw first frame of ghosts */
      g.drawImage(gImg10,ghost1.x,ghost1.y+Data.North_height,Color.BLACK,null);
      g.drawImage(gImg20,ghost2.x,ghost2.y+Data.North_height,Color.BLACK,null);
      g.drawImage(gImg30,ghost3.x,ghost3.y+Data.North_height,Color.BLACK,null);
      g.drawImage(gImg40,ghost4.x,ghost4.y+Data.North_height,Color.BLACK,null);
      ghost1.frameCount++;
    }
    else {
      /* Draw second frame of ghosts */
      g.drawImage(gImg11,ghost1.x,ghost1.y+Data.North_height,Color.BLACK,null);
      g.drawImage(gImg21,ghost2.x,ghost2.y+Data.North_height,Color.BLACK,null);
      g.drawImage(gImg31,ghost3.x,ghost3.y+Data.North_height,Color.BLACK,null);
      g.drawImage(gImg41,ghost4.x,ghost4.y+Data.North_height,Color.BLACK,null);
      if (ghost1.frameCount >=high)
        ghost1.frameCount=0;
      else
        ghost1.frameCount++;
    }

    if (New==0) /* Do it only when needed (don't draw when playing newGame sound. */
    /* Draw Pacman's Image.*/
	if (player.frameCount < 3)
	    g.drawImage(pacmanImage,player.x,player.y+Data.North_height,Color.BLACK,null);	  
	else if (player.frameCount < 18) {
	    /* Draw Pacman's mouth open in appropriate direction */
	    switch(player.currDirection) {
	        case 'L':
	        	if (player.frameCount < 6)
	        		PMI=pacmanLeftImage1;
	            else if (player.frameCount < 9)
	        		PMI=pacmanLeftImage2;
	            else if (player.frameCount < 12)
	        		PMI=pacmanLeftImage3;
	            else if (player.frameCount < 15)
	        		PMI=pacmanLeftImage2;
	            else if (player.frameCount < 18) 
	        		PMI=pacmanLeftImage1;
	            g.drawImage(PMI,player.x,player.y+Data.North_height,Color.BLACK,null);
	            break;     
	        case 'R':
	        	if (player.frameCount < 6)
	        		PMI=pacmanRightImage1;
	            else if (player.frameCount < 9)
	        		PMI=pacmanRightImage2;
	            else if (player.frameCount < 12)
	        		PMI=pacmanRightImage3;
	            else if (player.frameCount < 15)
	        		PMI=pacmanRightImage2;
	            else if (player.frameCount < 18) 
	        		PMI=pacmanRightImage1;
	            g.drawImage(PMI,player.x,player.y+Data.North_height,Color.BLACK,null);
	            break;     
	        case 'U':
	        	if (player.frameCount < 6)
	        		PMI=pacmanUpImage1;
	            else if (player.frameCount < 9)
	        		PMI=pacmanUpImage2;
	            else if (player.frameCount < 12)
	        		PMI=pacmanUpImage3;
	            else if (player.frameCount < 15)
	        		PMI=pacmanUpImage2;
	            else if (player.frameCount < 18) 
	        		PMI=pacmanUpImage1;
	            g.drawImage(PMI,player.x,player.y+Data.North_height,Color.BLACK,null);
	            break;     
	        case 'D':
	        	if (player.frameCount < 6) 
	        		PMI=pacmanDownImage1;
	            else if (player.frameCount < 9)
	        		PMI=pacmanDownImage2;
	            else if (player.frameCount < 12)
	        		PMI=pacmanDownImage3;
	            else if (player.frameCount < 15)
	        		PMI=pacmanDownImage2;
	            else if (player.frameCount < 18) 
	        		PMI=pacmanDownImage1;
	            g.drawImage(PMI,player.x,player.y+Data.North_height,Color.BLACK,null);
	            break;    
	      }
	      if (player.frameCount == 17)
	    	  player.frameCount=0;
	  }
  }
}

