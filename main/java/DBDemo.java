import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class DBDemo {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "atharva55";
        Connection con;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver Loaded");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find driver in the classpath", e);
        }
        listDrivers();
        try {
            System.out.println("Connection to database:" + jdbcURL);
            con = DriverManager.getConnection(jdbcURL, userName, password);
            System.out.println("Connection is Successfull!!!" + con);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void listDrivers(){
        Enumeration<Driver> driverlist = DriverManager.getDrivers();
        while(driverlist.hasMoreElements()){
            Driver driverclass = (Driver) driverlist.nextElement();
            System.out.println(" " +driverclass.getClass().getName());
        }
    }

}
