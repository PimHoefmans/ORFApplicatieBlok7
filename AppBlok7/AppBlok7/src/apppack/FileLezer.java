package apppack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;


/**
 * @author 		Pim Hoefmans
 * date 		15-03-2018
 * klas 		Bi2a
 * commentaar: 	Deze class is de FileLezer Class, hierin wordt het bestand geopent en gelezen, met
 * adequate exception handling, uit fileContent wordt de gelezen inhoud opgeslagen onder content 
 */

public class FileLezer {	
	
	public FileLezer(String filenaam, int grootteORF) {
		
		BufferedReader infile;
		try {
			infile = new BufferedReader(new FileReader(filenaam));
			infile.readLine();
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Het bestand is niet gevonden");
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(null, "Het bestand kan niet worden gelezen");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Er is iets fout gegaan bij het lezen van het bestand");
		}
	}

	public static String fileContent(String filenaam) throws FileNotFoundException {
		@SuppressWarnings("resource")
		String content = new Scanner(new File(filenaam)).useDelimiter("\\Z").next();
		return content;
	}
}
