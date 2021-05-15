import jdk.dynalink.linker.LinkerServices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {


    public enum IOService{CONSOLE_IO,FILE_IO,DB_IO,REST_IO}

    private List<EmployeePayrollData> employeePayrollList;
    private EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService(){
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData>employeePayrollList){
        this.employeePayrollList = employeePayrollList;
    }

    public static void main(String[] args){
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        Scanner consoleInputReader = new Scanner(System.in);
        employeePayrollService.readEmployeePayRollData(IOService.FILE_IO);
        employeePayrollService.writeEmployeePayRollData(IOService.CONSOLE_IO);
    }

    //method to read data
    public long readEmployeePayRollData(IOService ioService){
        if(ioService.equals(IOService.CONSOLE_IO)){
            Scanner consoleInputReadr = new Scanner(System.in);
            System.out.println("Enter Employee Id");
            int id = consoleInputReadr.nextInt();
            System.out.println("Enter Employee Name");
            String name =consoleInputReadr.next();
            System.out.println("Enter Employee Salary");
            double salary = consoleInputReadr.nextDouble();
            employeePayrollList.add(new EmployeePayrollData(id,name,salary));
        }
        List<String> employeeList = null;
        if(ioService.equals(IOService.FILE_IO))
            employeeList = new EmployeePayrollFileIOService().readData();
        return employeeList.size();
    }

    public void writeEmployeePayRollData(EmployeePayrollService.IOService ioService){
        if(ioService.equals(IOService.CONSOLE_IO))
            System.out.println("\nWriting Employee Payroll Roaster to console\n"+ employeePayrollList);
        else if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().writeData(employeePayrollList);

    }

    public List<EmployeePayrollData> readEmployeePayRollData1(IOService ioService){
        if(ioService.equals(IOService.DB_IO))
            this.employeePayrollList = EmployeePayrollDBService.getInstance().readData();
        return  this.employeePayrollList;
    }

    public void updateEmployeeSalary(String name, double basic_pay) {
        int result = EmployeePayrollDBService.getInstance().updateEmployeeData(name,basic_pay);
        if(result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmloyeePayrollData(name);
        if(employeePayrollData != null) employeePayrollData.basic_pay = basic_pay;
    }

    public List<EmployeePayrollData> readEmployeePayRollForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeePayrollForDateRange(startDate,endDate);
        return null;
    }

    public Map<String, Double> readAvgSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
    }



    public boolean checkEmployeepayrollInSyncWithDB(String name) {
        List<EmployeePayrollData> employeePayrollDataList = EmployeePayrollDBService.getInstance().getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmloyeePayrollData(name));
    }


    private EmployeePayrollData getEmloyeePayrollData(String name) {
        return employeePayrollList.stream()
                .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name) )
                .findFirst()
                .orElse(null);
        }


        public void printData(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().printData();
    }

    public long countEntries(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return 0;
    }


}

