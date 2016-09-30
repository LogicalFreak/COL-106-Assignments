/* Checkpoints :
 * Useful Classes : Queue,Node,Time,List,Stock,MyStock
 * All done 
 */
class Time{
	private static long st ;
	public static void setStartTime(){
		st = System.currentTimeMillis() ;
	}
	synchronized public static long getStartTime(){
		return st ; 
	} 
}
class List{
	private MyStock val[] = new MyStock[10000] ;
	private int upp_lim=0 ;
	private int size=0 ;
	synchronized public void add(MyStock st){
		val[upp_lim++]=st ;
		size++ ;
	}
	synchronized public boolean isEmpty(){
		return size==0 ; 
	}
	synchronized public int upp_lim(){
		return upp_lim ;
	}
	synchronized public void remove(int i){
		if(i>=upp_lim || i<0 || val[i]==null){
			System.out.println("Pls this just can't happen") ;
			return  ;
		}
		val[i]=null ;
		size-- ;
	}
	synchronized public MyStock get(int i){
		return val[i] ;
	}
	synchronized public void clear(){
		upp_lim=0 ;
		size=0 ;
	}
}
public class stock{
	stock(){
		Runnable odr = new test("Order") ;
		Runnable exc = new test("Exchange") ;
		Runnable clp = new test("Cleanup") ;
		Thread order = new Thread(odr) ;
		Thread exchange = new Thread(exc) ;
		Thread cleanup = new Thread(clp) ;
		Time.setStartTime() ; 
		order.start() ;
		exchange.start() ;
		cleanup.start() ;
	}
	void performAction(String actionString){
	    test.orders.enque(actionString) ;
		//System.out.println(actionString) ;			        	   
	}
}
class MyStock{
	long t0 ;
	String name ;
	long texp ;
	char type ;
	int InitialQty ;
	int PresentQty ;
	String stk ;
	long price ;
	boolean partial ;	
}
class MyQueue<E>{
	private Node<E> head=null ;
	private Node<E> tail=null ;
	public synchronized boolean isEmpty(){
		return head==null ;
	}
	synchronized public void enque(E e){
		Node<E> nd = new Node<E>(e) ;		
		if(isEmpty()){
			head=nd ;
			tail=nd ;
			return  ;
		}
		head.next=nd ;
		head=nd ;
	}
	synchronized public E deque(){
		if(isEmpty()){
			System.out.print("Pls this just can't happen") ;
			return null ;
		}
		Node<E> to_be_returned = tail ;		
		tail=tail.next ;		
		if(tail==null)
			head=null ;		
	    return (to_be_returned).data ;	
	}
}
class Node<E>{
	E data ;
	Node<E> next=null ;
	Node(E dat){
		data=dat ;
	}
}