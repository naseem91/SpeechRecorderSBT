/*
 * Date  : 05.11.2014
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.db.speech;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML adapter class to marshal java.util.Date properties to simple date format without time and timezone information
 * to store birth dates for example.
 * JAXB using XmlSchemaType(name="date") annotation does not suppress timezone (e.g.: 1966-12-27+01:00)  
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class XMLDateAdapter extends XmlAdapter<String, Date> {
    
    public final static String SIMPLE_DATE_FORMAT_STRING = "yyyy-MM-dd";
    private DateFormat simpleDateFormat;
    
    public XMLDateAdapter(){
        super();
        simpleDateFormat=new SimpleDateFormat(SIMPLE_DATE_FORMAT_STRING);
    }
    
    public String marshal(Date date) throws Exception {
        return simpleDateFormat.format(date);
    }

    public Date unmarshal(String dateString) throws Exception {
        return simpleDateFormat.parse(dateString);
    }
}
