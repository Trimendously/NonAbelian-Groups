// For selecting the image files
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

// For comparing the image files
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

// For rotating/flipping the images
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.Color;


public class dihedral 
{
    public static void main (String[] args) 
    {
        int rotations = 0, reflections = 0, elements = 0; // Number of elements in the group
        int result; // Store the file path
        boolean areEqual = false; // Checks if 2 imgs are the same

        File outputfile = null;

        // Sets the default directory for the file explorer to the directory with this file
        String directory = System.getProperty("user.dir");
        JFileChooser fileChooser = new JFileChooser(new File(directory));

        // Sets the fileChooser to accept only common image file extensions
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "gif","jpeg", "jpg", "png");
        fileChooser.setFileFilter(filter);

        // Shows the fileChooser for the image
        result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file1 = fileChooser.getSelectedFile();
            System.out.println("Image file has been selected  ( " + file1.getAbsolutePath() + " )");

            try 
            {
                // Loads the image
                BufferedImage img = ImageIO.read(file1);

                // Creates a new directory
                String newDirectory = directory + "/linear_transformations";
                File dir = new File(newDirectory);

                if (dir.exists())
                    clearDirectory(dir);
                else
                    dir.mkdirs();
                
                for (int i = 0; i < 360; i+=60 )
                {
                    int padding = calculatePadding(img, i);
                    BufferedImage temp = padImage(img, padding);
                    BufferedImage img_rotated = linear_transform(temp,i,false,false);
                    img_rotated = cropImage(img_rotated,padding);
                    // Compares the images
                    areEqual = compareImages(img, img_rotated, 60);
                    if (areEqual)
                    {
                        rotations++;
                        // Save the rotated image to the new directory
                        outputfile = new File(newDirectory + "/rotated_" + i +"_degress.png");
                        ImageIO.write(img_rotated, "png", outputfile);
                    } 
                    outputfile = new File(newDirectory + "/rotated_" + i +"_degress.png");
                    ImageIO.write(img_rotated, "png", outputfile);           
                }
                System.out.println("The image has " + rotations+ " rotation(s) that returns it to the identity.");     
            } 
            catch (IOException e) 
            {
                System.out.println("Error: " + e.getMessage());
            }
        
        }
    }

    /**
     * Compares two images to see if they are the same.
     *
     * @param img1 image #1
     * @param img2 image #2
     * @param tolerance % of max avg color difference between pixels
     * @return true if the images are the same, false otherwise
     */
    public static boolean compareImages(BufferedImage img1, BufferedImage img2, double tolerance) 
    {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) 
            return false;
        double totalDiff = 0.0;
        int numPixels = 0;
        for (int x = 0; x < img1.getWidth(); x++) 
        {
            for (int y = 0; y < img1.getHeight(); y++) 
            {
                int rgb1 = img1.getRGB(x,y);
                int rgb2 = img2.getRGB(x,y);

            // Calculate the color difference between the pixels
            int redDiff = Math.abs((rgb1 >> 16) & 0xFF - (rgb2 >> 16) & 0xFF);
            int greenDiff = Math.abs((rgb1 >> 8) & 0xFF - (rgb2 >> 8) & 0xFF);
            int blueDiff = Math.abs(rgb1 & 0xFF - rgb2 & 0xFF);
            double pixelDiff = (redDiff + greenDiff + blueDiff) / 3.0;

            // Accumulate the total difference and pixel count
            totalDiff += pixelDiff;
            numPixels++;
            }
        }

        // Calculate the average difference per pixel and compare to the tolerance
        double avgDiff = totalDiff / numPixels;
        System.out.println("Error: " + avgDiff);
        return avgDiff <= tolerance;
    }

    /**
     * Performs a specified linear transformation on an image.
     *
     * @param image Base image
     * @param degrees Degrees by which to rotate
     * @param flipHorizontal Whether or not to reflect about the x-axis
     * @param flipVertical Whether or not to reflect about the y-axis
     * @return new image
     */
    public static BufferedImage linear_transform(BufferedImage image, double degrees, boolean flipHorizontal, boolean flipVertical) 
    {
        int width = image.getWidth();
        int height = image.getHeight();
        double radians = Math.toRadians(degrees);

        AffineTransform transform = new AffineTransform();
        transform.translate(width / 2, height / 2);
        transform.rotate(radians);
        transform.translate(-width / 2, -height / 2);

        BufferedImage rotated = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = rotated.createGraphics();
        g2d.drawImage(image, transform, null);
        g2d.dispose();

        return rotated;
    }

    /**
     * Overlays two images
     * 
     * @param img1 image #1
     * @param img2 image #2
     * @return new image
     */
    public static BufferedImage overlayImages(BufferedImage img1, BufferedImage img2) 
    {
        int width = Math.max(img1.getWidth(), img2.getWidth());
        int height = Math.max(img1.getHeight(), img2.getHeight());
    
        BufferedImage overlayed = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    
        Graphics2D g2d = overlayed.createGraphics();
        g2d.drawImage(img1, 0, 0, null);
        g2d.drawImage(img2, 0, 0, null);
        g2d.dispose();
    
        return overlayed;
    }

    /**
     * Pads an image with white space
     *
     * @param img image #1
     * @param padding how much to pad the image by
     * @return padded image
     */
    public static BufferedImage padImage(BufferedImage img, int padding) 
    {
        int width = img.getWidth() + 2 * padding;
        int height = img.getHeight() + 2 * padding;

        BufferedImage paddedImage = new BufferedImage(width, height, img.getType());
        Graphics2D g2d = paddedImage.createGraphics();

        g2d.setColor(Color.WHITE); // set the padding color to white
        g2d.fillRect(0, 0, width, height); // fill the padded image with white
        g2d.drawImage(img, padding, padding, null); // draw the original image onto the padded image
        g2d.dispose();

        return paddedImage;
    }

    /**
     * Calculates the neccesary padding in order to have a rotated image not be cut off
     *
     * @param img image #1
     * @param degree the degree by which the image is rotated
     * @return appropriate padding value
     */
    public static int calculatePadding(BufferedImage image, double degrees) 
    {
        int width = image.getWidth();
        int height = image.getHeight();
    
        // Calculates the distance between the center of the image and the farthest corner of the image
        double maxDistance = Math.sqrt((width / 2.0) * (width / 2.0) + (height / 2.0) * (height / 2.0));
    
        // Rotates the point representing the farthest corner by the desired angle of rotation
        double radians = Math.toRadians(degrees);
        double rotatedX = (width / 2.0) * Math.abs(Math.cos(radians)) + (height / 2.0) * Math.abs(Math.sin(radians));
        double rotatedY = (height / 2.0) * Math.abs(Math.cos(radians)) + (width / 2.0) * Math.abs(Math.sin(radians));
    
        // Calculates the distance between the new rotated point and the center of the image
        double rotatedDistance = Math.sqrt(rotatedX * rotatedX + rotatedY * rotatedY);
    
        // Adds the two distances together to get maximum displacement
        double maxDisplacement = maxDistance + rotatedDistance;
    
        // Rounds up
        return (int) Math.ceil(maxDisplacement);
    }

    /**
     * Crops an image to reverse padding
     *
     * @param paddedImage the padded image
     * @param paddingValue the value by which the image was padded
     * @return original image wuthiyt padding
     */
    public static BufferedImage cropImage(BufferedImage paddedImage, int paddingValue) 
    {
        int width = paddedImage.getWidth() - 2 * paddingValue;
        int height = paddedImage.getHeight() - 2 * paddingValue;
    
        BufferedImage croppedImage = new BufferedImage(width, height, paddedImage.getType());
    
        Graphics2D g2d = croppedImage.createGraphics();
        g2d.drawImage(paddedImage, -paddingValue, -paddingValue, null);
        g2d.dispose();
    
        return croppedImage;
    }

    /**
     * Clears a directory 
     *
     * @param directory the directory that is to be cleared
     */
    public static void clearDirectory(File directory) 
    {
        File[] files = directory.listFiles();

        if (files != null) 
            for (File file : files)
                if (!file.isDirectory())
                    file.delete();  
    }
}

