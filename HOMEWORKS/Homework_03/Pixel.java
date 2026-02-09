public class Pixel{ // Create a Pixel class (does not extends CompressedImage because it is not a type of CompressedImage, but rather a component of it)

    private int red; // Red color component of the pixel (0-255)
    private int green; // Green color component of the pixel (0-255)
    private int blue; // Blue color component of the pixel (0-255)

    public Pixel() { // Here, the RGB values will be provided with initial values.
        this.red = 0; // Default red value
        this.green = 0; // Default green value
        this.blue = 0; // Default blue value
    }

    public int getRed() { // Getter for the red color component
        return red; // Return the red value of the pixel
    }

    public int getGreen() { // Getter for the green color component
        return green; // Return the green value of the pixel
    }

    public int getBlue() { // Getter for the blue color component
        return blue; // Return the blue value of the pixel
    }
    
    public void setRGB(int red, int green, int blue) { // Method to set RGB values of the pixel
        this.red = red; // Set the red value of the pixel to the provided value
        this.green = green; // Set the green value of the pixel to the provided value
        this.blue = blue; // Set the blue value of the pixel to the provided value
    }

    
    
}


/*
    public Pixel(int red, int green, int blue) { // Method to set RGB values of the pixel
        this.red = red; // Set the red value of the pixel to the provided value
        this.green = green; // Set the green value of the pixel to the provided value
        this.blue = blue; // Set the blue value of the pixel to the provided value
    }
*/