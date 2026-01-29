import java.awt.*; // For Color
import java.awt.image.BufferedImage; // For BufferedImage, which is used to create and manipulate images
import java.io.File; // For File, which is used to specify the output file
import java.io.IOException; // For IOException, which is used to handle input/output exceptions and errors don't crash the program
import javax.imageio.ImageIO; // For ImageIO, which is used to write the image to a file

public class Main { // Main class
    public static void main(String[] args) { // Main method
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB); // Create a new BufferedImage object with width 400, height 300, and RGB color model
        
        // Draws a red rectangle with the BufferedImage measures.
        for(int x = 0; x < img.getWidth(); x++){ // Loop through each pixel in the width of the image
            for(int y = 0; y < img.getHeight(); y++){ // Loop through each pixel in the height of the image up to the current x value
                img.setRGB(x, y, Color.red.getRGB()); // Set the color of the pixel at (x, y) to red
            } 
        }
        
        // Drawing diagonally half of the rectangle with blue color.
        for (int y = 0; y < img.getHeight(); y++) { // Loop through each pixel in the height of the image (y axis, from top to bottom)
            int xLimit = (y * img.getWidth()) / img.getHeight(); // Calculate the limit for X based on the current Y value to create a diagonal using the slope formula
            // Now to set the pixels from (0, y) to (xLimit, y) to blue
            for (int x = 0; x < xLimit; x++) {  // Loop through each pixel in the width of the image up to the calculated xLimit
                img.setRGB(x, y, Color.blue.getRGB()); // Set the color of the pixel at (x, y) to blue
            }
        }

        File outputImage = new File("image.jpg"); // Create a new File object to specify the output image file
        try{ // Try to write the image to the file
            ImageIO.write(img, "jpg", outputImage); // Write the BufferedImage to the specified file in JPEG format
        } catch(IOException e){ // Catch any IOException that occurs during the writing process
            throw new RuntimeException(e); // Throw a RuntimeException if an IOException occurs
        }
    }
}

// If an ArrayIndexOutOfBoundsException occurs, it means that the code is trying to access an index of an array that is out of bounds. 
// This can happen if the loops are not correctly defined to stay within the image dimensions.