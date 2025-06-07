import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.*;

@SuppressWarnings("all")
public class ClientServices extends Thread implements Serializable {
	ObjectInputStream in;
	ObjectOutputStream out;
	Socket connection;
	String profile;
	int typeOfRequest;
	Master master;

	public ClientServices(Socket connection, Master master) {
		try {
			this.connection = connection;
			this.out = new ObjectOutputStream(connection.getOutputStream());
			this.in = new ObjectInputStream(connection.getInputStream());
			this.master = master;
			this.profile = in.readUTF();
			this.typeOfRequest = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		if (profile.equalsIgnoreCase("manager")) {
			if (typeOfRequest == 1) {
				Add();
			} else if (typeOfRequest == 2) {
				Show();
			}
		} else if (profile.equalsIgnoreCase("renter")) {
			if (typeOfRequest == 1) {
				Filter();
			} else if (typeOfRequest == 2) {
				Reservation();
			} else if (typeOfRequest == 3) {
				Rate();
			}
		} else if (profile.equalsIgnoreCase("worker")) {
			AddList();
		} else if (profile.equalsIgnoreCase("renter result")) {
			GetFinalFilterResults();
		}else if(profile.equalsIgnoreCase("manager Result")){

			GetFinalShowResults();
		}

	}

	

	Accommodation t;

	public void setAccommodation(Accommodation ac) {
		this.t = ac;
	}

	public Accommodation getAccommodation() {
		return this.t;
	}

	public void Add() {
		try {
			JSONObject j;
			String received;
			try {
				Object object = in.readObject();
				Class<?> objectClass = object.getClass();

				j = (JSONObject) object; // Read the JSON file that Manager sends

				System.out.println("Add the Accommodation : " + (String) j.get("roomName"));

				/*
				 * Assigns the json elements to variables in
				 * order to create an Accommodation object with them
				 */
				String rName = (String) j.get("roomName");
				int nOfPersons = (Integer) j.get("noOfPersons");
				String area = (String) j.get("area");
				int stars = (Integer) j.get("stars");
				int reviews = (Integer) j.get("noOfReviews");
				int price = (Integer) j.get("price");
				String manager = (String) j.get("managerName");

				/*
				 * we have 3 months for each of them we have a beginning and an end,
				 * it reads them from the file, assigns them to variables and will
				 * change unavailable dates to available dates for each month
				 */
				Date StartIounios = (Date) j.get("StartDateIounios");
				Date EndIounios = (Date) j.get("EndDateIounios");
				Date StartIoulios = (Date) j.get("StartDateIoulios");
				Date EndIoulios = (Date) j.get("EndDateIoulios");
				Date startAugoustos = (Date) j.get("StartDateAugoustos");
				Date EndAugoustos = (Date) j.get("EndDateAugoustos");

				// Create the Accommodation object with the values from json
				Accommodation newAcmdt = new Accommodation(rName, nOfPersons, area, stars, reviews, price);
				newAcmdt.setManager(manager);
				t = newAcmdt;
				this.setAccommodation(newAcmdt);
				newAcmdt.showMore();

				/*
				 * 
				 * Pairnoume ta StartDate kai ta EndDate kathe mhna apo tous 3
				 * Gia kathe mhna allazoume tis diathesimes imerominies
				 * 0 --> Iounios
				 * 1 --> Ioulios
				 * 2 --> Augoustos
				 * Theloume na paroume se Integer thn hmera enos mhna wste na thn
				 * xrhsimopoihsoume se for loop(Xrhsimopoioume Calendar)
				 * 
				 */

				// Gia Iounio
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(StartIounios); // Gia Start Iouniou
				int IouniosStart = calendar.get(Calendar.DAY_OF_MONTH);
				calendar.setTime(EndIounios); // Gia End Iouniou
				int IouniosEnd = calendar.get(Calendar.DAY_OF_MONTH);
				for (int i = IouniosStart; i <= IouniosEnd; i++) {
					newAcmdt.setDiathesimi(0, i);
				}

				// Gia Ioulio
				calendar.setTime(StartIoulios); // Gia Start Iouliou
				int IouliosStart = calendar.get(Calendar.DAY_OF_MONTH);
				calendar.setTime(EndIoulios); // Gia End Iouliou
				int IouliosEnd = calendar.get(Calendar.DAY_OF_MONTH);
				for (int i = IouliosStart; i <= IouliosEnd; i++) {
					newAcmdt.setDiathesimi(1, i);
				}

				// Gia Augousto
				calendar.setTime(startAugoustos); // Gia Start Augoustou
				int AugoustosStart = calendar.get(Calendar.DAY_OF_MONTH);
				calendar.setTime(EndAugoustos); // Gia End Augoustou
				int AugoustosEnd = calendar.get(Calendar.DAY_OF_MONTH);
				for (int i = AugoustosStart; i <= AugoustosEnd; i++) {
					newAcmdt.setDiathesimi(2, i);
				}

				newAcmdt.displayCalendar(0);
				newAcmdt.displayCalendar(1);
				newAcmdt.displayCalendar(2);

				out.writeUTF(" [!] accommodation acquired succesfully [!] ");
				out.flush();

				master.setMaster(master);
				master.sendAccommodationToWorker(newAcmdt);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void Show() {
		String nameOfManager;
			try{
				nameOfManager = in.readUTF(); 

				out.writeUTF(" [!] request acquired succesfully [!] ");
				out.flush();

				master.setMaster(master);
				master.sendShowRequestToWorker(nameOfManager,in,out);

			

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}


	public void Filter() {
		try {
			Filters filters;
			String received;
			try {

				Object object = in.readObject();

				filters = (Filters) object; // Read the JSON file that Manager sends

				out.writeUTF(" [!] Filters acquired succesfully from Master[!] ");
				out.flush();

				master.setMaster(master);
				master.sendFilterToWorkers(filters, in, out);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Reservation() {
		String roomName;
		Date startDate = null;
		Date endDate = null;
		boolean value = false;

		try {
			try {
				roomName = in.readUTF();
				Object object1 = in.readObject(); // StartDate
				Object object2 = in.readObject(); // End Date
				startDate = (Date) object1;
				endDate = (Date) object2;

				out.writeUTF(" [!] Master received request for reservation [!] ");
				out.flush();

				master.setMaster(master);
				value = master.sendReservationRequestToWorker(roomName, startDate, endDate);
				if (value == true) {
					out.writeUTF("Epityxia");
					out.flush();
				} else {
					out.writeUTF("Apotyxia");
					out.flush();
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} // keep the connection open for results
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void Rate() {
		String roomName;
		int stars;
		boolean value = false;

		try {
			roomName = in.readUTF();
			stars = in.readInt();

			out.writeUTF(" [!] Master received request for Rating [!] ");
			out.flush();

			master.setMaster(master);
			value = master.sendRateRequestToWorker(roomName,stars);
			if (value == true) {
				out.writeUTF("Epityxia");
				out.flush();
			} else {
				out.writeUTF("Apotyxia");
				out.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} // keep the connection open for results
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void AddList() {
		try {
			int port;
			port = typeOfRequest;
			master.setPorts(port);

			out.writeUTF(" [!] Port of worker received from Master [!] " + port);
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void GetFinalFilterResults() {
		HashMap<Integer, List<Accommodation>> results;
		List<Accommodation> rooms = new ArrayList<Accommodation>();
		int mapId = 0;
		Integer key = 0;
		List<Accommodation> value = null;
		try {
			Object obj = in.readObject();
			results = (HashMap<Integer, List<Accommodation>>) obj;
			for (Map.Entry<Integer, List<Accommodation>> entry : results.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
			}

			ObjectOutputStream outrenter = master.getOutputs().get(key);

			out.writeUTF(" [!] Filtered accommodations acquired from reducer [!] ");
			out.flush();

			if (outrenter != null) {
				outrenter.writeObject(value);
				outrenter.flush();
			}
			try {
				outrenter.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}


		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void GetFinalShowResults() {
		HashMap<Integer,List<Accommodation>> results;
		List<Accommodation> rooms = new ArrayList<Accommodation>();
		int mapId = 0;
		Integer key =0;
		List<Accommodation> value=null;
		try {
			Object obj = in.readObject();
			results = (HashMap<Integer,List<Accommodation>>) obj;
			for (Map.Entry<Integer, List<Accommodation>> entry : results.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
			}

				
			
			ObjectOutputStream outmanager = master.getOutputs().get(key);
			out.writeUTF(" [!] Results acquired from reducer [!] ");
			out.flush();

			if (outmanager!=null) {
				outmanager.writeObject(value);
				outmanager.flush();
			}

			try {
				outmanager.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				in.close();
				out.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

}
