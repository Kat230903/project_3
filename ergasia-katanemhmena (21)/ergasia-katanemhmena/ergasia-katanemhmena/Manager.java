import java.util.*;
import java.io.*;
import java.net.*;
import org.json.simple.JSONObject;
import java.util.concurrent.ThreadLocalRandom;
import java.text.SimpleDateFormat;
import java.text.ParseException;

@SuppressWarnings("all")
public class Manager {

    Scanner scan = new Scanner(System.in); // Create a Scanner object
    private String n; // Name of Manager
    int type = -1;
    static List_Of_Names list_Of_Names= new List_Of_Names();

    public Manager() {
        this.type = 1;

    }

    // Constructor
    public Manager(String name) {
        this.n = name;

    }

    public void setName(String name){
        this.n=name;
    }

    public String getNAME(){
        return this.n;
    }

    // Method to add Accommodation
    public void add() {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            JSONObject obj = new JSONObject(); // JSON object created with the values given from Manager

            System.out.print("Insert name Of Manager: ");
            n = scan.nextLine(); // Read user input
            this.setName(n);
            obj.put("managerName", this.n);
            list_Of_Names.add(n);

            System.out.print("Insert room name: ");
            String roomName = scan.nextLine(); // Read user input
            obj.put("roomName", roomName);

            System.out.print("Insert location: ");
            String location = scan.nextLine(); // Read user input
            obj.put("area", location);

            System.out.print("Insert price: ");
            String price = scan.nextLine(); // Read user input
            int pricee = Integer.parseInt(price);
            obj.put("price", pricee);

            // Insert the dates and check for the ideal format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            // JSONObject obj = new JSONObject();

            for (int month = 0; month <= 2; month++) {
                String mName = null;
                if (month == 0) {
                    mName = "Iounios";
                }
                if (month == 1) {
                    mName = "Ioulios";
                }
                if (month == 2) {
                    mName = "Augoustos";
                }

                System.out.println("Enter availability dates for month " + mName + ":");

                // Enter the StartDate and EndDate for 3 months and check if input is in right
                // format
                boolean validDate = false;
                while (!validDate) {

                    System.out.println("Enter start date (dd/mm/yyyy): "); // Start Date
                    String startDateStr = scan.nextLine();
                    System.out.println("Enter end date (dd/mm/yyyy): "); // End Date
                    String endDateStr = scan.nextLine();

                    try {
                        Date startDate = dateFormat.parse(startDateStr);
                        Date endDate = dateFormat.parse(endDateStr);

                        if (startDate.compareTo(endDate) > 0) { // If start Date is before end Date --> ERROR
                            System.out.println("WARNING...Date of start must be before date of end");
                            startDate = null;
                            endDate = null;
                        } else {
                            validDate = true;
                        }

                        // Put the clues to the json object
                        obj.put("StartDate" + mName, startDate);
                        obj.put("EndDate" + mName, endDate);

                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please enter dates in dd/mm/yyyy format.");
                    }
                }
            }

            // Check if capacity between 1-10
            while (true) {
                System.out.print("Insert capacity (between 1 and 10): ");
                String input = scan.nextLine();
                int capacity = 0;

                try {
                    capacity = Integer.parseInt(input);
                    if (capacity >= 1 && capacity <= 10) {
                        obj.put("noOfPersons", capacity);
                        break;
                    } else {
                        System.out.println("Please enter a number between 1 and 10.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }

            int reviewsValue;
            // Check if numOf Revies >=0
            while (true) {
                System.out.print("Insert number of reviews (must be >= 0): ");
                String num_of_reviews = scan.nextLine();

                try {
                    reviewsValue = Integer.parseInt(num_of_reviews);
                    if (reviewsValue >= 0) {
                        obj.put("noOfReviews", reviewsValue);
                        break;
                    } else {
                        System.out.println("Please enter a number greater than or equal to 0.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }

            // Check if starts between 1-5
            while (true) {
                System.out.print("Insert stars: ");
                System.out.println("[!] Warning: Please insert the TOTAL number of stars you have received [!]");
                String stars = scan.nextLine();

                try {
                    int starsValue = Integer.parseInt(stars);
                    if (starsValue >= 1*reviewsValue && starsValue <= 5*reviewsValue) {
                        obj.put("stars", starsValue);
                        break;
                    } else {
                        System.out.println("Please enter a number within the margins.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
            // Write the JSON files to this folder

            // [!] path is changed based the computer our programm is running [!]
            String path = "C:/Users/Katerina/Downloads/ergasia-katanemhmena (21)/ergasia-katanemhmena/ergasia-katanemhmena/rooms/";
            try (FileWriter file = new FileWriter(path + roomName + ".json")) {
                file.write(obj.toString());
                System.out.println("Successfully Copied JSON Object to File...");
                System.out.println("\nJSON Object: " + obj);
            } catch (EOFException eofException) {
                // Handle EOFException
                System.err.println("Error: End of file reached unexpectedly while reading response from the server.");
                eofException.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            requestSocket = new Socket("127.0.0.1", 4321); // Stelnei request gia add se auto to Port
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
            out.writeUTF("manager");
            out.writeInt(1);
            out.writeObject(obj); // Write the json object to outputstream to send it to Master
            out.flush();

            System.out.println("---> accommodation sent from manager to master <---");
            System.out.println(in.readUTF());

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();
                out.close(); // Close the connection
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } // end finally

    } // end method add

    public String getName() {
        return this.n;
    }

    public void show() {
        Scanner scan = new Scanner(System.in);
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String name=null;
        try {

            for(;;){
                System.out.println("Please insert the name of Manager: If you want to exit press B");
                name = scan.nextLine();
                if(list_Of_Names.Name_exists(name)){
                    break;
                }else if(name.equals("B")){
                    break;
                }
                else{
                    System.out.println("This manager doesn't exists");
                                 
                }
            }

            if(!name.equals("B")){

                
            
                requestSocket = new Socket("127.0.0.1", 4321); // Stelnei request gia add se auto to Port
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());
                out.writeUTF("manager");
                out.writeInt(2);
                out.writeUTF(name);
                out.flush();

                System.out.println(" [!] Results sent to master [!] ");
                System.out.println(in.readUTF());
            
                List<Accommodation> value =null;
                try {
                    Object obj = in.readObject();
                    System.out.println(obj);
                    value = (List<Accommodation>) obj;

                    if(value.isEmpty()) {
                        System.out.println("[!] Sorry there are no reserved accommodations under this manager [!]");
                    }
                    else {
                        for(Accommodation acc : value) {
                            acc.showMore();
                            acc.displayCalendar(0);
                            acc.displayCalendar(1);
                            acc.displayCalendar(2);
                            
                        }
                    }
                
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if(out!=null && in!=null && requestSocket!=null){
                    out.close(); // Close the connection
                    in.close();
                    requestSocket.close();

                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Manager m = new Manager();
        m.add();
    }

}
