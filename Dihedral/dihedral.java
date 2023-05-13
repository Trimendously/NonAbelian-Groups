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
import java.awt.AlphaComposite;

public class dihedral 
{
    public static void main (String[] args) 
    {
        int rotations = 0, reflections = 0, elements = 0; // Number of elements in the group
        int result; // Store the file path
        boolean rotational_symmetry = false; // Checks if a rotation(s) return a img back to identity
        boolean reflectional_symmetry = false; // Checks if a reflection(s) return a mg back to identity

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
                
                    // Resaves base image
                outputfile = new File(newDirectory + "/base" +".png");
                ImageIO.write(img, "png", outputfile);   
                
                for (int i = 0; i < 360; i+= 90)
                {
                    int padding = calculatePadding(img, i);
                    BufferedImage temp = padImage(img, padding);
                    BufferedImage img_rotated = rotate(temp,i);
                    img_rotated = cropImage(img_rotated,padding);
                    BufferedImage img_rotated_then_reflected = reflect(img_rotated);
                    // Compares the images
                    System.out.println("\n\nRotation by "+ i + " degrees: \n");
                    rotational_symmetry = compareImages(img, img_rotated, 5);
                    System.out.println("\n\nRotation by "+ i + " degrees and then reflected across the y axis: \n");
                    reflectional_symmetry = compareImages(img, img_rotated_then_reflected, 5);
                    // Only if the images are the same (very close)
                    if (rotational_symmetry)
                    {
                        rotations++;

                        // Save the rotated image to the new directory
                        outputfile = new File(newDirectory + "/rotated_" + i +"_degress.png");
                        ImageIO.write(img_rotated, "png", outputfile);
                    }
                    if (reflectional_symmetry)
                    {
                        reflections++;

                        // Save the rotated image to the new directory
                        outputfile = new File(newDirectory + "/rotated_" + i +"_degress_&_reflected.png");
                        ImageIO.write(img_rotated_then_reflected, "png", outputfile);
                    }

                    outputfile = new File(newDirectory + "/rotated_" + i +"_degress.png");
                    ImageIO.write(img_rotated, "png", outputfile);
                    outputfile = new File(newDirectory + "/rotated_" + i +"_degress_&_reflected.png");
                    ImageIO.write(img_rotated_then_reflected, "png", outputfile);
                            
                }
                System.out.println("The image has rotational symmetry of degree: " + rotations);
                System.out.println("The image has reflectional symmetry of degree: " + reflections);     
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
        int width = img1.getWidth();
        int height = img1.getHeight();
        double totalDiff = 0.0; // # total difference in pixel values
        int numPixels = 0; // total # of pixels
        int diff_counter = 0 ; // # of different pixels
        int edge_cases = 0; // # of pixels where image was cuttoff

        for (int x = 0; x < width; x++) 
        {
            for (int y = 0; y < height; y++) 
            {
                int rgb1 = img1.getRGB(x,y);
                int rgb2 = img2.getRGB(x,y);

                int red1 = (rgb1 >> 16) & 0xFF;
                int green1 = (rgb1 >> 8) & 0xFF;
                int blue1 = rgb1 & 0xFF;
                int alpha1 = (rgb1 >> 24) & 0xFF;

                int red2 = (rgb2 >> 16) & 0xFF;
                int green2 = (rgb2 >> 8) & 0xFF;
                int blue2 = rgb2 & 0xFF;
                int alpha2 = (rgb2 >> 24) & 0xFF;

                if ((alpha1 != 0) && (alpha2 == 0))   // Is transparent    
                    edge_cases++;
                else if (rgb1 != rgb2)
                {
                    diff_counter++;
                    
                    // Calculates the color difference between the pixels
                    int redDiff = Math.abs(red1-red2);
                    int greenDiff = Math.abs(green1-green2);
                    int blueDiff = Math.abs(blue1-blue2);
                    double pixelDiff = (redDiff + greenDiff + blueDiff) / 3.0;

                    // The total pixel difference
                    totalDiff += pixelDiff;
                }
                numPixels++;
                
            }
        }
      
        // Calculates the average difference per pixel and compares to the tolerance
        double percOff = (double) diff_counter / (numPixels-edge_cases);
        double avgDiff = (double) totalDiff / (width * height);
        if (avgDiff <= tolerance)
        {
            System.out.println("Error: " + avgDiff + " %");
            System.out.println("Percent of Pixels different (that are included): " + percOff*100 + " %");
            System.out.println("Total Pixels: " + (width * height));
            System.out.println("Edge Cases: " + edge_cases);
            System.out.println("Total Number of Diferent Pixels: " + diff_counter);
            return true;
        }
        else
            return false;
    }

    /**
     * Performs a rotation by n degrees on an image.
     *
     * @param image Base image
     * @param degrees Degrees by which to rotate
     * @return new image
     */
    public static BufferedImage rotate(BufferedImage image, double degrees) 
    {
        int width = image.getWidth();
        int height = image.getHeight();
        double radians = Math.toRadians(degrees);

        BufferedImage transparentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = transparentImage.createGraphics();
        AffineTransform transform = AffineTransform.getRotateInstance(radians, width / 2, height / 2);
        
        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
    
        BufferedImage rotatedImage = new BufferedImage(transparentImage.getWidth(), transparentImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D finalG2d = rotatedImage.createGraphics();
        finalG2d.setComposite(AlphaComposite.Clear); // Set composite to clear the background
        finalG2d.fillRect(0, 0, rotatedImage.getWidth(), rotatedImage.getHeight());
        finalG2d.setComposite(AlphaComposite.Src); // Set composite to draw pixels normally
        finalG2d.drawImage(transparentImage, 0, 0, null);
        finalG2d.dispose();

        return rotatedImage;
    }

        /**
     * Performs a reflection along the y-axis on an image.
     *
     * @param image Base image
     * @return new image
     */
    public static BufferedImage reflect(BufferedImage image) 
    {
        int width = image.getWidth();
        int height = image.getHeight();
  
        BufferedImage reflectedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    
        AffineTransform transform = new AffineTransform(-1, 0, 0, 1, width, 0);
        Graphics2D g2d = reflectedImage.createGraphics();
        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
    
        return reflectedImage; 
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

        BufferedImage paddedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = paddedImage.createGraphics();

        g2d.setColor(new Color(0, 0, 0, 0)); // set the padding color to transparent
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
        double radians = Math.toRadians(degrees);

        // Calculates the distance between the center of the image and the farthest corner of the image
        double maxDistance = Math.sqrt((width / 2.0) * (width / 2.0) + (height / 2.0) * (height / 2.0));
    
        // Rotates the point representing the farthest corner by the desired angle of rotation
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
     * @return original image wiht padding
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

