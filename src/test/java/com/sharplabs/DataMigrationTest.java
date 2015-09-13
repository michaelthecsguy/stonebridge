package com.sharplabs;

import static org.junit.Assert.*;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import com.sharplabs.TestSuite.QuickSmokeTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by yangm on 8/28/15.
 * JUnit 4 annotation instead of 3
 */
public class DataMigrationTest
{
  @ClassRule
  public static JUnitConfigurationRunner jRunner = new JUnitConfigurationRunner();

  public static String documentQueryEndPoint = "Document.Query";
  public static String documentPostRequestProperties = "X-NXDocumentProperties";
  public static String documentQueryStatement = "SELECT * FROM DOCUMENT WHERE ecm:uuid=";
  protected Document fromDoc, toDoc;
  protected Documents fromDocs, toDocs;

  @Category(QuickSmokeTest.class)
  @Test
  public void testSampleTest() throws Exception
  {
    assertEquals(1, 1 + 0);
    System.out.println("Test Passed 1.");
  }

  @Category(QuickSmokeTest.class)
  @Test
  public void testSampling() throws Exception
  {
    System.out.println(jRunner.username);
    System.out.println(jRunner.documentUuids.size());
  }

  @Category(QuickSmokeTest.class)
  @Test
  public void testDocumentInputType() throws Exception
  {
    //documentUuids
    //For Demo purpose, grab one of uuids from Set
    String uuid = "82c088be-ac26-41b7-8ec1-d3034755687d";

    fromDocs = (Documents)jRunner.fromSession.newRequest(documentQueryEndPoint).
      setHeader("X-NXDocumentProperties", "*").
      set("query", documentQueryStatement + "'" + uuid + "'").execute();

    //Original Intention to Try and Disabled until DM2 runway is up
    /*
    toDoc = (Document)jRunner.toSession.newRequest("UserWorkspace.Get").execute();
    toDocPath = toDoc.getPath();
    toDocs = (Documents)jRunner.toSession.newRequest("Document.Query").
      set("query", "SELECT * FROM Document where ecm:path STARTSWITH '" + toDocPath +
        "' and ecm:primaryType='Folder' or ecm:primaryType='File'").execute();
    */

    //Trial and Error on imitating OSA client to query the PageProvider end point
    /*
    fromDoc = (Document)jRunner.fromSession.newRequest("UserWorkspace.Get").execute();
    fromDocPath = fromDoc.getPath();

    PaginableDocuments pDocs = (PaginableDocuments)jRunner.fromSession.newRequest("Document.PageProvider").
      set("query", "SELECT * FROM Document where ecm:path STARTSWITH '" + fromDocPath +
        "' and ecm:primaryType='Folder' or ecm:primaryType='File'").set("pageSize", 50).set("page", 50).execute();
    */

    toDocs = (Documents)jRunner.fromSession.newRequest(documentQueryEndPoint).
      setHeader("X-NXDocumentProperties", "*").
      set("query", "SELECT * FROM Document where ecm:uuid='" + uuid + "'").execute();

    assertEquals(fromDocs.getInputType(), toDocs.getInputType());
  }

  @Category(QuickSmokeTest.class)
  @Test
  public void testDocumentTitle() throws Exception
  {
    //documentUuids
    //For Demo purpose, grab one of uuids from Set randomly
    Random randomizer = new Random();
    List<String> uuidList = new ArrayList<String>(jRunner.documentUuids);
    String uuid = uuidList.get(randomizer.nextInt(uuidList.size()));

    fromDocs = (Documents)jRunner.fromSession.newRequest(documentQueryEndPoint).
      setHeader("X-NXDocumentProperties", "*").
      set("query", documentQueryStatement + "'" + uuid + "'").execute();

    toDocs = (Documents)jRunner.fromSession.newRequest(documentQueryEndPoint).
      setHeader("X-NXDocumentProperties", "*").
      set("query", "SELECT * FROM Document where ecm:uuid='" + uuid + "'").execute();

    assertEquals(fromDocs.get(0).getTitle(), toDocs.get(0).getTitle());
  }

  @Category(QuickSmokeTest.class)
  @Test
  public void testDocumentType() throws Exception
  {
    //documentUuids
    //For Demo purpose, grab one of uuids from Set randomly
    Random randomizer = new Random();
    List<String> uuidList = new ArrayList<String>(jRunner.documentUuids);
    String uuid = uuidList.get(randomizer.nextInt(uuidList.size()));

    fromDocs = (Documents)jRunner.fromSession.newRequest(documentQueryEndPoint).
      setHeader("X-NXDocumentProperties", "*").
      set("query", documentQueryStatement + "'" + uuid + "'").execute();

    toDocs = (Documents)jRunner.fromSession.newRequest(documentQueryEndPoint).
      setHeader("X-NXDocumentProperties", "*").
      set("query", "SELECT * FROM Document where ecm:uuid='" + uuid + "'").execute();

    assertEquals(fromDocs.get(0).getType(), toDocs.get(0).getType());
  }
}