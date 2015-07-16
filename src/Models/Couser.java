package Models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Couser {
    String name;
    String password;
    List<Cosystem> cosystems;

    public Couser(String name, String password)
    {
        this.name = name;
        this.password = password;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public List<Cosystem> getCosystems() {
        return this.cosystems;
    }

    public void setCosystems(List<Cosystem> cosystems) {
        this.cosystems = cosystems;
    }


    public void retrieveCosystems()
            throws Exception {
        String url = "http://127.0.0.1/api/systems.php";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "action=retrieve&username=" + this.getName() + "&password=" + this.getPassword();

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
        if (responseCode == 200) {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        in.close();
        JSONArray array = (JSONArray) JSONValue.parse(response.toString());

        List<Cosystem> systems = new ArrayList<Cosystem>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject tempobj = (JSONObject) (array.get(i));
            if(!tempobj.get("last_ip").toString().equals(Inet4Address.getLocalHost().getHostAddress()))
                systems.add(new Cosystem(tempobj.get("last_ip").toString(), tempobj.get("key").toString(), tempobj.get("key").toString()));
        }
        this.setCosystems(systems);
        for(int i =0;i<systems.size();i++){
            System.out.println("ip => "+systems.get(i).getIp());
        }
    }

    public boolean exist()
            throws Exception {
        String url = "http://127.0.0.1/api/user.php";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "action=connection&username=" + this.getName() + "&password=" + this.getPassword();

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
        if (responseCode == 200) {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        in.close();

        return Integer.parseInt(response.toString()) == 1?true:false;
    }
}
