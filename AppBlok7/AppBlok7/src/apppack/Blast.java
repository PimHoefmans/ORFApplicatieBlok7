package apppack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

//import org.biojava.nbio.core.search.io.Hsp;
import org.biojava.nbio.core.search.io.Hit;
import org.biojava.nbio.core.search.io.Result;
import org.biojava.nbio.core.search.io.blast.BlastXMLParser;
import org.biojava.nbio.core.sequence.io.util.IOUtils;
import org.biojava.nbio.ws.alignment.qblast.BlastOutputParameterEnum;
import org.biojava.nbio.ws.alignment.qblast.BlastProgramEnum;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastAlignmentProperties;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastOutputProperties;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastService;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.search.io.*;

/**
 * @author 		Pim Hoefmans
 * date 		19-04-2018
 * klas 		Bi2a
 * commentaar: 	Deze class is de Blast Class, hierin zou de ORFs worden geblast tegen een database en
 * de resultaten opgeslagen worden in een tekstfile, maar deze class is nog niet volledig afgerond.
 * known bugs:  werkt nog niet 
 */

public class Blast {

		   /**
		    * @Blast Deze class is een samenvoegsel van code van biojava betreffende blast functionaliteit en
		    * het opslaan van de resultaten hieruit
		    * @param seq
		    */
		    public Blast(String seq) {
				String SEQUENCE = seq;
		        String BLAST_OUTPUT_FILE = "output.xml";
		        NCBIQBlastService service = new NCBIQBlastService();

		        NCBIQBlastAlignmentProperties props = new NCBIQBlastAlignmentProperties();
		        props.setBlastProgram(BlastProgramEnum.blastp);
		        props.setBlastDatabase("swissprot");
		        // set output options
		        NCBIQBlastOutputProperties outputProps = new NCBIQBlastOutputProperties();
		        // in this example we use default values set by constructor (XML format, pairwise alignment, 100 descriptions and alignments) 
		        // Example of two possible ways of setting output options
		        //outputProps.setAlignmentNumber(200);
		        outputProps.setOutputOption(BlastOutputParameterEnum.ALIGNMENTS, "200");
		        String rid = null;          // blast request ID
		        FileWriter writer = null;
		        BufferedReader reader = null;
		        try {
		            // send blast request and save request id
		            rid = service.sendAlignmentRequest(SEQUENCE, props);
		            // wait until results become available. Alternatively, one can do other computations/send other alignment requests
		            while (!service.isReady(rid)) {
		                System.out.println("Waiting for results. Sleeping for 5 seconds");
		                Thread.sleep(5000);
		            }
		            // read results when they are ready
		            InputStream in = service.getAlignmentResults(rid, outputProps);
		            File targetFile = new File(BLAST_OUTPUT_FILE);
		            OutputStream os = new FileOutputStream(targetFile);
		            byte[] buf = new byte[1024];
		            int numRead;
		            while ((numRead = in.read(buf)) >= 0) {
		                os.write(buf, 0, numRead);
		            }
		            BlastXMLParser blastParser = new BlastXMLParser();
		            blastParser.setFile(new File(BLAST_OUTPUT_FILE));
		            List<Result> blastResultaten = blastParser.createObjects(1.0);
		            Iterator<Hit> Hits = blastResultaten.get(0).iterator();
		            PrintWriter fileWriter = new PrintWriter("BLASTResults.txt");
		            while (Hits.hasNext()) {
		                Hit h = Hits.next();
		                for (Hsp hsp : h) {
		                    String sequence = hsp.getHspHseq();
		                    double evalue = hsp.getHspEvalue();
		                    int score = hsp.getHspScore();
		                    int identity = hsp.getHspIdentity();
		                    int positive = hsp.getHspPositive();
		                    SequencePair alignment = hsp.getAlignment();
		                    fileWriter.println("sequentie: " + sequence + "\n" + "evalue: " + evalue + "\n" + "score: " + score + "\n" + "identity: " + identity + "/n" + "positive: " + positive + "\n" + "alignment: " + alignment);
		                }
		                fileWriter.close();
		                JOptionPane.showMessageDialog(null, "De BLAST resultaten zijn te vinden inBLASTResults.txt");
		            }
		        } catch (Exception e) {
		            System.out.println(e.getMessage());
		            e.printStackTrace();
		        } finally {
		            //clean up
		            IOUtils.close(writer);
		            IOUtils.close(reader);
		            // delete given alignment results from blast server (optional operation)
		            service.sendDeleteRequest(rid);
		        }
		    }
	}

//Mogelijke query voor blast:
//
//