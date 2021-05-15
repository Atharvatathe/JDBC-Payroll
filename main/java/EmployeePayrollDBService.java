import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class EmployeePayrollDBService {

    private PreparedStatement employeepayrollDataStatement;
    private  static EmployeePayrollDBService employeePayrollDBService;

    private EmployeePayrollDBService(){ }

    public static EmployeePayrollDBService getInstance(){
        if(employeePayrollDBService == null)
                employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    public List<EmployeePayrollData> readData() {
        String sql = "SELECT * FROM employee_payroll";
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "atharva55";
        System.out.println("Connection to database:" + jdbcURL);
        Connection con;
        con = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connection is Successfull!!!" +con);
        return con;

    }


    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeeDataUsingSatement(name,salary);
    }

    private int updateEmployeeDataUsingSatement(String name, double basic_pay) {
        String sql = String.format("update employee_payroll set salary = %2f where name = '%s';", basic_pay, name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        if(this.employeepayrollDataStatement==null)
            this.prepareStatementForEmployeeData();
        try{
            employeepayrollDataStatement.setString(1,name);
            ResultSet resultSet = employeepayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("basic_pay");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private void prepareStatementForEmployeeData() {
        try {
            Connection connection = this.getConnection();
            String sql = "SELECT * FROM employee_payroll WHERE name = ?";
            employeepayrollDataStatement = connection.prepareStatement(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
