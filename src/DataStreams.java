import javax.swing.SwingUtilities;

/**
 * Lab 09 - Java Data Streams
 * Entry point. Builds the Swing GUI on the Event Dispatch Thread.
 * Note: main does NOT declare "throws IOException" - all file errors are
 * handled with try/catch inside DataStreamsFrame.
 */
public class DataStreams
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            DataStreamsFrame frame = new DataStreamsFrame();
            frame.setVisible(true);
        });
    }
}