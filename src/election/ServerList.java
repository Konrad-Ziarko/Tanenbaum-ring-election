package election;

import java.util.ArrayList;

public class ServerList extends ArrayList<ServerClass>{

	private static final long serialVersionUID = 1L;
	
	public int objectIdx(ServerClass proc){
		int idx=-1;
		for (int i =0;i<this.size();i++){
			ServerClass tmp = this.get(i);
			if (proc.id == tmp.id && proc.ip.equals(tmp.ip)&&proc.port==tmp.port){
				return i;
			}
		}
		return idx;
	}
}
