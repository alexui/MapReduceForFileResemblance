import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

class Comparison{
	
	FileStat f1,f2;
	Double simRate;
	
	public Comparison(FileStat f1,FileStat f2,Double sr){
		this.f1=f1;
		this.f2=f2;
		simRate=sr;
	}
	
	public String toString(){
		float val = (new BigDecimal(simRate).setScale(4,BigDecimal.ROUND_DOWN).floatValue());
		String nr = String.valueOf(val);
		
		return f1.file+";"+f2.file+";"+nr.replace(".", ",");
	}
}

public class CompareWorker extends Worker<ComparePartialSolution> {

	static Vector<Comparison> pairs = new Vector<Comparison>();	
	
	public CompareWorker(WorkPool<ComparePartialSolution> workpool) {
		super(workpool);
	}

	@Override
	void processPartialSolution(ComparePartialSolution ps) {
		
		if(ps.term2+1<ps.files.size()){
			this.wp.putWork(new ComparePartialSolution(ps.files, ps.term1, ps.term2+1));
		}
		else{
			if(ps.term1+1<ps.files.size()-1){
				this.wp.putWork(new ComparePartialSolution(ps.files, ps.term1+1, ps.term1+2));
			}
		}
		//comparatia intre doua hashListuri
		
		HashMap<String, Integer> hl1 = ps.files.get(ps.term1).hashList;
		HashMap<String, Integer> hl2 = ps.files.get(ps.term2).hashList;
		Double frecv1,frecv2;
		int nw1,nw2; //number of words
		nw1=ps.files.get(ps.term1).no_words;
		nw2=ps.files.get(ps.term2).no_words;
		
		
		Set<String> keySet = new HashSet<String>();
		keySet.addAll(hl1.keySet());
		keySet.addAll(hl2.keySet());
		
		
		Double simRate = 0.0;
		for(String word : keySet){
			Integer val1,val2;
			val1 = hl1.get(word);
			val2 = hl2.get(word);
						
			if(val1!=null){
				frecv1 = ((double)val1/nw1)*100f;
			}
			else {
				frecv1=0.0;
			}
			
			if(val2!=null){
				frecv2 = ((double)val2/nw2)*100f;
			}
			else {
				frecv2=0.0;
			}
			
						
			simRate=((simRate + (((frecv1)*(frecv2))/100)));
			
		}
		
		synchronized (pairs) {
			pairs.add(new Comparison(ps.files.get(ps.term1), ps.files.get(ps.term2),simRate));
		}
		
	}
	

}
