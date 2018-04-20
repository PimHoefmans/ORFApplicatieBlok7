package apppack;


import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JOptionPane;
import org.biojava.nbio.core.sequence.*;
import org.biojava.nbio.core.sequence.compound.*;
import org.biojava.nbio.core.sequence.io.*;
import org.biojava.nbio.core.sequence.template.*;
import org.biojava.nbio.core.sequence.transcription.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author 		Pim Hoefmans
 * date 		15-04-2018
 * klas 		Bi2a
 * commentaar: 	Deze class is de AppLogica Class, hierin worden de ORFs gezocht met bijbehorende frames
 * known bugs:  werkt niet naar behoren, nog niet af, installatie issues met Git  
 */


public class AppLogica{

	/**
	 * @getFrames Deze functie geeft per sequentie in het bestand de eiwit sequentie in de 3 frames. 
	 * Hierna worden deze sequenties in de getORFs functie gestopt waaruit ORFs (zouden moeten) komen. 
	 * Deze code is lichtelijk overgenomen van de biojava module gevonden op github
	 * @throws SQLException 
	 */
	public static void getFrames(String file, int grootteORF) throws SQLException {

		try {
			InputStream stream = new ByteArrayInputStream(file.getBytes());
			AmbiguityDNACompoundSet ambiguityDNACompoundSet = AmbiguityDNACompoundSet.getDNACompoundSet();
			System.out.println(ambiguityDNACompoundSet);
			CompoundSet<NucleotideCompound> nucleotideCompoundSet = AmbiguityRNACompoundSet.getRNACompoundSet();
			FastaReader<DNASequence, NucleotideCompound> proxy = new FastaReader<>(stream, new GenericFastaHeaderParser<>(), new DNASequenceCreator(ambiguityDNACompoundSet));
			HashMap<String, DNASequence> fastaMap = proxy.process();
			TranscriptionEngine engine = new TranscriptionEngine.Builder().dnaCompounds(ambiguityDNACompoundSet).rnaCompounds(nucleotideCompoundSet).build();
			Frame[] sixFrames = Frame.getAllFrames();
			for (String header : fastaMap.keySet()) {
				DNASequence DNAseq = fastaMap.get(header);
				Map<Frame, Sequence<AminoAcidCompound>> results = engine.multipleFrameTranslation(DNAseq, sixFrames);
				//door de 6 frames lopen en getORFs aanroepen
				for (Frame frame : sixFrames) {
					getORFs(results.get(frame), header, 0, grootteORF, frame);
				}
			}
		} catch (IOException | ClassNotFoundException | SQLException e) {
			JOptionPane.showMessageDialog(null, "Fout bij omzetten van DNA naar proteine sequentie");
		}
	}

	/**
	 * @getORFs is een functie die zoekt naar ORF's
	 * @param subSeq, header, positie, grootteORF
	 * @throws ClassNotFoundException 
	 */
	public static void getORFs(Sequence subSeq, String header, int positie, int grootteORF, Frame frame) throws ClassNotFoundException {
		int stopPositie = subSeq.getSequenceAsString().indexOf("*");

		if (stopPositie >= grootteORF) {
			String ORF = subSeq.getSequenceAsString().substring(0, stopPositie);
			int id = stopPositie + positie;
			Blast blastORFs = new Blast(ORF);
		}
		
		if (stopPositie >= 1) {
			positie = positie + stopPositie;
			getORFs(subSeq.getSubSequence(stopPositie + 2, subSeq.getLength()), header, positie, grootteORF, frame);
		}
	}
}

//Mogelijke query's AppLogica
//String QueryORF = "INSERT INTO DNA_Features (DNA_feature_sequence, DNA_feature, DNA_feature_ID)" + "VALUES ('" + Sequence + "', "ORF" ," + id + ")";
//String QueryORF2 = "INSERT INTO Location (Start, Stop)" + "VALUES ('" + positie + "', '" + stopPositie + "');
//