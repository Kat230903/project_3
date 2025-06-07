import java.util.*;

@SuppressWarnings("all")
public class Dummy {
    static Scanner scan = new Scanner(System.in); // Create a Scanner object

    public static void main(String args[]) {
        boolean app = true;

        while (app) {
            System.out.println("Chose your profile: ");
            System.out.println("1. Manager \n2. User \n3.Exit");
            String ans1 = scan.nextLine();
            switch (ans1) {
                case "1":
                    System.out.println();
                    System.out.println();
                    System.out.println("-----WELCOME-----");
                    Manager newManager = new Manager();
                    boolean managerMode = true;
                    while (managerMode) {
                        System.out.println("What do you wish to do?");
                        System.out.println("1. Add a room \n2. See all rooms under your ownership \n3. Exit");
                        String ans2 = scan.nextLine();
                        switch (ans2) {
                            case "1":
                                System.out.println();
                                System.out.println("Want to add a room....");
                                System.out.println(
                                        "!!!!!---Please enter valid room details, otherwise penalties are provided----!!!!!");
                                newManager.add();
                                break;
                            case "2":
                                newManager.show();
                                break;
                            case "3":
                                managerMode = false;
                                break;
                        }
                    }
                    break;
                case "2":
                    boolean userMode = true;
                    Renter newRenter = new Renter();
                    while (userMode) {
                        System.out.println("What do you wish to do?");
                        System.out.println("1. Search rooms \n2. Exit");
                        String ans2 = scan.nextLine();
                        switch (ans2) {
                            case "1":
                                System.out.println("Please insert your desired features: ");
                                newRenter.filtered_rooms();
                                break;
                            case "2":
                                userMode = false;
                                break;
                        }
                    }
                    break;
                case "3":
                    app = false;
                    break;
            }
        }
    }
}