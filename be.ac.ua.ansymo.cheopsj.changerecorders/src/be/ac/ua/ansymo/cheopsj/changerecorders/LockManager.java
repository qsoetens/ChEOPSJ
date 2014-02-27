package be.ac.ua.ansymo.cheopsj.changerecorders;

public class LockManager {
	
	//The LockManager is a Singleton entity, hence the constructor is private.
	//You should always call the static method getInstance() to get the ModelManager instance.
	private static LockManager INSTANCE = null;
	private boolean isLocked = false;
	
	private LockManager() {}

	/**
	 * The LockManager is a Singleton entity. Therefore the constructor is private.
	 * This method returns an instance of the LockManager. If no instance existed 
	 * before it will call the private constructor to create a new instance. Else
	 * It will return the existing instance. 
	 *  
	 * @return the Singleton LockManager instance
	 */
	public static LockManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new LockManager();
		return INSTANCE;
	}
		
	public synchronized void lock()
			throws InterruptedException{
		while(isLocked){
			wait();
		}
		isLocked = true;
	}

	public synchronized void unlock(){
		isLocked = false;
		notify();
	}

	
}