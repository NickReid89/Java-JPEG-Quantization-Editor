/*
 AUTHOR: Nickolas Reid
 COURSE: COMP435
 PURPOSE: The main part of the application. Sets up the GUI, and objects that will need to be used
 in image manipulation.
 */
package comp435.tma2;

import com.sun.media.jai.codec.FileSeekableStream;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class COMP435TMA2 extends Frame {

    //How big an image should be.
    static int scale = 10;
    RenderedOp ro;
    ShowImage original;
    ShowImage modified;
    QuantificationPanel controls;
    //Object which turns Bitmap images to JPEG format.
    //BitmapToJPEG bj = new BitmapToJPEG();
    PlanarImage src;
    String srcLocation;
    //Controls to create a menu bar.
    Menu menu = new Menu("File");
    MenuItem open = new MenuItem("Open");
    MenuBar mb = new MenuBar();
    //Checks if this is the first time the application is running.
    boolean startUp = true;

    COMP435TMA2() {
        setTitle("COMP435 - Assignment Two");
        //This allows the user to press the x to close the window
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        pickFile();

        setupPanels();
        pack();
        //Let the user view the application
        setVisible(true);

    }

    //takes in an absolute file path and creates a PlanarImage out of it.
    private PlanarImage loadImage(String imageName) {

        ParameterBlock pb = (new ParameterBlock()).add(imageName);
        PlanarImage srcImage = JAI.create("fileload", pb);

        if (srcImage == null) {
            JOptionPane.showMessageDialog(null, "Error in loading image: " + imageName);
            System.exit(1);
        }
        return srcImage;
    }

    private void setupPanels() {

        //Add the menu bar.
        mb.add(menu);
        menu.add(open);
        open.addActionListener((ActionEvent e) -> {
            pickFile();
        });

        //Set the layout for the windows.
        setLayout(new FlowLayout());

        //Set up the quantification side.
        controls = new QuantificationPanel(modified);
        //Add the original BMP image
        add(original);
        //Add the JPEG image.
        add(modified);
        //Add the quantification controls panel.
        add(controls);
        //Set up the menu bar.
        setMenuBar(mb);
    }

    // Method to pick a file.
    public void pickFile() {
        try {
            //File chooser to select a picture.
            JFileChooser fc = new JFileChooser();
            //Filter only bmp files.
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Bitmap Images", "bmp"));
            //Turn off the all files option
            fc.setAcceptAllFileFilterUsed(false);
            //Grab users choice.
            int returnValue = fc.showOpenDialog(null);
            //If the picked a file.
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                srcLocation = fc.getSelectedFile().getAbsolutePath();
                //Grab the file
                File location = new File(srcLocation);
                //Get the file data
                FileSeekableStream stream = new FileSeekableStream(location.getAbsolutePath());
                //Turn the data into a renderedOo object for image processing and to display the original image.
                ro = JAI.create("stream", stream);
            // Set up the panels for the window

                // Create the source op image.
                src = loadImage(srcLocation);
                ParameterBlock pb = new ParameterBlock();
                pb.addSource(src);
                //If theapplication is just starting it needs to create the ShowImage objects.
                if (original == null) {
                    original = new ShowImage(ro, "Original Image", src);
                    modified = new ShowImage(ro, "Converted Image", src);
                    //Converts the image to jpeg and refreshes the image.
                    modified.convertToJPEG();
                    //Otherwise replace the images.
                } else {
                    original.replaceImage(ro, src);
                    modified.replaceImage(ro, src);
                    //Converts the image to jpeg and refreshes the image.
                    modified.convertToJPEG();
                }
                startUp = false;
            } else {
                //Application needs an image on start up. Without it, it will shut down.
                if (startUp) {
                    JOptionPane.showMessageDialog(null, "Needed image to start, shutting down.");
                    System.exit(1);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(COMP435TMA2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args)  {
        //Create a new Object to run application.
        COMP435TMA2 test = new COMP435TMA2();
    }

}
