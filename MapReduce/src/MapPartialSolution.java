public class MapPartialSolution extends PartialSolution {

		
	//RandomAccessFile raf;
	long offset;
	int fragmentSize;
	
	public MapPartialSolution(long o, int fs){
		/*try {
			this.raf= new RandomAccessFile(f, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
		this.offset=o;
		this.fragmentSize =fs;
	}
	
	public String toString() {
		return ""+offset+" "+fragmentSize;
		
	}
}
