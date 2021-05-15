import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {
    public int id;
    public String name;
    public double basic_pay;
    LocalDate startDate;

    public EmployeePayrollData(int id,String name,double basic_pay){
        this.id = id;
        this.name = name;
        this.basic_pay = basic_pay;
    }

    public EmployeePayrollData(int id, String name, double basic_pay, LocalDate startDate){
        this(id, name, basic_pay);
        this.startDate = startDate;
    }


    public  String toString(){
        return "EmployeePayRollData[id=" + id + ",name=" + name + ",salary=" + basic_pay + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id && Double.compare(that.basic_pay, basic_pay) == 0 &&
                name.equals(that.name) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, basic_pay, startDate);
    }
}
