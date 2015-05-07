
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Couser {
    String name;
    String password;
    List<Cosystem> Cosystems;

    public Couser(String name,String password){
        this.name=name;
        this.password=password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Cosystem> getCosystems() {
        return Cosystems;
    }

    public void setCosystems(List<Cosystem> cosystems) {
        Cosystems = cosystems;
    }


    public void  retrieveCosystems() throws Exception {
        String url = "http://cosync.local/api/systems.php";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "action=retrieve&username="+this.getName()+"&password="+this.getPassword();

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        if(responseCode==200){
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        in.close();
        JSONArray array=(JSONArray)JSONValue.parse(response.toString());
        List<Cosystem> systems=new ArrayList<Cosystem>();
        for(int i=0;i<array.size();i++){
            JSONObject tempobj=(JSONObject)(array.get(i));
            systems.add(new Cosystem(tempobj.get("last_ip").toString(),tempobj.get("key").toString(),tempobj.get("key").toString()));
        }
        this.setCosystems(systems);

    }
}
