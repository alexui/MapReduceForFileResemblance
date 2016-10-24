import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;


class FileStat{
	
	private static int files=0;
	HashMap<String , Integer> hashList;
	File file;
	int no_words;
	int index;
	
	public FileStat(File f,HashMap<String, Integer> hm,int nw){
		this.file=f;
		this.hashList=hm;
		no_words=nw;
		index=files;
		files++;
	}
	
	public String toString(){
		return file.getName() + "no words: "+no_words+" \n" + hashList+"\n";
	}
}

public class MapReduce {

	int D; //dimensiunea fragmentelor
	float X;//prag de similitudine
	int ND;//numarul de documente
	ArrayList<String>files; //fisierele de intrare
	Vector<FileStat> files_stat= new Vector<FileStat>();
	//fisiere impreuna cu hasmap-ul asociat
	
	String inputFile,outputFile;
	int NT; //number of threads
	
	FileInputStream is;
	PrintWriter pw;
	
	//citeste date din fisier
	void readInput(String name){
		try {
			is = new FileInputStream(name);
			
			Scanner scan= new Scanner(is);
			D = scan.nextInt();
			X = scan.nextFloat();
			ND = scan.nextInt();
			
			scan.nextLine();
			files = new ArrayList<String>();
						
			for(int i=0;i<ND;i++)
				files.add(scan.nextLine());
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	void writeOutput(){
		try {
			
			pw = new PrintWriter(new File(outputFile));
			{
				for(int i=0;i<CompareWorker.pairs.size();i++)
					if(CompareWorker.pairs.get(i).simRate>X)
						pw.println(CompareWorker.pairs.get(i));
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	void MapReduceOperation(String fileName,int fragmentSize,int no_threads){
				
		File file = new File(fileName);
		ArrayList<HashMap<String, Integer>> hashLists = new ArrayList<HashMap<String, Integer>>();
		
		
		//OPERATIA MAP
		
		WorkPool<MapPartialSolution> wp = new WorkPool<MapPartialSolution>(no_threads);
		MapPartialSolution mps = new MapPartialSolution(0, fragmentSize);
		ArrayList<MapWorker> workers = new ArrayList<MapWorker>();
		wp.putWork(mps);
		
		for(int i=0;i<no_threads;i++)
			workers.add(new MapWorker(file,new HashMap<String,Integer>(), wp));
		
		
		//long time1 = System.currentTimeMillis();
		for(MapWorker mw : workers)
			mw.start();
		
		for(MapWorker mw : workers)
			try {
				mw.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		
		//long time1end = System.currentTimeMillis()-time1;
			
		for(MapWorker mw : workers){
				//System.out.println(mw.hash);
				hashLists.add(mw.hash);
			}
		
		
		//OPERATIA REDUCE
		
		WorkPool<ReducePartialSolution> wp2 = new WorkPool<ReducePartialSolution>(no_threads);
		ArrayList<ReduceWorker> workers2 = new ArrayList<ReduceWorker>();
		
		ReduceWorker.hashLists = hashLists;
		ReducePartialSolution rps = new ReducePartialSolution(ReduceWorker.hashLists);
		wp2.putWork(rps);
		
		for(int i=0;i<no_threads;i++)
			workers2.add(new ReduceWorker(wp2));
			
		//long time2 = System.currentTimeMillis();
		
		for(ReduceWorker mw : workers2)
			mw.start();
		
		for(ReduceWorker mw : workers2)
			try {
				mw.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		//System.out.println("time1:"+time1end+"\ntime2:"+(System.currentTimeMillis()-time2));
		
			
		//adauga structura file_stat in vector
		files_stat.add(new FileStat(file, ReduceWorker.hashLists.get(0),ReduceWorker.getNoWords()));
		
	}
	
	public void CompareOperation(Vector<FileStat> files,int no_threads){
		
		if(files.size()<2)
			return;
				
		WorkPool<ComparePartialSolution> wp = new WorkPool<ComparePartialSolution>(no_threads);
		ComparePartialSolution cps = new ComparePartialSolution(files, 0, 1);
		ArrayList<CompareWorker> workers = new ArrayList<CompareWorker>();
		wp.putWork(cps);
		
		for(int i=0;i<no_threads;i++)
			workers.add(new CompareWorker(wp));
		
		//long time1 = System.currentTimeMillis();
		for(CompareWorker mw : workers)
			mw.start();
		
		for(CompareWorker mw : workers)
			try {
				mw.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		
		//long time1end = System.currentTimeMillis()-time1;
		//System.out.println("time1:"+time1end);
		
		Collections.sort(CompareWorker.pairs,new Comparator<Comparison>() {
			
			@Override
			public int compare(Comparison c1, Comparison c2) {
				
				int min1,min2;
				min1 = Math.min(c1.f1.index,c1.f2.index);
				min2 = Math.min(c2.f1.index,c2.f2.index);
				
				if(c1.simRate.compareTo(c2.simRate)>0)
					return -1;
				else
					if(Math.abs(c1.simRate-c2.simRate)<0.0000001){
						if(min1<min2)
							return 1;
					}
					else return 1;
				
				return 1;
			}
		});
		
		
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		MapReduce mr = new MapReduce();
		mr.NT = Integer.parseInt(args[0]);
		mr.inputFile = args[1];
		mr.outputFile = args[2];
		
		mr.readInput(mr.inputFile);
				
		long time = System.currentTimeMillis();
		for(int i=0;i<mr.files.size();i++){
			//System.out.println("file "+i);
			mr.MapReduceOperation(mr.files.get(i), mr.D, mr.NT);
		}
		
		mr.CompareOperation(mr.files_stat, mr.NT);
		
		System.out.println("Total time: "+(System.currentTimeMillis()-time));
		
		mr.writeOutput();
	}
}
