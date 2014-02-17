package com.wwsean08.snow.server;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.wwsean08.snow.common.ReportPOJO;

public class ReportAPITest
{
    @Test
    public void getReportTest()
    {
        ReportPOJO testReport = new ReportPOJO();
        Gson GSON = new Gson();
        testReport.setLastUpdateTime(new Date().toString());
        testReport.setLiftsOpen(2);
        testReport.setOpenStatus("Open");
        testReport.setResortLocation("Rhode Island");
        testReport.setResortName("Yawgoo Valley");
        testReport.setSnowInLast24Hours(0);
        testReport.setSnowInLast48Hours(3);
        testReport.setSurfaceType("Machine Groomed");
        testReport.setTrailsOpen(8);
        Assert.assertEquals(testReport.toString(), GSON.toJson(testReport));
    }
}
