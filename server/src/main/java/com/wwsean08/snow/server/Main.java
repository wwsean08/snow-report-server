package com.wwsean08.snow.server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main class.
 * 
 */
public class Main
{
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:8080/";
    
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
     * application.
     * 
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer()
    {
        // create a resource config that scans for JAX-RS resources and
        // providers
        // in com.wwsean08.snow.server package
        final ResourceConfig rc = new ResourceConfig().packages("com.wwsean08.snow.server");
        // Create the table if it doesn't already exist on startup
        try
        {
            Cache cache = new Cache();
            cache.createTable();
            setupCleanupSchedule(cache);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
    
    /**
     * Will schedule a thread to perform a database cleanup every hour and 30
     * minutes
     * 
     * @param cache
     */
    private static void setupCleanupSchedule(final Cache cache)
    {
        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        Runnable cleanup = new Runnable()
        {
            @Override
            public void run()
            {
                cache.cleanup();
            }
        };
        scheduler.scheduleAtFixedRate(cleanup, 90, 90, TimeUnit.MINUTES);
    }
    
    /**
     * Main method.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}
