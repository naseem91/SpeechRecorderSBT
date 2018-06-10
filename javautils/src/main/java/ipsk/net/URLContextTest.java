/*
 * Date  : Jul 1, 2011
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.net;


import ipsk.net.URLContext;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class URLContextTest {

    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#getContextURL(URL,String)}.
     * @throws MalformedURLException 
     *
     */
    @Test
    public void testURLContextStringRelative1() throws MalformedURLException{
        String testappend="append%20with%20blanks/resources/foo";
        String userHome=System.getProperty("user.home");
        File homeDir=new File(userHome);
        URI homeUri= homeDir.toURI();
        URL context=new URL(homeUri+"/unit%20test/");
        URL targetURL=new URL(homeUri+"unit%20test/"+testappend);
        URL resURL=URLContext.getContextURL(context, testappend);
        Assert.assertTrue(targetURL.sameFile(resURL));
        
    }
    
//    /**
//     * Test method for
//     * {@link ipsk.net.URLContext#getURLContext(URL,String)}.
//     * @throws MalformedURLException 
//     *
//     */
//    @Test
//    public void testURLContextStringRelative1Win() throws MalformedURLException{
//        String testappend="append%20with%20blanks/resources/foo";
//        
//        URL context=new URL("file:C:/unit%20test/");
//        URL targetURL=new URL("file:C:/unit%20test/"+testappend);
//        URL resURL=URLContext.getContextURL(context, testappend);
//        Assert.assertTrue(targetURL.sameFile(resURL));
//        
//    }
//    
    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#getContextURL(URL,String)}.
     * @throws MalformedURLException 
     *
     */
    @Test
    public void testURLContextStringRelative2() throws MalformedURLException{
        String testappend="append%20with%20blanks/resources/foo";
        String userHome=System.getProperty("user.home");
        File homeDir=new File(userHome);
        URI homeUri= homeDir.toURI();
        URL context=new URL(homeUri+"/unit%20test/");
        URL targetURL=new URL(homeUri+"/unit%20test/"+testappend);
        URL resURL=URLContext.getContextURL(context, "file:"+testappend);
        Assert.assertTrue(targetURL.sameFile(resURL));
        
    }
    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#getContextURL(URL,String)}.
     * @throws MalformedURLException 
     *
     */
    @Test
    public void testURLContextStringInContext() throws MalformedURLException{
        String testappend="append%20with%20blanks/resources/foo";
        String userHome=System.getProperty("user.home");
        File homeDir=new File(userHome);
        URI homeUri= homeDir.toURI();
        URL context=new URL(homeUri+"/unit%20test/");
        URL targetURL=new URL(homeUri+"/unit%20test/"+testappend);
        URL resURL=URLContext.getContextURL(context, homeUri+"/unit%20test/"+testappend);
        Assert.assertTrue(targetURL.sameFile(resURL));
        
    }
    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#getContextURL(URL,String)}.
     * @throws MalformedURLException 
     * @throws URISyntaxException 
     *
     */
    @Test
    public void testURLContextURLInContextUseURIEncoding() throws MalformedURLException, URISyntaxException{
        String testappend="append with blanks/resources/foo";
        String userHome=System.getProperty("user.home");
        File contextDir=new File(userHome+"/unit test/");
//        URI cURI=new URI("file:"+userHome+"/unit test/");
        URI cURI=contextDir.toURI();
        URL context=cURI.toURL();
        File targetFile=new File(userHome+"/unit test/"+testappend);
        URL targetURL=targetFile.toURI().toURL();
        URL resURL=URLContext.getContextURL(context, targetURL.toExternalForm());
        Assert.assertTrue(targetURL.sameFile(resURL));
        
    }
    /**
     * Test method for
     * {@link ipsk.net.URLContext#getContextURL(URL,String)}.
     * @throws MalformedURLException 
     * @throws URISyntaxException 
     *
     */
    @Test
    public void testURLContextAbsURLOutOfContextUseURIEncoding() throws MalformedURLException, URISyntaxException{
        String testappend="append with blanks/resources/foo";
        String userHome=System.getProperty("user.home");
        File contextDir=new File(userHome+"/unit test/");
        File testFile=new File(userHome+"/unit test other dir/"+testappend);
        URL testURL=testFile.toURI().toURL();
//        URI cURI=new URI("file:"+userHome+"/unit test/");
        URI cURI=contextDir.toURI();
        URL context=cURI.toURL();
        File targetFile=new File(userHome+"/unit test/"+testappend);
        URL targetURL=targetFile.toURI().toURL();
        URL resURL=URLContext.getContextURL(context, testURL.toExternalForm());
        Assert.assertFalse(targetURL.sameFile(resURL));
        
    }
    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#getContextURL(URL,String)}.
     * @throws MalformedURLException 
     * @throws URISyntaxException 
     *
     */
    @Test
    public void testURLContextAbsURLNullContext() throws MalformedURLException, URISyntaxException{
    	String absUrlStr="http://www.phonetik.uni-muenchen.de/test.bla?q=foo";
    	URL absURL=new URL(absUrlStr);
        URL resURL=URLContext.getContextURL(null, absUrlStr);
        Assert.assertTrue(absURL.sameFile(resURL));
        
    }
    
    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#relativize(URL,URL)}.
     * @throws MalformedURLException     
     *
     */
    @Test
    public void testHttpURLRelativize() throws MalformedURLException{
        String absUrlStr="http://www.phonetik.uni-muenchen.de/prja/sessions////test.bla?q=foo#chapter1";
        String ctx="http://www.phonetik.uni-muenchen.de/prja/";
        String relPath=URLContext.relativize(new URL(ctx),new URL(absUrlStr));
        Assert.assertTrue(relPath.equals("sessions/test.bla?q=foo#chapter1"));
        
    }
    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#relativize(URL,URL)}.
     * @throws MalformedURLException     
     *
     */
    @Test
    public void testFileURLRelativize() throws MalformedURLException{
        String absUrlStr="file:/homes/foo/prja/sessions////test.bla";
        String ctx="file:/homes/foo//prja/";
        String relPath=URLContext.relativize(new URL(ctx),new URL(absUrlStr));
        Assert.assertTrue(relPath.equals("sessions/test.bla"));
        
    }
    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#relativize(URL,URL)}.
     * @throws MalformedURLException     
     *
     */
    @Test
    public void testWinFileURLRelativize() throws MalformedURLException{
        String absUrlStr="file:C:/Users/foo/prja/sessions////test.bla";
        String ctx="file:C:/Users/foo//prja/";
        String relPath=URLContext.relativize(new URL(ctx),new URL(absUrlStr));
        Assert.assertTrue(relPath.equals("sessions/test.bla"));
        
    }
    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#relativize(URL,URL)}.
     * @throws MalformedURLException     
     *
     */
    @Test
    public void testWinFileBsURLRelativize() throws MalformedURLException{
        String absUrlStr="file:C:\\Users\\\\foo\\prja\\sessions\\test.bla";
        String ctx="file:C:/Users/foo//prja/";
        String relPath=URLContext.relativize(new URL(ctx),new URL(absUrlStr));
        Assert.assertTrue(relPath.equals("sessions/test.bla"));
        
    }
    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#relativize(URL,URL)}.
     * @throws MalformedURLException     
     *
     */
    @Test
    public void testFailRelativize() throws MalformedURLException{
        String absUrlStr="file:C:\\Users\\\\foo\\prja\\sessions\\test.bla";
        String ctx="file:C:/Users/fo";
        String relPath=URLContext.relativize(new URL(ctx),new URL(absUrlStr));
        Assert.assertNull(relPath);
        
        absUrlStr="file:C:\\Users\\\\foo\\prja\\sessions\\test.bla";
        ctx="http://www.bla.com:/Users/foo///";
        relPath=URLContext.relativize(new URL(ctx),new URL(absUrlStr));
        Assert.assertNull(relPath);
        
    }
    
    
    /**
     * Test method for
     * {@link ipsk.net.URLContext#renameContextSpec(URL,String)}.
     * @throws MalformedURLException     
     *
     */
    @Test
    public void testRename() throws MalformedURLException{
        String ctxStrOld="file:/C:/Users/foo//prja/";
        String ctxStrNew="file:/C:/Users/foo//bprj/";
        URL ctxOld=new URL(ctxStrOld);
        URL ctxNew=new URL(ctxStrNew);
        URLContext oldCtx=new URLContext(ctxOld);
        
        // relative
        String relUrlStr="sessions/test.bla";
        String renRelSpec=oldCtx.renameContextSpec(ctxNew,relUrlStr);
        Assert.assertTrue(renRelSpec.equals("sessions/test.bla"));
        
        // absolute inside context
        String absUrlStr="file:/C:/Users/foo/prja/sessions/test.bla";
        String renAbsSpec=oldCtx.renameContextSpec(ctxNew,absUrlStr);
        Assert.assertTrue(renAbsSpec.equals("file:/C:/Users/foo/bprj/sessions/test.bla"));
        
        // outside context
        String absOsUrlStr="file:/C:/Users/foo/prjc/sessions/test.bla";
        String renAbsOsSpec=oldCtx.renameContextSpec(ctxNew,absOsUrlStr);
        Assert.assertTrue(renAbsOsSpec.equals(absOsUrlStr));
    }
}
