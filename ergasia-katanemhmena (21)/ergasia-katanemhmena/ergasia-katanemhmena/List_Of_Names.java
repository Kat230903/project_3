import java.util.ArrayList;
import java.util.*;

public class List_Of_Names {

    static List<String> names =new ArrayList<>();

    public List_Of_Names(){}

    public void add(String name){
        names.add(name);
    }

    public List<String> getList(){
        return names;
    }

    public boolean Name_exists (String name){

        if(names.contains(name)){
            return true;
        }

        return false;
    }

}
