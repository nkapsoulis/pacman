import java.io.IOException;
/* Data contains all game important dimensions. 
 * North, Center, South are the 3 parts that the game window is divided in.*/ 
/* The Center has 22x19 squares, 24x24 pixels each. */
public class Data {
	protected static int 
     squareSize = 24, // 24 pixels per map square
	 mapHeightInSquares=22, // -----> y
	 mapWidthInSquares=19,  // -----> x
	 width = mapWidthInSquares*squareSize, 		 /* 456 = 19 squares * 24 pixels */
	 North_height = 36, 
	 Center_height = mapHeightInSquares*squareSize	 /* =22*24 */,
	 South_height = 48,
	 height = North_height+Center_height+South_height;
	protected static boolean teleport[][];
	
	/* 
	 * LoadLevel method: Data: Loads specific internal game level. 
	 * Map.class level can be 0, 1, ... n. Depending on how many we put in the project. 
	 *!!!!!!!!!!
	 * Internal game levels should ALWAYS have a name like level0.txt and be located in levels/ folder.  
	 *
	 */
	protected char[][] LoadLevel(int level) {
	  /* Load level */
	  char[][] LevelInCharArray = null;
		try {
			LevelInCharArray = new ReadTextFile("/bin/levels/level"+String.valueOf(level)+".txt").getFileInChars();
		} catch (IOException e) {e.printStackTrace();}
	   /* System.out.println("Print current level:");
		for (int i=0;i<LevelInCharArray.length;i++) {
			for(int j=0;j<LevelInCharArray[i].length;j++)  
				System.out.print(LevelInCharArray[i][j]);
			System.out.println();
		}*/ /*String s = new ReadTextFile("levels/level0.txt").getFileInStringArray(); System.out.println(s);*/
		return LevelInCharArray;
	}
}
