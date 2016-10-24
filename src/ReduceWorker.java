import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class ReduceWorker extends Worker<ReducePartialSolution> {

	public static ArrayList<HashMap<String, Integer>> hashLists = 
			new ArrayList<HashMap<String, Integer>>();
	
	public static int getNoWords(){
		HashMap<String, Integer> hash = hashLists.get(0);
		int val=0;
		for(Integer i : hash.values())
			val+=i;
		return val;
	}
	
	public ReduceWorker(WorkPool<ReducePartialSolution> workpool) {
		super(workpool);
	}

	@Override
	void processPartialSolution(ReducePartialSolution ps) {
		
		if(ps.hashLists.size()==1){
			return;
		}
		else
			{
				HashMap<String, Integer> hl1=null,hl2=null;
				
				synchronized (hashLists) 
				{
					int last = hashLists.size()-1;
					if(ps.hashLists.size()>=2){
						hl1= hashLists.remove(last);
						last--;
						hl2= hashLists.remove(last);
						if(hashLists.size()>0)
							this.wp.putWork(new ReducePartialSolution(hashLists));
					}
				}
				
				if(hl1!=null && hl2!=null)
				{
					Set<String> keySet = hl1.keySet();
					for(String key:keySet){
						Integer val1 = hl1.get(key);
						Integer val2 = hl2.get(key);
						
						if(val2==null){
							hl2.put(key, val1);
						}
						else{
							val2+=val1;
							hl2.put(key, val2);
						}
											
						//result hl2
					}
					
					synchronized (hashLists) 
					{
						hashLists.add(hl2);
						if(hashLists.size()>0)
							this.wp.putWork(new ReducePartialSolution(hashLists));
						
					}
				}
				
			}
			
	}

}
