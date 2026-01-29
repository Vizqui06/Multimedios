import javax.swing.*; // All Swing components (the * means all componentes I could need will be imported)
import java.awt.*; // For colors and other awt components I might need to make the GUI
import java.awt.event.ActionEvent; // For action events (like button clicks)
import java.awt.event.ActionListener; // For listening the action events

public class GeometryCalculator extends JFrame { // The main class but incudes JFrame for GUI window
    // Attributes (components)
    public JPanel mainPanel; // Main panel
    private JTextField field1, field2; // The input fields
    private JLabel label1, label2, areaLabel, perimeterLabel; // Labels for inputs and results
    private JComboBox<String> shapeMenu; // Dropdown menu for shape selection
    private JButton calculateButton; // Button to trigger the calculations in menu

    public GeometryCalculator() { // The constructor
        // JFrame settings
        this.setSize(450, 500); // Window size
        this.setTitle("Geometry Calculator - Vizca"); // Window title
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // To close the window properly
        this.setLocationRelativeTo(null); // Center the window
        
        initComponents(); // Call method to add components
    }

    private void initComponents() {
        setupPanel(); // Initialize the panel
        setupMenu(); // Initialize the dropdown menu
        setupInputs(); // Initialize labels and text fields
        setupButton(); // Initialize the action button
    }

    private void setupPanel() { // Method to setup main panel
        mainPanel = new JPanel(); // Create new panel
        mainPanel.setBackground(new Color(45, 45, 45)); // Dark background color
        mainPanel.setLayout(null); // No layout manager for free positioning (now I can set exact positions and everything)
        this.getContentPane().add(mainPanel); // Add panel to the frame so can be seen
    }

    private void setupMenu() { // Method to setup dropdown menu
        String[] shapes = {"Square", "Rectangle", "Circle", "Regular Pentagon", "Pentagram", "Semicircle"}; // Valid shapes options for the menu
        shapeMenu = new JComboBox<>(shapes); // Create dropdown menu with shapes
        shapeMenu.setBounds(130, 30, 180, 30); // Position and size of the menu
        mainPanel.add(shapeMenu); // Add to main panel so it can be seen

        // Event for when the user picks a shape from the menu
        shapeMenu.addActionListener(new ActionListener() { // Action listener for shape selection
            @Override // Override the actionPerformed method because it's abstract and makes the class concrete
            public void actionPerformed(ActionEvent e) { // Method called when an action occurs so can update labels
                updateLabels(); // Changes labels based on user selection
            }
        });
    }

    private void setupInputs() { // Method to setup input labels and fields
        // First input label configuration
        label1 = new JLabel("Side:");  // First label (default to side)
        label1.setBounds(50, 100, 120, 25); // Position and size
        label1.setForeground(Color.CYAN); // Text color
        mainPanel.add(label1); // Add to main panel

        // First input field configuration
        field1 = new JTextField();  // First input field
        field1.setBounds(180, 100, 100, 25); // Position and size
        mainPanel.add(field1); // Add to main panel

        label2 = new JLabel(""); // Hidden label for the second input (by default empty because not all shapes may need it)
        label2.setBounds(50, 150, 120, 25); // Position and size
        label2.setForeground(Color.CYAN); // Text color
        mainPanel.add(label2); // Add to main panel

        // Second input field configuration
        field2 = new JTextField(); 
        field2.setBounds(180, 150, 100, 25); // Position and size
        field2.setVisible(false); // Hidden by default (because not all shapes need a second input)
        mainPanel.add(field2); // Add to main panel

        // Area Label configuration
        areaLabel = new JLabel("Area: "); 
        areaLabel.setBounds(50, 220, 350, 30); // Position and size
        areaLabel.setForeground(Color.WHITE); // Text color
        mainPanel.add(areaLabel); // Add to main panel

        // Perimeter Label configuration
        perimeterLabel = new JLabel("Perimeter: "); // Perimeter Label configuration
        perimeterLabel.setBounds(50, 260, 350, 30); // Position and size
        perimeterLabel.setForeground(Color.WHITE); // Text color
        mainPanel.add(perimeterLabel); // Add to main panel
    }

    private void updateLabels() { // Method to update input labels based on selected shape
        String selected = (String) shapeMenu.getSelectedItem(); // Get selected shape from dropdown menu (this returns an Object, so need to cast to String)
        
        // Reset secondary input visibility
        field2.setVisible(false); // Hide second input field by default
        label2.setText(""); // Clear second label text by default

        // Logic to show/hide fields based on the shape
        if (selected.equals("Square")) { // If square is selected
            label1.setText("Side:"); // First label is always side
        } 
        else if (selected.equals("Rectangle")) { // If rectangle is selected
            label1.setText("Base:"); // First label is base
            label2.setText("Height:"); // Second label is height
            field2.setVisible(true); // Show second input field
        } 
        else if (selected.equals("Circle") || selected.equals("Semicircle")) { // If circle or semicircle is selected
            label1.setText("Radius:"); // First label is always radius
        } 
        else if (selected.equals("Regular Pentagon")) { // If regular pentagon is selected
            label1.setText("Side:"); // First label is always side
            label2.setText("Apothem:"); // Second label is apothem
            field2.setVisible(true); // Show second input field
        } 
        else if (selected.equals("Pentagram")) { // If pentagram is selected
            label1.setText("Arm length:"); // First label is arm length
        }
    }

    private void setupButton() { // Method to setup calculation button so can show area and perimeter results
        calculateButton = new JButton("Calculate Area & Perimeter"); // Button text
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

    private void performCalculations() { // Method to calculate area and perimeter for selected shape
        try { // Try-catch block to handle invalid inputs (in case someone dress up a clown suit and inputs letters instead of numbers)
            String shape = (String) shapeMenu.getSelectedItem(); // Get selected shape
            double val1 = Double.parseDouble(field1.getText()); // First value (side, radius, etc.)
            double val2 = 0; // Second value for shapes that require it 
            
            // Only read field2 if it's visible to avoid errors

            if (field2.isVisible()) { // If second input is needed
                val2 = Double.parseDouble(field2.getText()); // Read second value if needed
            }

            double area = 0; // Area (for all shapes)
            double perimeter = 0; // Used for all shapes (in case need it)

            // Manual logic for each shape
            if (shape.equals("Square")) { // If selected shape is a square
                area = val1 * val1; // Area = side^2
                perimeter = 4 * val1; // Perimeter = 4 * side

            } else if (shape.equals("Rectangle")) { // If selected shape is a rectangle
                area = val1 * val2; // Area = base * height
                perimeter = (2 * val1) + (2 * val2); // Perimeter = 2*base + 2*height

            } else if (shape.equals("Circle")) { // If selected shape is a circle
                area = 3.1416 * (val1 * val1); // Area = PI * r^2
                perimeter = 2 * 3.1416 * val1; // Perimeter = 2 * PI * r

            } else if (shape.equals("Semicircle")) { // If selected shape is a semicircle
                area = (3.1416 * (val1 * val1)) / 2; // Area is half that of a full circle
                perimeter = (3.1416 * val1) + (2 * val1); // Perimeter is half the circumference plus the diameter

            } else if (shape.equals("Regular Pentagon")) { //If selected shape is a regular pentagon
                perimeter = 5 * val1; // Perimeter is 5 times the side length
                area = (perimeter * val2) / 2; // Area = (Perimeter * Apothem) / 2

            } else if (shape.equals("Pentagram")) { // If selected shape is a pentagram
                // Approximate formula for a regular star
                area = 1.902 * (val1 * val1); // To keep it simple, searched for an approximate area formula in Google
                perimeter = 10 * val1; // 5 arms, each counted twice makes 10 sides
            }

            // Update result labels with calculated values
            areaLabel.setText("Area: " + area); // Set area label text
            perimeterLabel.setText("Perimeter: " + perimeter); // Set perimeter label text

        } catch (Exception ex) {
            areaLabel.setText("Error: Enter valid numbers."); // Throws error message if input is invalid (ex: non-numeric)
            perimeterLabel.setText("Error: Enter valid numbers."); // Throws error message if input is invalid (ex: non-numeric)
        }
    }
}