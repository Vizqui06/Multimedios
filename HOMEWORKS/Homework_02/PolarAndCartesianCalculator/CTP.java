// Create a program that converts from Cartesian to Polar Coordinates 
// based on the info supplied by the user.

// In CTP, the calc will convert from Cartesian (x,y) to Polar (r,angle)
// So, having these equations in mind:
// x = r * cos(angle)
// y = r * sin(angle)
//To find r and angle:
// r = sqrt(x^2 + y^2)
// angle = atan2(y, x)

import java.util.Scanner;

public class CTP{
    public static void main(String[] args) {
        CTP calculator = new CTP(0, 0); // Create an instance of CTP with initial coordinates (0,0)
        calculator.getCartesianCoordinates(); // Get Cartesian coordinates from user
        calculator.polar(); // Convert to Polar coordinates and display the result
    }

    private float coordX; // X coordinate in Cartesian
    private float coordY; // Y coordinate in Cartesian

    public CTP(float x, float y) { // Constructor to initialize Cartesian coordinates
        this.coordX = x;
        this.coordY = y;
    }

    public float getCoordX() {
        return coordX;
    }

    public void setCoordX(float coordX) {
        this.coordX = coordX;
    }

    public float getCoordY() {
        return coordY;
    }

    public void setCoordY(float coordY) {
        this.coordY = coordY;
    }

    // Get cartesian coordinates from user
    public void getCartesianCoordinates() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the X coordinate: ");
        this.coordX = scanner.nextFloat();
        System.out.print("Enter the Y coordinate: ");
        this.coordY = scanner.nextFloat();
        // scanner.close(); // Apparentley, it is not recommended to close the scanner to avoid closing System.in
    }

    private void polar() {
        try {
            float distanceR = (float) Math.sqrt(this.coordX * this.coordX + this.coordY * this.coordY); // Calculate radius
            if(distanceR < 0) {
                throw new IllegalArgumentException("");
            }
            float theta = (float) Math.atan2(this.coordY, this.coordX); // Calculate angle in radians
            
            System.out.println("\nPolar Coordinates: \nDistance 'r' = " + distanceR + "\nAngle = " + theta);
        } catch (IllegalArgumentException e) {
            System.out.println("Distance cannot be negative. Cannot compute correctly polar coordinates.");
            // The program cannot be terminated if the calculus for distance results in a negative distance
            // So, the method must be called again to ask for valid input
            getCartesianCoordinates();
            polar();
        }
        
    }
}

