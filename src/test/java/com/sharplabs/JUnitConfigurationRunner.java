package com.sharplabs;

import com.sharplabs.Enum.Environment;
import org.apache.commons.codec.binary.Base64;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.nuxeo.ecm.automation.client.Session;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yangm on 8/31/15.
 */
public class JUnitConfigurationRunner implements TestRule
{
  protected ClientSessionManager fromClientSessionManager;
  protected Session fromSession;
  protected ClientSessionManager toClientSessionManager;
  protected Session toSession;

  protected TrustManager[] trustManagers;
  protected SSLContext sslContext;
  protected URL uidFileURL;

  protected String fromEnv = System.getProperty("fromAppEnvironment");
  protected String toEnv = System.getProperty("toAppEnvironment");

  protected String username = System.getProperty("userAccount").isEmpty()? "test2" : System.getProperty("userAccount");
  protected String password = System.getProperty("userPassword").isEmpty()? "$harp123" : System.getProperty("userPassword");
  protected String loginCredential = username + ":" + password;

  protected Environment fromEnvironment = fromEnv.isEmpty()? Environment.findByEnvName("usqa3dm1") : Environment.findByEnvName(fromEnv);
  protected Environment toEnvironment = toEnv.isEmpty()? Environment.findByEnvName("usqa3dm2") : Environment.findByEnvName(toEnv);
  protected String fromEnvironmentUrl;
  protected String toEnvironmentUrl;

  protected String environmentSubUrl = "app1/site/automation";
  protected String environmentMigrationUrl = "migration/migration.txt";

  protected Set<String> documentUuids;

  public JUnitConfigurationRunner()
  {

  }

  //Only allow one Connection for entire test suite
  public Statement apply(final Statement statement, Description description)
  {
    StringBuilder fromEnvBuildUrl = new StringBuilder(fromEnvironment.getUrl());
    fromEnvironmentUrl = fromEnvBuildUrl.append(environmentSubUrl).toString();

    StringBuilder toEnvBuildUrl = new StringBuilder(toEnvironment.getUrl());
    toEnvironmentUrl = toEnvBuildUrl.append(environmentSubUrl).toString();


    return new Statement()
    {
      public void evaluate() throws Throwable {
        // Here is BEFORE TEST CLASS CODE
        fromClientSessionManager = new ClientSessionManager(fromEnvironmentUrl, username, password);
        fromSession = fromClientSessionManager.getClientSession();

        //Disabled until DM2 runway is up
        toClientSessionManager = new ClientSessionManager(toEnvironmentUrl, username, password);
        toSession = toClientSessionManager.getClientSession();

        disableValidationCertificationSSL();
        documentUuids = getDocUuids();

        System.out.println("Got the Uuid text file!");

        try {
          statement.evaluate();
        }
        catch (Exception e)
        {

        }
        finally
        {
          // Here is AFTER TEST CLASS CODE
          if (fromSession != null)
            fromSession.close();

          if (toSession != null)
            toSession.close();
        }
      }
    };
  }

  protected void disableValidationCertificationSSL()
  {
    //Create a new trust manager that trust all certificates
    trustManagers = new TrustManager[]
    {
      new X509TrustManager()
      {
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }
        public void checkClientTrusted(
          X509Certificate[] certs, String authType) {
        }
        public void checkServerTrusted(
          X509Certificate[] certs, String authType) {
        }
      }
    };

    //Activate the new trust manager
    try
    {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustManagers, new SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    catch(Exception e)
    {

    }
  }

  public Set<String> getDocUuids()
  {
    Set<String> docUuids = new HashSet<>();

    //The code is needed when there is authentication required to get a list of uuids
    /*
    String encodedCredential = Base64.encodeBase64(loginCredential.getBytes()).toString();
    */

    try
    {
      uidFileURL = new URL(fromEnvironment.getUrl() + environmentMigrationUrl);
      URLConnection connection = uidFileURL.openConnection();
      //connection.setRequestProperty("Authorization", "Basic " + encodedCredential);
      InputStream is = connection.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));

      String uuid;
      while((uuid = br.readLine()) != null)
      {
        //System.out.println(uuid);
        docUuids.add(uuid);

      }
      br.close();
    }
    catch(IOException ex)
    {
      ex.printStackTrace();
    }

    return docUuids;
  }

}
