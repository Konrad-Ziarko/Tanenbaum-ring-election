package ipserver;

public class ServerClass {
    public String ip;
    public int port;
    public int id;
    public ServerClass(String ip, int port, int id) {
        this.ip = ip;
        this.port = port;
        this.id = id;
    }
    public ServerClass(String ip, String port, String id) {
        this.ip = ip;
        this.port = Integer.parseInt(port);
        this.id =Integer.parseInt(id);
    }
    public String toString(){
        return this.ip+ ":"+this.port+":"+id;
    }
}
