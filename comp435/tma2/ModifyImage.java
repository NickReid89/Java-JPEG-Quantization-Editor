/*
AUTHOR: Nickolas Reid
COURSE: COMP435
PURPOSE: To take in an image and magnify or shrink it.

*/


package comp435.tma2;

import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

public class ModifyImage {

    public RenderedOp modify(RenderedOp ro, double scaleX, double scaleY) {

        Interpolation interp = Interpolation.getInstance(
                Interpolation.INTERP_BILINEAR);

        //ParameterBlock stores all the necessary data about an image to be used for scaling.
        ParameterBlock params = new ParameterBlock();
        //add the users image file as a RenderedOp object.
        params.addSource(ro);
        /*Figure out the size. For example:
         1*1 = 1 = normal image size
         1* (0.1 * 11) = 1 * 1.1 = 1.1 so the image is a little bigger
         1 * (0.1 * 5) = 1 * 0.5 = 0.5 so the image is half size of normal, so the user zoomed out.
         */
        //width
        params.add(1 * (float) scaleX);
        //height
        params.add(1 * (float) scaleY);
        //Where image is on page.(width wise)
        params.add(0.0F);
        //Whereimage is on page (height wise)
        params.add(0.0F);
        // interpolation method
        params.add(interp);
        //Create a new RenderedOp that will replace the old image.
        return JAI.create("scale", params);
        //If the panel is new make a new scrollingImagePanel.

    }
}
