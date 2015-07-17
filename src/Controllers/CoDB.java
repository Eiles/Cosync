package Controllers; /**
 * Created by elie on 18/05/15.
 */
import java.sql.*;

public class CoDB {

        Connection c = null;
        PreparedStatement insertFileBatch;
        PreparedStatement updateFileBatch;
        PreparedStatement insertLastDBBatch;
        PreparedStatement updateLastDBBatch;
        String insertFileSQL = "INSERT INTO FILES"
            + "(PATH,DATE,SUPPRESSED,MODIFIEDAT) VALUES"
            + "(?,?,?,?)";

        String updateFileSQL = "UPDATE FILES "
            + "SET DATE=?, SUPPRESSED=?, MODIFIEDAT=? WHERE PATH=?" ;

        String insertLastDBSQL = "INSERT INTO LASTDB"
            + "(SYSTEM, UPDATEDATE) VALUES"
            + "(?,?)";

        String updateLastDBSQL = "UPDATE LASTDB " +
                "SET UPDATEDATE=? WHERE SYSTEM=?";

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

        CoDB(String name)
            throws SQLException {
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:"+name+".db");
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

    /*public Models.Models.Cofile query(String sql) throws SQLException{
        Statement stmt = c.createStatement();
        Models.Models.Cofile ret=null;
        try{
            stmt = c.createStatement();
            ResultSet res=stmt.executeQuery(sql);
            if(res.next())
                ret =new Models.Models.Cofile(res.getString("PATH"), res.getString("FILENAME"), res.getString("DATE"));

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

    public long getDateForFile(String path)
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

    public ResultSet getModifiedFiles(long date) throws SQLException {
        Statement stmt=null;
        String ret=null;
        stmt = c.createStatement();
        ResultSet res = stmt.executeQuery("SELECT * FROM FILES WHEN MODIFIEDAT > "+date);
        c.commit();
        //System.out.println("Query executed successfully");
        return res;
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
        this.insertFileBatch =c.prepareStatement(sql);
    }
    public void prepareUpdateBatch(String sql)
            throws SQLException {
        this.updateFileBatch =c.prepareStatement(sql);
    }

    public void prepareUpdateLastDBBatch(String sql)
            throws SQLException {
        this.updateLastDBBatch=c.prepareStatement(sql);
    }

    public void prepareInsertLastDBBatch(String sql)
            throws SQLException {
        this.updateLastDBBatch=c.prepareStatement(sql);
    }

    public void addForBatchInsert(String path,String name, long date,int suppressed)
            throws SQLException {
            insertFileBatch.setString(1, path);
            insertFileBatch.setLong(2, date);
            insertFileBatch.setInt(3, suppressed);
            insertFileBatch.setLong(4, System.currentTimeMillis());
            insertFileBatch.addBatch();
            insertSize++;

        }

        public void executeBatchInsert()
                throws SQLException {
            if(this.getInsertSize() == 0)
                return;
            insertFileBatch.executeBatch();
            c.commit();
            this.setInsertSize(0);
            insertFileBatch.close();
            prepareInsertBatch(insertFileSQL);
        }

        public void addForBatchUpdate(String path, long date)
                throws SQLException {
            updateFileBatch.setLong(1, date);
            updateFileBatch.setInt(2, 0);
            updateFileBatch.setLong(3, System.currentTimeMillis());
            updateFileBatch.setString(4, path);

            updateFileBatch.addBatch();
            updateSize++;
        }

        public void executeBatchUpdate()
                throws SQLException {
            if(this.getUpdateSize() == 0)
                return;
            updateFileBatch.executeBatch();
            c.commit();
            this.setUpdateSize(0);
            updateFileBatch.close();
            prepareUpdateBatch(updateFileSQL);
        }

    public void addForBatchLastDBUpdate(String system)
            throws SQLException {
        updateLastDBBatch.setLong(1, System.currentTimeMillis());
        updateLastDBBatch.setString(2, system);
        updateLastDBBatch.addBatch();
    }

    public void executeBatchLastDBUpdate()
            throws SQLException {
        if(this.getInsertSize() == 0)
            return;
        updateLastDBBatch.executeBatch();
        c.commit();
        this.setInsertSize(0);
        updateLastDBBatch.close();
        prepareUpdateLastDBBatch(updateLastDBSQL);
    }

    public void executeBatchLastDBInsert()
            throws SQLException {
        if(this.getInsertSize() == 0)
            return;
        insertLastDBBatch.executeBatch();
        c.commit();
        this.setInsertSize(0);
        insertLastDBBatch.close();
        prepareUpdateLastDBBatch(insertLastDBSQL);
    }

    public void addForBatchLastDBInsert(String system)
            throws SQLException {
        updateLastDBBatch.setLong(1, System.currentTimeMillis());
        updateLastDBBatch.setString(2, system);
        updateLastDBBatch.addBatch();
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
            ResultSet res = stmt.executeQuery("SELECT * FROM FILES");
            c.commit();
            //System.out.println("Query executed successfully");
            return res;
        }

        public long getLastUpdate(String key)  {
            Statement stmt=null;
            long ret=0;
            try {
                stmt = c.createStatement();
                ResultSet res = stmt.executeQuery("SELECT UPDATEDATE FROM LASTDB WHERE SYSTEM='" + key  + "' LIMIT 1");
                if (res.next())
                    ret = res.getLong("UPDATEDATE");
                else{
                    ret= 0;
                }
                c.commit();
            }
            catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
            return ret;
        }

}
