import java.util.Random;
import java.util.Scanner;

/* Everything's done */
class MobilePhone extends Thread{
	private int id ;
	private boolean status ;
	private boolean busy ;
	private Exchange B ; 
	private MobilePhone CPhone ; 
	MyQueue<String> requests = new MyQueue<String>() ;	
	MobilePhone(int Number){
		id=Number ;
		status=false ;
		B=null ;
		busy=false ;
	}
	synchronized void setCPhone(MobilePhone m){
		CPhone=m ;
	}
	synchronized MobilePhone CPhone(){
		return CPhone ;
	}
	public void run(){
		if(var.mode()==1){
			while(!var.endThread()){
				if(!requests.isEmpty()){
					String req = requests.deque() ;
					Scanner s = new Scanner(req) ;
					String query = s.next() ;
					if(query.compareTo("switchon")==0){
						status=true ;
						   //System.out.println(time+" MobilePhone "+this.number()+" switched on") ;					    				
					}
					else if(query.compareTo("switchoff")==0){
						status=false ;
						   //System.out.println(time+" MobilePhone "+this.number()+" switched off") ; 											
					}
					else if(query.compareTo("Called")==0){
						busy=true ;
						long CallTime = s.nextInt() ;
						try{Thread.sleep(1000*CallTime) ;} catch(Exception e){e.printStackTrace();} ;
						busy=false ;				
						setCPhone(null) ;
					}
					else if(query.compareTo("CallAccepted")==0){
						busy=true ;
						long CallTime = s.nextInt() ;
						try{Thread.sleep(1000*CallTime) ;} catch(Exception e){e.printStackTrace() ;} ;
						busy=false ;
						setCPhone(null) ;
					}
					s.close() ;
				}
				try{Thread.sleep(15) ;} catch(Exception e){} 
			}
		}
		else{
			while(!var.endThread()){
				if(requests.isEmpty()){
					Random rn = new Random() ;
					int rint = rn.nextInt(100) ;
					if(rint<2){
						int size=var.Exchanges.size() ;
						int rex = rn.nextInt(size) ;
						int eid = ((Exchange) var.Exchanges.get(rex)).id() ;
						CentralServerThread.requests.enque(0+" movePhone "+this.number()+" "+eid) ;
					}
					else if(rint<6){
						status=false ;
					}
					else if(rint<10){
						status=true ;
					}
					else if(rint<20){
						int size = var.Phones.size() ;
						int rmob = rn.nextInt(size) ;
						int mid = ((MobilePhone) var.Phones.get(rmob)).number() ;
						if(rmob!=this.id){							
						   int CallingTime = 3+rn.nextInt(3) ;
						   CentralServerThread.requests.enque(0+" CallAttempt "+this.number()+" "+mid+" "+CallingTime) ;
						}
					}
				}
				else{
					String req = requests.deque() ;
					Scanner s = new Scanner(req) ;
					String query = s.next() ;
					if(query.compareTo("Called")==0){
						busy=true ;
						long CallTime = s.nextInt() ;
						try{Thread.sleep(1000*CallTime) ;} catch(Exception e){e.printStackTrace();} ;
						busy=false ;					
					}
					else if(query.compareTo("CallAccepted")==0){
						busy=true ;
						long CallTime = s.nextInt() ;
						try{Thread.sleep(1000*CallTime) ;} catch(Exception e){e.printStackTrace() ;} ;
						busy=false ;						
					}
					s.close() ;
				}
			    try{Thread.sleep(400) ;}catch(Exception e){e.printStackTrace();} ;
			}
		}
	}
	synchronized public boolean busy(){
		return busy ;
	}
	synchronized public void setbusy(boolean b){
		busy=b ;
	}
	synchronized public int number(){
		return id ;
	}
	synchronized public boolean status(){
		return status ;
	}
	synchronized public void SwitchOn(){
		status=true ;
	}
	synchronized public void SwitchOff(){
		status=false ;
	}
	synchronized public Exchange location(){
		return B ;
	} 
    synchronized public void setAsExchange(Exchange E){
    	B=E ;
    }
}
class MobilePhoneSet{
	Myset numbers ;
	MobilePhoneSet(){
		numbers = new Myset(new MyCompm()) ;
	}
}
class Exchange{
	private int id ;
	private Exchange parent ;
	private ExchangeList children ;
	private boolean isroot=false ;
	private MobilePhoneSet ResidentSet ;
	Exchange(int number){
		id=number ;
		ResidentSet = new MobilePhoneSet() ;
		children = new ExchangeList() ;
	}
	public int id(){
		return id ;
	}
	public void setAsRoot(){
		isroot=true ;
	}
	public boolean isRoot(){
		return isroot ;
	}		
	public Exchange parent(){
		return parent ;
	}
	public void setParent(Exchange E){
		parent=E ;
	}	
	public Exchange child(int i){
		return children.get(i) ;
	}
	public void AddChild(Exchange E){
		children.add(E) ;
	}
	public int numChild(){
		return children.size() ;
	}
	public RoutingMapTree subtree(int i){
		return new RoutingMapTree(child(i)) ;
	}
	public MobilePhoneSet residentSet(){
	    return ResidentSet ;	
	}		
}
class ExchangeList{
	private int size ;
	private ExchangeNode head ;
	private ExchangeNode tail ;	
	ExchangeList(){
		size=0 ;
	}
	public void PrintList(){
		ExchangeNode node = head ; 
		while(node!=null){
			System.out.print(node.data.id()+((node.next==null) ? "" : ", ")) ;
			node=node.next ;
		}
	}
	public int size(){
		return size ;
	}
	public void reverse(){
		if(size<2)
			return ;
		tail=head ; 
		ExchangeNode node = head ;
		ExchangeNode nnode = head.next ;
		head.next=null ;
		while(nnode.next!=null){
			ExchangeNode tem = node ;
			node=nnode ;
			nnode=nnode.next ;
			node.next=tem ;			
		}
		nnode.next=node ;
		head=nnode ;
	}
	public void merge(ExchangeList E){
		tail.next = E.head ;
		tail=E.tail ;		
	}
	public void add(Exchange e){
		ExchangeNode n = new ExchangeNode(e) ;
		if(size==0){
			head=n ;
			tail=n ;
			size++ ;
			return ;
		}
		tail.next=n ;
		tail=n ;
		size++ ;
	}
	public Exchange get(int i){
		if(i>=size)
			throw new IndexOutOfBoundsException() ;
		int ind=0 ;
		ExchangeNode node = head ;
		while(ind!=i){
			node = node.next ;
			ind++ ;
		}
		return node.data ;
	}
}
class ExchangeNode{
	Exchange data ;
	ExchangeNode next ;
	ExchangeNode(Exchange E){
		data=E ;
	}
}
class Myset{   
   private ArrayList data ; 
   private Comparator cp ;
   Myset(Comparator o){
	   cp=o ;
	   data = new ArrayList(cp) ;
   }
   public boolean isEmpty(){
	   return data.isEmpty() ;
   }
   public boolean isMember(Object o){
	   for(int i=0 ; i<data.size() ; i++)
		   if(cp.compare(data.get(i),o)==0)
	           return true ;
	   return false ; 
   }
   public void insert(Object o){
	   int ind=0 ;
	   while(ind<data.size()){
		   if(cp.compare(o,data.get(ind))<=0)
			   break ;
		   ind++ ;
	   }
	   data.addAt(ind,o) ;
   }
   public void delete(Object o){
	   data.remove(o) ;
   }
   private Myset CreateSet(Myset a,char c){
	   Myset newset = new Myset(cp) ;
	   int ptr1=0 ;
	   int ptr2=0 ;
	   while(ptr1<this.data.size() && ptr2<a.data.size()){
		   int comp = cp.compare(this.data.get(ptr1),a.data.get(ptr2)) ;
		   if(comp==-1){
			   if(c=='u')
				   newset.data.add(this.data.get(ptr1++)) ;
			   else
				   ptr1++ ;
		   }
		   else if(comp==0){
			   newset.data.add(this.data.get(ptr1++)) ;
			   ptr2++ ;
		   }else{
			   if(c=='u')
				   newset.data.add(a.data.get(ptr2++)) ;
			   else
				   ptr2++ ;
		   }
	   }
	   if(c=='u'){
		  while(ptr1<this.data.size())
			  newset.data.add(this.data.get(ptr1++)) ;		  
		  while(ptr2<a.data.size())
			  newset.data.add(a.data.get(ptr2++));
	   }
	   return newset ;
   }
   public Myset union(Myset a){
	   return CreateSet(a,'u') ;
   }
   public Myset intersection(Myset a){
	   return CreateSet(a,'l') ;
   }
   public Object getMatchingObjectinSet(Object o) throws Exception{
	   return data.getMatchingObject(o) ;
   }
   public void PrintSet(){
	   for(int i=0 ; i<data.size() ; i++){
		   MobilePhone ph = (MobilePhone) data.get(i) ;
		   if(ph.status())
		      System.out.print(ph.number()+((i==data.size()-1) ? "" : ", ")) ;
	   }
	   System.out.println() ;
   }
}
class ArrayList{
	private Object elem[] = new Object[1] ;
	private int capacity=1 ;
	private int size=0 ;
	private Comparator cp ;	   
	ArrayList(Comparator c){
		elem = new Object[1] ;
		capacity=1 ;
		cp = c ;
	}
	synchronized public int size(){
		return size ;
	}
	synchronized public boolean isEmpty(){
		return size==0 ;
	}
	synchronized public void addAt(int i,Object o){
		if(i>size)
			throw new IndexOutOfBoundsException() ;
		else{
			if(i==size)
				add(o) ;
			else{
				if(size==capacity)
					ensureCapacity('u') ;
				for(int j=size ; j>i ; j--)
					elem[j]=elem[j-1] ;
				elem[i]=o ;
				size++ ;
			}
		}
	}
	synchronized public void add(Object o){
		if(size==capacity)
			ensureCapacity('u') ;
		elem[size++]=o ;
	}
	synchronized public void set(int i,Object o){
		if(i>=size)
			throw new IndexOutOfBoundsException() ;
		else
			elem[i]=o ;		
	}
	synchronized public Object remove(int i){
		if(i>=size || this.isEmpty())
			throw new IndexOutOfBoundsException() ;
		Object o = elem[i] ;
		for(int j=i ; j<size-1 ; j++)
			elem[j]=elem[j+1] ; 
		size-- ;
		if(size<=(capacity/2))
			ensureCapacity('l') ;
		return o ;
	}
	synchronized public void remove(Object o){
		for(int i=0 ; i<size ; i++)
			if(cp.compare(o,elem[i])==0)
				this.remove(i) ;		
	}
	synchronized public Object get(int i){
		return elem[i] ;
	}
	synchronized public Object getMatchingObject(Object o) throws Exception{
    	return get(Find(o)) ;
    }
	private void ensureCapacity(char c){
		if(c=='u'){
		   Object temp[] = new Object[2*capacity] ;
		   for(int i=0 ; i<size ; i++)
			  temp[i]=elem[i] ;
		   elem=temp ;
		   capacity*=2 ;
		}
		else{
			Object temp[] = new Object[capacity/2] ;
			for(int i=0 ; i<size ; i++)
			  temp[i]=elem[i] ;
		    elem=temp ;
		    capacity/=2 ;
		}
	}
    private int Find(Object o) throws Exception{
    	for(int i=0 ; i<size ; i++)
    		if(cp.compare(elem[i],o)==0)
    			return i ;
    	throw new Exception() ;
    }
}
class MyCompm extends Comparator{
	public int compare(Object o1,Object o2){
		MobilePhone m1 = (MobilePhone) o1 ;
		MobilePhone m2 = (MobilePhone) o2 ;
		return Integer.compare(m1.number(),m2.number()) ;
	}
}
class MyCompe extends Comparator{
	public int compare(Object o1,Object o2){
		Exchange e1 = (Exchange) o1 ;
		Exchange e2 = (Exchange) o2 ;
		return Integer.compare(e1.id(),e2.id()) ;		
	}
}
abstract class Comparator{
	abstract public int compare(Object o1,Object o2) ;
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