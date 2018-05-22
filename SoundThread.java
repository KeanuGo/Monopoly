import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundThread{
	private String resource;
	private boolean loop;
	Clip clip;
	
	public SoundThread(String resource, boolean loop) {
		
		this.resource = resource;
		this.loop = loop;
		if(!Monopoly.mute){
			new Thread(){
				public void run() {					
					try{
						 File file = new File(resource);
						 clip = AudioSystem.getClip();
						 AudioInputStream Audio = AudioSystem.getAudioInputStream(file);
						 clip.open(Audio);
						 
						 if(loop) {
							clip.loop(Clip.LOOP_CONTINUOUSLY);
						 }else{
							clip.start();
						 }
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}.start();
		}
    }
	
	public void mute() {
		clip.stop();
		clip.close();
	}
	
}