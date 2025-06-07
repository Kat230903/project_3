import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.*;

@SuppressWarnings("all")
public class WorkerServices extends Thread implements Serializable {
    ObjectInputStream in;
    ObjectOutputStream out;
    Socket connection;
    String profile;
    int typeOfRequest;
    Worker worker;

    public WorkerServices(Socket connection, Worker worker) {
        try {
            this.connection = connection;
            this.out = new ObjectOutputStream(connection.getOutputStream());
            this.in = new ObjectInputStream(connection.getInputStream());
            this.worker = worker;
            this.profile = in.readUTF();
            this.typeOfRequest = in.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        if (profile.equalsIgnoreCase("manager")) {
            if (typeOfRequest == 1) {
                System.out.println("Typer of Request -> Add Accommodation ");
                AddRoom();
            } else if (typeOfRequest == 2) {
                System.out.println("Typer of Request -> Show Reservations ");
                try {
                    int numberOfWorkers = in.readInt();
                    worker.setNumberOfWorkers(numberOfWorkers);
                    ShowReservations();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else if (profile.equalsIgnoreCase("renter")) {
            if (typeOfRequest == 1) {
                System.out.println("Typer of Request -> Filtering ");
                try {
                    int numberOfWorkers = in.readInt();
                    worker.setNumberOfWorkers(numberOfWorkers);
                    FilterProcess();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (typeOfRequest == 2) {
                System.out.println("Typer of Request -> Reserve Accommodation ");
                ReserveRoom();
            } else if (typeOfRequest == 3) {
                System.out.println("Typer of Request -> Rate Accommodation ");
                RateRoom();
            }
        }

    }

    public void AddRoom() { // Add room to worker

        try {

            Accommodation ac;
            int id;
            try {
                Object obj = in.readObject();
                ac = (Accommodation) obj;
                int workerId = in.readInt();
                worker.addAc(ac); // Prosthetei to Accommodation sthn lista tou Worker
                List<Accommodation> helper = worker.getList();
                System.out.println("--->  Worker " + (workerId) + " has these accommodations : <---");
                for (Accommodation a : helper) {
                    System.out.println("-> " + a.getName());

                }

                out.writeUTF(" [!] Accommodation received succesfully by Worker [!] ");
                out.flush();

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

    public void ShowReservations() {
        String managerName = "";
        HashMap<Integer, String> f;
        int MapId = -1;
        try {
            Object obj = in.readObject();
            f = (HashMap<Integer, String>) obj;
            for (Map.Entry<Integer, String> entry : f.entrySet()) { // examine each entry on the hashmap
                MapId = entry.getKey();
                managerName = entry.getValue();
            }

            out.writeUTF(" [!] Request for Manager Show with MapId: " + MapId + " received [!] ");
            out.flush();

            List<Accommodation> listoWithAccommodations = new ArrayList<Accommodation>();
            HashMap<Integer,List<Accommodation>> result = new HashMap<Integer,List<Accommodation>>();

            for (Map.Entry<Integer, String> entry : f.entrySet()) { // examine each entry on the hashmap                                                                       
                MapId = entry.getKey();
                managerName = entry.getValue();
            }

            List<Accommodation> rooms = worker.getList();
           
            
            for(Accommodation room : rooms) {
                if(room.getManager().equalsIgnoreCase(managerName)) {
                    for(int j=0; j<=2; j++ ){
                        boolean found=false; 
                        for(int i =1 ; i<31; i++){

                            if(!room.IsDiathesimi(j, i)){ // if we found at least one reserved day
                                                        // we add the room to the list of reservations
                                listoWithAccommodations.add(room);
                                found=true;
                                break;

                            }

                        }

                        if(found){ // if we found at least one month that the room is reserved 
                            break; //we dont need to iterate through the rest of the months
                        }

                    }

                }
            }

            result.put(MapId,listoWithAccommodations);
            
            worker.SendShowResultsToReducer(result,worker.getNumberOfWorkers());

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

    // ----------------------------Actions For
    // Master-Renter-------------------------

    public void FilterProcess() {

        try {

            Filters filters = null;
            HashMap<Integer, Filters> f;
            int MapId = 0;
            try {

                Object obj = in.readObject();
                f = (HashMap<Integer, Filters>) obj;
                worker.setMap(f); // get the request filter and give to each worker
                for (Map.Entry<Integer, Filters> entry : worker.getMap().entrySet()) { // examine each entry on the
                                                                                       // hashmap
                    MapId = entry.getKey();
                    filters = entry.getValue();
                }

                List<Accommodation> listforAccom0 = worker.FilteringAccommodations();
                HashMap<Integer, List<Accommodation>> Worker0 = new HashMap<Integer, List<Accommodation>>();

                for (Accommodation a : worker.getList()) { // Gia kathe Accommodation ston worker
                   
                    if (a.matchesFilters(filters)) {
                        listforAccom0.add(a);
                        break;
                    }
                }

                Worker0.put(MapId, listforAccom0);


                out.writeUTF(" [!] Filters acquired from master [!] ");
                out.flush();
                worker.SendFilterResultsToReducer(Worker0, worker.getNumberOfWorkers()); // send the filters to reducer

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

    public void ReserveRoom() {
        String roomName;
        Date startDate = null;
        Date endDate = null;
        boolean done = false;

        try {
            roomName = in.readUTF();
            Object obj1 = in.readObject();
            Object obj2 = in.readObject();

            startDate = (Date) obj1;
            endDate = (Date) obj2;

            out.writeUTF("[!] Worker: Received Reservation Request for Accommodation: " + roomName + "[!]");
            out.flush();

            boolean roomFound = false;
            Accommodation room = null;
            for (Accommodation acc : worker.getList()) {
                if (acc.getName().equalsIgnoreCase(roomName)) {
                    room = acc;
                    roomFound = true;
                    break;
                }
            }

            if (!roomFound) {
                done = false;
                // out.writeUTF("Apotyxia");
                // out.flush();
                System.out.println("[!] I do not have the accommodation " + roomName + " [!]");
            } else {
                synchronized (room) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);
                    int startMonth = calendar.get(Calendar.MONTH) - 5;
                    int startDay = calendar.get(Calendar.DAY_OF_MONTH);
                    calendar.setTime(endDate);
                    int endMonth = calendar.get(Calendar.MONTH) - 5;
                    int endDay = calendar.get(Calendar.DAY_OF_MONTH);

                    boolean available = true;
                    if (startMonth == endMonth) {
                        available = room.checkAvailability(startMonth, startDay, endDay);
                    } else {
                        available = room.checkAvailability(startMonth, startDay, 30)
                                && room.checkAvailability(endMonth, 1, endDay);
                    }

                    if (available) {
                        done = true;
                        // out.writeUTF("Epityxia");
                        // out.flush();

                        System.out.println("The room is available for these dates.");
                        room.reserveDays(startMonth, startDay, endMonth, endDay);
                    } else {
                        done = false;
                        // out.writeUTF("Apotyxia");
                        // out.flush();
                        System.out.println("Unavailable Accommodation!!!");
                    }

                }

            }
            if (done) {
                out.writeUTF("Epityxia");
                out.flush();
            } else {
                out.writeUTF("Apotyxia");
                out.flush();
            }

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }


    public void RateRoom() {
        String roomName;
        int stars;
        Date startDate = null;
        Date endDate = null;
        boolean done = false;

        try {
            roomName = in.readUTF();
            stars = in.readInt();

            out.writeUTF("[!] Worker: Received Rating Request for Accommodation: " + roomName + "[!]");
            out.flush();

            boolean roomFound = false;
            Accommodation room = null;
            for (Accommodation acc : worker.getList()) {
                if (acc.getName().equalsIgnoreCase(roomName)) {
                    room = acc;
                    roomFound = true;
                    break;
                }
            }

            if (!roomFound) {
                done = false;
                System.out.println("[!] I do not have the accommodation " + roomName + " [!]");
            } else {
                synchronized (room) {
                    room.setStars(room.getStars() + stars);
                    room.setReviews(room.getReviews() + 1);
                    done = true;
                    room.showMore();
                }

            }
            if (done) {
                out.writeUTF("Epityxia");
                out.flush();
            } else {
                out.writeUTF("Apotyxia");
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
