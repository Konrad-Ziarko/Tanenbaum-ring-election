package ipserver;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class IpWindow extends JFrame{
    private static final long serialVersionUID = 1L;

    // declare the variables used as public in this class
    public JPanel contentPane; // declare the contentPane for UI
    public JButton btnStart = new JButton("Start"); // create a button  in the UI
    public JTextArea textArea = new JTextArea(); // create a text area for messages to be displayed

    public JLabel ipServer;
    public JTextArea ipS = new JTextArea();

    public JLabel ipServerPort;
    public JTextArea ipSPort = new JTextArea();

    // Variable to update the process list ***** DE-SCOPED *****
    // public ArrayList<Integer> processList = new ArrayList<Integer>();


    // Constructor which executes the UI for user
    public void initGUI() {
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(200, 200, 550, 450);
        contentPane = new JPanel(); // create a new panel
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        textArea.setEditable(false); // set the text are to non-editable

        // create a scrollable pane for the text area
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);
        scrollPane.setBounds(30, 65, 475, 180);
        contentPane.add(scrollPane);

        // Create a Manual Election button
        btnStart.setBounds(40, 272, 125, 23);
        btnStart.setEnabled(true);
        contentPane.add(btnStart);


        ipServer = new JLabel("Baza IP");
        ipServer.setBounds(300, 330, 149, 23);
        contentPane.add(ipServer);

        ipS.setBounds(370, 330, 105, 23);
        ipS.setEditable(false);
        contentPane.add(ipS);

        ipServerPort = new JLabel("Baza Port");
        ipServerPort.setBounds(300, 360, 149, 23);
        contentPane.add(ipServerPort);

        ipSPort.setBounds(370, 360, 105, 23);
        ipSPort.setEditable(true);
        contentPane.add(ipSPort);
    }


    public IpWindow() {
        initGUI();
        this.setVisible(true); // setting the frame visible
        this.setTitle("Serwer IP");
    }
}
