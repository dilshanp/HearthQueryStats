import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.net.HttpURLConnection;

/*
Added a small feature when dealing with only 1 card (in the random command).
Random returns a random card and this ImageUrl class is able to fetch
the link for the physical card image and background via HearthstoneJSON's
new card art API.
 */
public class ImageUrl {
    public static void displayImageFromUrl(String arg) {
        Image image;
        try {
            URL url = new URL("https://art.hearthstonejson.com/v1/render/latest/enUS/256x/" + arg + ".png");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            image = ImageIO.read(connection.getInputStream());
            JFrame frame = new JFrame();
            frame.setSize(256, 382);
            JLabel label = new JLabel(new ImageIcon(image));
            frame.add(label);
            frame.setVisible(true);
            try {
                Thread.sleep(10000);
                frame.setVisible(false);
                frame.dispose();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
