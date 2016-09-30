import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Assn3exchecker {
	public static void main(String[] args) {	
		Scanner sc = new Scanner(System.in) ;
    	var.setmode(sc.nextInt()) ;
    	sc.close();
    	var.setstime(System.currentTimeMillis()) ;
		BufferedReader br = null;		
		try {
			String actionString ;
			br = new BufferedReader(new FileReader((var.mode()==1) ? "file.txt" : "randominput.txt")) ;
            CentralServerThread Cserver = new CentralServerThread() ;
            DashBoard db = new DashBoard() ;
            db.start() ;
            Cserver.start() ;
			while ((actionString = br.readLine()) != null) {
			    Scanner s = new Scanner(actionString) ;
			    long time = s.nextLong()*1000 ;
			    s.close();
			    while((System.currentTimeMillis()-var.Time())<=time){
			    	try{Thread.sleep(20) ;}catch(Exception e){e.printStackTrace();}
			    }
			    CentralServerThread.requests.enque(actionString) ;
			}
			if(var.mode()==1){
			  try{Thread.sleep(6000) ;} catch(Exception e){e.printStackTrace();} ;
			}
			else{
				try{Thread.sleep(40000) ;} catch(Exception e){e.printStackTrace();} ;
			}
			var.endTheThread();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	
	}
}
class var{
	static private boolean endThread = false ;
	static private int mode ;
	static private long stime ;
	static public ArrayList Phones = new ArrayList(new MyCompm()) ;
	static public ArrayList Exchanges = new ArrayList(new MyCompe()) ;
	synchronized static void setstime(long l){
		stime=l ;
	}
	synchronized static long Time(){
		return stime ;
	}
	synchronized static void endTheThread(){
		endThread=true ;
	}
	synchronized static boolean endThread(){
		return endThread ; 
	}
	synchronized static int mode(){
		return mode ;
	}
	synchronized static void setmode(int i){
		mode=i ;
	}
}
class CentralServerThread extends Thread{
    RoutingMapTree tr = new RoutingMapTree() ;	
    static MyQueue<String> requests = new MyQueue<String>() ;
    public void run(){
    	while(!var.endThread()){
    	    if(!requests.isEmpty()){
    	   	   String s = requests.deque() ;
    	   	   tr.performAction(s) ;
    	    }
    	    try{Thread.sleep(15) ;} catch(Exception e){e.printStackTrace() ;}
    	}      
    }
}
class DashBoard extends Thread{
	public void run(){
		try{Thread.sleep(500) ;} catch(Exception e){e.printStackTrace() ;} ;		   	
		while(!var.endThread()){
			long time = (System.currentTimeMillis()-var.Time())/1000 ;
			System.out.println(time) ;
		    int size = var.Phones.size() ;		    
		    for(int i=0 ; i<size ; i++){
		    	MobilePhone mp = (MobilePhone) var.Phones.get(i) ;
		    	System.out.print("MobilePhone :"+mp.number()) ;
		    	System.out.print(" Location :"+mp.location().id()) ;
		    	System.out.print(" Is swtiched On : "+((mp.status()) ? "Yes" : "No")) ;
		    	System.out.print(" Status :") ;
		    	if(mp.busy())
		    		System.out.print(" Is in call with "+mp.CPhone().number()) ;
		    	else 
		    		System.out.print(" Free") ;
		    	System.out.println() ;
		    }
		    try{Thread.sleep(1000) ;} catch(Exception e){e.printStackTrace() ;} ;
		    for(int i=0 ; i<5*(size+10) ; i++)
		    	System.out.println() ;
		}
	}
}