package Models;

public class Cosystem {
    String ip;
    String name;
    String key;
    Boolean online;


    public Cosystem(String ip, String key, String name)
    {
        this.ip = ip;
        this.key = key;
        this.name = name;
        this.online = true;
    }

    public String getIp()
    {

        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
