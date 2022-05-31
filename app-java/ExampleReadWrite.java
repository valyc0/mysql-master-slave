import java.sql.*;

public class ExampleReadWrite {
 // JDBC driver name and database URL                                                                    
 static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
 static final String DB_URL = "jdbc:mysql:replication://localhost:4406,localhost:5506/mydb";

 // Database credentials                                                                                
 static final String USER = "root";
 static final String PASS = "111";

 public static void main(String[] args) {
  Connection conn = null;
  Statement stmt = null;
  try {
   // Register JDBC driver                                                                       
   Class.forName("org.mariadb.jdbc.Driver");

   // Open a connection                                                                          
   while (true) {
    conn = DriverManager.getConnection(DB_URL, USER, PASS);
    stmt = conn.createStatement();

    String insert_sql = "INSERT INTO tbl_test (data) VALUES (1)";
    String get_hostname = "SELECT @@hostname";
    String read_sql = "SELECT @@hostname AS hostname, count(id) AS count FROM tbl_test";

    // Turn off readonly so write is forwarded to master - HAProxy port 3307
    conn.setReadOnly(false);
    conn.setAutoCommit(false);
    ResultSet hrs = stmt.executeQuery(get_hostname);
    stmt.executeUpdate(insert_sql);
    conn.commit();

    if (hrs.next()) {
     String write_hostname = hrs.getString(1);
     System.out.println("[WRITE] Hostname: " + write_hostname);
    }

    // Turn on readonly so read is forwarded to master/slave(s) - HAProxy port 3308
    conn.setReadOnly(true);
    ResultSet rs = stmt.executeQuery(read_sql);

    while (rs.next()) {
     String hostname = rs.getString("hostname");
     int row_count = rs.getInt("count");
     System.out.println("[READ ] Hostname: " + hostname + " | Row counts: " + row_count);
     System.out.println("");
    }
    rs.close();
    stmt.close();
    conn.close();
    // Pause for 2 seconds before loop
    Thread.sleep(2000);
   }

  } catch (SQLException se) {
   se.printStackTrace();
  } catch (Exception e) {
   e.printStackTrace();
  } finally {
   try {
    if (stmt != null)
     stmt.close();
   } catch (SQLException se2) {}
   try {
    if (conn != null)
     conn.close();
   } catch (SQLException se) {
    se.printStackTrace();
   }
  }
  System.out.println("Goodbye!");
 }
}
