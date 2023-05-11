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

public class dihedral {
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
                    BufferedImage img_rotated = linear_transform(img,i,false,false);
                    
                    // Compares the images
                    areEqual = compareImages(img, img_rotated);
                    if (areEqual)
                    {
                        rotations++;

                        // Save the rotated image to the new directory
                        outputfile = new File(newDirectory + "/rotated_" + i +"_degress.png");
                        ImageIO.write(img_rotated, "png", outputfile);
                    }
                
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
     * @return true if the images are the same, false otherwise
     */
    public static boolean compareImages(BufferedImage img1, BufferedImage img2) 
    {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) 
            return false;

        for (int x = 0; x < img1.getWidth(); x++) 
            for (int y = 0; y < img1.getHeight(); y++) 
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) // Pixel is different
                    return false;

        return true;
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


    public static void clearDirectory(File directory) 
    {
        File[] files = directory.listFiles();

        if (files != null) 
            for (File file : files)
                if (!file.isDirectory())
                    file.delete();  
    }
}

