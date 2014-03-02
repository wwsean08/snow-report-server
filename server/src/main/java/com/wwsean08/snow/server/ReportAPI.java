package com.wwsean08.snow.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.wwsean08.snow.common.ReportPOJO;

@Path("report")
public class ReportAPI
{
    private Properties prefs;
    private Cache cache;
    
    public ReportAPI()
    {
        // getPreferences("config.properties");
        try
        {
            cache = new Cache();
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getReport(@QueryParam("id") int id)
    {
        getReportById(id);
        return "Insert actual report here";
    }
    
    private void getReportById(int id)
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String token = prefs.getProperty("token");
        String domain = prefs.getProperty("domain");
        String requestURL = "http://services.onthesnow.com/axis2/services/SnowReport2009/lite/resort/snow/";
        requestURL += id + "/";
        requestURL += domain + "/";
        requestURL += token + "?lang=en-us";
        try
        {
            URI url = new URIBuilder()
                    .setScheme("http")
                    .setHost("services.onthesnow.com")
                    .setPath("/axis2/services/SnowReport2009/lite/resort/snow/" + domain + "/" + token)
                    .setParameter("lang", "en-us")
                    .build();
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                
            }
            else
            {
                System.out.println("Error occured during request");
                System.out.println(response.getStatusLine().toString());
            }
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTestReport(@QueryParam("id") int id) throws SQLException
    {
        String result = "";
        ReportPOJO report = cache.getReport(id);
        result = report.toString();
        return result;
    }
    
    public void getPreferences(String prefsLocation)
    {
        prefs = new Properties();
        try
        {
            Reader FR = new FileReader(new File(prefsLocation));
            prefs.load(FR);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
