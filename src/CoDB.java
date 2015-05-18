/**
 * Created by elie on 18/05/15.
 */
import java.sql.*;
public class CoDB {

        Connection c = null;
        CoDB(){
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:test.db");
            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
            System.out.println("Opened database successfully");
        }

        public void query(String sql) throws SQLException{
            Statement stmt = c.createStatement();
            try{
                stmt = c.createStatement();
                stmt.executeUpdate(sql);
                System.out.println("Query executed successfully");
            }catch(SQLException se){
                //Handle errors for JDBC
                se.printStackTrace();

            }finally{
                //finally block used to close resources
                try{
                    if(stmt!=null)
                        stmt.close();
                }catch(SQLException se2){
                }
                try{
                    if(c!=null)
                        c.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }//end finally try
            }//end try
        }

}