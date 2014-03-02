package com.wwsean08.snow.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.Gson;
import com.wwsean08.snow.common.ReportPOJO;

/**
 * The cache is actually an in memory database being run by an h2 database. the
 * basic schema of the table which will store the cached items is <primary key>,
 * <update time>, <resort id>, <json data>. If there is a row in the database
 * for the item and it has not expired that will be returned, otherwise a rest
 * request will be performed to get the item and it will be persisted in the
 * database.
 * 
 * @author wwsean08
 * 
 */
public class Cache
{
    private final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private Connection connection;
    
    public Cache() throws SQLException, ClassNotFoundException
    {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.
                getConnection("jdbc:h2:~/test", PrivateConstants.dbUserName, PrivateConstants.dbPass);
        connection = conn;
    }
    
    /**
     * Create the table which will contain the ski report data (if it doesn't
     * already exist)
     * 
     * @throws SQLException
     */
    public void createTable() throws SQLException
    {
        // TODO: come up with a better size for the report varchar
        String statement = "CREATE MEMORY TABLE IF NOT EXISTS reportCache (ID INT AUTO_INCREMENT PRIMARY KEY, resortId INT, reportTime BIGINT, report VARCHAR(2147483647));";
        connection.prepareCall(statement).execute();
    }
    
    /**
     * Checks the cache for a report within a certain timeframe (TBD) for the
     * given id, if none gets it from the api, inserts it into the database and
     * returns it
     * 
     * @param id
     *            the id of the resort
     * @return
     * @throws SQLException
     */
    public ReportPOJO getReport(int id) throws SQLException
    {
        // used to determine if a report is too old
        long currentTime = System.currentTimeMillis();
        long oldTimeThreshold = currentTime - 60 * 1000 * 1000;
        
        String sql = "SELECT report FROM reportCache WHERE resortId=" + id + " AND reportTime > " + oldTimeThreshold;
        PreparedStatement statment = connection.prepareStatement(sql);
        ResultSet results = statment.executeQuery();
        ReportPOJO report = null;
        if (results.first())
        {
            Gson GSON = new Gson();
            report = GSON.fromJson(results.getString("report"), ReportPOJO.class);
        }
        else
        {
            ReportRequest request = new ReportRequest();
            report = request.getReport(id);
            // TODO: escape the json because of all the quotes that will be in
            // it
            sql = "INSERT INTO reportCache (resortId, reportTime, report) VALUES (";
            // the result set is empty perform a get request (in a separate
            // future thread) and pass that report to insertReportIntoCache
        }
        return report;
    }
    
    /**
     * Inserts a new report into the database for future use by the cace
     * 
     * @param report
     */
    public void insertReportIntoCache(ReportPOJO report)
    {
        // lock table
        // check if item exists
        // if it doesn't insert
        // if it does do nothing
        // unlock
    }
    
    /**
     * Clears out old data when called. This should prevent lots of memory being used by the
     * in memory database.
     */
    public void cleanup()
    {
        // 1000*60*60 = 1 hour in milliseconds
        long oldThreshold = System.currentTimeMillis() - (1000 * 60 * 60);
        String sql = "SELECT ID FROM reportCache WHERE reportTime < " + oldThreshold;
        try
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            if (statement.execute())
            {
                Statement deleteStatement = connection.createStatement();
                sql = "DELETE FROM reportCache WHERE ID=";
                while (statement.getMoreResults() && statement.getUpdateCount() != -1)
                {
                    ResultSet result = statement.getResultSet();
                    int ID = result.getInt("ID");
                    deleteStatement.addBatch(sql + ID);
                }
                deleteStatement.executeBatch();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * @throws SQLException
     */
    public void closeConnection() throws SQLException
    {
        connection.close();
    }
    
    @Override
    public void finalize()
    {
        try
        {
            if (!connection.isClosed())
            {
                connection.close();
            }
            else
            {
                connection = null;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
