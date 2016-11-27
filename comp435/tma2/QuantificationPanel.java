/*
AUTHOR: Nickolas Reid
COURSE: COMP 435
PURPOSE: To create a boxLayout panel that contains a visual represenation of 
         the q tables, as well as give options to modify the qTables.
*/

package comp435.tma2;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;

public class QuantificationPanel extends Panel {

    //I need this box layout to make it look similar to the screenshot of the demo application
    //in the assignment instructions.
    BoxLayout boxlayout = new BoxLayout(this, BoxLayout.Y_AXIS);
    
    //Layout to hold the buttons
    private final Panel bottomMatrix = new Panel(new FlowLayout());
    //The different options for users.
    private final Button defaultOption = new Button("Default");
    private final Button constant = new Button("Constant");
    private final Button DCOnly = new Button("DC Only");

    //Grid to show the Q tables to the user.
    private final Panel gridLum = new Panel(new GridLayout(8, 8));
    private final Panel gridChrome = new Panel(new GridLayout(8, 8));
    //TextFields to hold the data of the Q tables that go into the grid.
    private final TextField[] luminance = new TextField[64];
    private final TextField[] chrominance = new TextField[64];
    //This object is to display the modified image on the middle panel.
    public ShowImage mi;

    //This holds the Quantization arrays. It cleans up code by having them seperate.
    private final QuantizationDefaults defaultQ = new QuantizationDefaults();

    
    //Constructor for QuantificationPanel object.
    QuantificationPanel(ShowImage modifiedImage) {
        mi = modifiedImage;
        setLayout(boxlayout);

        //When the QuantificationPanel is first made, the program knows to fill the textfields with the
        //default values from the stored arrays. 
        for (int i = 0; i < 64; i++) {
            chrominance[i] = new TextField(Integer.toString(defaultQ.chrominance[i]));
            luminance[i] = new TextField(Integer.toString(defaultQ.luminance[i]));
            //Add the textfields to the panels.
            gridLum.add(luminance[i]);
            gridChrome.add(chrominance[i]);
        }

        //Set up gui
        add(new Label("Quantization Matrix"));
        add(new Label("Luminance:"));
        add(gridLum);
        add(new Label("Chrominance"));
        add(gridChrome);
        bottomMatrix.add(defaultOption);
        bottomMatrix.add(constant);
        bottomMatrix.add(DCOnly);
        add(bottomMatrix);
        //Give the buttons their jobs.
        setUpButtons();
    }

    //Set up the buttons to do their job with action events.
    public void setUpButtons() {
        //Set the compression to default.
        defaultOption.addActionListener((ActionEvent e) -> {

            try {
                for (int i = 0; i < 64; i++) {
                    chrominance[i].setText(Integer.toString(defaultQ.chrominance[i]));
                    luminance[i].setText(Integer.toString(defaultQ.luminance[i]));
                }

                mi.resetCompression();
            } catch (IOException ex) {
                Logger.getLogger(QuantificationPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        //Set constants for the luminance and chrominance of an image.
        constant.addActionListener((ActionEvent e) -> {
            try {
                String chromConstantValue = JOptionPane.showInputDialog("Type in a constant value for chrominance");
                String lumaConstantValue = JOptionPane.showInputDialog("Type in a constant value for luminance");
                for (int i = 0; i < 64; i++) {
                    chrominance[i].setText(chromConstantValue);
                    defaultQ.constChrom[i] = Integer.parseInt(chromConstantValue);
                    luminance[i].setText(lumaConstantValue);
                    defaultQ.constLuma[i] = Integer.parseInt(lumaConstantValue);
                }

                mi.setConstant();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(QuantificationPanel.class.getName()).log(Level.SEVERE, "The program has lost the image!", ex);
            } catch (IOException ex) {
                Logger.getLogger(QuantificationPanel.class.getName()).log(Level.SEVERE, "There was an error reading the file.", ex);
            }catch(NumberFormatException nfe){
                JOptionPane.showMessageDialog(gridChrome, "Needed a number.");
            }
        });
        //This sets the image to be DC only
        DCOnly.addActionListener((ActionEvent e) -> {
            try {
                int dcValue = Integer.parseInt(JOptionPane.showInputDialog("Please type in a value for the DC component."));
                for (int i = 0; i < 64; i++) {
                    if (i == 0) {
                        chrominance[i].setText(Integer.toString(dcValue));
                        luminance[i].setText(Integer.toString(dcValue));
                    } else {
                        chrominance[i].setText(Integer.toString(0));
                        luminance[i].setText(Integer.toString(0));
                    }

                }

                mi.DCT(dcValue);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(gridChrome, "You did not put in a number, please try again.");
            } catch (IOException ex) {
                Logger.getLogger(QuantificationPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

}
