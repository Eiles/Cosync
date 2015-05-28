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

        public void update(String sql) throws SQLException{
            Statement stmt = c.createStatement();
            try{
                stmt = c.createStatement();
                stmt.executeUpdate(sql);
                //System.out.println("Query executed successfully");
            }catch(SQLException se){
                //Handle errors for JDBC
                se.printStackTrace();

            }
        }

    public ResultSet query(String sql) throws SQLException{
        Statement stmt = c.createStatement();
        ResultSet res=null;
        try{
            stmt = c.createStatement();
            res=stmt.executeQuery(sql);


            //System.out.println("Query executed successfully");
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();

        }
        return res;
    }

}