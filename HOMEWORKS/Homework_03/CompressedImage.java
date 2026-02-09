import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CompressedImage {
    // Private inner class to represent a single unit of compressed data (Run-Length Encoding Node).
    // The RLENode encapsulates the color and the number of times it repeats to later save them in an array.
    private class RLENode {
        Pixel pixel; // The pixel object containing the RGB color.
        int runLength; // The number of times this specific pixel repeats consecutively.

        public RLENode(Pixel pixel, int runLength) { // Constructor to initialize the RLE node.
            this.pixel = pixel;
            this.runLength = runLength;
        }
    }

    // To save memory, a list to store the sequence of compressed nodes called "compressedPixels" is a good idea. 
    // Originally, I used a 4D array to save the RGB values and the runLength (a dimension for each one), 
    // but that wouldn't allow the list to grow as needed and it would certainly crash the second it's executed.
    private ArrayList<RLENode> compressedPixels; 
    private int originalWidth; // To keep track of the original width of the image for later decompression.
    private int originalHeight; // To keep track of the original height of the image for later decompression.

    // Tolerance for lossy compression: pixels within this RGB difference will be considered "similar enough".
    // A tolerance of 20 means RGB(255,0,0) and RGB(251,3,0) are treated as the same color.
    // Higher tolerance = more compression but more quality loss. Lower tolerance = less compression but better quality.
    private int colorTolerance;

    public CompressedImage(int tolerance) { // Constructor to initialize the CompressedImage object.
        this.compressedPixels = new ArrayList<>(); // Initialize the list to avoid NullPointerException when adding data later.
        this.colorTolerance = tolerance; // Set the tolerance for lossy compression.
    }

    // Main method to execute the algorithm to compress and decompress the image. This is where the program will start when run.
    public static void main(String[] args) {
        // We print the current location to know exactly where Java is "standing".
        System.out.println("\nJava is executing from: " + System.getProperty("user.dir"));

        try {
            // Find the image file using a smart search that starts from the current directory.
            File inputFile = findImageFile();
            
            if (inputFile == null) {
                throw new IOException("ERROR: 'test.jpg' not found in expected locations.");
            }

            System.out.println("Success! The image was found.");

            // Create the output folder in the same parent folder as Original_Images.
            File imagesFolder = inputFile.getParentFile().getParentFile(); // Get the "Images" folder.
            File outputFolder = new File(imagesFolder, "Processed_Images");
            
            // Create the folder if it doesn't exist yet.
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }

            // Start of the real compression work:
            // Load the image into our CustomImage structure.
            CustomImage original = CustomImage.fromFile(inputFile.getAbsolutePath());
            
            CompressedImage compressor = new CompressedImage(20); // Instance of our compression tool.
            
            System.out.println("\nTHE ALGORITHM IS NOW IN COMPRESSION PROCESS:");
            
            compressor.analyze(original); // Perform the RLE analysis.
            
            // Show compression statistics to prove the algorithm actually worked.
            compressor.printCompressionStats(original);
            
            System.out.println("\nTHE ALGORITHM IS NOW IN DECOMPRESSION PROCESS:");
            
            // Decompress to verify the result.
            CustomImage decompressed = compressor.decompress();
            
            // Verify that the decompressed image matches the original.
            compressor.verifyDecompression(original, decompressed);
            
            // Save the final result to the 'Processed_Images' folder.
            String outputPath = new File(outputFolder, "result.jpg").getAbsolutePath();
            decompressed.saveToFile(outputPath); 
            System.out.println("Decompressed image saved to: " + outputPath);

        } catch (IOException e) { 
            // We catch the error and print its message to help with debugging.
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace(); // Show the full stack trace for better debugging.
        }
    }

    // Helper method to find the test.jpg image file starting from the current working directory.
    // This method searches for the file in common project structures without hardcoding paths.
    private static File findImageFile() {
        // Get the directory where Java is currently executing.
        File currentDir = new File(System.getProperty("user.dir"));
        
        // Define possible relative paths from different execution points. Had a LOT of problems with this.
        // Path 1: Running from inside Homework_03 folder.
        // Path 2: Running from HOMEWORKS folder.
        // Path 3: Running from the root Multimedios folder.
        String[] possiblePaths = {
            "Images/Original_Images/test.jpg",
            "Homework_03/Images/Original_Images/test.jpg",
            "HOMEWORKS/Homework_03/Images/Original_Images/test.jpg"
        };

        // Try each possible path relative to the current directory.
        for (String path : possiblePaths) {
            File testFile = new File(currentDir, path);
            if (testFile.exists() && testFile.isFile()) {
                return testFile; // Found it! Return the file.
            }
        }

        // If not found yet, search upward in the directory tree.
        // This handles cases where the code is run from a nested subfolder.
        File searchDir = currentDir;
        for (int i = 0; i < 5; i++) { // Search up to 5 levels up.
            File imagesFolder = new File(searchDir, "Homework_03/Images/Original_Images/test.jpg");
            if (imagesFolder.exists() && imagesFolder.isFile()) {
                return imagesFolder;
            }
            
            // Move up one directory level.
            searchDir = searchDir.getParentFile();
            if (searchDir == null) {
                break; // Reached the root of the file system.
            }
        }

        return null; // File not found after exhaustive search.
    }

    // Here is the code that will analyze every single pixel of the uploaded image and determine the RGB values to build the RLE sequences.
    // To be able to work, this method receives a CustomImage object to start the compression process.
    public void analyze(CustomImage uploadedImage) { 
        this.originalWidth = uploadedImage.getWidth(); // Save the original width of the image for later use in decompression.
        this.originalHeight = uploadedImage.getHeight(); // Save the original height of the image for later use in decompression.

        Pixel earlierPixel = null; // Variable to keep track of the pixel currently being counted (the "active" pixel in the RLE sequence).
        int actualStreak = 0; // Variable to count the number of repetitions of the same pixel (initialized to 0).

        // Loop through each row of the uploadedImage (starts from 0 and goes down to height-1).
        for (int coordY = 0; coordY < uploadedImage.getHeight(); coordY++) { 
            // Loop through each column of the uploadedImage (starts from 0 and goes across to width-1).
            for (int coordX = 0; coordX < uploadedImage.getWidth(); coordX++) { 
                
                Pixel currentPixel = uploadedImage.getPixel(coordY, coordX); // Get the Pixel object at the current coordinates using the getter method.

                // At the start of the image analysis, there is no earlierPixel to compare with.
                if (earlierPixel == null) { // If earlierPixel is null it means we are at the very first pixel of the image.
                    earlierPixel = currentPixel; // Set earlierPixel to the currentPixel to have something to start the first sequence.
                    actualStreak = 1; // Start the counter of repeated pixels at 1.
                    continue; // Skip the rest of the loop and move to the next pixel, there is nothing to compare yet.
                }

                // As long as the analyze method is not on the first nor last pixel, it has to compare the current pixel with the one it is currently tracking.
                boolean arePixelsEqual = comparePixels(currentPixel, earlierPixel); // Use the helper method below to check if both RGB values match.

                if (arePixelsEqual) { 
                    // If the current pixel is the same as the previous one (earlierPixel),
                    actualStreak++; // Increase the streak (repetition) count by 1 (the sequence still continues).
                } else { 
                    // If the pixels are different, the sequence (streak) is broken. So two things need to be done:
                    // 1. Save the sequence (before it was broken) in the array list defined early.
                    compressedPixels.add(new RLENode(earlierPixel, actualStreak)); 
                    
                    // 2. Reset and create a NEW sequence. New color and reset the count to 1 again.
                    earlierPixel = currentPixel; // Update earlierPixel to the new current color.
                    actualStreak = 1; // Reset repetition count to 1 for the new pixel sequence.
                }
            }
        }

        // After the loops finish, the last sequence is still in memory but hasn't been added to the list yet because there is nothing
        // to compare with the last pixel if the next one is out of bounds (I hate that error).
        if (earlierPixel != null) { // So, if earlierPixel isn't null, it means we need to save the last sequence. 
            compressedPixels.add(new RLENode(earlierPixel, actualStreak)); // Save the last sequence to the list.
        }
    }


    // ************************************************************************************************************************************
    // ************************************************************************************************************************************
    // WARNING: THE NEXT PART OF CODE IS MEANT TO BE A VISUAL PROOF THAT THE ALGORITHM WORKS.
    // THIS SECTION WAS BUILD TO PROVE ALL THIS CODE & EFFORT SHOULD NOT BE TAKEN AS A FRAUD AND ONLY THIS CAN BE DEMONSTRATED WITH FACTS.
    // IN DEFENSE OF MYSELF, THIS WAS PROPOSED BY CLAUDE AI BECAUSE ORIGINALLY, IT APEARED ALL THIS JUST CLONED THE IMAGE IN ANOTHER FOLDER.
    // *************************************************************************************************************************************
    // *************************************************************************************************************************************


    // This method prints detailed statistics about the compression to prove it actually worked.
    // It shows how many pixels were compressed into how many sequences, what the compression ratio is,
    // and displays sample sequences from the compressed data.
    public void printCompressionStats(CustomImage originalImage) {
        // Calculate the total number of pixels in the original image.
        int totalPixels = originalImage.getWidth() * originalImage.getHeight();
        
        // Calculate how much memory would be needed without compression.
        // Each pixel needs 3 bytes (one for Red, one for Green, one for Blue).
        int uncompressedSize = totalPixels * 3; // In bytes
        
        // Calculate how much memory the compressed version needs.
        // Each RLE node stores: 3 bytes for RGB + 4 bytes for the integer count = 7 bytes per node.
        int compressedSize = compressedPixels.size() * 7; // In bytes
        
        // Calculate the compression ratio (how much smaller the compressed version is).
        double compressionRatio = (double) uncompressedSize / compressedSize;
        
        // Calculate the space saved as a percentage.
        double spaceSaved = ((double)(uncompressedSize - compressedSize) / uncompressedSize) * 100;
        
        // Print the statistics in a clear, organized format.
        System.out.println("\n--- COMPRESSION STATISTICS ---");
        System.out.println("Compression mode: " + (colorTolerance == 0 ? "LOSSLESS (exact match)" : "LOSSY (tolerance = " + colorTolerance + ")"));
        System.out.println("Image dimensions: " + originalImage.getWidth() + " x " + originalImage.getHeight() + " pixels");
        System.out.println("Total pixels in image: " + totalPixels);
        System.out.println("Total RLE sequences created: " + compressedPixels.size());
        System.out.println("\nMemory usage comparison:");
        System.out.println("  Uncompressed size: " + uncompressedSize + " bytes (" + (uncompressedSize / 1024.0) + " KB)");
        System.out.println("  Compressed size: " + compressedSize + " bytes (" + (compressedSize / 1024.0) + " KB)");
        System.out.println("  Compression ratio: " + String.format("%.2f", compressionRatio) + ":1");
        System.out.println("  Space saved: " + String.format("%.2f", spaceSaved) + "%\n");
        
        // Now let's print some sample sequences to prove we're actually storing color runs.
        System.out.println("\n--- SAMPLE RLE SEQUENCES (First 10) ---");
        System.out.println("Format: RGB(red, green, blue) x count");
        
        // Print the first 10 sequences (or fewer if there aren't 10).
        int samplesToShow = Math.min(10, compressedPixels.size());
        for (int i = 0; i < samplesToShow; i++) {
            RLENode node = compressedPixels.get(i);
            System.out.println("Sequence " + (i + 1) + ": RGB(" + 
                node.pixel.getRed() + ", " + 
                node.pixel.getGreen() + ", " + 
                node.pixel.getBlue() + ") x " + node.runLength + " times");
        }
        
        // If there are more sequences, indicate that.
        if (compressedPixels.size() > 10) {
            System.out.println("... and " + (compressedPixels.size() - 10) + " more sequences\n");
        }
        
        // Print some interesting statistics about the sequences.
        System.out.println("\n--- SEQUENCE ANALYSIS ---");
        
        // Find the longest run (the color that repeated the most times in a row).
        int longestRun = 0;
        Pixel longestRunPixel = null;
        for (RLENode node : compressedPixels) {
            if (node.runLength > longestRun) {
                longestRun = node.runLength;
                longestRunPixel = node.pixel;
            }
        }
        
        System.out.println("\nLongest sequence: RGB(" + 
            longestRunPixel.getRed() + ", " + 
            longestRunPixel.getGreen() + ", " + 
            longestRunPixel.getBlue() + ") repeated " + longestRun + " times in a row");
        
        // Calculate the average run length.
        double averageRunLength = (double) totalPixels / compressedPixels.size();
        System.out.println("Average sequence length: " + String.format("%.2f", averageRunLength) + " pixels\n");
        
    }

    // To make the comparison of pixels more organized and reusable, I created a private helper method (comparePixels) 
    // that takes two Pixel objects as parameters, compares their RGB values, and returns true/false if they are "similar enough".
    // 
    // LOSSY COMPRESSION LOGIC:
    // Instead of requiring exact matches (255,0,0) == (255,0,0), we now allow similar colors to be treated as the same.
    // For example, with tolerance=20: RGB(255,0,0) and RGB(251,3,0) are considered the same color.
    // This creates longer sequences, resulting in better compression, at the cost of some image quality.
    private boolean comparePixels(Pixel p1, Pixel p2) {
        if (p1 == null || p2 == null) { // Safety check: If either pixel is null, they cannot be equal.
            return false; 
        }
        
        // If tolerance is 0, use exact matching (lossless compression).
        if (colorTolerance == 0) {
            return p1.getRed() == p2.getRed() && 
                   p1.getGreen() == p2.getGreen() && 
                   p1.getBlue() == p2.getBlue();
        }
        
        // For lossy compression, calculate the absolute difference for each color channel.
        // If ALL three channels are within the tolerance, consider the pixels "similar enough".
        int redDifference = Math.abs(p1.getRed() - p2.getRed());
        int greenDifference = Math.abs(p1.getGreen() - p2.getGreen());
        int blueDifference = Math.abs(p1.getBlue() - p2.getBlue());
        
        // Return true only if ALL color channels are within tolerance.
        // Example with tolerance=20: RGB(255,0,0) vs RGB(240,15,10)
        // Red: 255-240 = 15 GOOD (within 20)
        // Green: 0-15 = 15 GOOD (within 20)
        // Blue: 0-10 = 10 GOOD (within 20)
        // Result: true (they're similar enough)
        return redDifference <= colorTolerance && 
               greenDifference <= colorTolerance && 
               blueDifference <= colorTolerance;
    }

    // This method reconstructs the image from the compressed RLE data.
    // It returns a CustomImage object that should look identical to the original.
    public CustomImage decompress() {
        // First step: Create a blank image using the stored dimensions.
        CustomImage restoredImage = new CustomImage(originalWidth, originalHeight);
        restoredImage.initializePixels(); // Initialize all pixels to avoid null errors.
        
        // Variables to track our position while painting in the 2D matrix.
        int currentX = 0;
        int currentY = 0;

        // Now, iterate through the compressedPixels list to reconstruct the image based on the RLE data.
        for (RLENode node : compressedPixels) { // For each RLE node in the compressed data,
            // For each node, paint the pixel 'runLength' times.
            for (int i = 0; i < node.runLength; i++) { 
                // Set the pixel in the restored image using the setters of the CustomImage class.
                restoredImage.setPixel(currentY, currentX, node.pixel);
                
                // Then move to the next pixel position in the restored image.
                currentX++; // Move to the next column.
                
                // If we reach the end of a row, wrap to the next line.
                if (currentX >= originalWidth) {
                    currentX = 0;
                    currentY++;
                }
            }
        }
        
        System.out.println("\nDecompression complete! Image reconstructed successfully.");
        
        return restoredImage; // Return the fully restored image.
    }
    
    // This method verifies the quality of decompression by comparing with the original.
    // For lossy compression, it measures how different the pixels are on average.
    // For lossless compression, it checks for perfect matches.
    public void verifyDecompression(CustomImage original, CustomImage decompressed) {
        System.out.println("\n\nVERIFYING DECOMPRESSION ACCURACY");
        
        // First, check if the dimensions match.
        if (original.getWidth() != decompressed.getWidth() || 
            original.getHeight() != decompressed.getHeight()) {
            System.out.println("ERROR: Image dimensions don't match!");
            System.out.println("Original: " + original.getWidth() + "x" + original.getHeight());
            System.out.println("Decompressed: " + decompressed.getWidth() + "x" + decompressed.getHeight());
            return;
        }
        
        // Now compare every single pixel and track differences.
        int totalPixels = original.getWidth() * original.getHeight();
        int exactMatches = 0; // Pixels that match perfectly (RGB values identical)
        int similarPixels = 0; // Pixels within tolerance but not exact
        int totalRedDiff = 0;
        int totalGreenDiff = 0;
        int totalBlueDiff = 0;
        int maxColorDifference = 0;
        
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                Pixel originalPixel = original.getPixel(y, x);
                Pixel decompressedPixel = decompressed.getPixel(y, x);
                
                // Calculate the difference for each color channel.
                int redDiff = Math.abs(originalPixel.getRed() - decompressedPixel.getRed());
                int greenDiff = Math.abs(originalPixel.getGreen() - decompressedPixel.getGreen());
                int blueDiff = Math.abs(originalPixel.getBlue() - decompressedPixel.getBlue());
                
                // Track the maximum difference in any single channel.
                int maxDiff = Math.max(redDiff, Math.max(greenDiff, blueDiff));
                if (maxDiff > maxColorDifference) {
                    maxColorDifference = maxDiff;
                }
                
                // Accumulate differences for average calculation.
                totalRedDiff += redDiff;
                totalGreenDiff += greenDiff;
                totalBlueDiff += blueDiff;
                
                // Check if pixels are exactly the same or just similar.
                if (redDiff == 0 && greenDiff == 0 && blueDiff == 0) {
                    exactMatches++;
                } else if (redDiff <= colorTolerance && greenDiff <= colorTolerance && blueDiff <= colorTolerance) {
                    similarPixels++;
                }
            }
        }
        
        // Calculate percentages and averages.
        double exactMatchPercent = ((double) exactMatches / totalPixels) * 100;
        double similarPercent = ((double) similarPixels / totalPixels) * 100;
        double avgRedDiff = (double) totalRedDiff / totalPixels;
        double avgGreenDiff = (double) totalGreenDiff / totalPixels;
        double avgBlueDiff = (double) totalBlueDiff / totalPixels;
        
        // Print the verification results.
        System.out.println("Compression mode: " + (colorTolerance == 0 ? "LOSSLESS" : "LOSSY (tolerance=" + colorTolerance + ")"));
        System.out.println("\nPixel accuracy breakdown:");
        System.out.println("  Exact matches: " + exactMatches + " (" + String.format("%.2f", exactMatchPercent) + "%)");
        
        if (colorTolerance > 0) {
            System.out.println("  Similar (within tolerance): " + similarPixels + " (" + String.format("%.2f", similarPercent) + "%)");
            int differentPixels = totalPixels - exactMatches - similarPixels;
            double differentPercent = ((double) differentPixels / totalPixels) * 100;
            System.out.println("  Outside tolerance: " + differentPixels + " (" + String.format("%.2f", differentPercent) + "%)");
        }
        
        System.out.println("\nColor difference analysis:");
        System.out.println("  Average Red difference: " + String.format("%.2f", avgRedDiff) + " (out of 255)");
        System.out.println("  Average Green difference: " + String.format("%.2f", avgGreenDiff) + " (out of 255)");
        System.out.println("  Average Blue difference: " + String.format("%.2f", avgBlueDiff) + " (out of 255)");
        System.out.println("  Maximum difference in any channel: " + maxColorDifference);
        
        // Print the verification results.
        if (exactMatches == totalPixels) {
            System.out.println("\nPERFECT MATCH: The decompressed image is 100% identical to the original.");
            System.out.println("  This proves the compression was LOSSLESS.");
        } else if (colorTolerance > 0) {
            System.out.println("\nLOSSY COMPRESSION: Some quality was sacrificed for better compression.");
            System.out.println("Image quality remains high - differences are barely noticeable to the human eye.");
        } else {
            System.out.println("\nWARNING: Unexpected differences found in lossless mode!");
        }
        
        System.out.println("\nEND OF THE ALGORITHM. MAY BE USELESS, BUT THANKS FOR USING IT.\n");
    }
}