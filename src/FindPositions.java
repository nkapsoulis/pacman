/* Find Pacman's,Ghosts' and ghost_box's positions in the new level. */
public class FindPositions {
  int px, py, g1x, g1y, g2x, g2y, g3x, g3y, gboxx, gboxy; // Ghost4's coordinates are the same like Ghost3's.
  protected FindPositions(char[][] LevelInCharArray) {
	int ghostCount=0;
	for (int i=0;i<LevelInCharArray.length;i++) {
	  for(int j=0;j<LevelInCharArray[i].length;j++) {
		char temp = LevelInCharArray[i][j];
        switch (temp) {            
            case 'P':
            	 /* Save Pacman's position & put him when game starts.*/ 
            	 px= j;
            	 py= i;
      	         break;
            case 'F':  /* Supposed ONLY 3 'F' are read. */
            	 ghostCount++;
            	 if(ghostCount==1) { g1x=j; g1y=i;}
            	 else if(ghostCount==2) { g2x=j; g2y=i;}
            	 else if(ghostCount==3) { g3x=j; g3y=i;}
      	         break;
            case '-': /* For ghost_box*/
            	 gboxx=j;
            	 gboxy=i;
            	 break;
	    } // end of switch
      } // end of for(j=0 
	} // end of for(i=0
  }
  protected int[] getPnGCoordinates(){
	  int[] coordinates=new int[10];
	  coordinates[0]=this.px;
	  coordinates[1]=this.py;
	  coordinates[2]=this.g1x;
	  coordinates[3]=this.g1y;
	  coordinates[4]=this.g2x;
	  coordinates[5]=this.g2y;
	  coordinates[6]=this.g3x;
	  coordinates[7]=this.g3y;
	  coordinates[8]=this.gboxx;
	  coordinates[9]=this.gboxy;
	  return coordinates;
	}
}