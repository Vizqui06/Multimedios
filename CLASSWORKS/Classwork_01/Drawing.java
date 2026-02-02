import java.awt.*;  // For Color
import java.awt.image.BufferedImage; // This is used to create and manipulate images
import java.io.File; // This is used to specify the output file
import java.io.IOException; // This is used to handle input/output exceptions and errors don't crash the program
import javax.imageio.ImageIO; // This is used to write the image to a file

public class Drawing {
    public static void main(String[] args) {
        
        // To have a workspace, start with an 800x600 empty space. TYPE_INT_RGB means the use of
        // standard red, green, and blue values to build the colors.
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB); 

        // By default, a new workspace is painted in black. To have it white, loop through every single pixel 
        // (x for width, y for height) to repaint the whole workspace with white.
        for(int x = 0; x < img.getWidth(); x++){ 
            for(int y = 0; y < img.getHeight(); y++){ 
                img.setRGB(x, y, Color.white.getRGB()); 
            } 
        }

        // To drawing the sun rays, fist goes the rays so they appear behind the sun.
        // First, the horizontal ray: with some trial and error, draw it at the hight of the future sun center so can be well placed.
        // The inner loop (Y) gives it a 4-pixel thickness so it's not a super thick line.
        for(int x = 30; x < 270; x++){ 
            for(int y = 123; y < 127; y++){ 
                img.setRGB(x, y, Color.RED.getRGB()); 
            } 
        }
        // Next, the vertical bar: it sits right in the middle of the sun center.
        // Same logic that was used for the horizontal bar, but now vertically.
        for(int x = 148; x < 152; x++){ 
            for(int y = 5; y < 245; y++){ 
                img.setRGB(x, y, Color.RED.getRGB()); 
            } 
        }

        // Just left the "X" cross centered at sun center to finish the sun rays.
        // We only check pixels in a 160x160 area around the center to save processing time. 
        int crossCenterX = 150; 
        int crossCenterY = 125; 
        // Cross rays must be shirter than the horizontal and vertical ones.
        for(int x = 70; x < 230; x++){ 
            for(int y = 45; y < 205; y++){ 
                // In Geometry, a diagonal line is made by the distance from the center 
                // to both axes. (x-cx == y-cy) is the '\' line, and (x-cx == -(y-cy)) is the '/' line.
                // So if a pixel meets either of both conditions, it's part of the cross.
                if(x - crossCenterX == y - crossCenterY || x - crossCenterX == -(y - crossCenterY)){ 
                    img.setRGB(x, y, Color.RED.getRGB()); 
                }
            }
        }

        // The sun must be drawn AFTER the cross, so it will naturally overlap and cover the center.
        int sunCenterX = 150; 
        int sunCenterY = 125; 
        int sunRadius = 70; 
        for(int x = 0; x < img.getWidth(); x++){ // Loop through each pixel in the determined width
            for(int y = 0; y < img.getHeight(); y++){  // Loop through each pixel in the determined height
                // Now calculate how far each pixel is from the sun's center using dx and dy.
                int dx = x - sunCenterX; 
                int dy = y - sunCenterY; 

                // Pythagorean theorem: if distance squared (dx^2 + dy^2) is less than 
                // radius squared, the pixel is inside the circle.
                if(dx * dx + dy * dy <= sunRadius * sunRadius){ 
                    img.setRGB(x, y, Color.yellow.getRGB()); // The pixels inside the sun are painted yellow.
                }
            }
        }

        // With the sun and its rays drawn, it's time to draw the green hills.
        // To draw the hills, I'll use several sine waves to create a wavy hill effect. 
        // The hills goes through all the width of the image, so have to go through all x values.
        for(int x = 0; x < img.getWidth(); x++){ 
            // The "surface" of the hill (startY) is calculated per column (x):
            int startY = (int)(img.getHeight() * 0.7 + 40 * Math.sin(x / 19.0));
            // - (img.getHeight() * 0.7): Puts the baseline at 70% of the image height. Means the hills start at 420 pixels from the top (600 * 0.7 = 420).
            // - (40 * Math.sin(x / 19.0)): '40' is the hill height (amplitude), and '19.0' controls the width of the waves (frequency).
            // This particular parameters were chosen after some trial and error to get accurate to the example image given by the teacher.

            // Once the surface margin is determined, the only thing left is paint everything from there to the bottom to color green.
            for(int y = startY; y < img.getHeight(); y++){  // From the calculated surface to the bottom of the image.
                if(y >= 0 && y < img.getHeight()){ // Makes sure to stay within pixel bounds and avoid ArrayIndexOutOfBoundsException.
                    img.setRGB(x, y, Color.green.getRGB()); // Paint everything below the sin function green to represent the hills.
                }
            }
        }

        // With the instructions of the drawing finished, it's time to make the drawing visible.
        File outputImage = new File("Drawing.jpg"); // This specify the output image file
        try{ 
            ImageIO.write(img, "jpg", outputImage); // This encodes the pixel data into a standard JPEG file.
            System.out.println("Everything is OK."); // Confirmation message
        } catch(IOException e){ 
            // If there is an error, this message will explain what went wrong.
            throw new RuntimeException("Something went wrong: " + e.getMessage()); 
        }
    }
}