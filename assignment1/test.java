import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;
/* CheckList 
 * Written all the code for the running of the three threads, namely Order,Exchange,CleanUp.
 * All done.
*/
public class test implements Runnable{
	static MyQueue<MyStock> orderq = new MyQueue<MyStock>() ;
	static MyQueue<String> orders = new MyQueue<String>() ;	
	static List Buy = new List() ;
	static List Sell = new List() ;
	static private boolean order_terminated=false ;
	static private boolean exchange_terminated = false ;
	synchronized static void set_terminated_flag(boolean b,char c){
		switch(c){
		case 'o' : order_terminated=b ;
		break ;
		case 'e' : exchange_terminated=b ;
		break ;
		}
	}
	synchronized static boolean get_terminated_flag(char c){
		switch(c){
		case 'o' : return order_terminated ;
		case 'e' : return exchange_terminated ;
		}
		return false ;
	}	
	synchronized static String formatStock(MyStock st){
		String s = "" ;
		s+=(st.t0+" "+st.name+" "+st.texp+" ") ;
		s+=(((st.type=='b')?"buy" : "sell")+" ") ;
		s+=(st.InitialQty+" "+st.stk+" "+st.price+" ") ;
		s+=(st.partial ? "T" : "F") ;
		return s ;   
	}
	char id ;
	test(String s){
		id = s.charAt(0) ;
	}
	public void run(){
		// todo : To fill the running code for the Exchange thread.
		switch(id){
		   case 'O' :{
			   try{
			       FileOutputStream fout = new FileOutputStream("order.out") ;
			       PrintStream op = new PrintStream(fout) ;
			       Thread.sleep(100) ;
			       while(!orders.isEmpty()){
					   String s = orders.deque() ;
					   String delims = "[ 	]+" ;
					   String tokens[] = s.split(delims) ;
					   MyStock st = new MyStock() ;
					   try{
						   st.t0 = Integer.parseInt(tokens[0]) ;
						   if(st.t0<0)
							   throw new Exception() ;
						   if(tokens[1].compareTo("?")==0)
							   throw new InputMismatchException() ;
						   st.name = tokens[1] ;
						   st.texp = Integer.parseInt(tokens[2]) ;
						   if(st.texp<=0)
							   throw new Exception() ;
						   tokens[3] = tokens[3].toLowerCase() ;
						   if(tokens[3].compareTo("buy")==0 || tokens[3].compareTo("sell")==0)
							   st.type = tokens[3].charAt(0) ;						   
						   else
							   throw new InputMismatchException() ;
						   st.InitialQty = Integer.parseInt(tokens[4]) ;
						   if(st.InitialQty<=0)
							   throw new NumberFormatException() ;						   
						   st.PresentQty = st.InitialQty ;
						   st.stk = tokens[5] ;
						   st.price = Integer.parseInt(tokens[6]) ;
						   if(st.price<0)
							   throw new NumberFormatException() ;						   
						   char par = tokens[7].charAt(0) ;
						   if(par=='T' || par=='F')
						       st.partial = par=='T' ;
						   else
							   throw new InputMismatchException() ;
						   if((System.currentTimeMillis()-Time.getStartTime())>=1000*(st.t0+st.texp))
							   continue ;
						   while((System.currentTimeMillis()-Time.getStartTime())<1000*(st.t0))
							   Thread.sleep(10) ;	
						   long tim = System.currentTimeMillis()-Time.getStartTime() ;
						   tim = (tim+500)/1000 ;
						   op.println(tim+" "+formatStock(st)) ;
						   orderq.enque(st) ;
					   }
					   catch(Exception e){
						   op.println("EXCEPTION "+s) ;					   
					   }
				   }
			       op.close();
			       set_terminated_flag(true,'o') ;
			   }
			   catch(Exception e){
				   e.printStackTrace() ;
			   }			   
		   } 
		   break ;
		   case 'E' : {
			   try{
			       FileOutputStream fout = new FileOutputStream("exchange.out") ;
			       PrintStream op = new PrintStream(fout) ;
			       Exchange.prt=op ;
				   while(!(get_terminated_flag('o') && orderq.isEmpty())){
			    	   Thread.sleep(10) ;
			    	   if(!orderq.isEmpty()){
			    	      MyStock dequed_element = orderq.deque() ;
			    	      boolean isSatisfied = Exchange.Perform_transaction(dequed_element);
			    	      long tim = System.currentTimeMillis()-Time.getStartTime() ;
			    	      tim = (tim+500)/1000 ;
			    	      char typ = dequed_element.type ;
			    	      if(!isSatisfied){
			    	    	  op.println(((typ=='b') ? "P" : "S")+" "+tim+" "+formatStock(dequed_element)) ;			    	      
			    	          if(typ=='b')
			    	    	     Buy.add(dequed_element) ;
			    	          else
			    	    	     Sell.add(dequed_element) ;
			    	      }			    	            
 			    	   }				   
			       }
				   op.println(Exchange.Profit) ;
				   op.close() ; 
			       set_terminated_flag(true,'e') ;
			   }
			   catch(Exception e){
				   e.printStackTrace() ;
			   }			   
		   }
		   break ;
		   case 'C' : {
			   try{
			       FileOutputStream fout = new FileOutputStream("cleanup.out") ;
			       PrintStream op = new PrintStream(fout) ;
			       while(!(Buy.isEmpty() && Sell.isEmpty() && get_terminated_flag('e'))){
			    	   Thread.sleep(10) ;		    		   
			    	   for(int i=0 ; i<Buy.upp_lim() ; i++){
			    		   if(Buy.get(i)==null)
			    			   continue ;
			    		   MyStock stk = Buy.get(i) ;
			    		   long tex = (stk.t0+stk.texp) ;
			    		   long tm = System.currentTimeMillis()-Time.getStartTime() ;
			    		   if(tm>(1000*tex) || stk.PresentQty==0){
			    			   tm = (tm+500)/1000 ;
			    			   op.println(tm+" "+formatStock(stk)) ;
			    			   Buy.remove(i) ;			    			   
			    			   break ;
			    		   }			    		   
			    	   }
			    	   for(int i=0 ; i<Sell.upp_lim() ; i++){
			    		   if(Sell.get(i)==null)
			    			   continue ;			    		   
			    		   MyStock stk = Sell.get(i) ;
			    		   long tex = (stk.t0+stk.texp) ;
			    		   long tm = System.currentTimeMillis()-Time.getStartTime() ;
			    		   if(tm>(1000*tex) || stk.PresentQty==0){
			    			   tm = (tm+500)/1000 ;
			    			   op.println(tm+" "+formatStock(stk)) ;			    			   
			    			   Sell.remove(i) ;			    			   
			    			   break ;
			    		   }			    		   
			    	   }
			       }
			       op.close() ;
			   }
			   catch(Exception e){
				   e.printStackTrace() ;
			   }
		   }
		   break ;
		}
	}//Thread wrapper class
}
