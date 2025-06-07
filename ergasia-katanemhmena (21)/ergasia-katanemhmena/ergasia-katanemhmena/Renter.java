import java.util.*;
import java.io.*;
import java.net.*;
import org.json.simple.JSONObject;
import java.util.concurrent.ThreadLocalRandom;
import java.text.SimpleDateFormat;
import java.text.ParseException;

@SuppressWarnings("all")
public class Renter extends Thread {
    Date Start;
    Date End;

    public Renter() {
    }

    public void filtered_rooms() {

        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            Filters filters = new Filters();

            Scanner scan = new Scanner(System.in);

            // Insert the Area
            System.out.println("Choose filters for your accommodation :");
            System.out.print("1) Area: ");
            String area = scan.nextLine();
            filters.setArea(area);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = null;
            Date endDate = null;
            boolean validDate = false;

            while (!validDate) {
                System.out.println("2) Date: ");
                System.out.println("Enter start date (dd/mm/yyyy): ");
                String startDateStr = scan.nextLine();
                System.out.println("Enter end date (dd/mm/yyyy): ");
                String endDateStr = scan.nextLine();

                try {
                    startDate = dateFormat.parse(startDateStr);
                    Start = startDate;
                    endDate = dateFormat.parse(endDateStr);
                    End = endDate;

                    if (startDate.compareTo(endDate) > 0) {
                        System.out.println("WARNING...Date of start must be before date of end");
                    } else {
                        validDate = true;
                    }

                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please enter dates in dd/mm/yyyy format.");
                }

                filters.setStartDate(startDateStr);
                filters.setEndDate(endDateStr);

            }

            // Insert number of people and check the input
            int nOfPeople = 0;
            while (true) {
                System.out.print("3) Number of people: ");
                String numOfPeople = scan.nextLine();
                

                try {
                    nOfPeople = Integer.parseInt(numOfPeople);
                    if (nOfPeople >= 1 && nOfPeople <= 10) {
                        break; // Eksodos apo ton broxo
                    } else {
                        System.out.println("Please enter a number between 1 and 10.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }

            }
            filters.setNumberOfPeople(nOfPeople);

            // Insert the prices and check the inputs
            boolean validPrices = false;
            while (!validPrices) {
                // Prompt for lowest price
                System.out.println("4) Price: ");
                System.out.print("-> Lowest Price: ");
                String lowestStr = scan.nextLine();

                // Prompt for biggest price
                System.out.print("-> Biggest Price: ");
                String biggestStr = scan.nextLine();

                try {
                    // Parse strings to integers
                    int lowest = Integer.parseInt(lowestStr);
                    int biggest = Integer.parseInt(biggestStr);

                    // Check if lowest price is lower than biggest price
                    if (lowest < biggest) {
                        validPrices = true; // Set validPrices to true to exit the loop
                    } else {
                        System.out.println("Lowest price must be lower than biggest price. Please enter again.");
                    }
                } catch (NumberFormatException e) {
                    // If parsing fails, it means the input is not an integer
                    System.out.println("Invalid input. Please enter integers for prices.");
                }

                filters.setLowestPrice(Integer.parseInt(lowestStr));
                filters.setMaxPrice(Integer.parseInt(biggestStr));

            }

            // Insert the number of stars and check the input
            while (true) {
                System.out.print("5) Stars: ");
                String stars = scan.nextLine();

                try {
                    int starsValue = Integer.parseInt(stars);
                    if (starsValue >= 1 && starsValue <= 5) {
                        break; // Eksodos apo ton broxo
                    } else {
                        System.out.println("Please enter a number between 1 and 5.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }

                filters.setStars(Integer.parseInt(stars));
            }

            requestSocket = new Socket("127.0.0.1", 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("renter");
            out.writeInt(1);
            out.writeObject(filters);
            out.flush();

            System.out.println(" [!] Filters sent to master [!] ");
            System.out.println(in.readUTF());

            List<Accommodation> value = null;
            try {
                Object obj = in.readObject();
                value = (List<Accommodation>) obj;

                if (value.isEmpty()) {
                    System.out
                            .println("[!] Sorry there are no available accommodations with these characteristics [!]");
                } else {
                    for (Accommodation acc : value) {
                        acc.showMore();
                    }

                    System.out.println("Would you like to make a reservation to any of these accommodations? (y/n)");
                    String answer = scan.nextLine();

                    switch (answer) {
                        case "y":
                            HashMap<Integer,String> rooms = new HashMap<Integer,String>();
                            int i = 1;
                            String room;
                            System.out.println("Searching results: ");
                            for (Accommodation a : value) {
                                System.out.println(String.valueOf(i) + ".  " + a.getName());
                                rooms.put(i,a.getName());
                                i++;
                            }
                            System.out.println("Which accommodation do you prefer? (enter number)");
                            while(true) {
                                int roomNumber = scan.nextInt();
                                if(rooms.containsKey(roomNumber) == true) {
                                    room = rooms.get(roomNumber);
                                    break;
                                } else {
                                    System.out.println("Invalid number. Please try again!");
                                }
                            }
                            
                            
                            reserve(room);
                    }
                }

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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

    }// end filtered_rooms

    private Date getStart() {
        return this.Start;
    }

    private Date getEnd() {
        return this.End;
    }

    // Kratish
    public void reserve(String room) {
        Scanner scan = new Scanner(System.in);
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = this.getStart();
        Date endDate = this.getEnd();
        boolean validDate = false;
        System.out.println("Aithma krathshs tou Accommodation: " + room + " gia: ");
        System.out.println("Arxh: " + startDate);
        System.out.println("Telos: " + endDate);

        try {
            requestSocket = new Socket("127.0.0.1", 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("renter");
            out.writeInt(2);
            out.writeUTF(room);
            out.writeObject(startDate);
            out.writeObject(endDate);
            out.flush();

            System.out.println(" [!] Request for reserve sent to master [!] ");
            System.out.println(in.readUTF());
            String resultOfReservation = in.readUTF();
            if (resultOfReservation.equals("Epityxia")) {
                System.out.println("H kratisi egine!");
            } else {
                System.out.println("H kratisi den pragmatopoihthike!");
            }
            /*
             * String resultOfReservation = in.readUTF();
             * if (resultOfReservation.equalsIgnoreCase("Epityxia")) {
             * System.out.println("H krathsh pragmatopoihthike me epityxia!");
             * } else {
             * System.out.println("H krathsh den pragmatopoihthike");
             * }
             */
            System.out.println("Would you like to rate to any of these accommodations? (y/n)");
            String answer = scan.nextLine();

            int stars;
            switch (answer) {
                case "y":
                    while(true) {
                        System.out.println("Please enter your rating (1 to 5 stars): ");
                        stars = scan.nextInt();
                        if(stars >=1 && stars<=5) {
                            break;
                        }
                        else {
                            System.out.println("Please enter a valid rating number between 1 and 5!!!");
                        }
                    }
                    
                    System.out.print("Accommodation name: " + room);
                    rate(room,stars);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }


    public void rate(String roomName, int stars) {

        Scanner scan = new Scanner(System.in);
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        

        try {
            requestSocket = new Socket("127.0.0.1", 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("renter");
            out.writeInt(3);
            out.writeUTF(roomName);
            out.writeInt(stars);
            out.flush();

            System.out.println(" [!] Request for reserve sent to master [!] ");
            System.out.println(in.readUTF());
            String resultOfReservation = in.readUTF();
            if (resultOfReservation.equals("Epityxia")) {
                System.out.println("H bathmologhsh egine!");
            } else {
                System.out.println("H bathmologhsh den pragmatopoihthike!");
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Renter m = new Renter();
        m.filtered_rooms();
    }

}
