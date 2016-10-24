import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.StringTokenizer;


public class MapWorker extends Worker<MapPartialSolution> {

	HashMap<String, Integer> hash;
	File file;
	RandomAccessFile raf;
	
	public MapWorker(File f,HashMap<String,Integer> hash,WorkPool<MapPartialSolution> workpool) {
		super(workpool);	
		this.hash=hash;
		file =f;
		try {
			raf = new RandomAccessFile(f, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	public String readString(RandomAccessFile raf) throws IOException{
		String word="";
		Character c = null;
				
		try {
			c=(char)raf.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		boolean EOF =false;
		while(!separator(c) && !EOF){
				word+=Character.toLowerCase(c);
			
			try {
					if(raf.getFilePointer()==raf.length()){
						EOF=true;
						break;
					}
					c=(char)raf.read();	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return word;
	}

	boolean separator(char c){
		return
				(c==' ' ||
				c==',' ||
				c=='.' ||
				c==';' ||
				c==':' ||
				c=='?' ||
				c=='!' ||
				c=='-' ||
				c=='~' ||
				c=='<' ||
				c=='>' ||
				c==']' ||
				c=='[' ||
				c=='(' ||
				c==')' ||
				c=='{' ||
				c=='}' ||
				c=='@' ||
				c=='#' ||
				c=='&' ||
				c=='$' ||
				c=='%' ||
				c=='^' ||
				c=='*' ||
				c=='+' ||
				c==',' ||
				c=='\t' ||
				c=='\r' ||
				c=='\n' ||
				c=='\"' ||
				c=='\'' ||
				c=='|' ||
				c=='/' ||
				c=='\\' ||
				c=='`' ||
				c=='=' ||
				c=='_');
	}
	
	@Override
	void processPartialSolution(MapPartialSolution ps) {
	
		try {
			if(ps.offset+ps.fragmentSize<raf.length()){
				this.wp.putWork(new MapPartialSolution(ps.offset+ps.fragmentSize , ps.fragmentSize));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//pt inceputul unui cadru de caractere ne uitam la primul careacter d-inainte
		int pos=1;
		long init = ps.offset;
		long fin=ps.offset+ps.fragmentSize-1;
		try {
			if(fin>raf.length())
				fin=raf.length();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		boolean jump = false;
			
		try {
			
			Character c;
			
			//ma duc la inceput
			raf.seek(ps.offset);
			
			if(raf.getFilePointer()>0){
							
				
				//in caz ca inainte avem cr sau endl sarim peste
				
				if(!separator(c=(char)raf.read())){
					raf.seek(ps.offset-1);
					do{
						raf.seek(ps.offset-pos);
						c = (char) raf.read();
						pos++;
					}while(c=='\r' || c=='\n');
				}
				
				//daca nu a fost un separator se sare peste cuvant -> il citim dar nu il salvam
				if(!separator(c)){
					jump = true;
				}
			}
				
				if(fin<raf.length()-1)
					do{
						raf.seek(fin);
						c= (char)raf.read();	
						fin++;
					}while(!separator(c));
				
				fin--;
				
				raf.seek(ps.offset);
				int dim = (int) (fin-init+1);
				byte[] bytes = new byte[dim];
				//raf.read(bytes,(int)(init),(int)(fin-1));
				raf.readFully(bytes);
				
				String s= new String(bytes);
				
				StringTokenizer st = new StringTokenizer(s," ;,.<>?/:\"\'\\[]{}|-_=+!@#$%^&*()`~\r\t\n");
				
				if(jump)
					st.nextToken();
				
				while(st.hasMoreTokens()){
					String word = st.nextToken().toLowerCase();
					if(hash.containsKey(word)){
						int val= hash.get(word);
						val++;
						hash.put(word, val);
					}
					else
						hash.put(word, 1);
				}
				
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	/*	try {
			//prima conditie din while permite citirea completa a ultimului cuvant din fragment
			while(raf.getFilePointer()<ps.offset+ps.fragmentSize && raf.getFilePointer()<raf.length()){
				String word = readString(raf);
				
				if(word.length()>0)
					if(hash.containsKey(word)){
						int val= hash.get(word);
						val++;
						hash.put(word, val);
					}
					else
						hash.put(word, 1);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
	}
	
}
