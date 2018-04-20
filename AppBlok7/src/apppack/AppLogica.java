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
 * date 		15-03-2018
 * klas 		Bi2a
 * commentaar: 	Deze class is de AppLogica Class, hierin worden de ORFs gezocht met bijbehorende frames
 * known bugs: werkt niet, installatie issues met git,  
 */


public class AppLogica{

    /**
     * @ApplicatieLogica roept met het bestand de functies aan die het bestand inlezen en de ORF's zoeken 
     * @param bestandnaam
     * @param grootteORF 
     */
 
	/**
     * @getFrames zorgt eerst dat de database leeg is, en genereert vervolgens per sequentie in het bestand de eiwit sequentie in de 3 verschillende frame. 
     * Elk frame wordt opgeslagen in de database, en vervolgens in de getORFs functie gegooid. 
     * @param bestand
     * @param grootteORF
     * @throws SQLException 
     */
    public static void getFrames(String file, int grootteORF) throws SQLException {
        
        try {
            //verkrijgen van de 3 verschillende frames, gebaseerd op code van https://github.com/biojava/biojava-tutorial/blob/master/core/translating.md (21-3-2018)
            InputStream stream = new ByteArrayInputStream(file.getBytes());
            AmbiguityDNACompoundSet ambiguityDNACompoundSet = AmbiguityDNACompoundSet.getDNACompoundSet();
            System.out.println(ambiguityDNACompoundSet);
            CompoundSet<NucleotideCompound> nucleotideCompoundSet = AmbiguityRNACompoundSet.getRNACompoundSet();
            FastaReader<DNASequence, NucleotideCompound> proxy = new FastaReader<>(stream, new GenericFastaHeaderParser<>(), new DNASequenceCreator(ambiguityDNACompoundSet));
            HashMap<String, DNASequence> fastaMap = proxy.process();
            TranscriptionEngine engine = new TranscriptionEngine.Builder().dnaCompounds(ambiguityDNACompoundSet).rnaCompounds(nucleotideCompoundSet).build();
            Frame[] sixFrames = Frame.getAllFrames();
            //door sequenties lopen en opslaan
            for (String header : fastaMap.keySet()) {
                DNASequence DNAseq = fastaMap.get(header);
                String deQuery = "INSERT INTO DNA (SEQUENTIE, HEADER)" + "VALUES ('" + DNAseq + "','" + header + "')";
                Database.executeQuery(deQuery); //inserten in de database uitcommenten als de server het doet
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
     * @getORFs is een recursieve functie en zoekt naar ORF's in een sequentie met meegegeven grootte van ORF
     * Wanneer er een ORF gevonden is wordt deze opgeslagen in de database
     * @param subSeq
     * @param header
     * @param positie
     * @param grootteORF
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public static void getORFs(Sequence subSeq, String header, int positie, int grootteORF, Frame frame) throws SQLException, ClassNotFoundException {
        int stopPositie = subSeq.getSequenceAsString().indexOf("*");
        //opslaan van ORF wanneer deze minimaal de ingevoerde grootte heeft
        if (stopPositie >= grootteORF) {
            String ORF = subSeq.getSequenceAsString().substring(0, stopPositie);
            int id = stopPositie + positie;
            String deQueryORF = "INSERT INTO ORF (SEQUENTIE, START_POSITIE, STOP_POSITIE, FRAME, DNA_HEADER, ORFID)" + "VALUES ('" + ORF + "', '" + positie + "', '" + stopPositie + "', '" + frame + "', " + header + "', " + id + ")";
            Blast blastORFs = new Blast(ORF);
        }
        //subsequentie opnieuw zoeken naar eerst volgende ORF
        if (stopPositie >= 1) {
            positie = positie + stopPositie;
            getORFs(subSeq.getSubSequence(stopPositie + 2, subSeq.getLength()), header, positie, grootteORF, frame);
        }
    }
}