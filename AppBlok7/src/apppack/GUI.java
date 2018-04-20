package apppack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author 		Pim Hoefmans
 * date 		15-03-2018
 * klas 		Bi2a
 * commentaar: 	Deze class is de GUI Class, hierin wordt de GUI gemaakt met buttons, textfields, textpane etc. 
 */

public class GUI extends JFrame implements ActionListener {

	private JFileChooser fileChooser;
	private JButton buttonSearch, buttonBlast;
	private JTextField fieldFile, fieldORF;
	private JLabel labelORFgrootte;
	private JTextPane textpaneSeq;

	/**
	 * @main Creatie van de GUI met grootte
	 * @param args
	 */
	public static void main(String args[]) {
		GUI frame = new GUI();
		frame.setSize(1000, 1000);
		frame.createGUI();
		frame.setVisible(true);
	}

	/**
	 * @createGUI voegt de buttons, textfields, textpane en FileChooser toe aan de GUI
	 */
	private void createGUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container window = getContentPane();
		window.setLayout(new FlowLayout());

		buttonSearch = new JButton("Selecteer uw bestand");
		window.add(buttonSearch);
		buttonSearch.addActionListener(this);

		fieldFile = new JTextField(25);
		window.add(fieldFile);
		
		labelORFgrootte = new JLabel("ORF grootte: ");
        window.add(labelORFgrootte);
		
		fieldORF = new JTextField(5);
        fieldORF.setText("100");
        window.add(fieldORF);

		textpaneSeq = new JTextPane();
		textpaneSeq.setPreferredSize(new Dimension(900, 350));
		JScrollPane scrollPane = new JScrollPane(textpaneSeq);
		window.add(scrollPane);

		buttonBlast = new JButton("Blast gevonden ORF's");
		window.add(buttonBlast);
		buttonBlast.addActionListener(this);
	}

	/**
	 *Deze functie zorgt voor de input van de Search knop, deze knop zorgt ervoor dat een bestand wordt
	 *geopend en wordt ingeladen. 
	 *@param event
	 */
	public void actionPerformed(ActionEvent event) {

		int Path;
		File selectedFile;

		//de search button zorgt dat de gebruiker een lokaal bestand kan selecteren
		if (event.getSource() == buttonSearch) {
			fileChooser = new JFileChooser();
			Path = fileChooser.showSaveDialog(this);
			if (Path == JFileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
				fieldFile.setText(selectedFile.getAbsolutePath());
			} try {
				String tekstBestand = FileLezer.fileContent(fieldFile.getText());
				textpaneSeq.setText(tekstBestand);
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Het bestand is niet gevonden");
			}
		}

		//de ORF blast button roept de blast aan van alle gevonden ORF's
		//de resultaten worden opgeslagen in een ..... bestand en geopend in een pop-up
		if (event.getSource() == buttonBlast) {
			try {
				String tekstBestand = FileLezer.fileContent(fieldFile.getText());
				AppLogica.getFrames(tekstBestand, Integer.parseInt(fieldORF.getText()));
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Het bestand is niet gevonden");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Er is iets fout gegaan bij het lezen van het bestand");

			
			} 
		}

	}
}