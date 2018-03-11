import java.awt.*;
import javax.swing.*;

public class GameWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;

	protected GameWindow(int width, int height) {
		
		setTitle("MediaLab Pac-Man");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		/* Align the game window in the center of the computer screen */
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(width, height);
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}
}