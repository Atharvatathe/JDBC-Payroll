import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
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
        return  this.getEmployeePayrollDataUsingDB(sql);
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


    public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("SELECT * FROM employee_payroll WHERE START BETWEEN '%s' AND '%S'; ",
                                    Date.valueOf(startDate),Date.valueOf(endDate));

        return  this.getEmployeePayrollDataUsingDB(sql);
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try (Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public Map<String, Double> getAverageSalaryByGender() {
        String sql = "SELECT gender, AVg(basic_pay) as avg_salary FROM employee_payroll GROUP BY gender;";
        Map<String,Double> genderAvgSalaryMap = new HashMap<>();
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                String gender = resultSet.getString("gender");
                Double salary = resultSet.getDouble("basic_pay");
                genderAvgSalaryMap.put(gender,salary);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return genderAvgSalaryMap;
    }

    public EmployeePayrollData addEmployeeToPayrollUC7(String name, double basic_pay, LocalDate startDate, String gender) {
        int employeeId = -1;
        EmployeePayrollData employeePayrollData = null;
        String sql = String.format("INSERT INTO employee_payroll (name,gender,basic_pay,start)" +
                "VALUES('%s','%s','%s','%s')",name,gender,basic_pay,Date.valueOf(startDate));
        try(Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            int rowAffected = statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1){
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) employeeId = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(employeeId,name,basic_pay,startDate);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollData;
    }

    public EmployeePayrollData addEmployeeToPayroll(String name, double basic_pay, LocalDate startDate,
                                                    String gender) {
        int employeeId = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement()) {
            String sql = String.format("INSERT INTO employee_payroll (name,gender,basic_pay,start)" +
                    "VALUES('%s','%s','%s','%s')", name, gender, basic_pay, Date.valueOf(startDate));
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) employeeId = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement statement = connection.createStatement()) {
            double deduction = basic_pay * 0.2;
            double taxablepay = basic_pay - deduction;
            double tax = taxablepay * 0.1;
            double netPay = basic_pay - tax;
            String sql = String.format("INSERT INTO payroll_details (employeeID, basic_pay,deduction,taxablepay,tax,netPay)" +
                    "VALUES ('%s','%s','%s','%s','%s','%s')", employeeId, basic_pay, deduction, taxablepay, tax, netPay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1) {
                employeePayrollData = new EmployeePayrollData(employeeId, name, basic_pay, startDate);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollData;
    }

    }
