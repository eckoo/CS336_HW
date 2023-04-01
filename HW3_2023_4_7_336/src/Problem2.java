import java.sql.*;
import java.util.Random;

class Problem2{
	private static final String[] first_names = {"John", "Emma", "Michael", "Sophia", "William", "Olivia", "James",
            "Ava", "Benjamin", "Isabella", "Lucas", "Mia", "Henry", "Charlotte", "Mason", "Amelia", "Ethan", "Harper",
            "Alexander", "Evelyn", "Sebastian", "Abigail", "Daniel", "Emily", "Matthew", "Elizabeth", "Samuel", "Sofia",
            "Joseph", "Victoria"};
    private static final String[] last_names = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller",
            "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson",
            "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall", "Allen",
            "Young", "King"};
    private static final String[] Departments = {"Bio", "Chem", "CS", "Eng", "Math", "Phys"};
    private static final String[] Campuses = {"Busch", "CAC", "Livi", "CD"};
    private static final String[] Course_Names = {"Introduction to Computer Science", "Data Structures",
            "Algorithms", "Discrete Mathematics", "Operating Systems", "Computer Networks",
            "Database Systems", "Artificial Intelligence", "Computer Architecture", "Programming Languages", "Biochemistry and Molecular Biology",
            "Organic Chemistry I", "Organic Chemistry II", "Software Engineering", "Probability and Statistics", "Electricity and Magnetism",
            "Differential Equations", "Multivariable Calculus", "Electronic Devices", "Genetics and Genomics", "Quantum Mechanics", "Anatomy and Physiology",
            "Thermodynamics", "Physical Chemistry", "Ecology", "Signal Processing", "Zoology and Botany", "Inorganic Chemistry", "Principles of EE I", "Principles of EE II",
            "Astrophysics", "Computational Astrophysics", "Computer Vision", "Machine Learning", "Polymer Science", "Neuroscience", "Biotechnology Principles"};
    private static final int[] Credits = {3,4};
    private static final int NUM_STUDENTS = 100;
    private static final Random rand = new Random();
    
    
	public static void main(String[] args) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homework3", "root", "kkkk");
		
		//Generates Random Departments
		PreparedStatement department = conn.prepareStatement("INSERT INTO Departments (name, campus) VALUES (?, ?)");
		for (String dept:Departments) {
            String campus = Campuses[rand.nextInt(Campuses.length)];
            department.setString(1, dept);
            department.setString(2, campus);
            department.executeUpdate();
        }
        
		//Generates a list of classes with a random amount of credits
		PreparedStatement classes = conn.prepareStatement("INSERT INTO Classes(name, credits) VALUES (?, ?)");
		for (String courseName:Course_Names) {
            int credits = Credits[rand.nextInt(Credits.length)];
            PreparedStatement checkCourse = conn.prepareStatement("SELECT name FROM Classes WHERE name = ?");
            checkCourse.setString(1, courseName);
            ResultSet result = checkCourse.executeQuery();
            if (!result.next()) { // check if the course name already exists in the table
                classes.setString(1, courseName);
                classes.setInt(2, credits);
                classes.executeUpdate();
            }
        }
		
		//Generates a random list of students with an increasing student ID #
		PreparedStatement students = conn.prepareStatement("INSERT INTO Students (first_name, last_name, id) VALUES (?, ?, ?)");
		for (int i = 1; i <= NUM_STUDENTS; i++) {
            String firstName = first_names[rand.nextInt(first_names.length)];
            String lastName = last_names[rand.nextInt(last_names.length)];
            int id = 100000000 + i; // generate a 9-digit ID number
            students.setString(1, firstName);
            students.setString(2, lastName);
            students.setString(3,  Integer.toString(id));
            students.executeUpdate();
		}
		
		SQLWarning warning_students = students.getWarnings();
		SQLWarning warning_classes = classes.getWarnings();
		SQLWarning warning_department = department.getWarnings();
		
		while((warning_students != null) & (warning_classes != null) & (warning_department != null)){
			//System.out.println(warning.getMessage());
			warning_students = warning_students.getNextWarning();
			warning_classes = warning_classes.getNextWarning();
			warning_department = warning_department.getNextWarning();
		}
		conn.close();
		System.out.println("Connection Closed =" + conn.isClosed());
		
	}
}