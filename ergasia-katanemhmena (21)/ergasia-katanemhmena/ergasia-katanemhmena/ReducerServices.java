import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.*;

@SuppressWarnings("all")
public class ReducerServices extends Thread implements Serializable {

    ObjectInputStream in;
    ObjectOutputStream out;
    Socket connection;
    String profile;
    Reducer reducer;
    int workers;
    static int count = 0;
    static boolean flag = true;
    private final Object mutex = new Object();

    public ReducerServices(Socket connection, Reducer reducer) {
        try {
            this.connection = connection;
            this.out = new ObjectOutputStream(connection.getOutputStream());
            this.in = new ObjectInputStream(connection.getInputStream());
            this.reducer = reducer;
            this.profile = in.readUTF();
            this.workers = in.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        if (profile.equalsIgnoreCase("manager")) {
            int key=0;
            try {
                Object obj = in.readObject();
                HashMap<Integer, List<Accommodation>> list = (HashMap<Integer, List<Accommodation>>) obj;
                for (Map.Entry<Integer, List<Accommodation>> entry : list.entrySet()) {
                    key = entry.getKey();
                    List<Accommodation> value = entry.getValue();
                }
                if(reducer.getList2().keySet().contains(key) == true) {
                    reducer.getSublist2(key).add(list);
                }
                else {
                    List<HashMap<Integer, List<Accommodation>>> sublist = new ArrayList<HashMap<Integer, List<Accommodation>>>();
                    sublist.add(list);
                    reducer.getList2().put(key,sublist);
                }
                if (reducer.getSublist2(key).size() == workers) {
                    ReduceShowResults(reducer.getSublist2(key));
                }

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (profile.equalsIgnoreCase("renter")) {
            int key = 0;
            try {
                Object obj = in.readObject();
                HashMap<Integer, List<Accommodation>> list = (HashMap<Integer, List<Accommodation>>) obj;
                for (Map.Entry<Integer, List<Accommodation>> entry : list.entrySet()) {
                    key = entry.getKey();
                    List<Accommodation> value = entry.getValue();
                }
                if(reducer.getList().keySet().contains(key) == true) {
                    reducer.getSublist(key).add(list);
                }
                else {
                    List<HashMap<Integer, List<Accommodation>>> sublist = new ArrayList<HashMap<Integer, List<Accommodation>>>();
                    sublist.add(list);
                    reducer.getList().put(key,sublist);
                }
                if (reducer.getSublist(key).size() == workers) {
                    ReduceFilterResults(reducer.getSublist(key));
                }
                

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void ReduceFilterResults(List<HashMap<Integer, List<Accommodation>>> list) {
        try {
            out.writeUTF("Reducer: Elaba ta apotelesmata workers!!");
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Reduce filtering results in proccess...");

        List<Accommodation> list_result = new ArrayList<Accommodation>();
        Integer id = 0;
        HashMap<Integer, List<Accommodation>> hassh = null;
        for (HashMap<Integer, List<Accommodation>> ac : list) {

            for (Map.Entry<Integer, List<Accommodation>> entry : ac.entrySet()) {
                id = entry.getKey();
                List<Accommodation> accommodations = entry.getValue();

                for (Accommodation acc : accommodations) {
                    list_result.add(acc);
                }
            }

        }
        hassh = new HashMap<Integer, List<Accommodation>>();
        hassh.put(id, list_result);

        reducer.setClear();
        reducer.SendFilterResultsToMaster(hassh);
    }

    public void ReduceShowResults(List<HashMap<Integer,List<Accommodation>>> list) {
        try {
            out.writeUTF(" [!] Accommodations acquired from all workers [!] ");
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       

		count=0;
        //<id><<names>,<persons>,<stars>,<reviews>,<dates>,<price>>
		List<Accommodation> list_result= new ArrayList<Accommodation>();
		Integer id=0;
		HashMap<Integer,List<Accommodation>> hassh=null;
		for(HashMap<Integer, List<Accommodation>> ac : list){

			for ( Map.Entry<Integer, List<Accommodation>> entry : ac.entrySet()) {
				id = entry.getKey();
				List<Accommodation> accommodations = entry.getValue();
				
				for(Accommodation acc : accommodations){
					list_result.add(acc);
				}
                
			}

		}
		hassh= new HashMap<Integer,List<Accommodation>>();
		hassh.put(id, list_result);
       


        reducer.setClear2();
		reducer.SendShowResultsToMaster(hassh);
    }

}
