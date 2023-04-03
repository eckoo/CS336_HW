import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Problem3 {
	public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homework3", "root", "kkkk");
            //prompt the user for a search string
            try (Scanner scanner = new Scanner(System.in);) {
            	while(true) {
	                System.out.println("Welcome to the university database. Queries available:");
	                System.out.println("1. Search students by name.");
	                System.out.println("2. Search students by year.");
	                System.out.println("3. Search for students with a GPA >= threshold.");
	                System.out.println("4. Search for students with a GPA <= threshold.");
	                System.out.println("5. Get department statistics.");
	                System.out.println("6. Get class statistics.");
	                System.out.println("7. Execute an arbitrary SQL query.");
	                System.out.println("8. Exit the application.");
	
	                int choice;
	                do {
	                    System.out.print("\n" + "Which query would you like to run (1-8)? ");
	                    choice = scanner.nextInt();
	
	                    switch (choice) {
	                        case 1:
	                            System.out.print("Please enter the name: ");
	                            scanner.nextLine();
	                            String name = scanner.nextLine();
	                            searchStudentsByName(conn, name);
	                            break;
	                        case 2:
	                            System.out.print("Please enter the year: ");
	                            scanner.nextLine();
	                            String year = scanner.nextLine();
	                            searchStudentsByYear(conn, year);
	                            break;
	                        case 3:
	                            System.out.print("Please enter the threshold: ");
	                            scanner.nextLine();
	                            double gpaThresholdAbove = scanner.nextDouble();
	                            searchForGPAAbove(conn, gpaThresholdAbove);
	                            break;
	                        case 4:
	                            System.out.print("Please enter the threshold: ");
	                            scanner.nextLine();
	                            double gpaThresholdBelow = scanner.nextDouble();
	                            searchForGPABelow(conn, gpaThresholdBelow);
	                            break;
	                        case 5:
	                            System.out.print("Please enter the department: ");
	                            scanner.nextLine();
	                            String department = scanner.nextLine();
	                            getDepartmentStatistics(conn, department);
	                            break;
	                        case 6:
	                            System.out.print("Please enter the class name: ");
	                            scanner.nextLine();
	                            String className = scanner.nextLine();
	                            getClassStatistics(conn, className);
	                            break;
	                        case 7:
	                            System.out.print("Please enter the SQL query: ");
	                            scanner.nextLine();
	                            String sql = scanner.nextLine();
	                            executeSQLQuery(conn, sql);
		                        break;
	                        case 8:
	                            System.out.println("Goodbye.");
	                            System.exit(0);
	                        default:
	                            System.out.println("Invalid input. Please enter a number from 1 to 8.");
	                            break;
	                    }
	                } while (choice != 8);
            	}
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	//part A
	public static void searchStudentsByName(Connection conn, String name) throws SQLException {
		try {
			String query = "SELECT Students.first_name, Students.last_name, Students.id, " +
	                "SUM(CASE WHEN HasTaken.grade IN ('A', 'B', 'C', 'D') THEN Classes.credits ELSE 0 END) AS credits_completed, " +
	                "SUM(CASE WHEN HasTaken.grade = 'A' THEN 4 * Classes.credits " +
	                "WHEN HasTaken.grade = 'B' THEN 3 * Classes.credits " +
	                "WHEN HasTaken.grade = 'C' THEN 2 * Classes.credits " +
	                "WHEN HasTaken.grade = 'D' THEN 1 * Classes.credits ELSE 0 END) / " +
	                "SUM(Classes.credits) AS gpa, " +
	                "CASE WHEN Majors.dname IS NOT NULL THEN Majors.dname ELSE '' END AS major, " +
	                "CASE WHEN Minors.dname IS NOT NULL THEN Minors.dname ELSE '' END AS minor " +
	                "FROM Students " +
	                "JOIN HasTaken ON Students.id = HasTaken.sid " +
	                "JOIN Classes ON HasTaken.name = Classes.name " +
	                "LEFT JOIN Majors ON Students.id = Majors.sid " +
	                "LEFT JOIN Minors ON Students.id = Minors.sid " +
	                "GROUP BY Students.id, major, minor " +
	                "HAVING LOWER(Students.first_name) LIKE ? OR LOWER(Students.last_name) LIKE ?";
	        PreparedStatement nameResult = conn.prepareStatement(query);
	        nameResult.setString(1, "%" + name.toLowerCase() + "%");
	        nameResult.setString(2, "%" + name.toLowerCase() + "%");
	        ResultSet result = nameResult.executeQuery();
	        
	        int counter = 0;
	        while (result.next()) {
	        	System.out.println();
	            counter++;
	            String fullName = result.getString("last_name") + ", " + result.getString("first_name") + "\n";
	            String studentId = "ID: " + result.getString("id") + "\n";
	            String major = "Major: " + result.getString("major") + "\n";
	            String minor = "Minor: " + result.getString("minor") + "\n";
	            String gpa = "GPA: " + result.getFloat("gpa") + "\n";
	            String credits = "Credits: " + result.getInt("credits_completed") + "\n";
	            System.out.printf(fullName);
	            System.out.printf(studentId);
	            System.out.printf(major);
	            System.out.printf(minor);
	            System.out.printf(gpa);
	            System.out.printf(credits);
		    }
		        
		    if (counter == 0) {
		            System.out.println("No students found.");
		    } 
		    else {    
		    	System.out.println();
		    	System.out.printf("%d student(s) found.%n", counter);
		    }
		} 
		catch (SQLException e) {
        System.err.println("Error executing query: " + e.getMessage());
		}    
    }
	
	//part B
	public static void searchStudentsByYear(Connection conn, String year) throws SQLException {
	    try {
	        String query = "SELECT Students.first_name, Students.last_name, Students.id, " +
	                "SUM(CASE WHEN HasTaken.grade IN ('A', 'B', 'C', 'D') THEN Classes.credits ELSE 0 END) AS credits_completed, " +
	                "SUM(CASE WHEN HasTaken.grade = 'A' THEN 4 * Classes.credits " +
	                "WHEN HasTaken.grade = 'B' THEN 3 * Classes.credits " +
	                "WHEN HasTaken.grade = 'C' THEN 2 * Classes.credits " +
	                "WHEN HasTaken.grade = 'D' THEN 1 * Classes.credits ELSE 0 END) / " +
	                "SUM(Classes.credits) AS gpa, " +
	                "CASE WHEN Majors.dname IS NOT NULL THEN Majors.dname ELSE '' END AS major, " +
	                "CASE WHEN Minors.dname IS NOT NULL THEN Minors.dname ELSE '' END AS minor " +
	                "FROM Students " +
	                "JOIN HasTaken ON Students.id = HasTaken.sid " +
	                "JOIN Classes ON HasTaken.name = Classes.name " +
	                "LEFT JOIN Majors ON Students.id = Majors.sid " +
	                "LEFT JOIN Minors ON Students.id = Minors.sid " +
	                "GROUP BY Students.id, major, minor;";
	        
	        PreparedStatement yearResult = conn.prepareStatement(query);
	        ResultSet result = yearResult.executeQuery();
	        
	        ArrayList<String> listOfStudents = new ArrayList<String>();
	        
	        int counter = 0;
	        while(result.next()) {
	            int creditsCompleted = result.getInt("credits_completed");
	            String stringCredits = Integer.toString(creditsCompleted);
	            double gpa = result.getDouble("gpa");
	            String allNecessaryData = result.getString("last_name") + ", " +
	                                      result.getString("first_name") + "\n" +
	                                      "ID: " + result.getString("id") + "\n" +
	          	                          "Major: " + result.getString("major") + "\n" +
	        	                          "Minor: " + result.getString("minor") + "\n" +
	        	                          "GPA: " + String.format("%.3f", gpa) + "\n" +
	                                      "Credits: " + stringCredits + "\n";
	            
	            if ((creditsCompleted >= 90) & (year.equals("Sr"))) {
	                listOfStudents.add(allNecessaryData);    
	            } else if ((creditsCompleted >= 60) & (creditsCompleted <= 89) & (year.equals("Ju"))) {
	                listOfStudents.add(allNecessaryData);
	            } else if ((creditsCompleted >= 30)  & (creditsCompleted <= 59) & (year.equals("So"))) {
	                listOfStudents.add(allNecessaryData);
	            } else if ((creditsCompleted <= 29) & (year.equals("Fr"))){
	                listOfStudents.add(allNecessaryData);
	            }
	        }
	        
	        for (String s : listOfStudents) {
	            System.out.println(s);
	            counter++;
	        }
	        
	        if (counter == 0) {
	            System.out.println("No students found.");
	        } else {    
	            System.out.println();
	            System.out.printf("%d student(s) found.%n", counter);
	        }
	    } catch (SQLException e) {
	        System.err.println("Error executing query: " + e.getMessage());
	    }   
	}
	
	//part C
	public static void searchForGPAAbove(Connection conn, double threshold) throws SQLException {
	    try {
	        ArrayList<String> listOfStudents = new ArrayList<String>();
	        String query = "SELECT Students.first_name, Students.last_name, Students.id, " +
	                "SUM(CASE WHEN HasTaken.grade = 'A' THEN 4 * Classes.credits " +
	                "WHEN HasTaken.grade = 'B' THEN 3 * Classes.credits " +
	                "WHEN HasTaken.grade = 'C' THEN 2 * Classes.credits " +
	                "WHEN HasTaken.grade = 'D' THEN 1 * Classes.credits ELSE 0 END) / " +
	                "SUM(Classes.credits) AS gpa " +
	                "FROM Students " +
	                "JOIN HasTaken ON Students.id = HasTaken.sid " +
	                "JOIN Classes ON HasTaken.name = Classes.name " +
	                "GROUP BY Students.id " +
	                "HAVING gpa >= ?";
	        PreparedStatement gpaResult = conn.prepareStatement(query);
	        gpaResult.setDouble(1, threshold);
	        ResultSet result = gpaResult.executeQuery();

	        int counter = 0;
	        while(result.next()) {
	            counter++;
	            int sid = result.getInt("id");
	            double gpa = result.getDouble("gpa");
	            int creditsCompleted = 0;
	            String major = "";
	            String minor = "";
	            String creditsQuery = "SELECT SUM(CASE WHEN HasTaken.grade IN ('A', 'B', 'C', 'D') THEN Classes.credits ELSE 0 END) AS credits_completed, " +
	                    "Majors.dname AS major, " +
	                    "Minors.dname AS minor " +
	                    "FROM Students " +
	                    "JOIN HasTaken ON Students.id = HasTaken.sid " +
	                    "JOIN Classes ON HasTaken.name = Classes.name " +
	                    "LEFT JOIN Majors ON Students.id = Majors.sid " +
	                    "LEFT JOIN Minors ON Students.id = Minors.sid " +
	                    "WHERE Students.id = ? " +
	                    "GROUP BY Students.id, Majors.dname, Minors.dname";
	            PreparedStatement creditsResult = conn.prepareStatement(creditsQuery);
	            creditsResult.setInt(1, sid);
	            ResultSet resultCredit = creditsResult.executeQuery();
	            if(resultCredit.next()) {
	                creditsCompleted = resultCredit.getInt("credits_completed");
	                major = resultCredit.getString("major");
	                minor = resultCredit.getString("minor");
	            }
	            if (gpa >= threshold) {
	                String allNecessaryData = result.getString("last_name") + ", " + result.getString("first_name") + "\n"
	                        + "ID: " + sid + "\n"
	                        + "Major: " + major + "\n"
	                        + "Minor: " + minor + "\n"
	                        + "GPA: " + String.format("%.3f", gpa) + "\n"
	                        + "Credits: " + creditsCompleted + "\n";
	                listOfStudents.add(allNecessaryData);
	            }
	        }

	        for (String s:listOfStudents) {
	            System.out.println(s);
	        }
	        if (counter == 0) {
	            System.out.println("No students found.");
	        } else {
	            System.out.println();
	            System.out.printf("%d student(s) found.%n", counter);
	        }

	    } catch (SQLException e) {
	        System.err.println("Error executing query: " + e.getMessage());
	    }
	}
	
	//part D
	public static void searchForGPABelow(Connection conn, double threshold) throws SQLException {
	    try {
	        ArrayList<String> listOfStudents = new ArrayList<String>();
	        String query = "SELECT Students.first_name, Students.last_name, Students.id, " +
	                "SUM(CASE WHEN HasTaken.grade = 'A' THEN 4 * Classes.credits " +
	                "WHEN HasTaken.grade = 'B' THEN 3 * Classes.credits " +
	                "WHEN HasTaken.grade = 'C' THEN 2 * Classes.credits " +
	                "WHEN HasTaken.grade = 'D' THEN 1 * Classes.credits ELSE 0 END) / " +
	                "SUM(Classes.credits) AS gpa " +
	                "FROM Students " +
	                "JOIN HasTaken ON Students.id = HasTaken.sid " +
	                "JOIN Classes ON HasTaken.name = Classes.name " +
	                "GROUP BY Students.id " +
	                "HAVING gpa <= ?";
	        PreparedStatement gpaResult = conn.prepareStatement(query);
	        gpaResult.setDouble(1, threshold);
	        ResultSet result = gpaResult.executeQuery();

	        int counter = 0;
	        while(result.next()) {
	            counter++;
	            int sid = result.getInt("id");
	            double gpa = result.getDouble("gpa");
	            int creditsCompleted = 0;
	            String major = "";
	            String minor = "";
	            String creditsQuery = "SELECT SUM(CASE WHEN HasTaken.grade IN ('A', 'B', 'C', 'D') THEN Classes.credits ELSE 0 END) AS credits_completed, " +
	                    "Majors.dname AS major, " +
	                    "Minors.dname AS minor " +
	                    "FROM Students " +
	                    "JOIN HasTaken ON Students.id = HasTaken.sid " +
	                    "JOIN Classes ON HasTaken.name = Classes.name " +
	                    "LEFT JOIN Majors ON Students.id = Majors.sid " +
	                    "LEFT JOIN Minors ON Students.id = Minors.sid " +
	                    "WHERE Students.id = ? " +
	                    "GROUP BY Students.id, Majors.dname, Minors.dname";
	            PreparedStatement creditsResult = conn.prepareStatement(creditsQuery);
	            creditsResult.setInt(1, sid);
	            ResultSet resultCredit = creditsResult.executeQuery();
	            if(resultCredit.next()) {
	                creditsCompleted = resultCredit.getInt("credits_completed");
	                major = resultCredit.getString("major");
	                minor = resultCredit.getString("minor");
	            }
	            if (gpa >= threshold) {
	                String allNecessaryData = result.getString("last_name") + ", " + result.getString("first_name") + "\n"
	                        + "ID: " + sid + "\n"
	                        + "Major: " + major + "\n"
	                        + "Minor: " + minor + "\n"
	                        + "GPA: " + String.format("%.3f", gpa) + "\n"
	                        + "Credits: " + creditsCompleted + "\n";
	                listOfStudents.add(allNecessaryData);
	            }
	        }

	        for (String s:listOfStudents) {
	            System.out.println(s);
	        }
	        if (counter == 0) {
	            System.out.println("No students found.");
	        } else {
	            System.out.println();
	            System.out.printf("%d student(s) found.%n", counter);
	        }

	    } catch (SQLException e) {
	        System.err.println("Error executing query: " + e.getMessage());
	    }
	}
	
	//Part E
	public static void getDepartmentStatistics(Connection conn, String department) throws SQLException {
	    try {
	    	//query to get number of students and GPA for given department
	        String query = "SELECT COUNT(DISTINCT Students.id) AS num_students, " +
	                "ROUND(AVG(CASE WHEN HasTaken.grade = 'A' THEN 4 * Classes.credits " +
                    "WHEN HasTaken.grade = 'B' THEN 3 * Classes.credits " +
                    "WHEN HasTaken.grade = 'C' THEN 2 * Classes.credits " +
                    "WHEN HasTaken.grade = 'D' THEN 1 * Classes.credits ELSE 0 END) / " +
                    "AVG(Classes.credits), 2) AS avg_gpa " +
				     "FROM Students " +
				     "JOIN HasTaken ON Students.id = HasTaken.sid " +
				     "JOIN Classes ON HasTaken.name = Classes.name " +
				     "JOIN Majors ON Students.id = Majors.sid " +
				     "JOIN Departments ON Majors.dname = Departments.name " +
				     "WHERE Departments.name = ? ";
	        
	        PreparedStatement departmentResult = conn.prepareStatement(query);
	        departmentResult.setString(1, department);
	        ResultSet result = departmentResult.executeQuery();
	        
	        while(result.next()) {
	            int numStudents = result.getInt("num_students");
	            double gpa = result.getDouble("avg_gpa");
	            System.out.printf("Department: %s%n", department);
	            System.out.printf("Number of students: %d%n", numStudents);
	            System.out.printf("Average GPA: %.2f%n", gpa);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error executing query: " + e.getMessage());
	    }
	}
	
	//Part F
	public static void getClassStatistics(Connection conn, String className) throws SQLException {
	    try {
	        String query = "SELECT COUNT(DISTINCT IsTaking.sid) AS num_students_taking_class, " +
	                "SUM(CASE WHEN (HasTaken.grade = 'A') THEN 1 ELSE 0 END) AS num_a, " +
	                "SUM(CASE WHEN (HasTaken.grade = 'B') THEN 1 ELSE 0 END) AS num_b, " +
	                "SUM(CASE WHEN (HasTaken.grade = 'C') THEN 1 ELSE 0 END) AS num_c, " +
	                "SUM(CASE WHEN (HasTaken.grade = 'D') THEN 1 ELSE 0 END) AS num_d, " +
	                "SUM(CASE WHEN (HasTaken.grade = 'F') THEN 1 ELSE 0 END) AS num_f " +
	                "FROM IsTaking " +
	                "LEFT JOIN HasTaken ON (IsTaking.sid = HasTaken.sid) AND (IsTaking.name = HasTaken.name) " +
	                "WHERE (IsTaking.name = ?)";
	        
	        PreparedStatement classResult = conn.prepareStatement(query);
	        classResult.setString(1, className);
	        ResultSet result = classResult.executeQuery();
	        
	        while(result.next()) {
	            int numTaking = result.getInt("num_students_taking_class");
	            int numA = result.getInt("num_a");
	            int numB = result.getInt("num_b");
	            int numC = result.getInt("num_c");
	            int numD = result.getInt("num_d");
	            int numF = result.getInt("num_f");
	            
	            System.out.printf("%d students are currently enrolled %s.%n", numTaking);
	            System.out.printf("Grades of previous enrollees:%n");
	            System.out.printf("A %d%n", numA);
	            System.out.printf("B %d%n", numB);
	            System.out.printf("C %d%n", numC);
	            System.out.printf("D %d%n", numD);
	            System.out.printf("F %d%n", numF);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error executing query: " + e.getMessage());
	    }
	}
	
	//Part G
	public static void executeSQLQuery(Connection conn, String query) throws SQLException {
	    try {
	    	
	    	PreparedStatement queryStatement = conn.prepareStatement(query);
            ResultSet result = queryStatement.executeQuery();
           
           // Get metadata of result set
           ResultSetMetaData metaData = result.getMetaData();
           int columnCount = metaData.getColumnCount();
           
           // Print column headers
           for (int i = 1; i <= columnCount; i++) {
               System.out.print(metaData.getColumnName(i) + "\t");
           }
           System.out.println();
           
           // Print row data
           while (result.next()) {
               for (int i = 1; i <= columnCount; i++) {
                   System.out.print(result.getString(i) + "\t");
               }
               System.out.println();
           }
	    } catch (SQLException e) {
	        System.err.println("Error executing query: " + e.getMessage());
	    }
	}
	
}
	
