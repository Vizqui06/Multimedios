package AspectRatio;

import java.awt.*; // All Swing components (the * means all componentes I could need will be imported)
import java.awt.event.ActionEvent; // For colors and other awt components I might need to make the GUI
import java.awt.event.ActionListener; // For action events (like button clicks)
import javax.swing.*; // For listening the action events

public class ARCalc extends JFrame{

    public JPanel mainPanel; // Main panel
    private JTextField widthField, heightField; // The input fields
    private JLabel widthLabel, heightLabe, resultLabel; // Labels for inputs and results
    private JButton calculateButton; // Button to trigger the calculations in menu

    public ARCalc(){
        this.setTitle("Aspect Ratio Calculator"); // Title of the window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        this.setSize(500,500); // Size of the window
        this.setResizable(false); // Disable resizing
        this.setLocationRelativeTo(null); // Center the window on the screen
        this.setVisible(true); // Make the window visible

        initComponents(); // Call method to add components
    }

    private void initComponents() {
        setupPanel(); // Initialize the panel
        setupInputs(); // Initialize labels and text fields
        setupButton(); // Initialize the action button
    }

    private void setupPanel() { // Method to setup main panel
        mainPanel = new JPanel(); // Create new panel
        mainPanel.setBackground(new Color(45, 45, 45)); // Dark background color
        mainPanel.setLayout(null); // No layout manager for free positioning (now I can set exact positions and everything)
        this.getContentPane().add(mainPanel); // Add panel to the frame so can be seen
    }

    private void setupInputs() { // Method to setup input labels and fields
        // First input label configuration
        widthLabel = new JLabel("Width: ");  // First label (default to side)
        widthLabel.setBounds(50, 100, 120, 25); // Position and size
        widthLabel.setForeground(Color.CYAN); // Text color
        mainPanel.add(widthLabel); // Add to main panel

        // First input field configuration
        widthField = new JTextField();  // First input field
        widthField.setBounds(180, 100, 100, 25); // Position and size
        mainPanel.add(widthField); // Add to main panel

        heightLabe = new JLabel("Height: "); // Hidden label for the second input (by default empty because not all shapes may need it)
        heightLabe.setBounds(50, 150, 120, 25); // Position and size
        heightLabe.setForeground(Color.CYAN); // Text color
        mainPanel.add(heightLabe); // Add to main panel

        // Second input field configuration
        heightField = new JTextField(); 
        heightField.setBounds(180, 150, 100, 25); // Position and size
        mainPanel.add(heightField); // Add to main panel

        // Area Label configuration
        resultLabel = new JLabel("Aspect Ratio:  "); // Label to show the aspect ratio
        resultLabel.setBounds(50, 220, 350, 30); // Position and size
        resultLabel.setForeground(Color.WHITE); // Text color
        mainPanel.add(resultLabel); // Add to main panel
    }
    
    private void setupButton() { // Method to setup calculation button so can show area and perimeter results
        calculateButton = new JButton("Calculate The Aspect Ratio"); // Button text
        calculateButton.setBounds(100, 320, 250, 40); // Position and size
        calculateButton.setBackground(Color.ORANGE); // Button background color
        mainPanel.add(calculateButton); // Add to main panel

        // Action event for the calculation button
        calculateButton.addActionListener(new ActionListener() { // Action listener for button click
            @Override // Override the actionPerformed method because it's abstract and makes the class concrete
            public void actionPerformed(ActionEvent e) { // Method called when button is clicked
                performCalculations(); // Call method to perform calculations
            }
        });
    }
    private void performCalculations() {
        // For the AR calculator, must calculate the aspect ratio based on width and height inputs
        // This using the gcd (greatest common divisor) to get the simplest form of the ratio
        try {
            int width = Integer.parseInt(widthField.getText()); // Get width from input field
            int height = Integer.parseInt(heightField.getText()); // Get height from input field

            int gcd = gcd(width, height); // Calculate GCD of width and height

            int aspectWidth = width / gcd; // Simplified width
            int aspectHeight = height / gcd; // Simplified height

            resultLabel.setText("Aspect Ratio:  " + aspectWidth + ":" + aspectHeight); // Display result
        } catch (NumberFormatException ex) {
            resultLabel.setText("Invalid input. Only integers are valid."); // Error message for invalid input
        }
    }

    private int gcd(int width, int height) {
        if (height == 0 && width == 0) {
            return 0;
        }
        if (height == 0) {
            return width;
        }
        if (width == 0) {
            return height;
        }
        return gcd(height % width, width % height);
    }
}
