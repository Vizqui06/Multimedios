import java.awt.*; // For Color
import java.awt.image.BufferedImage; // For BufferedImage, which is used to create and manipulate images
import java.io.File; // For File, which is used to specify the output file
import java.io.IOException; // For IOException, which is used to handle input/output exceptions and errors don't crash the program
import javax.imageio.ImageIO; // For ImageIO, which is used to write the image to a file

public class Clock{
        public static void main(String[] args) {
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB); // Create a new BufferedImage object with width 400, height 300, and RGB color model
        // Draw the circunference of a circle with the BufferedImage measures:
        int centerX = img.getWidth() / 2; // Calculate the center X coordinate
        int centerY = img.getHeight() / 2; // Calculate the center Y coordinate
        int rad = 200; // Calculate the radius as half of the smaller dimension

        for(int x = 0; x < img.getWidth(); x++){ // Loop through each pixel in the width of the image
            for(int y = 0; y < img.getHeight(); y++){ // Loop through each pixel in the height of the image
                int dx = x - centerX; // Calculate the difference in X from the center
                int dy = y - centerY; // Calculate the difference in Y from the center

                if(dx * dx + dy * dy <= rad * rad){ // Check if the pixel is within the circle using the circle equation
                    img.setRGB(x, y, Color.black.getRGB()); // Set the color of area of the circle to black
                }
                else if (dx * dx + dy * dy <= (rad + 4) * (rad + 5) && dx * dx + dy * dy >= rad * rad){ // Check if the pixel fits with the circunference of the circle
                    img.setRGB(x, y, Color.white.getRGB()); // Set the color of the circunference pixels to white
                }
                else {
                    img.setRGB(x, y, Color.black.getRGB()); // Set every pixel out of the circle to black
                }

                // Draw the clock numbers as big gray dots on the circumference
                for(int i = 1; i <= 12; i++){ // Loop through each
                    double numberAngle = Math.toRadians((i / 12.0) * 360 - 90); // Calculate the angle for each clock hour dot
                    int numberX = (int)(centerX + (rad -10) * Math.cos(numberAngle)); // Calculate the X coordinate for the clock hour dot
                    int numberY = (int)(centerY + (rad -10) * Math.sin(numberAngle)); // Calculate the Y coordinate for the clock hour dot

                    if(Math.abs(x - numberX) < 5 && Math.abs(y - numberY) < 5){ // Check if the pixel is close to the clock hour dot position
                        img.setRGB(x, y, Color.gray.getRGB()); // Set the color of the clock hour dot pixels to gray
                    }
                }

                int setHour = 10; // Set the hour to 10
                int setMinute = 10; // Set the minute to 10

                // Draw both clock hands as white lines that starts at the center of the circle (hour is 10:10)
                float hourAngle = (setHour % 12 + setMinute / 60.0f) / 12.0f * 360 - 90; // Calculate the angle for the hour hand (hour is 10)
                float minuteAngle = (setMinute / 60.0f) * 360 - 90; // Calculate the angle for the minute hand (minute is 10)
                int hourX = (int)(centerX + (rad - 80) * Math.cos(Math.toRadians(hourAngle))); // Calculate the X coordinate for the hour hand, 
                int hourY = (int)(centerY + (rad - 80) * Math.sin(Math.toRadians(hourAngle))); // Calculate the Y coordinate for the hour hand
                int minuteX = (int)(centerX + (rad - 40) * Math.cos(Math.toRadians(minuteAngle))); // Calculate the X coordinate for the minute hand 
                int minuteY = (int)(centerY + (rad - 40) * Math.sin(Math.toRadians(minuteAngle))); // Calculate the Y coordinate for the minute hand

                // Note: 80 and 40 are subtracted from the radius to make the hands nearer to the hour dots.

                // From circle center to each hand, draw a lines to represent both hands
                // Draw hour hand
                for(int hx = Math.min(centerX, hourX); hx <= Math.max(centerX, hourX); hx++){ // Loop through the X coordinates from center to hour hand
                    int hy = centerY + (hourY - centerY) * (hx - centerX) / (hourX - centerX); // Calculate the corresponding Y coordinate
                    if(Math.abs(x - hx) < 2 && Math.abs(y - hy) < 2){ // Check if the pixel is close to the hour hand line
                        img.setRGB(x, y, Color.white.getRGB()); // Set the color of the hour hand pixels to white
                    }
                }
                // Draw minute hand
                for(int mx = Math.min(centerX, minuteX); mx <= Math.max(centerX, minuteX); mx++){ // Loop through the X coordinates from center to minute hand
                    int my = centerY + (minuteY - centerY) * (mx - centerX) / (minuteX - centerX); // Calculate the corresponding Y coordinate
                    if(Math.abs(x - mx) < 2 && Math.abs(y - my) < 2){ // Check if the pixel is close to the minute hand line
                        img.setRGB(x, y, Color.white.getRGB()); // Set the color of the minute hand pixels to white
                    }
                } 
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
