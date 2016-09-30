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
		if(containsNumber(a)){
			a=getPhone(a) ;
			RemoveNumber(a) ;
		}
		a.SwitchOn() ; 
		a.setAsExchange(b) ;
		Exchange node=b ;
		while(true){
			node.residentSet().numbers.insert(a) ;
			if(node.isRoot())
				break ;
			node=node.parent() ;
		}		
	}
	public boolean containsNumber(MobilePhone a){
		return root.residentSet().numbers.isMember(a) ;
	}
	public void switchOff(MobilePhone a)throws Exception{	     
		a = getPhone(a) ;
		a.SwitchOff() ; 
	}
	private void printNotFoundError(int n,String s,char c){
		System.out.println(s+" : Error - No "+((c=='e')?"Exchange":"Mobile Phone")+" with identifier "+n+" found in the network") ;		 
	}
	public void performAction(String actionMessage) {
		Scanner S = new Scanner(actionMessage) ;	
		try{
		   String command = S.next() ;
		   if(command.compareTo("addExchange")==0){						
			  int a = S.nextInt() ;
			  int b = S.nextInt() ;
			  if(ContainsExchange(a)){
				  Exchange E = getExchange(a) ;
				  Exchange node = new Exchange(b) ;
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
					  System.out.println(actionMessage+": "+E.child(b).id()) ;
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
		}
		catch(Exception e){
		    System.out.println(actionMessage+" : There is a mismatch in input format") ;
		}
		S.close();
	}
}
