/**
 * Clasa ce reprezinta un thread worker.
 */
public abstract class Worker<T extends PartialSolution> extends Thread {
	WorkPool<T> wp;
	
	public Worker(WorkPool<T> workpool) {
		this.wp = workpool;
	}

	/**
	 * Procesarea unei solutii partiale. Aceasta poate implica generarea unor
	 * noi solutii partiale care se adauga in workpool folosind putWork().
	 * Daca s-a ajuns la o solutie finala, aceasta va fi afisata.
	 */
	abstract void processPartialSolution( T ps);
	
	public void run() {
		//System.out.println("Thread-ul worker " + this.getName() + " a pornit...");
		while (true) {
			T ps = wp.getWork();
			if (ps == null)
				break;
			
			processPartialSolution(ps);
		}
		//System.out.println("Thread-ul worker " + this.getName() + " s-a terminat...");
	}

	
}



