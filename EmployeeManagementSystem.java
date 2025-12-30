package Mini_project2;

import java.io.*;
import java.util.*;

class Employee implements Serializable {
    private int id;
    private String name;
    private double salary;
    private String department;

    public Employee(int id, String name, double salary, String department) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.department = department;
    }

    public int getId() { return id; }
    public double getSalary() { return salary; }
    public String getDepartment() { return department; }
    public void setSalary(double salary) { this.salary = salary; }

    @Override
    public String toString() {
        return "ID: " + id +
               ", Name: " + name +
               ", Salary: " + salary +
               ", Department: " + department;
    }
}

class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

public class EmployeeManagementSystem {

    static final String FILE_NAME = "employees.dat";
    static ArrayList<Employee> employees = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadFromFile();

        if (!login()) {
            System.out.println("❌ Invalid Login. Exiting...");
            return;
        }

        int choice;
        do {
            System.out.println("\n===== Employee Management Menu =====");
            System.out.println("1. Add Employee");
            System.out.println("2. Display All Employees");
            System.out.println("3. Search Employee by ID");
            System.out.println("4. Update Employee Salary");
            System.out.println("5. Delete Employee");
            System.out.println("6. Display Sorted Employees (By Salary)");
            System.out.println("7. Display Departments");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            try {
                switch (choice) {
                    case 1 -> addEmployee();
                    case 2 -> displayEmployees();
                    case 3 -> searchEmployee();
                    case 4 -> updateSalary();
                    case 5 -> deleteEmployee();
                    case 6 -> sortEmployees();
                    case 7 -> displayDepartments();
                    case 8 -> saveToFile();
                    default -> System.out.println("⚠ Invalid Choice");
                }
            } catch (ValidationException e) {
                System.out.println("❌ " + e.getMessage());
            }

        } while (choice != 8);
    }

    static boolean login() {
        System.out.print("Username: ");
        String user = sc.next();
        System.out.print("Password: ");
        String pass = sc.next();
        return user.equals("admin") && pass.equals("admin123");
    }

    static void addEmployee() throws ValidationException {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();

        for (Employee e : employees)
            if (e.getId() == id)
                throw new ValidationException("Employee ID must be unique");

        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Salary: ");
        double salary = sc.nextDouble();
        if (salary <= 0)
            throw new ValidationException("Salary must be positive");

        sc.nextLine();
        System.out.print("Enter Department: ");
        String dept = sc.nextLine();
        if (dept.isEmpty())
            throw new ValidationException("Department cannot be empty");

        employees.add(new Employee(id, name, salary, dept));
        saveToFile();
        System.out.println("✅ Employee Added Successfully");
    }

    static void displayEmployees() {
        if (employees.isEmpty())
            System.out.println("No employees found");
        else
            employees.forEach(System.out::println);
    }

    static void searchEmployee() {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();
        for (Employee e : employees)
            if (e.getId() == id) {
                System.out.println(e);
                return;
            }
        System.out.println("❌ Employee Not Found");
    }

    static void updateSalary() throws ValidationException {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();

        for (Employee e : employees) {
            if (e.getId() == id) {
                System.out.print("Enter New Salary: ");
                double sal = sc.nextDouble();
                if (sal <= 0)
                    throw new ValidationException("Salary must be positive");

                e.setSalary(sal);
                saveToFile();
                System.out.println("✅ Salary Updated");
                return;
            }
        }
        System.out.println("❌ Employee Not Found");
    }

    static void deleteEmployee() {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();
        employees.removeIf(e -> e.getId() == id);
        saveToFile();
        System.out.println("✅ Employee Deleted (if existed)");
    }

    static void sortEmployees() {
        employees.sort(Comparator.comparingDouble(Employee::getSalary));
        displayEmployees();
    }

    static void displayDepartments() {
        HashSet<String> departments = new HashSet<>();
        for (Employee e : employees)
            departments.add(e.getDepartment());

        System.out.println("Departments:");
        departments.forEach(System.out::println);
    }

    static void saveToFile() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(employees);
        } catch (IOException e) {
            System.out.println("⚠ File Save Error");
        }
    }

    static void loadFromFile() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            employees = (ArrayList<Employee>) ois.readObject();
        } catch (Exception ignored) {
        }
    }
}

