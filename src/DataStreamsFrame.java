import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Claire Battelle
 * November 2, 2026
 * Main window original file on the left and filtered lines on the right
 */
public class DataStreamsFrame extends JFrame
{
    private final JTextArea originalArea = new JTextArea();
    private final JTextArea filteredArea = new JTextArea();
    private final JTextField searchField = new JTextField(20);
    private final JButton loadButton = new JButton("Load File");
    private final JButton searchButton = new JButton("Search File");
    private final JButton quitButton = new JButton("Quit");
    private final JLabel statusLabel = new JLabel("No file loaded.");

    // The file currently loaded (null until the user picks one)
    private Path loadedFile = null;

    /// search only avaliable after file loads
    /// enter key in text field also runs search
    public DataStreamsFrame()
    {
        super("Data Streams - File Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildTextPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);
        searchButton.setEnabled(false);

        searchField.addActionListener(e -> {
            if (searchButton.isEnabled())
            {
                searchFile();
            }
        });

        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        quitButton.addActionListener(e -> System.exit(0));

        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    /// @return
    /// search string label
    private JPanel buildTopPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.add(new JLabel("Search string:"));
        panel.add(searchField);
        return panel;
    }

    /// @return
    /// sets the 2 text panels up
    private JPanel buildTextPanel()
    {
        originalArea.setEditable(false);
        filteredArea.setEditable(false);
        originalArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        filteredArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JScrollPane leftScroll = new JScrollPane(originalArea);
        leftScroll.setBorder(BorderFactory.createTitledBorder("Original File"));
        JScrollPane rightScroll = new JScrollPane(filteredArea);
        rightScroll.setBorder(BorderFactory.createTitledBorder("Filtered Results"));

        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        panel.add(leftScroll);
        panel.add(rightScroll);
        return panel;
    }

    /// @return
    /// builds the bottom panel
    private JPanel buildBottomPanel()
    {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        buttons.add(loadButton);
        buttons.add(searchButton);
        buttons.add(quitButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttons, BorderLayout.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 6, 10));
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    /** Lets the user pick a text file and shows it in the left text area */
    private void loadFile()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a text file");

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
        {
            return; // user canceled
        }

        Path path = chooser.getSelectedFile().toPath();

        try (Stream<String> lines = Files.lines(path))
        {
            String text = lines.collect(Collectors.joining("\n"));

            originalArea.setText(text);
            originalArea.setCaretPosition(0);
            filteredArea.setText("");
            loadedFile = path;
            searchButton.setEnabled(true);
            statusLabel.setText("Loaded: " + path.getFileName());
        }
        catch (IOException | UncheckedIOException ex)
        {
            showError("Could not read the file:\n" + path + "\n\n" + ex.getMessage());
        }
    }

    /** Uses a Stream + lambda filter to keep only lines containing the search string */
    private void searchFile()
    {
        if (loadedFile == null)
        {
            showError("Please load a file first.");
            return;
        }

        String searchText = searchField.getText();
        if (searchText.isEmpty())
        {
            showError("Please enter a search string.");
            return;
        }

        try (Stream<String> lines = Files.lines(loadedFile))
        {
            String result = lines
                    .filter(line -> line.contains(searchText))   // lambda filter
                    .collect(Collectors.joining("\n"));

            filteredArea.setText(result);
            filteredArea.setCaretPosition(0);

            long count = result.isEmpty() ? 0 : result.lines().count();
            statusLabel.setText("Found " + count + " matching line(s) for \""
                    + searchText + "\" in " + loadedFile.getFileName());
        }
        catch (IOException | UncheckedIOException ex)
        {
            showError("Could not search the file:\n" + loadedFile + "\n\n" + ex.getMessage());
        }
    }

    /// @param message
    /// shows an error
    private void showError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}