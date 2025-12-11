import javax.sound.sampled.*;
import java.io.File;

public class Sound {
    private static Clip bgMusic;
    private static boolean isMuted = false;
    private static Clip helicopterClip;

    public static void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    public static void playBackground(String filePath) {
        if (isMuted) return;
        try {
            if (bgMusic != null && bgMusic.isRunning()) {
                return;
            }

            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            bgMusic = AudioSystem.getClip();
            bgMusic.open(audioStream);
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
            bgMusic.start();

        } catch (Exception e) {
            System.out.println("Error playing background music: " + e.getMessage());
        }
    }


    public static void stop() {
        if (bgMusic != null) {
            bgMusic.stop();
        }
    }


    public static void toggleMute() {
        if (isMuted) {
            isMuted = false;
            if (bgMusic != null) bgMusic.start();
        } else {
            isMuted = true;
            if (bgMusic != null) bgMusic.stop();
        }
    }

    public static boolean isMuted() {
        return isMuted;
    }

    public static void playLoopSound(String filePath) {

        try {
            // 1. تحقق من أن الكليب يعمل حالياً (إذا كان يعمل، لا تفعل شيئاً)
            if (helicopterClip != null && helicopterClip.isRunning()) {
                return;
            }

            File soundFile = new File(filePath);

            // 🆕 إذا كان الكليب موجوداً ولكنه مغلق، نقوم بإعادة تهيئته.
            // أو ببساطة، ننشئ كليب جديد دائماً لضمان النظافة (أفضل).

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile); // 💡 يتم إنشاء تيار جديد هنا

            // 2. إنشاء كليب جديد في كل مرة يتم فيها التشغيل (مهم لـ open)
            helicopterClip = AudioSystem.getClip();

            helicopterClip.open(audioStream);
            helicopterClip.loop(Clip.LOOP_CONTINUOUSLY); // 💡 تشغيل متكرر
            helicopterClip.start();

        } catch (Exception e) {
            System.out.println("Error playing looping sound: " + e.getMessage());
        }
    }

    public static void stopHelicopterSound() {
        if (helicopterClip != null) {
            helicopterClip.stop();
            helicopterClip.close();
            helicopterClip = null;
        }
    }
}
