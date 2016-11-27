/*
 AUTHOR: Nickolas Reid
 COURSE: COMP435
 PURPOSE: This class takes in a bitmap file, converts it to JPEG and does a number
 of user defined modifications to it. Namely it can reset a jpeg to its default parameters,
 it can set a user defined custom table, and it can show a user defined DC only component.
 */
package comp435.tma2;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.widget.ScrollingImagePanel;
import javax.swing.JOptionPane;

public final class ShowImage extends Panel {

    //Holds the buttons for the first two panels
    private final Panel optionsContainer = new Panel(new FlowLayout());
    private final Label imageLabel = new Label("Converted Image:");
    //Scroll pane for images.
    protected final ScrollingImagePanel ipImage;
    protected final Button btnZoomIn = new Button("Zoom In");
    protected final Button btnZoomOut = new Button("Zoom Out");
    protected final Button btnReset = new Button("Actual Size");
    //This is the image it'll modify.
    private RenderedOp ro;
    //Encoder object that holds data on how the jpeg will be encoded.
    private ImageEncoder encoder = null;
    protected JPEGEncodeParam encodeParam = new JPEGEncodeParam();
    //Quanization tables stored here.
    private final QuantizationDefaults defaultQ = new QuantizationDefaults();
    private int scale = 10;
    //Source image.
    PlanarImage src;
    //Global string object that holds user input.
    String output;
    //global stream to read in data, saves creating multiple objects that do the
    //same thing.
    FileSeekableStream stream;

    //Constructor.
    ShowImage(RenderedOp ro, String labelName, PlanarImage pi) {
        //Store the planar image for future use.
        src = pi;
        //set up layout pane.
        setLayout(new BorderLayout());
        //Grab RenderedOp from super.
        this.ro = ro;
        //set up label.
        imageLabel.setText(labelName);
        //Add panel
        add(imageLabel, BorderLayout.NORTH);
        //Set up the initial image
        ipImage = new ScrollingImagePanel(ro, 450, 415);
        add(ipImage, BorderLayout.CENTER);
        //add buttons.
        optionsContainer.add(btnZoomIn);
        optionsContainer.add(btnZoomOut);
        optionsContainer.add(btnReset);
        add(optionsContainer, BorderLayout.SOUTH);
        //Set up the buttons under the images.
        setUpButtons();
    }

    public void replaceImage(RenderedOp newRo, PlanarImage newPi) {
        //Store the planar image for future use.
        src = newPi;
        //Grab RenderedOp
        this.ro = newRo;
        //Update the image.
        ipImage.set(ro);
    }

    //Set up the buttons to do their job with action events.
    public void setUpButtons() {
        //Object that allows image magnification.
        ModifyImage mi = new ModifyImage();
        //Zoom in image.
        btnZoomIn.addActionListener((ActionEvent e) -> {

            scale++;
            btnZoomIn.setEnabled((scale != 19));
            ipImage.set(mi.modify(ro, scale * 0.1, scale * 0.1));
        });
        //Zoom out of image.
        btnZoomOut.addActionListener((ActionEvent e) -> {
            scale--;
            //If scale is 1 the user is not allowed to zoom out anymore.
            btnZoomOut.setEnabled((scale != 1));
            ipImage.set(mi.modify(ro, scale * 0.1, scale * 0.1));
        });
        //Restore image to normal size and set controls to normal.
        btnReset.addActionListener((ActionEvent e) -> {
            scale = 10;
            btnZoomIn.setEnabled(true);
            btnZoomOut.setEnabled(true);
            ipImage.set(mi.modify(ro, 1, 1));
        });

    }

    //Converts an image to JPEG from bitmap
    public void convertToJPEG() throws FileNotFoundException {

        //default name for a default image.
        FileOutputStream out = new FileOutputStream("default.jpeg");

        encodeParam = new JPEGEncodeParam();
        encodeParam.setQuality(0.1F);
        encodeParam.setHorizontalSubsampling(0, 1);
        encodeParam.setHorizontalSubsampling(1, 2);
        encodeParam.setHorizontalSubsampling(2, 2);
        encodeParam.setVerticalSubsampling(0, 1);
        encodeParam.setVerticalSubsampling(1, 1);
        encodeParam.setVerticalSubsampling(2, 1);
        encodeParam.setRestartInterval(64);
        encodeParam.setChromaQTable(defaultQ.chrominance);
        encodeParam.setLumaQTable(defaultQ.luminance);
        encodeParam.setWriteImageOnly(false);

        try {
            encoder = ImageCodec.createImageEncoder("JPEG", out, encodeParam);
            //Encode the image with default values.
            encoder.encode(src);
            //Close the stream.
            out.close();
            //read in written file, and display it to user.
            File location = new File("default.jpeg");
            stream = new FileSeekableStream(location.getPath());
            ro = JAI.create("stream", stream);
            ipImage.set(ro);

        } catch (IOException e) {
            Logger.getLogger(COMP435TMA2.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "IOException encoding file.");
        }

    }

    //Resets the image to normal.
    public void resetCompression() throws FileNotFoundException {
        encodeParam = new JPEGEncodeParam();
        encodeParam.setQuality(0.1F);
        encodeParam.setHorizontalSubsampling(0, 1);
        encodeParam.setHorizontalSubsampling(1, 2);
        encodeParam.setHorizontalSubsampling(2, 2);
        encodeParam.setVerticalSubsampling(0, 1);
        encodeParam.setVerticalSubsampling(1, 1);
        encodeParam.setVerticalSubsampling(2, 1);
        encodeParam.setRestartInterval(64);
        encodeParam.setChromaQTable(defaultQ.chrominance);
        encodeParam.setLumaQTable(defaultQ.luminance);
        encodeParam.setWriteImageOnly(false);
        //Ask user if they want to put in new file name.
        output = JOptionPane.showInputDialog("Type in new file name or leave image as default.jpeg");
        if (output.equals("")) {
            output = "default";
        }
        FileOutputStream out = new FileOutputStream(output + ".jpeg");
        //Set up encoder.
        encoder = ImageCodec.createImageEncoder("JPEG", out, encodeParam);
        try {
            //encode
            encoder.encode(src);
            out.close();
            //read in written file, and update application.
            File location = new File(output + ".jpeg");
            stream = new FileSeekableStream(location.getPath());
            ro = JAI.create("stream", stream);
            ipImage.set(ro);

        } catch (IOException e) {
            System.out.println("IOException encoding");
            System.exit(1);
        }
    }

    //Sets the quant tables to a constant value.
    public void setConstant() throws FileNotFoundException, IOException {
        //Set up normal encoding.
        encodeParam = new JPEGEncodeParam();
        encodeParam.setQuality(0.1F);
        encodeParam.setHorizontalSubsampling(0, 1);
        encodeParam.setHorizontalSubsampling(1, 2);
        encodeParam.setHorizontalSubsampling(2, 2);
        encodeParam.setVerticalSubsampling(0, 1);
        encodeParam.setVerticalSubsampling(1, 1);
        encodeParam.setVerticalSubsampling(2, 1);
        encodeParam.setRestartInterval(64);
        //Set tables to new values.
        encodeParam.setChromaQTable(defaultQ.constChrom);
        encodeParam.setLumaQTable(defaultQ.constLuma);
        encodeParam.setWriteImageOnly(false);

        output = JOptionPane.showInputDialog("Type in new file name or leave image as default.jpeg");
        if (output.equals("")) {
            output = "default";
        }
        try (FileOutputStream out = new FileOutputStream(output + ".jpeg")) {
            encoder = ImageCodec.createImageEncoder("JPEG", out, encodeParam);
            encoder.encode(src);
        }
        //update image.
        File location = new File(output + ".jpeg");
        stream = new FileSeekableStream(location.getPath());
        ro = JAI.create("stream", stream);
        ipImage.set(ro);

    }

    //Set a DC Constant
    public void DCT(int value) throws FileNotFoundException, IOException {

        //Set up normal encoding.
        encodeParam = new JPEGEncodeParam();
        encodeParam.setQuality(0.1F);
        encodeParam.setHorizontalSubsampling(0, 1);
        encodeParam.setHorizontalSubsampling(1, 2);
        encodeParam.setHorizontalSubsampling(2, 2);
        encodeParam.setVerticalSubsampling(0, 1);
        encodeParam.setVerticalSubsampling(1, 1);
        encodeParam.setVerticalSubsampling(2, 1);
        encodeParam.setRestartInterval(64);
        encodeParam.setWriteImageOnly(false);

        //To set DC only, you need to only set the first value and force the other values to zero.
        int[] chrominanceTest = {
            value, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,};

        encodeParam.setLumaQTable(chrominanceTest);
        encodeParam.setChromaQTable(chrominanceTest);

        output = JOptionPane.showInputDialog("Type in new file name or leave image as default.jpeg");
        if (output.equals("")) {
            output = "default";
        }
        try (FileOutputStream out = new FileOutputStream(output + ".jpeg")) {
            encoder = ImageCodec.createImageEncoder("JPEG", out, encodeParam);
            encoder.encode(src);
        }
        //update image.
        File location = new File(output + ".jpeg");
        stream = new FileSeekableStream(location.getPath());
        ro = JAI.create("stream", stream);
        ipImage.set(ro);

    }

}
