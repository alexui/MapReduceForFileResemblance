import java.util.Vector;

public class ComparePartialSolution extends PartialSolution {
	
	Vector<FileStat> files;
	int term1,term2;
	
	public ComparePartialSolution(Vector<FileStat> hl,int t1,int t2){
		this.files=hl;
		this.term1=t1;
		this.term2=t2;
	}
}
