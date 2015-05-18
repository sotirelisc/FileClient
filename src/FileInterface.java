
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


/**
 *
 * @author ChristosPC
 */
public interface FileInterface extends Remote {
    public boolean exists(String filename) throws RemoteException;
    public void createFile(String filename, String content) throws RemoteException;
    public String viewFile(String filename) throws RemoteException;
    public boolean canEdit(String filename) throws RemoteException;
    public void editFile(String filename, String content) throws RemoteException;
    public ArrayList<String> getList() throws RemoteException;
}
