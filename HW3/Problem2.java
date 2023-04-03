import java.sql.*;
import java.util.ArrayList;
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
    private static final String[] Grades = {"A", "B", "C", "D", "F"};
    private static final int[] Credits = {3,4};
    private static final int NUM_STUDENTS = 100;
    private static final Random rand = new Random();
    
    
	public static void main(String[] args) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homework3", "root", "kkkk");
		//generates random departments
		PreparedStatement department = conn.prepareStatement("INSERT INTO Departments (name, campus) VALUES (?, ?)");
		for (String dept:Departments) {
            String campus = Campuses[rand.nextInt(Campuses.length)];
            department.setString(1, dept);
            department.setString(2, campus);
            department.executeUpdate();
        }
        
		//generates list of classes with random credits
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
		
		//generates random list of students with increasing ID #
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
		
		
		//populate majors table
		PreparedStatement majors = conn.prepareStatement("INSERT INTO Majors VALUES (?, ?)");
	    for (int i = 100000001; i <= 100000100; i++) {
	      int sid = i;
	      String major = Departments[rand.nextInt(Departments.length)];
	      majors.setInt(1, sid);
	      majors.setString(2, major);
	      majors.executeUpdate();
	    }
	    
		
	      //populate minors table
	    PreparedStatement minors = conn.prepareStatement("INSERT INTO Minors VALUES (?, ?)");
	    for (int i = 100000001; i <= 100000100; i++) {
		      int sid = i;
		      String minor = Departments[rand.nextInt(Departments.length)];
		      minors.setInt(1, sid);
		      minors.setString(2, minor);
		      minors.executeUpdate();
		    }
	    
	    
	    //populate isTaking, i use an arraylist to keep track of chosen courses
	    PreparedStatement isTakingClass = conn.prepareStatement("INSERT INTO IsTaking(sid, name) VALUES (?, ?)");
        //populate table for each student
        for (int i = 100000001; i <= 100000100; i++) {
        	ArrayList<String> chosenCourses = new ArrayList<>();
            while (chosenCourses.size() < 3) {
                //randomly choose course from the Course list
                String course = Course_Names[rand.nextInt(Course_Names.length)];

                PreparedStatement checkCourse = conn.prepareStatement("SELECT name FROM Classes WHERE name = ?");
                checkCourse.setString(1, course);
                ResultSet result = checkCourse.executeQuery();
                if (result.next()) {
                    //check if course has already been chosen for student
                    PreparedStatement checkIsTaking = conn.prepareStatement("SELECT * FROM IsTaking WHERE sid = ? AND name = ?");
                    checkIsTaking.setInt(1, i);
                    checkIsTaking.setString(2, course);
                    ResultSet result2 = checkIsTaking.executeQuery();
                    if (!result2.next()) {
                        //insert ID and course name into IsTaking table
                        isTakingClass.setInt(1, i);
                        isTakingClass.setString(2, course);
                        isTakingClass.executeUpdate();
                        chosenCourses.add(course);
                    }
                }
            }
        }
        
        //populate HasTaken field
        PreparedStatement hasTakenClass = conn.prepareStatement("INSERT INTO HasTaken(sid, name, grade) VALUES (?, ?, ?)");
        //populate table for each student
        for (int i = 100000001; i <= 100000100; i++) {
        	ArrayList<String> chosenCourses = new ArrayList<>();
        	int numCourses = rand.nextInt(35) + 1;
        	while (chosenCourses.size() < numCourses) {
                //randomly choose course from Course list
                String course = Course_Names[rand.nextInt(Course_Names.length)];
                String grade = Grades[rand.nextInt(Grades.length)];
                PreparedStatement checkCourse = conn.prepareStatement("SELECT name FROM Classes WHERE name = ?");
                checkCourse.setString(1, course);
                ResultSet result = checkCourse.executeQuery();
                if (result.next()) {
                    //check if course has already been chosen for student
                    PreparedStatement checkIsTaking = conn.prepareStatement("SELECT * FROM HasTaken WHERE sid = ? AND name = ?");
                    checkIsTaking.setInt(1, i);
                    checkIsTaking.setString(2, course);
                    ResultSet result2 = checkIsTaking.executeQuery();
                    if (!result2.next()) {
                        //insert student ID and course name into IsTaking table
                    	hasTakenClass.setInt(1, i);
                    	hasTakenClass.setString(2, course);
                    	hasTakenClass.setString(3, grade);
                    	hasTakenClass.executeUpdate();
                        chosenCourses.add(course);
                    }
                }
            }
        }
	    SQLWarning warning_majors = majors.getWarnings();
	    SQLWarning warning_minors = minors.getWarnings();
	    SQLWarning warning_students = students.getWarnings();
		SQLWarning warning_classes = classes.getWarnings();
		SQLWarning warning_department = department.getWarnings();
        SQLWarning warning_isTaken = isTakingClass.getWarnings();
        
        SQLWarning warning_hasTaken = hasTakenClass.getWarnings();
		
        
		while((warning_students != null) & (warning_classes != null) & (warning_department != null) & (warning_majors != null) & (warning_minors != null) & (warning_isTaken != null) & (warning_isTaken != null) & (warning_hasTaken != null)){
			warning_students = warning_students.getNextWarning();
			warning_classes = warning_classes.getNextWarning();
			warning_department = warning_department.getNextWarning();
			warning_majors = warning_majors.getNextWarning();
        	warning_isTaken = warning_isTaken.getNextWarning();
        	warning_hasTaken = warning_hasTaken.getNextWarning();
		}
		conn.close();
		System.out.println("Connection Closed =" + conn.isClosed());
		
	}
}