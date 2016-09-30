import java.util.Scanner;

public class RoutingMapTree{
    Exchange root ;
	public RoutingMapTree() {
		root = new Exchange(0) ;
		root.setAsRoot() ;
	}
	public RoutingMapTree(Exchange E){
		root=E ;
	}	
	public boolean ContainsExchange(int id){
		if(root.id()==id)
			return true ;
		for(int i=0 ; i<root.numChild() ; i++)
			if(root.subtree(i).ContainsExchange(id))
				return true ;
		return false ;
	}
	public Exchange getExchange(int id) throws Exception {
	    if(root.id()==id)
	    	return root ;
		for(int i=0 ; i<root.numChild() ; i++)
	    	if(root.subtree(i).ContainsExchange(id))
	    		return root.subtree(i).getExchange(id) ;
		throw new Exception() ;	   
	}
	private void RemoveNumber(MobilePhone a){
		root.residentSet().numbers.delete(a) ;
		for(int i=0 ; i<root.numChild() ; i++)
			if(root.subtree(i).root.residentSet().numbers.isMember(a))
				root.subtree(i).RemoveNumber(a) ;		
	}
	private MobilePhone getPhone(MobilePhone a) throws Exception{
		return (MobilePhone) root.residentSet().numbers.getMatchingObjectinSet(a) ;
	}
	public void switchOn(MobilePhone a,Exchange b)throws Exception{
		if(b.numChild()!=0)
			return ;
		boolean flag=containsNumber(a) ;
		if(flag){			
			a=getPhone(a) ;
			RemoveNumber(a) ;
		}
		else{
			var.Phones.add(a) ;
			a.start() ;
		}
		if(!a.busy())
		   a.requests.enque("switchon") ;
		if(a.location()!=b){
		   a.setAsExchange(b) ;
		   Exchange node=b ;
		   while(true){
			  node.residentSet().numbers.insert(a) ;
			  if(node.isRoot())
				  break ;
			  node=node.parent() ;
		   }
		}		
	}
	public void MovePhone(MobilePhone a,Exchange b) throws Exception{
		switchOn(a,b) ;
	}
	public boolean containsNumber(MobilePhone a){
		return root.residentSet().numbers.isMember(a) ;
	}
	public void switchOff(MobilePhone a)throws Exception{	     
		a = getPhone(a) ;
		if(!a.busy())
		   a.requests.enque("switchoff");  
	}
	private void printNotFoundError(int n,String s,char c){
		System.out.println(s+" : Error - No "+((c=='e')?"Exchange":"Mobile Phone")+" with identifier "+n+" found in the network") ;		 
	}
	public Exchange findPhone(MobilePhone m) throws Exception{
		MobilePhone a = getPhone(m) ;		   
		if(!a.status())
			throw new Exception() ;
		return a.location() ;
	}
	private int Level(Exchange E){
		int level=0 ;
		while(!E.isRoot()){
			level++ ;
			E=E.parent() ;
		}
		return level ;
	}
	public Exchange lowestRouter(Exchange a,Exchange b){
		int levela=Level(a) ;
		int levelb=Level(b) ;
		while(levela!=levelb){
			if(levela>levelb){
				b=b.parent() ;
				levelb++ ;
			}
			else{
				a=a.parent() ;
				levela++ ;
			}
		}
		while(a!=b){
			a=a.parent() ;
			b=b.parent() ;
		}
		return a ;
	}
	public ExchangeList routeCall(MobilePhone a,MobilePhone b){
		Exchange ea = a.location() ;
		Exchange eb = b.location() ;
		Exchange E = lowestRouter(ea,eb) ;
		ExchangeList ans = new ExchangeList() ;
		while(ea!=E){
			ans.add(ea) ; 
			ea=ea.parent() ;
		}
		ans.add(E) ;
		ExchangeList add = new ExchangeList() ;
		while(eb!=E){
			add.add(eb) ;
			eb = eb.parent() ;			
		}
		add.reverse() ;
		ans.merge(add) ;
		return ans ;
	}
	@SuppressWarnings("resource")
	synchronized public void performAction(String actionMessage) {
		Scanner S = new Scanner(actionMessage) ;
		long time = S.nextLong() ;
		try{
		   String command = S.next() ;
		   if(command.compareTo("addExchange")==0){						
			  int a = S.nextInt() ;
			  int b = S.nextInt() ;
			  if(ContainsExchange(a)){
				  Exchange E = getExchange(a) ;
				  Exchange node = new Exchange(b) ;
				  var.Exchanges.add(node) ;
				  node.setParent(E) ;
				  E.AddChild(node) ;
			  }
			  else
				  printNotFoundError(a,actionMessage,'e') ;
		   }
		   else if(command.compareTo("switchOnMobile")==0){
			  int a = S.nextInt() ;
			  int b = S.nextInt() ;
			  if(ContainsExchange(b))
				  switchOn(new MobilePhone(a),getExchange(b)) ;			  
			  else
				  printNotFoundError(b,actionMessage,'e') ;
		   }
		   else if(command.compareTo("switchOffMobile")==0){
			  int a = S.nextInt() ;
			  MobilePhone mp = new MobilePhone(a) ;
			  if(containsNumber(mp))
				  switchOff(mp) ;
			  else
				  printNotFoundError(a,actionMessage,'m') ;
		   }
		   else if(command.compareTo("queryNthChild")==0){
			  int a = S.nextInt() ;
			  int b = S.nextInt() ;
			  if(ContainsExchange(a)){
				  Exchange E = getExchange(a) ;
				  if(b>=E.numChild())
					  System.out.println(actionMessage+" : Error - "+b+"th child of Exchange with identifier "+a+" doesn't exist") ;
				  else
					  System.out.println(time+" "+actionMessage+": "+E.child(b).id()) ;
			  }
			  else
				  printNotFoundError(a,actionMessage,'e') ;
		   }
		   else if(command.compareTo("queryMobilePhoneSet")==0){
			  int a = S.nextInt() ;
			  if(ContainsExchange(a)){
				  Exchange E = getExchange(a) ;
				  System.out.print(actionMessage+": ") ;
				  E.residentSet().numbers.PrintSet() ;
			  }
			  else
				  printNotFoundError(a,actionMessage,'e') ;
		   }
		   else if(command.compareTo("findPhone")==0){
			   int a = S.nextInt() ;
			   MobilePhone mp = new MobilePhone(a) ;
			   if(containsNumber(mp))
				  System.out.println(actionMessage+" : "+findPhone(mp).id()) ;
			   else
				   printNotFoundError(a,actionMessage,'m') ;
		   }
		   else if(command.compareTo("lowestRouter")==0){
			   int a = S.nextInt() ;
			   int b = S.nextInt() ;
			   if(ContainsExchange(a))
				   if(ContainsExchange(b))
					   System.out.println(actionMessage+" : "+lowestRouter(getExchange(a),getExchange(b)).id());				   
				   else
					   printNotFoundError(b,actionMessage,'e') ;			   
			   else
				   printNotFoundError(a,actionMessage,'e') ;
		   }
		   else if(command.compareTo("CallAttempt")==0){
			   int a = S.nextInt() ;
			   int b = S.nextInt() ;
			   MobilePhone mpa = new MobilePhone(a) ;
			   MobilePhone mpb = new MobilePhone(b) ;
			   if(containsNumber(mpa)){
				   if(containsNumber(mpb)){
					   mpa = getPhone(mpa) ;
					   mpb = getPhone(mpb) ;
					   if(mpa.status() && mpb.status() && !mpa.busy() && !mpb.busy()){
						   int Calltime = S.nextInt() ;
						   mpb.requests.enque("CallAccepted "+Calltime) ;
						   mpa.requests.enque("Called "+Calltime) ;
						   mpa.setCPhone(mpb) ;
						   mpb.setCPhone(mpa) ;
						   /*ExchangeList li = routeCall(mpa,mpb) ;
						   System.out.print(time+" "+a+" Connects to "+b+" thorugh route  : ") ;
						   li.PrintList() ; 
						   System.out.println() ;*/
					   }
					   else{
						   if(!mpa.status())
					          System.out.println(actionMessage+" :Error - Mobile phone with identifier "+a+" is currently switched off") ;
						   else if(!mpb.status())
							  System.out.println(actionMessage+" :Error - Mobile phone with identifier "+b+" is currently switched off") ;
						   else 
							  System.out.println(actionMessage+" : MobilePhone with identifier "+b+" was busy") ;
					   }
				   }
				   else
					   printNotFoundError(b,actionMessage,'m') ;
			   }
			   else
				   printNotFoundError(a,actionMessage,'m') ;
		   }
		   else if(command.compareTo("movePhone")==0){
			   int a = S.nextInt() ;
			   int b = S.nextInt() ;
			   MobilePhone mp = new MobilePhone(a) ;
			   if(containsNumber(mp))
				  if(ContainsExchange(b)){
					  MovePhone(mp,getExchange(b)) ;					  
				  }				  
				  else
					  printNotFoundError(b,actionMessage,'e') ; 			   
			   else
				   printNotFoundError(a,actionMessage,'m') ;
		   }else{
			   throw new Exception() ;
		   }
		   
		}
		catch(Exception e){
		    e.printStackTrace() ;
		}
		S.close();
	}
}