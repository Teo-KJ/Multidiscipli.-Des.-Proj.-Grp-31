package python_output_tester;


import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
public class ImageDisplay {
	
	public static void DisplayImage(String filename) throws IOException
	{
		BufferedImage img=ImageIO.read(new File(filename));
		ImageIcon icon=new ImageIcon(img);
		JFrame frame=new JFrame();
		frame.setLayout(new FlowLayout());
		frame.setSize(500000,2000);
		JLabel lbl=new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
