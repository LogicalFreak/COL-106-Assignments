public class Myset{   
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
class MobilePhone{
	private int id ;
	private boolean status ;
	private Exchange B ; 
	MobilePhone(int Number){
		id=Number ;
	}
	public int number(){
		return id ;
	}
	public boolean status(){
		return status ;
	}
	public void SwitchOn(){
		status=true ;
	}
	public void SwitchOff(){
		status=false ;
	}
	public Exchange location(){
		return B ;
	} 
    public void setAsExchange(Exchange E){
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
	public int size(){
		return size ;
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
	public int size(){
		return size ;
	}
	public boolean isEmpty(){
		return size==0 ;
	}
	public void addAt(int i,Object o){
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
	public void add(Object o){
		if(size==capacity)
			ensureCapacity('u') ;
		elem[size++]=o ;
	}
	public void set(int i,Object o){
		if(i>=size)
			throw new IndexOutOfBoundsException() ;
		else
			elem[i]=o ;		
	}
	public Object remove(int i){
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
	public void remove(Object o){
		for(int i=0 ; i<size ; i++)
			if(cp.compare(o,elem[i])==0)
				this.remove(i) ;		
	}
	public Object get(int i){
		return elem[i] ;
	}
    public Object getMatchingObject(Object o) throws Exception{
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
abstract class Comparator{
	abstract public int compare(Object o1,Object o2) ;
}