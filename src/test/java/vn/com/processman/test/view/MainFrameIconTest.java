package vn.com.processman.test.view;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * @overview
 *  
 * @author dmle
 */
public class MainFrameIconTest {
  
  public static void main(String[] args) {
    // create a JFrame with a frame icon
    JFrame main = new JFrame("Test frame with icon");
    main.setSize(400, 500);
    
    Toolkit tk = Toolkit.getDefaultToolkit();
    URL imageURL = MainFrameIconTest.class.getResource("formMain.png");
    Image icon = tk.getImage(imageURL);
    main.setIconImage(icon);

    JLabel label = new JLabel(new ImageIcon(icon,null));
    main.add(label);
    
    main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    main.setVisible(true);
  }
}
