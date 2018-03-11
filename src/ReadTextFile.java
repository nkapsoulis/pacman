import java.io.*; 
import java.util.Scanner;
	
public class ReadTextFile {
		
		private char[][] fileInChars;
		// List<char[]> === fileInChars, Optimization, as soon as we have only small text files, it's OK.
		protected ReadTextFile(String fileMeta) throws IOException {
			
			Scanner fileScanner = null;
	        try {
	          if (fileMeta.matches(".*\\blevels\\b.*"))  // = file is in game project folders
	        	  fileScanner = new Scanner(new BufferedReader(new FileReader(
	        		  	new File("").getAbsolutePath()+fileMeta)));
	          else
	        	  fileScanner = new Scanner(new BufferedReader(new FileReader(new File(fileMeta))));
	          //System.out.println("fileMeta:"+fileMeta+"\nURL:"+this.getClass().getClassLoader().getResource(fileMeta));
	          
	          /* File -> how many lines */
	          int linecount=0; 
	          while (fileScanner.hasNextLine()) {
	        	  linecount++;
	        	  fileScanner.nextLine();
	          }          
	          
	          /* File -> store in fileInChars[][] */
	          fileInChars = new char [linecount][1];
	          if (fileMeta.matches(".*\\blevels\\b.*"))// fileMeta.length()>30) // = file isn't in game project folders
	        	  fileScanner = new Scanner(new BufferedReader(new FileReader(
	        		  	new File("").getAbsolutePath()+fileMeta)));
	          else
	        	  fileScanner = new Scanner(new BufferedReader(new FileReader(new File(fileMeta))));
	          int lineNumber=0;
	          while (fileScanner.hasNextLine()) {
	        	  char[] line = fileScanner.nextLine().toCharArray();
	        	  fileInChars[lineNumber]=line;
	        	  lineNumber++;
	          }
	        }
	        finally {
	          if (fileScanner != null) 
	            	fileScanner.close();
	        }       
		}
		protected char[][] getFileInChars(){
			return this.fileInChars;
		}
		protected String getFileInStringArray(){
			String stringArray = ""; 
			char[][] tempArray = this.fileInChars;
			int i;
			for (i=0;i<tempArray.length-1;i++) 
					stringArray+=new String(tempArray[i])+"\n";
			stringArray+=new String(tempArray[i]);
			return stringArray;
		}
}
