import java.io.File;
import java.net.*;	
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class Server implements DNSlookup {

	public Server() {}
	
	public static String flag = null;
	public static String layerFile = null;
	public static int id = -1;
	public static int port = 0;
	public static ArrayList<DNSreply> LUT = new ArrayList<DNSreply>();
	public static ArrayList<DNSreply> cache = new ArrayList<DNSreply>();
 	public static DNSreply resolved;
 	public boolean found = false;
	

	public DNSreply lookup(String hostname) {
	String address = "changeAddress";
	String host = "myhost";
    	String hosts[] = hostname.split("[.]");
    	String h1 = hosts[2];
    	String h2 = hosts[0].concat("."+hosts[1]);
    	DNSreply d;
    	
    	switch(id){
    	case 1: //first tier server
    		if(flag.equals("I")){
    			for(int i=0; i<LUT.size();i++){
        			if(h1.equals(LUT.get(i).hostname)){
        				address = LUT.get(i).address;
        			}
        		}
    		    	d = new DNSreply(address, h1);
    		}
    		else if(flag.equals("R")){
    			found = false;
    			if(!(cache.equals(null))) {
    				for(int i=0; i<cache.size(); i++){
    					if(hostname.equals(cache.get(i).hostname)){
            				address = cache.get(i).address;
            				found = true;
            			} 
    				}
    			}
    			
    			//does not exist in cache
    			if(found==false) { 
    				for(int i=0; i<LUT.size();i++){
            			if(h1.equals(LUT.get(i).hostname)){
            				host = LUT.get(i).address;
            			}
            		}
    				try {
    				    Registry registry = LocateRegistry.getRegistry(host, port);
    				    DNSlookup stub = (DNSlookup) registry.lookup("server2");
    				    address = stub.lookup(hostname).address;
    				    cache.add(new DNSreply(address,hostname));
    				} catch (Exception e) {
    				    System.err.println("Client exception: " + e.toString());
    				    e.printStackTrace();
    				}
    			}
    	    	d = new DNSreply(address, hostname);
    		}
    		else 
    			d = new DNSreply(address, hostname);
    		break;
    		
    	case 2: //second tier server 
    		for(int i=0; i<LUT.size();i++){
    			if(h2.equals(LUT.get(i).hostname)){
    				address = LUT.get(i).address;
    			}
    		}
        	d = new DNSreply(address, hostname);
    		break;
    	default:
    		d = new DNSreply(address, hostname);
    		System.out.println("Value of id is wrong!!!");
    		break;
    	}
    	    	
    	DNSreply d1 = new DNSreply(address, hostname);
    	return d;
	} 
	
    public static void main(String args[]){
    	if(args.length == 4){
    		flag = args[0];
    		layerFile = args[1];
    		id  = Integer.parseInt(args[2]);
    		port = Integer.parseInt(args[3]);
    	}
    	else {
    		System.out.println("usage : Java Server [flag] <Input file> [id] [port]");
    		
    	}//if-else ends here
    	
    	File file = new File(layerFile);
    	createLUT(file);
    	startServer(port);
    	String[] h = {"www.utampa.edu", "www.swlaw.edu", "www.utampa.edu", "www.twu.edu", "www.utampa.edu"};
    	
    	/*//Debug code : 
    	System.out.println("flag = " + flag);
    	System.out.println("layerfile = " + layerFile + " File = " + file);
    	System.out.println("id = " + id);
    	System.out.println("port = " + port);
    	/*for(int i=0; i<LUT.size(); i++) {
    		System.out.println(LUT.get(i).address + " " + LUT.get(i).hostname);
    	}*/
    	
    }//main method ends here
    
    public static void startServer(int port) {
    
    	try {
	          Socket s = new Socket("google.com", 80);
	          System.setProperty("java.rmi.server.hostname",s.getLocalAddress().getHostAddress());
	          s.close();
			  Server obj = new Server();
			  DNSlookup stub = (DNSlookup) UnicastRemoteObject.exportObject(obj, 0);
		
			  // Bind the remote object's stub in the registry
			  Registry registry = LocateRegistry.getRegistry(port);
			   
			  if(id == 1){
				  registry.bind("server1", stub);
			  }
			  else if(id == 2){
				  registry.bind("server2", stub);
			  }
			  System.err.println("Server ready");
		} catch (Exception e) {
		      System.err.println("Error : Server not working: " + e.toString());
			  e.printStackTrace();
		}
    } 
    	
    
    
    public static void createLUT(File f){
    	String tempHost = null;
    	String tempAddr = null;
    	try{
        	Scanner l = new Scanner(f);        	
            while (l.hasNext()) {
                tempHost = l.next();
                tempAddr = l.next();
                LUT.add(new DNSreply(tempAddr, tempHost));
            }
            l.close();
          }catch (Exception e){
              System.err.println("Error: Error in file reading = " + e.getMessage());
          }
    }
}//DNSlookup class ends here
  
