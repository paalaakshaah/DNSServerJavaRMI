import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
	
	public static ArrayList<String> hostList = new ArrayList<String>();
	public static ArrayList<DNSreply> result = new ArrayList<DNSreply>();
	public static int port;
	public static String serverIP;
	public static String id = "unknown";
	public static String out = "Test_Output.txt";
    private Client() {}

    public static void main(String[] args) {
	
	System.out.println("enter the name of output file : ");
    	Scanner in = new Scanner(System.in);
	out = in.next();
	in.close();
	String host = (args.length < 1) ? null : args[0];
	int port = Integer.valueOf(args[1]);
    	File file  = new File(args[2]); 
    	readFile(file);
    	DNSlookup stub;
	try {
	    Registry registry = LocateRegistry.getRegistry(host, port);
	        
	    for(int i=0;i<hostList.size(); i++){
		stub = (DNSlookup) registry.lookup("server1");
	    	result.add(stub.lookup(hostList.get(i)));
	    	if(result.get(i).hostname.equals(hostList.get(i))){ //its recursive	    		
			continue;
	    	}else { //its iterative
		    	stub = (DNSlookup) registry.lookup("server2");
		    	result.remove(i);
		    	result.add(stub.lookup(hostList.get(i)));
	    	}
	    	
	    	
	    }
	    
	} catch (Exception e) {
	    System.err.println("Client exception: " + e.toString());
	    e.printStackTrace();
	}
    	File wfile = new File(out);
    	writeFile(wfile);
    }//main method ends here


	public static void readFile(File f){
		try{
	    	Scanner h = new Scanner(f);        	
	        while (h.hasNext()) {
	            hostList.add(h.next());
	        }
	        h.close();
	      }catch (Exception e){
	          System.err.println("Error: Error in file reading = " + e.getMessage());
	      }
	}
	
	public static void writeFile(File f) {
		try{
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int j = 0; j<result.size(); j++){
				bw.write(result.get(j).hostname);
				bw.write('\t');
				if(result.get(j).address.equals("changeAddress")){
					bw.write("Not Found");
				}
				else bw.write(result.get(j).address);
				bw.write('\n');
			}
			bw.close();
		}catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}//Client class ends here
