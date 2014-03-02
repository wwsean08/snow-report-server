package com.wwsean08.snow.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.wwsean08.snow.common.ReportPOJO;

/**
 * The cache is actually an in memory database being run by an h2 database. the
 * basic schema of the table which will store the cached items is <primary key>,
 * <update time>, <resort id>, <json data>. If there is a row in the database
 * for the item and it has not expired that will be returned, otherwise a rest
 * request will be performed to get the item and it will be persisted in the
 * database.
 * 
 * @author wwsea_000
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
        String statement = "CREATE MEMORY TABLE IF NOT EXISTS reportCache (ID INT PRIMARY KEY, resortId INT, reportTime BIGINT, report VARCHAR(2147483647));";
        connection.prepareCall(statement).execute();
    }
    
    /**
     * Checks the cache for a report within a certain timeframe (TBD) for the
     * given id, if none returns null.
     * 
     * @param id
     * @return
     */
    public ReportPOJO getReportFromCache(int id)
    {
        
        return null;
    }
    
    /**
     * Inserts a new report into the database for future use by the cace
     * 
     * @param report
     */
    public void insertReportIntoCache(ReportPOJO report)
    {
        
    }
    
    /**
     * Clears out old and unused data from time to time. This should prevent
     * lots of memory being used by the in memory database
     */
    public void cleanup()
    {
        
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
            
        }
    }
}
