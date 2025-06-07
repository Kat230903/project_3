import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.util.*;


@SuppressWarnings("all")
public class Reducer {

    public static void main(String[] args) {
		Reducer reducer = new Reducer();
		reducer.openServer();
	}

    ServerSocket providerSocket;
	Socket connection = null;
	ObjectInputStream dis = null;
	ObjectOutputStream dos = null;
	List<HashMap<Integer, List<Accommodation>>> reducer_list= new ArrayList<>();

    HashMap<Integer,List<HashMap<Integer,List<Accommodation>>>> list = new HashMap<Integer,List<HashMap<Integer,List<Accommodation>>>>();
    HashMap<Integer,List<HashMap<Integer,List<Accommodation>>>> list_for_show = new HashMap<Integer,List<HashMap<Integer,List<Accommodation>>>>();
    List<HashMap<Integer, List<Accommodation>>> reducer_list_for_show= new ArrayList<>();
    HashMap<Integer,List<Accommodation>> managerMap = new HashMap<Integer,List<Accommodation>>();
    HashMap<Integer,Integer> managercounter = new HashMap<Integer,Integer>();
    
    HashMap<Integer,List<Accommodation>> renterMap = new HashMap<Integer,List<Accommodation>>();
    HashMap<Integer,Integer> rentercounter = new HashMap<Integer,Integer>();

	private ArrayList<Thread> threads = new ArrayList<Thread>();

	void openServer() {
		try {
            
            providerSocket = new ServerSocket(35671, 10); // Server Port for requests

            while (true) {
                connection = providerSocket.accept();
                System.out.println("Client connect accepted! " + connection);
                Thread t = new ReducerServices(connection, this);
                threads.add(t);
                t.start();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

    public void add2(HashMap<Integer, List<Accommodation>> reducer_list){
        synchronized(this.reducer_list_for_show) {
		    this.reducer_list_for_show.add(reducer_list);
        }
	}

    public void setClear2(){
        this.reducer_list_for_show.clear();
    }

	public List<HashMap<Integer, List<Accommodation>>> getReducerList2(){
        synchronized(this.reducer_list_for_show) {
		    return this.reducer_list_for_show;
        }
	}
	
	public void add(HashMap<Integer, List<Accommodation>> reducer_list){
        synchronized(this.reducer_list) {
            this.reducer_list.add(reducer_list);
        }
	}

    public void setClear(){
        this.reducer_list.clear();
    }

	public List<HashMap<Integer, List<Accommodation>>> getReducerList(){
		synchronized(this.reducer_list) {
            return this.reducer_list;
        } 
	}

    public List<HashMap<Integer,List<Accommodation>>> getSublist(int mapid) {
        synchronized(this.list) {
            return this.list.get(mapid);
        }
    }
    public HashMap<Integer,List<HashMap<Integer,List<Accommodation>>>> getList() {
        synchronized(this.list) {
            return this.list;
        }
    }

    public HashMap<Integer,List<HashMap<Integer,List<Accommodation>>>> getList2() {
        synchronized(this.list) {
            return this.list_for_show;
        }
    }

    public List<HashMap<Integer,List<Accommodation>>> getSublist2(int mapid) {
        synchronized(this.list_for_show) {
            return this.list_for_show.get(mapid);
        }
    }

    public HashMap<Integer,List<Accommodation>> getManagerMap() {
        return this.managerMap;
    }

    public HashMap<Integer,List<Accommodation>> getRenterMap() {
        return this.renterMap;
    }

    public HashMap<Integer,Integer> getManagerCounters() {
        return this.managercounter;
    }

    public HashMap<Integer,Integer> getRenterCounters() {
        return this.rentercounter;
    }
	
	
	public void SendFilterResultsToMaster(HashMap<Integer,List<Accommodation>> finalMap) {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            
            requestSocket = new Socket("127.0.0.1", 4321); // Stelnei request gia add se auto to Port
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
            out.writeUTF("Renter Result");
            out.writeInt(1);
            out.writeObject(finalMap);
            out.flush();

            System.out.println(in.readUTF());
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                out.close(); // Close the connection
                in.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void SendShowResultsToMaster(HashMap<Integer,List<Accommodation>> finalMap) {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            
            requestSocket = new Socket("127.0.0.1", 4321); // Stelnei request gia add se auto to Port
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
            out.writeUTF("manager Result");
            out.writeInt(2);
            out.writeObject(finalMap);
            out.flush();

            System.out.println(in.readUTF());
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                out.close(); // Close the connection
                in.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
