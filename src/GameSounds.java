import java.net.URL;
import javax.sound.sampled.*;


/* This class controls all sound effects*/
public class GameSounds{
    
    Clip nomNom;
    Clip newGame;
    Clip death;
    /* Keeps track of whether or not the eating sound is playing*/
    boolean stopped;
       

/* Initialize audio files */ 
    protected GameSounds(){
        stopped=true; 
        URL url;
        AudioInputStream audioIn;        
        try{
            // Pacman eating sound
            url = this.getClass().getClassLoader().getResource("sounds/nomnom.wav");
            audioIn = AudioSystem.getAudioInputStream(url);
            nomNom = AudioSystem.getClip();
            nomNom.open(audioIn);            
            // newGame        
            url = this.getClass().getClassLoader().getResource("sounds/newGame.wav");
            audioIn = AudioSystem.getAudioInputStream(url);
            newGame = AudioSystem.getClip();
            newGame.open(audioIn);            
            // death        
            url = this.getClass().getClassLoader().getResource("sounds/death.wav");
            audioIn = AudioSystem.getAudioInputStream(url);
            death = AudioSystem.getClip();
            death.open(audioIn);
        }catch(Exception e){}
    }    
    /* Play pacman eating sound */
    protected void nomNom(){
        /* If it's already playing, don't start it playing again!*/
        if (!stopped)
          return;
        stopped=false;
        nomNom.stop();
        nomNom.setFramePosition(0);
        nomNom.loop(Clip.LOOP_CONTINUOUSLY);
    } 
    /* Stop pacman eating sound */
    protected void nomNomStop(){
        stopped=true;
        nomNom.stop();
        nomNom.setFramePosition(0);
    }    
    /* Play new game sound */
    protected void newGame(){
        newGame.stop();
        newGame.setFramePosition(0);
        newGame.start();
    }    
    /* Play pacman death sound */
    protected void death(){
        death.stop();
        death.setFramePosition(0);
        death.loop(Clip.LOOP_CONTINUOUSLY); // stop it in Map.class when necessary (dying == 0)
    }
}
