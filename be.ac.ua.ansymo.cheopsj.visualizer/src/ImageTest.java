import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class ImageTest {

	public static void main(String[] args) {
		final Display display = new Display();
		
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		Label label = new Label(shell, SWT.NONE);
		label.setImage(getCheckedImage(display));
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();
	}

	  static Image getCheckedImage(Display display) {
		  Image icon = new Image(display, "/Users/nicolasdemarbaix/Documents/workspace_thesis/be.ac.ua.ansymo.cheopsj.visualizer/src/be/ac/ua/ansymo/cheopsj/visualizer/views/graph/figures/package_obj.gif");
		  int iwidth = icon.getBounds().width;
		  int iheight = icon.getBounds().height;
		  
		  double sizeH = 3.0;
		  int imWidth = (int) (iwidth*sizeH);
		  int imHeight = (int) (iheight*sizeH);
		  Image img = new Image(display, imWidth,imHeight);
		  GC gc = new GC(img);
		  gc.setBackground(new org.eclipse.swt.graphics.Color(display, 0, 255, 0));
		  gc.fillArc(0, 0, imWidth, imHeight, 0, 50);
		  gc.setBackground(new org.eclipse.swt.graphics.Color(display, 255, 255, 0));
		  gc.fillArc(0, 0, imWidth, imHeight, 50, 150);
		  gc.setBackground(new org.eclipse.swt.graphics.Color(display, 255, 0, 0));
		  gc.fillArc(0, 0, imWidth, imHeight, 200, 160);
		  
		  int xcoord = (imWidth/2)-(iwidth/2);
		  int ycoord = (imHeight/2)-(iheight/2);
		  gc.drawImage(icon, xcoord, ycoord);
		  
		  return img;
	  }
}
