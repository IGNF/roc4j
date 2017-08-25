/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 *
 * @author Yann Méneroux
 ******************************************************************************/

package fr.ign.cogit;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;


public class RocSpaceCollection {


	private ArrayList<RocSpace> SPACES;
	
	
	public int getRocSpaceNumber(){return SPACES.size();}


	public RocSpaceCollection(){

		SPACES = new ArrayList<RocSpace>();

	}

	public RocSpaceCollection(ArrayList<RocSpace> SPACES){

		this.SPACES = SPACES;

	}


	public RocSpace getRocSpace(int index){

		return SPACES.get(index);

	}

	public void addRocSpace(RocSpace space){

		SPACES.add(space);

	}


	public void save(String file, int svg_width, int svg_height){
		
		int columns = (int) (Math.sqrt(SPACES.size()));

		save(file, svg_width, svg_height, columns, true);

	}

	public void save(String file, int svg_width, int svg_height, int columns){

		save(file, svg_width, svg_height, columns, true);

	}

	public void save(String file, int svg_width, int svg_height, int columns, boolean verbose){

		try {

			write(file, svg_width, svg_height, columns, verbose);

		} catch (IOException e) {

			e.printStackTrace();

		}

	}


	private void write(String file, int svg_width, int svg_height, int columns, boolean verbose) throws IOException{


		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));

		String begin = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
		begin += "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\""+(columns*svg_width)+"\" height=\""+((SPACES.size()/columns+1)*svg_height)+"\"> \r\n";
		begin += "\t <title>ROC space SVG export</title>\r\n";
		begin += "\t <desc>Multiple roc spaces generated with roc4j open source program</desc>\r\n";

		writer.write(begin);


		int row = 0;
		int column = 0;
		
		for (int i=0; i<SPACES.size(); i++){
			
			if (column >= columns){
				
				column = 0;
				row ++;
				
			}

			String body = "";

			body += "<g transform=\"translate("+(column*svg_width)+","+(row*svg_width)+")\"> \r\n";

			body += SPACES.get(i).convertToSVGText(svg_width, svg_height);

			body += "</g>\r\n";

			writer.write(body);

			if (verbose){

				System.out.println("Roc space number "+(i+1)+" saved ["+row+"x"+column+"]");

			}
			
			column ++;

		}



		String end = "</svg>";

		writer.write(end);	



		writer.close();

		System.out.println("File "+file+" has been saved with success");

	}


}
