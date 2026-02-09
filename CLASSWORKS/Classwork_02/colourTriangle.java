import java.awt.*;  // For Color
import java.awt.image.BufferedImage; // This is used to create and manipulate images
import java.io.File; // This is used to specify the output file
import java.io.IOException; // This is used to handle input/output exceptions and errors don't crash the program
import javax.imageio.ImageIO; // This is used to write the image to a file

public class colourTriangle {
    public static void main(String[] args) {
        
        // Create an 800x800 pixel image with RGB color model (aspect ratio 1:1)
        BufferedImage img = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB); 

        // Fill the background with white color
        for(int x = 0; x < img.getWidth(); x++){ 
            for(int y = 0; y < img.getHeight(); y++){ 
                img.setRGB(x, y, Color.black.getRGB()); 
            } 
        }

        // Define isosceles triangle vertices
        int aX = 400, aY = 000; // Top vertex
        int bX = 000, bY = 800; // Bottom left vertex
        int cX = 800, cY = 800; // Bottom right vertex

       double commonDenominator = (double)((bY - cY) * (aX - cX) + (cX - bX) * (aY - cY));
        for(int x = 0; x < img.getWidth(); x++){ 
            for(int y = 0; y < img.getHeight(); y++){ 
                // Lambda formulas
                double lambda1 = ((bY - cY) * (x - cX) + (cX - bX) * (y - cY)) / commonDenominator;
                double lambda2 = ((cY - aY) * (x - cX) + (aX - cX) * (y - cY)) / commonDenominator;
                double lambda3 = 1.0 - lambda1 - lambda2;
                // System.out.println(lambda1 + " " + lambda2 + " " + lambda3); // debugging purposes

                // Check if the pixel is inside the triangle using barycentric coordinates
                if(lambda1 >= 0 && lambda2 >= 0 && lambda3 >= 0){                     
                    int blue  = (int)(lambda1 * 255); // lambda1 (Top) should be Blue
                    int red   = (int)(lambda2 * 255); // lambda2 (Bottom Left) should be Red
                    int green = (int)(lambda3 * 255); // lambda3 (Bottom Right) should be Green
                    // Ask IA and told me to bitwise shift to combine R, G, and B into a single INT_RGB value
                    int rgb = (red << 16) | (green << 8) | blue; // This combine the RGB values into a single integer
                    img.setRGB(x, y, rgb); // This sets the color of the pixel at (x, y) to the calculated RGB value, creating the gradient effect across the triangle.
                }
            } 
        }

        // Save the image to a file
        File outputImage = new File("CLASSWORKS/Classwork_02/colourTriangle.jpg"); // This specify the output image file
        try{ 
            ImageIO.write(img, "jpg", outputImage); // This encodes the pixel data into a standard JPEG file.
            System.out.println("Everything is OK."); // Confirmation message
        } catch(IOException e){ 
            // If there is an error, this message will explain what went wrong.
            throw new RuntimeException("Something went wrong: " + e.getMessage()); 
        }
    }
}