package election;

import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class Window extends JFrame{
	private static final long serialVersionUID = 1L;

	// declare the variables used as public in this class
	public JPanel contentPane; // declare the contentPane for UI
	public JButton btnStart = new JButton("Start"); // create a button  in the UI
	public JButton btnStop = new JButton("Stop"); // create a button in the UI
	public JButton btnRegister = new JButton("Rejestruj"); // create a button in the UI
	public JTextArea textArea = new JTextArea(); // create a text area for messages to be displayed
	public JButton btnRefresh = new JButton("Ping");
	
	public JLabel processNo; // create to label in the UI
	//public JTextArea pNo = new JTextArea(); // create a textArea to represent the Process Number
	public JLabel coornatorNo; // create to label in the UI
	//public JTextArea cNo = new JTextArea(); // create a textArea to represent the Coordinator Number
	public JFormattedTextField pNo = new JFormattedTextField(NumberFormat.getNumberInstance());
	public JLabel cNo;
	
	public JLabel portLabel;
	public JTextArea portTextArea = new JTextArea();
	
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

		// Create a new label and set the position
		portLabel = new JLabel("Port:");
		portLabel.setBounds(50, 350, 149, 23);
		contentPane.add(portLabel);

		// Create a new tetxbox in order to list the process number
		portTextArea.setBounds(150, 350, 85, 23);
		portTextArea.setEditable(true);
		contentPane.add(portTextArea);
		
		// Create a new label and set the position
		processNo = new JLabel("Id procesu");
		processNo.setBounds(50, 25, 149, 23);
		contentPane.add(processNo);

		// Create a new tetxbox in order to list the process number
		pNo.setBounds(150, 25, 85, 23);
		pNo.setEditable(true);
		contentPane.add(pNo);

		// Create a Refresh button in order to refresh the token passing
		btnRegister.setBounds(300, 25, 95, 23);
		btnRegister.setEnabled(true);
		contentPane.add(btnRegister);

		// Create a Election button
		btnStart.setBounds(40, 272, 125, 23);
		btnStart.setEnabled(false);
		contentPane.add(btnStart);

		// Create a Crash button
		btnStop.setBounds(230, 272, 95, 23);
		btnStop.setEnabled(false);
		contentPane.add(btnStop);
		
		btnRefresh.setBounds(380, 272, 95, 23);
		btnRefresh.setEnabled(false);
		contentPane.add(btnRefresh);

		// Create a new label and set the position
		coornatorNo = new JLabel("Koordynator");
		coornatorNo.setBounds(50, 330, 149, 23);
		contentPane.add(coornatorNo);

		// Create a new tetxbox in order to list the coordinator number
		cNo = new JLabel("brak");
		cNo.setBounds(150, 330, 85, 23);
		contentPane.add(cNo);
		
		ipServer = new JLabel("Baza IP");
		ipServer.setBounds(300, 330, 149, 23);
		contentPane.add(ipServer);
		
		ipS.setBounds(370, 330, 105, 23);
		ipS.setEditable(true);
		contentPane.add(ipS);
		
		ipServerPort = new JLabel("Baza Port");
		ipServerPort.setBounds(300, 360, 149, 23);
		contentPane.add(ipServerPort);
		
		ipSPort.setBounds(370, 360, 105, 23);
		ipSPort.setEditable(true);
		contentPane.add(ipSPort);
	}


	public Window() {
		initGUI();
		this.setVisible(true); // setting the frame visible
		this.setTitle("Pierscien Tanenbaum'a");
		//addListeners();
	}

}
