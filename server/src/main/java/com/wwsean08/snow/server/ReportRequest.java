package com.wwsean08.snow.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.wwsean08.snow.common.ReportPOJO;

public class ReportRequest
{
    public ReportPOJO getReport(int id)
    {
        URI url;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try
        {
            url = new URI("http://www.onthesnow.com/ots/webservice_tools/xml_samples/getResortSnowReportMobile.xml");
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                InputStream responseContents = response.getEntity().getContent();
                ReportPOJO pojo = ResponseParser.getReportPOJO(responseContents);
                return pojo;
            }
            else
            {
                System.out.println("Error occured during request");
                System.out.println(response.getStatusLine().toString());
            }
        }
        catch (URISyntaxException | IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
