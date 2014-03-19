/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ringcentral.qa.tool.pseudolocalizationtool;

import com.ringcentral.qa.xmn.i18n.pseudolocalization.tool.FileCollector;
import java.util.regex.Pattern;
import org.junit.Test;


/**
 *
 * @author julia.lin
 */
public class TestFileCollector {
    
//    @Test
    public void testCollectFile() {
//        FileCollector collector = new FileCollector("D:\\Projects\\amarosa_6-3_TELUS\\JEDI\\jedi-assembly\\target\\jedi-6.3.0-TELUS-XMN-SNAPSHOT\\localStorage", ".*[\\\\/]en.js$", "WebBatch.txt");
        FileCollector collector = new FileCollector("D:\\Projects\\amarosa_6-3_TELUS\\JEDI\\localStorage", ".*[\\\\/]en.js$", "WebBatch.txt");
        collector.collect();
    }
    
//    @Test
//    public void testFilter() {
//        String fileName = "D:\\Projects\\amarosa_6-2_SM-XMN\\JEDI\\localStorage\\rc-web\\repository\\browser\\default\\js\\accordians\\announcement\\en.js";
//        String regex = ".*[\\\\/]en.js$";
//        Pattern pattern = Pattern.compile(regex);
//        System.out.println(pattern.matcher(fileName).find());
//    }
    
    @Test
    public void testReplaceStrConnector() {
      String str =  "{HINT_TEXT:\"If you make international calls, the included plan mon the applicable international rate.\","+
    "AUTO_PURCHASE_DESCRIPTION:\"Auto Purchase feature ensures you will never run out of calling credits. The \" +"+
     " \"selected package will be automatically purchased when you are running low on calling redits, which prevents any potential interruption of service. Purchased funds will \" +\"" +
                                "\"rnths.\"," +
    "AUTO_PURCHASE_FIELD_LABEL:\"Auto-Purchase\"," +
    "CALLING_CREDITS_PACKAGE_FIELD_LABEL:\"Calling Credits Package\"," +
    "WINDOW_TITLE:\"Auto-Purchase\"," +
    "PACKAGES_INFO:\"(equivalent to {0} Plan minutes at {1})\"," +
    "REDIO_GROUP_HINT_TEXT:\"<b>Plan and DigitalLine </b><br/>Plan minutes are approximate values based on your available Calling Credits, and are calculated based on the domestic rate of {0} for your account. \" +" +
    "                \"If you make international calls, the included plan mon the applicable international rate.\"" +
     "}\"";
        System.out.println(str);
        str = str.replaceAll("(['\"])\\s*\\+\\s*\\1", "");
        System.out.println(str);
    }
}
