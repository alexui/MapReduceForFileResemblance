import java.util.ArrayList;
import java.util.HashMap;


public class ReducePartialSolution extends PartialSolution {
	
	ArrayList<HashMap<String, Integer>> hashLists;
	
	public ReducePartialSolution(ArrayList<HashMap<String, Integer>> lists){

		hashLists = lists;
	}
	
	public String toString(){
		
		String s="";
		return s+hashLists;
	}
	
}
