import java.awt.image.BufferedImage; // Standard Java library for handling image data in memory.
import java.io.File; // Standard Java library for file system operations.
import java.io.IOException; // Exception handling for file errors.
import javax.imageio.ImageIO;

public class CustomImage {
    private int width; // Width of the image in pixels
    private int height; // Height of the image in pixels
    private Pixel[][] pixels; // 2D array to store the pixel data of the image

    public CustomImage(int width, int height) { // Constructor to initialize the image with specified width and height
        this.width = width; // Set the width of the image to the provided value
        this.height = height; // Set the height of the image to the provided value
        this.pixels = new Pixel[height][width]; // Initialize the 2D array to store pixel data based on the specified dimensions
    }

    // Getters for the width, height, and pixels of the image.
    public int getWidth() { // Getter for the width of the image
        return width; // Return the width of the image
    }
    
    public int getHeight() { // Getter for the height of the image
        return height; // Return the height of the image
    } 

    public Pixel[][] getPixels() { // Getter for the pixel data of the image
        return pixels; // Return the 2D array containing the pixel data of the image
    }

    // Setters for the width, height, and pixels of the image.
    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPixels(Pixel[][] pixels) {
        this.pixels = pixels;
    }

    // Method to initialize all pixels in the 2D array with new Pixel objects.
    public void initializePixels() { 
        for (int coordY = 0; coordY < getHeight(); coordY++) { // Loop through each row of the image (starts from 0 and goes down to height-1).
            for (int coordX = 0; coordX < getWidth(); coordX++) { // Loop through each column of the image (starts from 0 and goes across to width-1).
                pixels[coordY][coordX] = new Pixel(); // Initialize each pixel in the 2D array with a new Pixel object (using the default 
                // constructor which sets RGB values to 0 to avoid NullPointerException when trying to access pixel data before initialization).
            }
        }
    }

    // To allow CompressedImage class to use CustomImage as a tool, I have to create a method (getPixel) that receives X & Y coordinates, 
    // and returns the Pixel object that is in that position.
    // getPixel is a method to get the Pixel object at a specific X,Y position in the image. It must be public so that it can be accessed 
    // from the CompressedImage class.
    public Pixel getPixel(int coordY, int coordX) { 
        return pixels[coordY][coordX]; // Return the Pixel object stored in the 2D array at the specified coordinates (Y is the row and X is the column).
    }

    // Auxiliary methods to help create an object same as the original image and to decompress the image:

    // Static method to create a CustomImage directly from a file path.
    // This communicates between image files and the custom class.
    public static CustomImage fromFile(String filePath) throws IOException {
        File file = new File(filePath);

        // 1. Validation: Check if the file is actually readable by the OS.
        if (!file.canRead()) {
            throw new IOException("PERMISSION ERROR: Java does not have permission to read: " + filePath);
        }

        // 2. Validation: Check if the file is empty (common cause for ImageIO failure).
        if (file.length() == 0) {
            throw new IOException("FILE ERROR: The file 'test.jpg' is empty (0 bytes).");
        }

        // 3. Attempting to read the image.
        BufferedImage validImage = ImageIO.read(file); 

        // 4. Checking if ImageIO failed to decode the format.
        if (validImage == null) {
            throw new IOException("FORMAT ERROR: 'test.jpg' is not a valid image or the format is not supported by ImageIO.");
        }
        
        // Create our CustomImage object and fill it with pixel data.
        CustomImage loadedImage = new CustomImage(validImage.getWidth(), validImage.getHeight());
        loadedImage.initializePixels(); 

        for (int y = 0; y < loadedImage.getHeight(); y++) {
            for (int x = 0; x < loadedImage.getWidth(); x++) {
                int rgb = validImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb) & 0xFF;
                
                // Use the Pixel setter to assign RGB values.
                loadedImage.getPixel(y, x).setRGB(r, g, b); 
            }
        }
        return loadedImage;
    }

    // Method to save the CustomImage back to a file (JPG format).
    // This method will take the pixel data from the CustomImage and write it to a new image file at the specified output path.
    public void saveToFile(String outputPath) throws IOException { 
        // Create a BufferedImage to hold the pixel data in a format that can be saved as an image file.
        BufferedImage img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);

        // Loop through all pixels and transfer them to the BufferedImage.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel p = pixels[y][x];
                // Recombine R, G, B into a single integer value for Java's image format.
                int rgb = (p.getRed() << 16) | (p.getGreen() << 8) | p.getBlue();
                img.setRGB(x, y, rgb);
            }
        }
        
        // CRITICAL FIX: Use the outputPath parameter instead of a hardcoded path.
        // Create a File object from the provided path.
        File outputFile = new File(outputPath);
        
        // Make sure the parent directories exist before trying to save.
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // Create any missing parent folders.
        }
        
        // Write the image to the file system as a JPG.
        ImageIO.write(img, "jpg", outputFile);
    }
    
    // This method allows the Decompressor to set the pixel data at specific coordinates in the CustomImage.
    public void setPixel(int y, int x, Pixel p) { 
        // If the coordinates are within the bounds of the image, set the pixel data at the specified coordinates to the provided Pixel object.
        if (y < height && x < width && y >= 0 && x >= 0) {
            // Copy the RGB values to avoid reference issues.
            this.pixels[y][x].setRGB(p.getRed(), p.getGreen(), p.getBlue());
        }
    }
}