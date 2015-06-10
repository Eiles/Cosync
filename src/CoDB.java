/**
 * Created by elie on 18/05/15.
 */
import java.sql.*;
public class CoDB {

        Connection c = null;
        PreparedStatement insertBatch;
        PreparedStatement updateBatch;
        String insertFileSQL = "INSERT INTO FILES"
            + "(PATH,DATE,SUPPRESSED,MODIFIEDAT) VALUES"
            + "(?,?,?,?)";

        String updateFileSQL = "UPDATE FILES "
            + "SET DATE=?, SUPPRESSED=?, MODIFIEDAT=? WHERE PATH=?" ;

        int insertSize=0;
        int updateSize=0;

        CoDB()
                throws SQLException {
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:cosync.db");
            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
            System.out.println("Opened database successfully");
            c.setAutoCommit(false);
        }

        public void update(String sql)
                throws SQLException{
            Statement stmt = c.createStatement();
            try{
                stmt = c.createStatement();
                stmt.executeUpdate(sql);
                //System.out.println("Query executed successfully");
                c.commit();
            }catch(SQLException se){
                //Handle errors for JDBC
                se.printStackTrace();
            }finally{
                try{
                    if(stmt!=null)
                        stmt.close();
                }catch(SQLException se2){
                }

            }//end try
        }

    /*public Cofile query(String sql) throws SQLException{
        Statement stmt = c.createStatement();
        Cofile ret=null;
        try{
            stmt = c.createStatement();
            ResultSet res=stmt.executeQuery(sql);
            if(res.next())
                ret =new Cofile(res.getString("PATH"), res.getString("FILENAME"), res.getString("DATE"));

            //System.out.println("Query executed successfully");
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();

        }
        finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            return ret;
        }
    }*/

    public long getDateForFile(String path,String filename)
            throws SQLException {
        Statement stmt = c.createStatement();
        long ret = 0;
        try {
            stmt = c.createStatement();
            ResultSet res = stmt.executeQuery("SELECT DATE FROM FILES WHERE PATH='" + path  + "' LIMIT 1");
            if (res.next())
                ret = res.getLong(1);
            c.commit();
            //System.out.println("Query executed successfully");
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();

        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            return ret;
        }
    }
    public boolean getSuppressedForFile(String path,String filename)
            throws SQLException {
        Statement stmt=null;
        boolean ret = false;
        try {
            stmt = c.createStatement();
            ResultSet res = stmt.executeQuery("SELECT SUPPRESSED FROM FILES WHERE PATH='" + path  + "' LIMIT 1");
            if (res.next())
                ret = res.getBoolean(1);
            c.commit();
            //System.out.println("Query executed successfully");
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();

        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            return ret;
        }
    }

    public int getInsertSize() {
        return insertSize;
    }

    public void setInsertSize(int insertSize) {
        this.insertSize = insertSize;
    }

    public int getUpdateSize() {
        return updateSize;
    }

    public void setUpdateSize(int updateSize) {
        this.updateSize = updateSize;
    }

    public void prepareInsertBatch(String sql)
            throws SQLException {
        this.insertBatch=c.prepareStatement(sql);
    }
    public void prepareUpdateBatch(String sql)
            throws SQLException {
        this.updateBatch=c.prepareStatement(sql);
    }

    public void addForBatchInsert(String path,String name, long date,int suppressed)
            throws SQLException {
            insertBatch.setString(1, path);
            insertBatch.setLong(2, date);
            insertBatch.setInt(3, suppressed);
            insertBatch.setLong(4, System.currentTimeMillis());
            insertBatch.addBatch();
            insertSize++;

        }

        public void executeBatchInsert()
                throws SQLException {
            if(this.getInsertSize() == 0)
                return;
            insertBatch.executeBatch();
            c.commit();
            this.setInsertSize(0);
            insertBatch.close();
            prepareInsertBatch(insertFileSQL);
        }

        public void addForBatchUpdate(String path, long date)
                throws SQLException {
            updateBatch.setLong(1, date);
            updateBatch.setInt(2,0);
            updateBatch.setLong(3, System.currentTimeMillis());
            updateBatch.setString(4, path);

            updateBatch.addBatch();
            updateSize++;
        }

        public void executeBatchUpdate()
                throws SQLException {
            if(this.getUpdateSize() == 0)
                return;
            updateBatch.executeBatch();
            c.commit();
            this.setUpdateSize(0);
            updateBatch.close();
            prepareUpdateBatch(updateFileSQL);
        }

        public String getFilePathById(int id) throws Exception {
            Statement stmt=null;
            String ret=null;
            stmt = c.createStatement();
            ResultSet res = stmt.executeQuery("SELECT PATH FROM FILES WHERE ID='" + id  + "' LIMIT 1");
            if (res.next())
                ret = res.getString(1);
            else{
                throw new Exception();
            }
            c.commit();
            //System.out.println("Query executed successfully");
            if (stmt != null)
                stmt.close();
            return ret;
        }

        public ResultSet getFiles() throws SQLException {
            Statement stmt=null;
            String ret=null;
            stmt = c.createStatement();
            ResultSet res = stmt.executeQuery("SELECT PATH FROM FILES");
            c.commit();
            //System.out.println("Query executed successfully");
            return res;
        }


}
