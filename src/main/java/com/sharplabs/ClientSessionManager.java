package com.sharplabs;

import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import java.io.IOException;

/**
 * Created by yangm on 8/28/15.
 */
public class ClientSessionManager
{
  private final String url;
  private final String username;
  private final String password;
  private final HttpAutomationClient httpAutomationClient;
  private final Session clientSession;

  public ClientSessionManager(String url, String username, String password)
  {
    this.url = url;
    this.username = username;
    this.password = password;

    httpAutomationClient = new HttpAutomationClient(url);

    clientSession = getClientSession();
  }

  public final HttpAutomationClient getHttpAutomationClient()
  {
    return httpAutomationClient;
  }

  public final Session getClientSession()
  {
    try
    {
      return httpAutomationClient.getSession(username, password);
    }
    catch (IOException e )
    {
      e.printStackTrace();
      return null;
    }
  }

}
