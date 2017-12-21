package prototype;

import com.sun.speech.freetts.VoiceManager;

/**
 *
 * @author Pablo Alonso
 */
public class Voice {
    
    private boolean voiceAllowed;
    private final com.sun.speech.freetts.Voice speaker;
    
    public Voice(boolean voiceAllowed){
        this.voiceAllowed = voiceAllowed;
        System.setProperty("mbrola.base", "mbr302a");
        VoiceManager vm = VoiceManager.getInstance();
        //Pueden ser las voces de kevin16, o mbrola_us1; mbrola_us2; mbrola_us3
        speaker = vm.getVoice("kevin16");
        speaker.allocate();
    }
    
    public void mute(){
        voiceAllowed = false;
    }
    
    public void unmute(){
        voiceAllowed = true;
    }
    
    public void speak(String textToVoice){
        if(voiceAllowed) speaker.speak(textToVoice);
    }
    
    public void deallocateVoice(){
        speaker.deallocate();
    }
    
}
