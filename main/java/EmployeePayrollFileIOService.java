import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollFileIOService {
    public String PAYROLL_FILE_NAME = "Payroll_file.txt";

    public void writeData(List<EmployeePayrollData> employeePayrollList) {
        StringBuffer empBuffer = new StringBuffer();
        employeePayrollList.forEach(employee -> {
            String emplyeeDataString = employee.toString().concat("\n");
            empBuffer.append(emplyeeDataString);
        });

        try{
            Files.write(Paths.get(PAYROLL_FILE_NAME),empBuffer.toString().getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void printData() {
        try {
            Files.lines(new File(PAYROLL_FILE_NAME).toPath())
                    .forEach(System.out::println);
        }catch (IOException e){
         e.printStackTrace();
        }
    }

    public long countEntries() {
        long entries = 0;
        try {
            entries = Files.lines(new File(PAYROLL_FILE_NAME).toPath()).count();
        }catch (IOException e){
            e.printStackTrace();
        }
        return entries;
    }

    public void ReadData(List<EmployeePayrollData> employeePayrollList) {
    }

    public List<String> readData() {
        List<String> employeeList = new ArrayList<>();
        try{
            Files.lines(new File(PAYROLL_FILE_NAME).toPath()).map(line -> line.trim())
                    .forEach(line-> { employeeList.add(line);
                    });
        }catch (IOException e){
            e.printStackTrace();
        }
        return employeeList;
    }
}
