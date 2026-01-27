package session1.mockapi;

public class Department {
    public String department;
    public String head;
    public int numStudents;
    public int facultyCount;
    public int availableToday;
    public String[] courses;

    public Department(String department, String head, int numStudents, int facultyCount, int availableToday, String[] courses) {
        this.department = department;
        this.head = head;
        this.numStudents = numStudents;
        this.facultyCount = facultyCount;
        this.availableToday = availableToday;
        this.courses = courses;
    }
}
