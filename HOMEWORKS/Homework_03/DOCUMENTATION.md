# Image Compression with Run-Length Encoding (RLE)
## Project Documentation

---

## What This Project Does

This project takes a JPG image and compresses it using a technique called Run-Length Encoding (RLE). After compression, it decompresses the image back and saves the result. The purpose is to understand how basic image compression works without relying on complex libraries or methods that only experienced programmers can understand.

The code is designed to be portable, meaning anyone can download it from GitHub and run it without changing paths or configurations. It will automatically find the image file no matter where you run the program from within the project structure.

---

## The Problem We're Solving

Images are made up of thousands or millions of pixels. Each pixel has a color defined by three values: Red, Green, and Blue (RGB). Storing every single pixel individually takes up a lot of space and memory.

Run-Length Encoding helps by looking for patterns. If the same color appears multiple times in a row, instead of storing that color ten times, we store it once and remember "this color repeats 10 times." This is particularly effective for images with large areas of the same color, like logos, diagrams, or simple graphics.

### Real-World Example

Imagine you have a sentence: "AAAAABBBCCCCC"

Instead of storing all 13 characters, RLE stores it as:
- A appears 5 times
- B appears 3 times  
- C appears 5 times

The same concept applies to pixels in an image.

## Project Structure

The project is organized into three main classes, each with a specific responsibility:

### Pixel.java
This is the simplest class. It represents a single pixel in an image and stores three values: red, green, and blue (each ranging from 0 to 255). Think of it as a container that holds a color.

**What it does:**
- Stores RGB color values
- Provides methods to get and set these values
- Starts with default values of (0, 0, 0) which represents black

### CustomImage.java
This class represents an entire image. It manages a 2D array of Pixel objects (think of it as a grid where each cell is a pixel).

**What it does:**
- Loads images from files on your computer
- Stores all the pixels in an organized way
- Saves images back to files
- Provides methods to get or set individual pixels at specific coordinates

**Why we need this:**
Java's built-in image classes (like BufferedImage) work, but they're designed for display and manipulation, not for understanding how images work at the pixel level. CustomImage gives us full control and makes the learning process clearer.

### CompressedImage.java
This is where the actual compression happens. It contains the RLE algorithm and the decompression logic.

**What it does:**
- Analyzes an image and creates a compressed representation
- Stores sequences of repeated pixels (instead of storing each pixel individually)
- Reconstructs the original image from the compressed data
- Handles file finding so the code works from anywhere in the project

---

## How the Algorithm Works

### The Compression Process (analyze method)

The algorithm reads the image pixel by pixel, from left to right, top to bottom (like reading a book). Here's what happens:

1. **Start with the first pixel**: Remember its color and set the counter to 1.

2. **Look at the next pixel**: 
   - If it's the same color, increase the counter by 1
   - If it's a different color, save the current sequence (color + count), then start a new sequence with the new color

3. **Repeat** for every pixel in the image.

4. **Save the last sequence** when you reach the end of the image.

### Example Walkthrough

Let's say we have a tiny 3x3 image with these colors (R=Red, B=Blue, G=Green):

```
R R R
R B B
B B G
```

Reading left to right, top to bottom: R, R, R, R, B, B, B, B, G

The algorithm processes this as:
- Found Red, count = 1
- Next pixel is also Red, count = 2
- Next pixel is also Red, count = 3
- Next pixel is also Red, count = 4
- Next pixel is Blue (different!), save "Red x4", start new sequence with Blue, count = 1
- Next pixel is also Blue, count = 2
- Next pixel is also Blue, count = 3
- Next pixel is also Blue, count = 4
- Next pixel is Green (different!), save "Blue x4", start new sequence with Green, count = 1
- Reached the end, save "Green x1"

Final compressed data: 
- Red appears 4 times
- Blue appears 4 times  
- Green appears 1 time

Instead of storing 9 individual pixels, we store 3 sequences. The savings become much more significant with larger images.

### The Decompression Process (decompress method)

Decompression is simpler than compression. We just reverse the process:

1. **Create a blank image** with the same dimensions as the original.

2. **Read each compressed sequence** and paint the pixels:
   - If the sequence says "Red x4", paint 4 red pixels in order
   - If the sequence says "Blue x4", paint 4 blue pixels next
   - Continue until all sequences are painted

3. **Return the reconstructed image**, which should look identical to the original.

### Why Use a Custom Node Class?

Inside CompressedImage, there's a private inner class called RLENode. This is our container for storing each compressed sequence.

Each RLENode holds:
- A Pixel object (the color)
- An integer (how many times that color repeats)

We use an ArrayList of these nodes because:
- The list can grow as needed (we don't know how many sequences we'll have until we analyze the image)
- It's memory-efficient
- It's easy to iterate through when decompressing

---

## Code Design Decisions

### Why Not Use Powerful Built-In Functions?

The goal is learning, not just getting results. Using advanced compression libraries would skip all the educational value. By implementing RLE ourselves, we understand exactly what's happening at each step.

### Why Three Separate Classes?

This follows Object-Oriented Programming principles:
- **Pixel**: Handles individual color data
- **CustomImage**: Handles image-level operations  
- **CompressedImage**: Handles the compression algorithm

Each class has one job and does it well. This makes the code easier to understand, test, and modify.

### Why So Many Comments?

Comments serve two purposes:
1. They help you understand what you wrote months later
2. They help others understand your logic without having to reverse-engineer everything

Every line of code has a reason for existing, and the comments explain that reason.

---

## How the File Finding Works

One of the biggest challenges was making the code work from anywhere without hardcoded paths. Here's how it's solved:

### The Problem
If you write `"/Users/YourName/Desktop/Multimedios/..."` in your code, it only works on your computer. Anyone else trying to run it will get an error.

### The Solution
The `findImageFile()` method uses a smart search strategy:

1. **Get the current directory** where Java is running from
2. **Try multiple relative paths** from that directory:
   - `Images/Original_Images/test.jpg` (if running from inside Homework_03)
   - `Homework_03/Images/Original_Images/test.jpg` (if running from HOMEWORKS)
   - `HOMEWORKS/Homework_03/Images/Original_Images/test.jpg` (if running from Multimedios)

3. **If not found, search upward** in the directory tree, checking up to 5 levels up

4. **Return the file** when found, or return null if not found

This means the code works whether you run it from:
- The Homework_03 folder directly
- The HOMEWORKS folder
- The root Multimedios folder
- Even from inside a subfolder

### Creating the Output Folder

Once the input file is found, the code automatically creates a "Processed_Images" folder next to the "Original_Images" folder. If the folder already exists, it does nothing. If it doesn't exist, it creates it. This keeps things organized without manual intervention.

---

## Common Pitfalls and How They Were Fixed

### Bug 1: Hardcoded Save Path
**The Problem:** The `saveToFile()` method received an `outputPath` parameter but ignored it completely. It tried to save to a hardcoded absolute path that only existed on my computer.

**The Fix:** Actually use the `outputPath` parameter. Create a File object from the path, check if parent directories exist, create them if needed, then save the image there.

**Why This Matters:** This was the main reason the program wasn't working. The decompressed image was never being saved, or was being saved to the wrong location.

### Bug 2: Uninitialized Pixels in Decompression
**The Problem:** When creating a blank image for decompression, the code created the 2D array but never initialized the Pixel objects inside it. When trying to set RGB values, it was calling methods on null objects.

**The Fix:** Add `restoredImage.initializePixels();` right after creating the blank image. This creates actual Pixel objects (initialized to black) in every position of the array.

**Why This Matters:** Without this, the program would crash with a NullPointerException during decompression.

### Bug 3: Missing Bounds Checks
**The Problem:** The `setPixel()` method checked if coordinates were less than width and height, but didn't check if they were negative.

**The Fix:** Add checks for `y >= 0` and `x >= 0` in addition to the upper bound checks.

**Why This Matters:** Negative indices would cause an ArrayIndexOutOfBoundsException. While this shouldn't happen with our algorithm, defensive programming prevents crashes if the code is modified later.

### Bug 4: Unused Code
**The Problem:** There was a method called `findHomework03Directory()` that was never called. It was created during development but became redundant when the path-finding logic was improved.

**The Fix:** Remove the entire method. Dead code adds confusion and makes maintenance harder.

---

## How to Use This Code

### Required Folder Structure

```
Homework_03/
├── Images/
│   ├── Original_Images/
│   │   └── test.jpg (your input image)
│   └── Processed_Images/ (created automatically)
├── CompressedImage.java
├── CustomImage.java
└── Pixel.java
```

### Running the Program

1. Make sure all three .java files are in the Homework_03 folder
2. Make sure test.jpg is in the Original_Images folder
3. Open a terminal in any of these locations:
   - Inside Homework_03
   - Inside HOMEWORKS
   - Inside Multimedios (the root)
4. Compile the code:
   ```
   javac CompressedImage.java CustomImage.java Pixel.java
   ```
5. Run the program:
   ```
   java CompressedImage
   ```

The program will:
- Print where Java is executing from
- Search for the image file
- Print where it found the image
- Compress the image (this happens in memory, you won't see it)
- Decompress the image
- Save the result to Processed_Images/result.jpg
- Print where the result was saved

---

## Understanding the Output

When you run the program, you should see something like:

```
Java is executing from: /Users/YourName/Desktop/Multimedios/HOMEWORKS/Homework_03
Success! Image found at: /Users/YourName/Desktop/Multimedios/HOMEWORKS/Homework_03/Images/Original_Images/test.jpg
Decompressed image saved to: /Users/YourName/Desktop/Multimedios/HOMEWORKS/Homework_03/Images/Processed_Images/result.jpg
```

The result.jpg should look identical to test.jpg. If it does, the algorithm worked correctly.

---

## Limitations and Future Improvements

### Current Limitations

1. **Only works with JPG files**: The code is hardcoded to look for test.jpg. It doesn't handle other formats or filenames.

2. **Not actually more efficient for photos**: Real photographs have lots of color variation, so RLE doesn't compress them well. RLE works best on images with large areas of solid color (logos, diagrams, pixel art).

3. **Loss of JPG compression**: When we load a JPG, decompress it to raw pixels, then save it as JPG again, we lose the benefits of JPG's compression. The file might actually be larger than the original.

4. **No memory usage tracking**: The code doesn't measure or report how much memory was saved by compression.

### Possible Improvements

1. **Accept any image filename**: Modify the code to accept command-line arguments for the input file path.

2. **Add compression statistics**: Calculate and display the compression ratio (original size vs. compressed size).

3. **Support other image formats**: Add support for PNG, BMP, etc.

4. **Implement more sophisticated compression**: RLE is the simplest compression algorithm. More advanced techniques like Huffman coding or Lempel-Ziv could achieve better results.

5. **Add error recovery**: Currently, if anything goes wrong (corrupt image, permission issues), the program just prints an error and stops. Better error handling could provide more helpful feedback.

---

## Key Takeaways

### What Makes This Code Good

- **Clear separation of concerns**: Each class has one job
- **Comprehensive comments**: Every line is explained
- **Defensive programming**: Checks for null values and invalid inputs
- **Portable**: Works on any computer without modification
- **Educational**: The logic is transparent and followable

### What You Should Learn From This

1. **Object-Oriented Design**: How to break a problem into logical classes
2. **Algorithm Implementation**: How to translate a concept (RLE) into working code  
3. **File Handling**: How to work with files and paths in a portable way
4. **Debugging**: The importance of initialization, bounds checking, and actually using your parameters
5. **Documentation**: How to write comments that explain not just what but why

### Moving Forward

This project is a foundation. You can extend it by:
- Implementing different compression algorithms
- Adding a GUI to select images
- Comparing compression ratios of different algorithms
- Working with different image formats
- Processing multiple images at once

The skills you learned here (OOP, algorithm implementation, file handling) apply to much larger projects. Keep the good practices (documentation, defensive programming, clean architecture) and you'll write better code for everything you build next.

---

## Final Notes

Writing good code isn't just about making it work. It's about making it understandable, maintainable, and reusable. This project demonstrates that you don't need fancy libraries or advanced techniques to accomplish meaningful tasks. Sometimes, the simple approach is the best approach, especially when the goal is learning.

If you come back to this code in six months, you should be able to understand exactly what you were thinking when you wrote each line. That's the mark of well-documented code.

Keep iterating, keep learning, and keep making your code better with each project.