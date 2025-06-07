import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("all")
public class Worker extends Thread {
    private int workerId;
    private int port;
    private ConcurrentHashMap<String, Accommodation> rooms = new ConcurrentHashMap<>();
    private List<Accommodation> AcList = new ArrayList<Accommodation>();
    Worker worker;
    Filters filters;
    int number_of_workers = 0;
    HashMap<Integer, Filters> FilterMap;

    public static void main(String[] args) {

        Random random = new Random();
        int port = 3100 + random.nextInt(1000); // Generate a random port number between 5000 and 5999
        String ip = "127.0.0.1";
        Worker w = new Worker(port);
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket("127.0.0.1", 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("worker"); // Type of request
            out.writeInt(port); // Sent port to Master
            out.flush();
            System.out.println(in.readUTF());

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } // end finally

        w.openServer();

    }

    public int getNumberOfWorkers() {
        return this.number_of_workers;
    }

    public void setNumberOfWorkers(int num) {
        this.number_of_workers = num;
    }

    public int getPort() {
        return this.port;
    }

    public int getWorkerId() {
        return this.workerId;
    }

    public Filters getfilters() {
        return this.filters;
    }

    public void setfilters(Filters filters) {
        this.filters = filters;
    }

    public void setMap(HashMap<Integer, Filters> f) {
        this.FilterMap = f;

    }

    public HashMap<Integer, Filters> getMap() {
        return this.FilterMap;
    }

    public Worker(int port) {
        this.port = port;
    }

    public void setWorker(Worker w) {
        this.worker = w;
    }

    public void addAc(Accommodation a) {
        synchronized (AcList) {
            AcList.add(a);
            System.out.println("Accomodation " + a.getName() + " has been added!!!");
        }

    }

    public Filters getFilter() {
        return this.filters;
    }

    public List<Accommodation> getList() {
        synchronized (AcList) {
            return this.AcList;
        }
    }

    public List<Accommodation> FilteringAccommodations() { // Ginetai etsi gia na ftiaxnetai mono topika
        return new ArrayList<Accommodation>(); // diladi mono otan theloume na filtraroume

    }

    ServerSocket serverSocket;
    Socket connection = null;
    ObjectInputStream dis = null;
    ObjectOutputStream dos = null;

    private ArrayList<Thread> threads = new ArrayList<Thread>();

    void openServer() {
        try {

            serverSocket = new ServerSocket(this.port, 10); // Server Port for requests

            while (true) {
                connection = serverSocket.accept();
                System.out.println("Connection accepted! " + connection);
                Thread t = new WorkerServices(connection, this);
                threads.add(t);
                t.start();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                serverSocket.close();
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

    public void SendFilterResultsToReducer(HashMap<Integer, List<Accommodation>> result, int number_of_workers) {
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {

            System.out.println("Connecting to Reducer to send them the filters ");

            socket = new Socket("127.0.0.1", 35671); // reducer port
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeUTF("renter"); // Type of request
            out.writeInt(number_of_workers);
            out.writeObject(result); // list of Accommodations
            out.flush();

            System.out.println(" [!] Filtered accommodation sent to reducer [!] ");
            System.out.println(in.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                if (socket != null) {
                    socket.close();
                }

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void SendShowResultsToReducer(HashMap<Integer,List<Accommodation>> result, int number_of_workers) {
        Socket socket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
        int mapId = 0;

		try {

			System.out.println("Connecting to Workers to send them the results ");

				socket = new Socket("127.0.0.1", 35671);
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
                for (Map.Entry<Integer,List<Accommodation>> entry : result.entrySet()) {
                    mapId = entry.getKey();
                }
                
				out.writeUTF("manager");
                out.writeInt(number_of_workers);
				out.writeObject(result);
				out.flush();

			System.out.println(" [!] Results sent to reducer [!] ");
			System.out.println(in.readUTF());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {

				if (socket != null) {
					socket.close();
				}

			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
    }

}
