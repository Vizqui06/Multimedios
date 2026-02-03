// Create a program that converts from Polar to Cartesian Coordinates 
// based on the info supplied by the user.

// In CTP, the calc will convert from Cartesian (x,y) to Polar (r,angle)
// So, having these equations in mind:
// x = r * cos(angle)
// y = r * sin(angle)
//To find r and angle:
// r = sqrt(x^2 + y^2)
// angle = atan2(y, x)

import java.util.Scanner;

public class PTC{
    public static void main(String[] args) {
        PTC calculator = new PTC(0, 0); // Create an instance of PTC with initial coordinates (0,0)
        calculator.getPolarCoordinates(); // Get Polar coordinates from user
        calculator.cartesian(); // Convert to Cartesian coordinates and display the result
    }

    private float distanceR; // Distance in Polar coordinates
    private float angle; // Angle in Polar coordinates

    public PTC(float distanceR, float angle) { // Constructor to initialize Polar coordinates
        this.distanceR = distanceR;
        this.angle = angle;
    }

    public float getDistanceR() {
        return distanceR;
    }

    public void setDistanceR(float distanceR) {
        this.distanceR = distanceR;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    // Get polar coordinates from user
    public void getPolarCoordinates() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the distance 'r': ");

            this.distanceR = scanner.nextFloat();
            if(distanceR < 0) {
                throw new IllegalArgumentException("");
            }

            System.out.print("Enter the angle in radians: ");
            this.angle = scanner.nextFloat();
            // scanner.close(); // Apparentley, it is not recommended to close the scanner to avoid closing System.in  
        } 
        catch (IllegalArgumentException e) {
            System.out.println("Distance 'r' cannot be negative. Please enter a valid positive number");
            // The program cannot be terminated if the user inputs a negative distance
            // So, the method must be called again to ask for valid input
            getPolarCoordinates();
        }
    }

    private void cartesian() {
        float coordX = this.distanceR * (float) Math.cos(this.angle); // Calculate X coordinate
        float coordY = this.distanceR * (float) Math.sin(this.angle); // Calculate Y coordinate
        System.out.println("\nCartesian Coordinates: \nCoord X = " + coordX + " and Coord Y = " + coordY);
    }
}

