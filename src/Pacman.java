import javax.swing.*;

import java.awt.event.*;
import java.io.IOException;
import java.awt.*;

public class Pacman extends JApplet implements MouseListener, KeyListener { 
  private static final long serialVersionUID = 1L;
  /* These timers are used to kill title, game over, and win screens after 5 seconds */
  long titleTimer = -1;
  long timer = -1;
  /* This timer is used to request that new frames to be drawn*/
  javax.swing.Timer frameTimer;
  Map m=new Map(); 

  private Pacman() {
    m.requestFocus(); 
    GameWindow Window = new GameWindow(Data.width, Data.height);
    Window.setVisible(true);
    Window.add(m,BorderLayout.CENTER);
    m.addMouseListener(this);  
    m.addKeyListener(this);  

    /* New game. */
    m.New=1;

    /* Call first frame to initialize the game. */
    stepFrame(true);

    /* A timer that calls stepFrame() every 30 milliseconds */
    frameTimer = new javax.swing.Timer(30,new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          stepFrame(false);
        }
      });

    /* Start the timer */
    frameTimer.start();
    m.requestFocus();
  }

  public void repaint() {    
    if (m.player.teleporting) {
      m.repaint(m.player.lastX-Data.squareSize,m.player.lastY-Data.squareSize,4*Data.squareSize,4*Data.squareSize);
      m.player.teleporting=false;
    }
    m.repaint(0,0,Data.width,Data.North_height); // repaint North
    m.repaint(0,Data.North_height+Data.Center_height,Data.width,Data.South_height); // repaint South
    m.repaint(m.player.x-Data.squareSize,m.player.y-Data.squareSize,4*Data.squareSize,4*Data.squareSize);
    m.repaint(m.ghost1.x-Data.squareSize,m.ghost1.y-Data.squareSize,4*Data.squareSize,4*Data.squareSize);
    m.repaint(m.ghost2.x-Data.squareSize,m.ghost2.y-Data.squareSize,4*Data.squareSize,4*Data.squareSize);
    m.repaint(m.ghost3.x-Data.squareSize,m.ghost3.y-Data.squareSize,4*Data.squareSize,4*Data.squareSize);
    m.repaint(m.ghost4.x-Data.squareSize,m.ghost4.y-Data.squareSize,4*Data.squareSize,4*Data.squareSize);
    /* Repaint all, if some pellets are half-eaten and should be run over by ghosts in order to be repainted */
    if (m.speedPelletsIncreased)  
        m.repaint(0,0,Data.width,Data.height);
  }

  /* Steps the screen forward one frame */
  private void stepFrame(boolean New) {
    /* If we aren't on a special screen than the timers can be set to -1 to disable them */
    if (!m.titleScreen && !m.winScreen && !m.overScreen) {
      timer = -1;
      titleTimer = -1;
    }

    /* If we are playing the dying animation, keep advancing frames until this animation is complete. */
    if (m.dying>0) {
      m.repaint();
      return;
    }

    /* New can either be specified by the New parameter in stepFrame function call or by the state
     * of m.New. Update New accordingly. */ 
    New = New || (m.New !=0);

    /* If this is the title screen, make sure to only stay on the title screen for 5 seconds.*/
    if (m.titleScreen) {
      if (titleTimer == -1)
        titleTimer = System.currentTimeMillis();
      long currTime = System.currentTimeMillis();
      if (currTime - titleTimer >= 5000) {
        m.titleScreen = false;
        titleTimer = -1;
      }
      m.repaint();
      return;
    } 
    /* If this is the winScreen or overScreen, make sure to only stay on this screen for 5 seconds.
     * Also wait more than 5 if user must submit nickname because of achieved highscore (m.highPos>=5)
     * Also wait more than 5 if endOfGame (over or levels finished) and user must choose what to do next (m.endOfGame) */
    else if (m.winScreen || m.overScreen) {
      m.checkScore();
      if (timer==-1)
        timer = System.currentTimeMillis();
      long currTime = System.currentTimeMillis();
      if (currTime - timer >= 5000 && (m.highPos>=5) && (!m.endOfGame)) {
        m.winScreen = false;
        m.overScreen = false;
        m.titleScreen = true;
        timer = -1;
      }
      m.repaint();
      return;
    }

    /* If we have a normal gamestate, move all Movers and update pellet status */
    if (!New) {      
    	int x = m.player.x;
        int y = m.player.y;
        // boolean tempScared=false;
        m.ghost1.closeX=x; 
        m.ghost1.closeY=y;
        // m.ghost1.scared=tempScared; 
        m.ghost2.closeX=x; 
        m.ghost2.closeY=y; 
    	// m.ghost2.scared=tempScared;
    	m.ghost3.closeX=x; 
        m.ghost3.closeY=y; 
    	// m.ghost3.scared=tempScared;
        m.ghost4.closeX=x; 
    	m.ghost4.closeY=y; 
    	// m.ghost4.scared=tempScared;      
	    m.player.move();
	    m.ghost1.move(); 
	    m.ghost2.move(); 
	    m.ghost3.move(); 
	    m.ghost4.move(); 
	    m.player.updatePellet();
	    m.ghost1.updatePellet();
	    m.ghost2.updatePellet();
        m.ghost3.updatePellet();
	    m.ghost4.updatePellet();      
    }

    /* If we either have a new game or the user has died, reset the board */
    if (m.stopped || New) {
      /*Temporarily stop advancing frames */
      frameTimer.stop();
      /* If user is dying ... */
      while (m.dying >0)
        /* Play dying animation. */
        stepFrame(false);

      /* Move all game elements back to starting positions and orientations */
      m.player.currDirection='L';
      m.player.desiredDirection='L';
      m.player.x = m.positions[0]*Data.squareSize;
      m.player.y = m.positions[1]*Data.squareSize;
      m.ghost1.x = m.positions[2]*Data.squareSize;
      m.ghost1.y = m.positions[3]*Data.squareSize;
      m.ghost2.x = m.positions[4]*Data.squareSize;
      m.ghost2.y = m.positions[5]*Data.squareSize;
      m.ghost3.x = m.positions[6]*Data.squareSize;
      m.ghost3.y = m.positions[7]*Data.squareSize;
      m.ghost4.x = m.positions[6]*Data.squareSize;
      m.ghost4.y = m.positions[7]*Data.squareSize;
      m.ghostsAreScared=false;

      /* Advance a frame to display main state*/
      m.repaint(0,0,Data.width,Data.height);

      /*Start advancing frames once again*/
      m.stopped=false;
      frameTimer.start();
    }
    /* Otherwise we're in a normal state, advance one frame*/
    else
      repaint(); 
  } 

  /* Handles user key presses*/
  public void keyPressed(KeyEvent e) {
    /* Pressing a key in the title screen starts a game */
    if (m.titleScreen) {
      m.titleScreen = false;
      return;
    }
    else if (m.winScreen || m.overScreen) {
	  m.checkScore();
	  /* m.highPos is changed inside m.checkScore() */ 
	  if (m.highPos<5) { 
		  m.submittedName = (String)JOptionPane.showInputDialog(
              new JFrame(),"","Your score is in TOP 5!",
              JOptionPane.PLAIN_MESSAGE,null,null,"your_nickname");
		  if (m.submittedName==null) m.submittedName="Unknown";
		  m.updateScore();
	  }
	  if (m.winScreen && m.level<1) { /* if necessary, must go to Next Level, last level here is 1 */
		  m.level++;
		  m.LevelInCharArray = m.data.LoadLevel(m.level);
		  m.F = new FindPositions(m.LevelInCharArray);
		  m.positions = m.F.getPnGCoordinates();
	      m.New = 1;
	      m.winScreen = false;
	      m.titleScreen = false;
	  }
	  else if (m.overScreen || (m.winScreen && m.level==1)) { // 1: Last Level, if n internal game levels: level==n
		  int what =  JOptionPane.showOptionDialog(null, 
			  		"'Esc' button will 'Exit Pacman Game'", 
			  		"Now, what do you want to do?",
			  		JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
			  		null, new String[]{"New Pacman Game","Exit Pacman Game"}, null);
		  if (what!=JOptionPane.OK_OPTION) System.exit(0);
		  else {
			  m.level=0;
			  m.LevelInCharArray = m.data.LoadLevel(m.level);
			  m.F = new FindPositions(m.LevelInCharArray);
			  m.positions = m.F.getPnGCoordinates();
		      m.New = 1;
		      m.winScreen = false;
		      m.overScreen = false;
			  m.endOfGame=false;
			  m.numLives=2;
	          m.currScore=0;
	          m.loadedGame=false;
		  }		  
	  }
      return;
    }    

    /* Otherwise, key presses control the player! */ 
    switch(e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
       m.player.desiredDirection='L';
       break;     
      case KeyEvent.VK_RIGHT:
       m.player.desiredDirection='R';
       break;     
      case KeyEvent.VK_UP:
       m.player.desiredDirection='U';
       break;     
      case KeyEvent.VK_DOWN:
       m.player.desiredDirection='D';
       break;     
    }
    repaint();
  }

  /* Handles user mouse presses*/
  public void mousePressed(MouseEvent e) {
    if (m.titleScreen || m.winScreen || m.overScreen)
      /* If we aren't in the game where a menu is showing, ignore clicks */
      return;

    /* Get coordinates of click */
    int x = e.getX();
    int y = e.getY();
    int w =Data.width/5, h=Data.North_height+Data.Center_height+Data.South_height/2+10;
	/*s="Start";     g.drawString(s,w,h); 
	s="Load"; 		 g.drawString(s,w+Data.squareSize*3,h);
	s="High Scores"; g.drawString(s,w+Data.squareSize*6,h);
	s="Exit";		 g.drawString(s,w+Data.squareSize*12,h);*/
    if ( h-Data.squareSize/2 <= y && y <= h)
      if ( w <= x && x <= w+Data.squareSize*2) {
        /* New game */
		m.New = 1;
		m.numLives=2;
		m.currScore=0;
		if (m.loadedGame) m.loadedGame=false;
      }
      else if (w+Data.squareSize*3 <= x && x <= w+Data.squareSize*5) {
        /* Load game */
  		JFileChooser fileChooser = new JFileChooser();
  	    fileChooser.setDialogTitle("Level Chooser: Level chosen must be strictly 22x19!");
  	    fileChooser.setVisible(true);
  	    int what = fileChooser.showOpenDialog(null); 
  	    if(what == JFileChooser.APPROVE_OPTION) {
          m.level=1; // Put last level number: When checking (m.overScreen || (m.winScreen && m.level==1))->(216)
          try {
  		    m.LevelInCharArray = new ReadTextFile(fileChooser.getSelectedFile().getAbsolutePath()).getFileInChars();
  		  } catch (IOException e1) {e1.printStackTrace();}
		  m.F = new FindPositions(m.LevelInCharArray);
		  m.positions = m.F.getPnGCoordinates();
	      m.New = 1;
	      m.titleScreen = false;
	      m.winScreen = false;
	      m.overScreen = false;
		  m.endOfGame = false;	
		  m.numLives=2;
          m.currScore=0;
          m.loadedGame=true;
  	    }  	    
      }
      else if (w+Data.squareSize*6 <= x && x < w+Data.squareSize*10.5) {
          /* Pop up HighScores */
		  String highscores = m.readHighScores();
		  JTextArea highTable = new JTextArea(highscores);
		  highTable.setFont(new Font("monospaced", Font.PLAIN, 14));
		  highTable.setEditable(false);
		  highTable.setBackground(java.awt.Color.CYAN);
		  int what = JOptionPane.showOptionDialog(null, 
			  		highTable, 
			  		"High Scores", 
			  		JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
			  		null, new String[]{"OK", "Clear High Scores"}, null);
		  if (what!=JOptionPane.OK_OPTION && what!=JOptionPane.CLOSED_OPTION) m.clearHighScores();
		  
      }
      else if (w+Data.squareSize*12 <= x && x <= w+Data.squareSize*13.5) {
        /* Exit */
        System.exit(0);
      }
  }  
 
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mouseReleased(MouseEvent e){}
  public void mouseClicked(MouseEvent e){}
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}
  
  /* Main function simply creates a new pacman instance*/
  public static void main(String [] args) {
      new Pacman();
  } 
}
