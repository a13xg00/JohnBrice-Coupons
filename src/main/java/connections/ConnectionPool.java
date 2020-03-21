package connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static connections.MySQL.getParamsMaps;
import static connections.MySQL.urlByDataBase;
import static connections.PathUtils.buildParamsFor;

public class ConnectionPool {

    private static ConnectionPool instance = null;
    private Set<Connection> connections;
    private final String URL;
    private final static int MAX_CONNECTIONS = 15;

    private ConnectionPool() {
        URL = buildParamsFor(urlByDataBase("coupons_db"), getParamsMaps());
        connections = createConnections(URL, MAX_CONNECTIONS);
    }

    public Set<Connection> createConnections(String url, int maxConnections) {
        Set<Connection> connections = new HashSet<>();
        for (int i = 0; i < maxConnections; i++) {
            try {
                Connection connection = DriverManager.getConnection(url);
                connections.add(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connections;
    }

    public synchronized Connection getConnection() {
        while (connections.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Iterator<Connection> iterator = connections.iterator();
        Connection connection = iterator.next();
        iterator.remove();
        return connection;
    }


    public synchronized void returnConnection(Connection connection) {
        connections.add(connection);
        notifyAll();
    }

    public synchronized void closeAllConnections (){
        int counter = 0;
        while (counter<MAX_CONNECTIONS){
            while (connections.isEmpty()){
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Someone interrupt waiting");
                }
            }
            Iterator<Connection> itCon = connections.iterator();
            while (itCon.hasNext()){
                Connection currentConnection = itCon.next();

                try {
                    currentConnection.close();
                    counter++;
                } catch (SQLException e) {
                    System.err.println("Couldnt close the current connection");
                }
            }
        }
    }

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }
}
