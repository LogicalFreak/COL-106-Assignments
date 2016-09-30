import java.io.PrintStream;
/*
 * Status Approximate Algo Written,Code's all working and everything's fine
 */
public class Exchange{
	static List Contenders = new List() ;
	static long Profit=0 ;
	static PrintStream prt ;
	private static long abd(long a,long b){
		long s = a-b ;
		return s>0 ? s : -s ;
	}
	public static boolean Perform_transaction(MyStock S){
	    if((System.currentTimeMillis()-Time.getStartTime())>=(S.t0+S.texp)*1000)
	    	return false ;
		char type = S.type ;
		long Qty = S.PresentQty ;
	    List sorb = type=='b' ? test.Sell : test.Buy ;
	    for(int i=0 ; i<sorb.upp_lim() ; i++){
	       	MyStock sk = sorb.get(i) ;
	       	if(sk==null || (System.currentTimeMillis()-Time.getStartTime())>=(sk.t0+sk.texp)*1000)
	       		continue ;
	       	if(S.stk.compareTo(sk.stk)==0)
	       		Contenders.add(sk) ;
	    }
	    if(!S.partial){
	      	int ind=-1 ;
	       	long price_lim = ((type=='s') ? -1 : Long.MAX_VALUE) ;
	       	int lim = Contenders.upp_lim() ;
	       	for(int i=0 ; i<lim ; i++){
	       		MyStock sk = Contenders.get(i) ;
	       		if(sk.partial){
	       			if(sk.PresentQty>=Qty && ((type=='s') ? (sk.price>price_lim) : (sk.price<price_lim))){
	       				ind=i ;
	       				price_lim=sk.price ;
	       			}	    	    			
	       		}
	       		else if(sk.PresentQty==Qty && ((type=='s') ? (sk.price>price_lim) : (sk.price<price_lim))){
	       			ind=i ;
	       			price_lim=sk.price ;
	       		}   	    		
	       	}
	       	if((type=='s')?(price_lim<S.price):(price_lim>S.price)){
	       		Contenders.clear();
	       		return false ;
	       	}
	       	else{
	       		long tm = System.currentTimeMillis()-Time.getStartTime() ;
	       		tm+=500 ;
	       		tm/=1000 ;
	       		prt.println("T "+tm+" "+Qty+" "+test.formatStock(Contenders.get(ind))) ;
	       		prt.println("T "+tm+" "+Qty+" "+test.formatStock(S)) ;
	       		Profit+=Qty*abd(S.price,Contenders.get(ind).price) ;
	       		S.PresentQty-=Qty ;
	       		Contenders.get(ind).PresentQty-=Qty ;
	       		Contenders.clear() ;
	       		return true ;
	       	}
	    }
	    else{
	    	long mm=0 ;
	    	while(S.PresentQty>0){
	     		int ind=-1 ;
	     		long price_lim = ((type=='s') ? -1 : Long.MAX_VALUE) ;		       	
		       	int lim = Contenders.upp_lim() ;
		       	for(int i=0 ; i<lim ; i++){
		       		if(Contenders.get(i)==null)
		       			continue ;
		       		MyStock sk = Contenders.get(i) ;
		       		if(sk.partial && ((type=='s') ? (sk.price>price_lim) : (sk.price<price_lim)) && sk.PresentQty>0){
		       			ind=i ;
		       		    price_lim = sk.price ;
		       		}   	 	
		       		else if(sk.PresentQty<=Qty && ((type=='s') ? (sk.price>price_lim) : (sk.price<price_lim)) && sk.PresentQty>0){
		       			ind=i ;
		       		    price_lim = sk.price ;
		       		}
		       	}
		       	if((type=='s')?(price_lim<S.price):(price_lim>S.price)){
		       		long tm = System.currentTimeMillis()-Time.getStartTime() ;
		       		tm = (tm+500)/1000 ;
		       		if(S.PresentQty<Qty){
		       		   Profit+=abd(mm,(Qty-S.PresentQty)*S.price) ;
			       	   prt.println("T "+tm+" "+(Qty-S.PresentQty)+" "+test.formatStock(S)) ;			       	   
			       	}
			       	Contenders.clear() ;
			       	return S.PresentQty==0 ;		       		
		       	}
		       	else{
		       		long Qt = Long.min(S.PresentQty,Contenders.get(ind).PresentQty) ;
		       		long tm = System.currentTimeMillis()-Time.getStartTime() ;
		       		tm+=500 ;
		       		tm/=1000 ;
		       		mm+=Contenders.get(ind).price*Qt ;
		       		prt.println("T "+tm+" "+Qt+" "+test.formatStock(Contenders.get(ind))) ;
		       		S.PresentQty-=Qt ;
		       		Contenders.get(ind).PresentQty-=Qt ;		       		
		       	}	       	
	     	}	 
	     	long tm = System.currentTimeMillis()-Time.getStartTime() ;
	     	tm+=500 ;
	     	tm/=1000 ;
	     	Profit+=abd(mm,(Qty-S.PresentQty)*S.price) ;	       	   
	       	prt.println("T "+tm+" "+(Qty-S.PresentQty)+" "+test.formatStock(S)) ;
	       	Contenders.clear() ;
	       	return S.PresentQty==0 ;
	    }    
	}
}
