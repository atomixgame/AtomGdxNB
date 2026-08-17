package com.atomgdx.viewer.media;

import com.atomgdx.viewer.media.ui.MediaViewerPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * End-to-End Visual test for the Audio & Media Studio player.
 */
public class AudioMediaViewerE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting LibGDX Audio & Media Studio Visual Test...");

        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("AtomGDX - Audio & Media Studio");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(900, 520);
            frame.setLocationRelativeTo(null);

            MediaViewerPanel panel = new MediaViewerPanel(new File("NeonCosmos_Theme.ogg"));
            panel.togglePlayback(); // Start playing to animate waveform

            frame.setContentPane(panel);
            frame.setVisible(true);

            Timer timer = new Timer(1200, e -> {
                try {
                    BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = img.createGraphics();
                    frame.paint(g2);
                    g2.dispose();

                    File out = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/media_viewer_proof.png");
                    ImageIO.write(img, "png", out);
                    System.out.println("Saved Audio & Media Viewer proof: " + out.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    frame.dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
        });

        Thread.sleep(2500);
        System.out.println(">>> AUDIO & MEDIA STUDIO TEST COMPLETED! <<<");
    }
}
