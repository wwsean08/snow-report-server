package com.wwsean08.snow.server;

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import com.wwsean08.snow.common.ReportPOJO;

public class ResponseParser
{
    public static ReportPOJO getReportPOJO(InputStream responseContents)
    {
        ReportPOJO pojo = new ReportPOJO();
        try
        {
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            xmlif.setProperty(
                    XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
                    Boolean.TRUE);
            xmlif.setProperty(
                    XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
                    Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
            // set the IS_COALESCING property to true
            // to get whole text data as one event.
            xmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            
            try
            {
                XMLEventReader r = null;
                r = xmlif.createXMLEventReader(responseContents);
                // iterate as long as there are more events on the input stream
                while (r.hasNext())
                {
                    XMLEvent e = r.nextEvent();
                    if (e.isStartElement())
                    {
                        if (hasStartTagName(e, "snow_date"))
                        {
                            pojo.setLastUpdateTime(getCharacters(r));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                        else if (hasStartTagName(e, "open_flag"))
                        {
                            pojo.setOpenStatus(getCharacters(r));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                        else if (hasStartTagName(e, "id"))
                        {
                            pojo.setId(Integer.parseInt(getCharacters(r)));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                        else if (hasStartTagName(e, "reporting_state"))
                        {
                            pojo.setResortLocation(getCharacters(r));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                        else if (hasStartTagName(e, "name"))
                        {
                            pojo.setResortName(getCharacters(r));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                        else if (hasStartTagName(e, "lifts_open"))
                        {
                            pojo.setLiftsOpen(Integer.parseInt(getCharacters(r)));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                        else if (hasStartTagName(e, "reported_snow_fall"))
                        {
                            pojo.setSnowInLast24Hours(Integer.parseInt(getCharacters(r)));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                        else if (hasStartTagName(e, "reported_snow_fall_72"))
                        {
                            pojo.setSnowInLast48Hours(Integer.parseInt(getCharacters(r)));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                        else if (hasStartTagName(e, "num_trails_slopes_open"))
                        {
                            pojo.setTrailsOpen(Integer.parseInt(getCharacters(r)));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                        else if (hasStartTagName(e, "surface_primary"))
                        {
                            pojo.setSurfaceType(getCharacters(r));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                        else if (hasStartTagName(e, "call_ahead_phone"))
                        {
                            pojo.setPhoneNumber(getCharacters(r));
                            pojo.setCreationTime(System.currentTimeMillis());
                        }
                    }
                }
            }
            catch (XMLStreamException ex)
            {
                System.out.println(ex.getMessage());
                if (ex.getNestedException() != null)
                {
                    ex.getNestedException().printStackTrace();
                }
                return null;
            }
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
        return pojo;
    }
    
    private static String getCharacters(XMLEventReader rdr) throws XMLStreamException
    {
        XMLEvent e = rdr.nextEvent();
        if (e.isCharacters())
        {
            return e.asCharacters().getData();
        }
        else
        {
            return null;
        }
    }
    
    private static boolean hasStartTagName(XMLEvent e, String name)
    {
        return e.asStartElement().getName().getLocalPart().equals(name);
    }
}
