import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Random;

@SuppressWarnings("all")
public class Master extends Thread {

	public Master master;
	Random random = new Random();
	int mapid = 0;
	HashMap<Integer, ObjectInputStream> inputs = new HashMap<Integer, ObjectInputStream>();
	HashMap<Integer, ObjectOutputStream> outputs = new HashMap<Integer, ObjectOutputStream>();

	public static void main(String[] args) {
		Master master = new Master();
		master.openServer();

	}

	public HashMap<Integer, ObjectInputStream> getInputs() {
		synchronized (this.inputs) {
			return this.inputs;
		}
	}

	public HashMap<Integer, ObjectOutputStream> getOutputs() {
		synchronized (this.outputs) {
			return this.outputs;
		}
	}

	private static ArrayList<Integer> ports = new ArrayList<Integer>();

	public ArrayList<Integer> getPorts() {
		return this.ports;
	}

	public void setPorts(int port) {
		this.ports.add(port);
	}

	public Master() {
	}

	public void setMaster(Master master) {
		this.master = master;
	}

	ServerSocket providerSocket;
	Socket connection = null;
	ObjectInputStream dis = null;
	ObjectOutputStream dos = null;

	private ArrayList<Thread> threads = new ArrayList<Thread>();

	void openServer() {
		try {
			providerSocket = new ServerSocket(4321, 10); // Server Port for requests

			while (true) {
				connection = providerSocket.accept();
				System.out.println("Client connect accepted! " + connection);
				Thread t = new ClientServices(connection, this);
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

	}

	public int calculateNodeId(String roomName) {
		int hash = roomName.hashCode();
		// Ensure non-negative value
		hash = Math.abs(hash);
		// Calculate node ID using modulus
		int nodeId = hash % ports.size();
		return nodeId;
	}

	// to store the accommodation to the right worker
	public void sendAccommodationToWorker(Accommodation accommodation) {

		Socket socket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try {
			int nodeId = calculateNodeId(accommodation.getName());
			if (nodeId < ports.size()) {
				int port = ports.get(nodeId);
				System.out.println("Connecting to Worker " + (nodeId + 1) + " at port "
						+ port);
				socket = new Socket("127.0.0.1", port);
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				out.writeUTF("manager");
				out.writeInt(1);
				out.writeObject(accommodation);
				out.writeInt(nodeId + 1);
				out.flush();

				System.out.println(" [!] Accommodation sent to worker [!] ");
				System.out.println(in.readUTF());

			} else {
				System.err.println("No workers available or invalid node ID.");
			}
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
		} // end finally
	}

	public void sendFilterToWorkers(Filters filters, ObjectInputStream input, ObjectOutputStream output) {

		Socket socket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		HashMap<Integer, Filters> filterMap = new HashMap<Integer, Filters>();

		try {
			mapid = random.nextInt(1000);
			

			for (int i = 0; i < ports.size(); i++) {
				socket = new Socket("127.0.0.1", ports.get(i));
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				out.writeUTF("renter");
				out.writeInt(1);
				out.writeInt(ports.size()); // send number of workers

				filterMap.put(mapid, filters);
				out.writeObject(filterMap);
				out.flush();
				
			}

			getInputs().put(mapid, input); // add the input stream to the hashmap for later recovery
			getOutputs().put(mapid, output); // add the output stream to the hashmap for later recovery
			System.out.println(" [!] Filters sent to all workers [!] ");
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
		} // end finally

	}

	public boolean sendReservationRequestToWorker(String roomName, Date startDate, Date endDate) {
		Socket socket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		int numberOfApotyxia = 0;
		int numberOfEpityxia = 0;
		boolean returnValue = false;

		try {
			int nodeId = calculateNodeId(roomName);
			if (nodeId < ports.size()) {
				int port = ports.get(nodeId);
				System.out.println("Accommodation take place in worker with port: " + port);
				System.out.println("Connecting to Worker " + (nodeId + 1) + " at port "
						+ port);
				socket = new Socket("127.0.0.1", port);
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				out.writeUTF("renter");
				out.writeInt(2);
				out.writeUTF(roomName);
				out.writeObject(startDate);
				out.writeObject(endDate);
				out.flush();

				System.out.println(" [!] Accommodation sent to worker with port" + port + "[!]");
				System.out.println(in.readUTF());

				String WorkerResult = in.readUTF();
				if (WorkerResult.equals("Epityxia")) {
					numberOfEpityxia++;
				} else {
					numberOfApotyxia++;
				}
				if (numberOfEpityxia == 1) {
					
					returnValue = true;
				} else {
					
					returnValue = false;
				}

			} else {
				System.err.println("No workers available or invalid node ID.");
			}

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
		return returnValue;

	}


	public boolean sendRateRequestToWorker(String roomName, int stars) {
		Socket socket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		int numberOfApotyxia = 0;
		int numberOfEpityxia = 0;
		boolean returnValue = false;

		try {
			int nodeId = calculateNodeId(roomName);
			if (nodeId < ports.size()) {
				int port = ports.get(nodeId);
				System.out.println("Accommodation take place in worker with port: " + port);
				System.out.println("Connecting to Worker " + (nodeId + 1) + " at port "
						+ port);
				socket = new Socket("127.0.0.1", port);
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				out.writeUTF("renter");
				out.writeInt(3);
				out.writeUTF(roomName);
				out.writeInt(stars);
				out.flush();

				System.out.println(" [!] Accommodation sent to worker with port" + port + "[!]");
				System.out.println(in.readUTF());

				String WorkerResult = in.readUTF();
				if (WorkerResult.equals("Epityxia")) {
					numberOfEpityxia++;
				} else {
					numberOfApotyxia++;
				}
				if (numberOfEpityxia == 1) {
					
					returnValue = true;
				} else {
					
					returnValue = false;
				}

			} else {
				System.err.println("No workers available or invalid node ID.");
			}

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
		return returnValue;

	}

	public void sendShowRequestToWorker(String managerName,ObjectInputStream input, ObjectOutputStream output) {
		Socket socket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		HashMap<Integer, String> managerRequest = new HashMap<Integer, String>();

		try {

			mapid = random.nextInt(1000);
			for(int i=0; i<ports.size(); i++) {
				socket = new Socket("127.0.0.1", ports.get(i));
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				out.writeUTF("manager");
				managerRequest.put(mapid, managerName);
				out.writeInt(2);
				out.writeInt(ports.size());
				out.writeObject(managerRequest);
				out.flush();


			}

			getInputs().put(mapid, input); //add the input stream to the hashmap for later recovery
			getOutputs().put(mapid, output);

			System.out.println("outputs = "+getOutputs() +" of map id = "+mapid);
			
			
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
