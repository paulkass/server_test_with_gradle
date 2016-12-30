package com.company;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by SalmonKiller on 11/3/16.
 */

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

    Vertx vertx;
    Integer port = 8000;
    String body_test = "lol hi";
    String title_test = "A post lel";
    String private_test ="false";
    Boolean call_complete = true;

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void setUp(TestContext context) throws Exception {
      vertx = Vertx.vertx();

      DeploymentOptions options = new DeploymentOptions()
        .setConfig(new JsonObject().put("http.port", port)
        );
      vertx.deployVerticle(MainVerticle.class.getName(), options, context.asyncAssertSuccess());

    }

    @After
    public void tearDown(TestContext context) throws Exception {
        vertx.close(context.asyncAssertSuccess());
    }


    public void start(TestContext context) throws Exception {
//        TestSuite suite = TestSuite.create("test_suite");
//        suite.test("basic_insert", context1 -> {
//            basicInsertTest(context1);
//        }).test("missing_param", context1 -> {
//           missingParametersTest(context1);
//        });
    }

    private void resetBasicValues() {
      body_test = "lol hi";
      title_test = "A post lel";
      private_test ="false";
      call_complete = true;
    }


    @Test
    public void basic_test(TestContext context) {
      System.out.println("Dummy Test Started");
      assertEquals(true, true);
      System.out.println("Dummy Test Ended");
    }

    interface Callback {
      void operation(TestContext context);
    }

    interface TimeCallback {
      void operation(String entry_id);
    }

  interface databaseCallback {
    void operation(ResultSet resultSet);
  }

    private void missingParameterTestForError(TestContext context, String insertion_string, Callback p) {


      HttpClient client = vertx.createHttpClient();

        client.post(port, "localhost", "/entries/")
                .putHeader("content-type", "application/x-www-form-urlencoded")
                .putHeader("content-length", ""+insertion_string.length())
                .handler(httpClientResponse -> {
                    httpClientResponse.bodyHandler(body -> {
                        String entry_id = body.toString();
                        //System.out.println(entry_id);
                        context.assertEquals(entry_id, MainVerticle.getMissing_params_message());
                        //System.out.println("asserted");
                        p.operation(context);
                    });
                })
                .write(insertion_string)
                .end();



      //client.close();
    }

    private void missingParameterTestWithoutError(TestContext context, String insertion_string, String table_name, String param_name, Callback p) {
      HttpClient client = vertx.createHttpClient();

      client.post(port, "localhost", "/entries/")
        .putHeader("content-type", "application/x-www-form-urlencoded")
        .putHeader("content-length", ""+insertion_string.length())
        .handler(httpClientResponse -> {
          httpClientResponse.bodyHandler(body -> {
            String entry_id = body.toString();
            context.assertTrue(checkIfPropertyIsNull(entry_id, table_name, param_name));
            p.operation(context);
          });
        })
        .write(insertion_string)
        .end();
    }

    private void missingTimeParam(TestContext context, String insertion_string, TimeCallback p) {


      HttpClient client = vertx.createHttpClient();

      client.post(port, "localhost", "/entries/")
        .putHeader("content-type", "application/x-www-form-urlencoded")
        .putHeader("content-length", ""+insertion_string.length())
        .handler(httpClientResponse -> httpClientResponse.bodyHandler(body -> {
          String entry_id = body.toString();
          p.operation(entry_id);
        }))
        .write(insertion_string)
        .end();
    }

    @Test
    public void checkPublicEntriesList() {

    }

    @Test
    public void missingTitleParameter(TestContext context) {
      Async async = context.async();

      Timestamp expires = generateTimestamp(100);

      String insertion_string = "";

      try {

        insertion_string = "body=" + URLEncoder.encode(body_test, "UTF-8");
        insertion_string += "&private=" + URLEncoder.encode(private_test, "UTF-8")+"&expires="+
          URLEncoder.encode(expires.toString(), "UTF-8");
        missingParameterTestWithoutError(context, insertion_string, "public", "title", context1 -> {
          async.complete();
        });
      } catch (UnsupportedEncodingException e) {
        System.out.println(e.getStackTrace());
      }
    }

     @Test
     public void missingExpirationDate(TestContext context) {

       resetBasicValues();
       Async async = context.async();

       String insertion_string = "";

       try {

         insertion_string = "body=" + URLEncoder.encode(body_test, "UTF-8");
         insertion_string += "&private=" + URLEncoder.encode(private_test, "UTF-8");
         missingTimeParam(context, insertion_string, entry_id -> {
           System.out.println(entry_id);
           String table_name = "";
           if (private_test.equals("true")) {
             table_name = "private";
           } else { table_name = "public";}
           String execution_string = "select entry_id, ttl(body) as time_to_expire, ttl(title) as time_to_title from entry_keyspace.entries_table_" + table_name + " where entry_id=" +
             entry_id + ";";
           System.out.println(execution_string);
           DatabaseAccess db = new DatabaseAccess();
           db.executeStatement(execution_string, resultSet -> {
             Row result_row = resultSet.one();
             System.out.println(result_row.toString());
             String result = result_row.getString("time_to_expire");
             System.out.println(result);


           });
           async.complete();
         });
       } catch (UnsupportedEncodingException e) {
         System.out.println(e.getStackTrace());
       }
     }

    @Test
    public void missingBodyParameter(TestContext context) {
      Async async = context.async();

      Timestamp expires = generateTimestamp(100);

      String insertion_string = "";

      try {

        insertion_string = "title=" + URLEncoder.encode(title_test, "UTF-8");
        insertion_string += "&private=" + URLEncoder.encode(private_test, "UTF-8")+"&expires="+
          URLEncoder.encode(expires.toString(), "UTF-8");
        missingParameterTestForError(context, insertion_string, context1 -> {
          //context1.async().complete();
          async.complete();
          // System.out.println("Completed the callback");
        });
      } catch (UnsupportedEncodingException e) {
        System.out.println(e.getStackTrace());
      }

    }

    private Timestamp generateTimestamp(Integer secs) {
        Long seconds = Integer.toUnsignedLong(secs);
        Timestamp expires = new Timestamp(System.currentTimeMillis()+seconds*1000);
        return expires;
    }

    private String getStandardInsertionString() {
      Timestamp expires = generateTimestamp(100);

      String insertion_string = "";

      try {

        insertion_string = "body=" + URLEncoder.encode(body_test, "UTF-8") + "&title=" + URLEncoder.encode(title_test, "UTF-8");
        insertion_string += "&private=" + URLEncoder.encode(private_test, "UTF-8")+"&expires="+
          URLEncoder.encode(expires.toString(), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        System.out.println(e.getStackTrace());
      }

      return insertion_string;
    }

    @Test
    public void jsonInsertTest(TestContext context) {
        Async async = context.async();

        JsonObject insertion_object = new JsonObject();

        Timestamp expires = generateTimestamp(100);

        insertion_object.put("body", body_test);
        insertion_object.put("title",title_test);
        insertion_object.put("private", private_test);
        insertion_object.put("expires", expires.toString());

        String insertion_string = insertion_object.encode();

        HttpClient client = vertx.createHttpClient();

        client.post(port, "localhost", "/entries/")
          .putHeader("content-type", "application/json")
          .putHeader("content-length", ""+insertion_string.length())
          .handler(httpClientResponse -> {
              httpClientResponse.bodyHandler(body -> {
                String entry_id = body.toString();
                DatabaseAccess db = new DatabaseAccess();
                db.executeStatement("select * from entry_keyspace.entries_table_public where entry_id=" +
                  entry_id + ";", resultSet -> {
                  context.assertFalse(resultSet.isExhausted());
                  if (call_complete) {
                    async.complete();
                  }
                });
                async.complete();
              });
          })
        .write(insertion_string)
        .end();
    }

   @Test
    public void basicInsertTest(TestContext context) {
        //WebDriver driver = new ChromeDriver();
        Async async = context.async();

        String insertion_string = getStandardInsertionString();

        HttpClient client = vertx.createHttpClient();

        client.post(port, "localhost", "/entries/")
                .putHeader("content-type", "application/x-www-form-urlencoded")
                .putHeader("content-length", ""+insertion_string.length())
                .handler(httpClientResponse -> {
                    httpClientResponse.bodyHandler(body -> {
                        String entry_id = body.toString();
                        //System.out.println(entry_id);
                        //Boolean x = checkIfExistsInDatabase(entry_id, "public");
                        DatabaseAccess db = new DatabaseAccess();
                        db.executeStatement("select * from entry_keyspace.entries_table_public where entry_id=" +
                          entry_id + ";", resultSet -> {
                          context.assertFalse(resultSet.isExhausted());
                          if (call_complete) {
                            async.complete();
                          }
                        });
                        //System.out.println(x);
                        //context.assertTrue(x);

                    });
                })
                .write(insertion_string)
                .end();


      //client.close();
    }

    @Test
    // might fail because bugs
    public void socketTest(TestContext context) {
      Async async = context.async();

      try {
        body_test = "much code";
        title_test = "wow";

        HttpClient client = vertx.createHttpClient();
        client.websocket(port, "localhost", "/latest/", websocket -> {
         try {
           websocket.frameHandler(frame -> {
             String response = frame.textData();

             JsonArray jsonArray = new JsonArray(response);

            //System.out.println(jsonArray.toString());

             context.assertEquals(jsonArray.getString(0), body_test);
             context.assertEquals(jsonArray.getString(1), title_test);

             if (frame.isFinal()) {
               async.complete();
               websocket.close();
               //System.out.println(websocket.);
             }
           });
         } finally {
           TestContext context1 = new TestContext() {
             @Override
             public <T> T get(String key) {
               return null;
             }

             @Override
             public <T> T put(String key, Object value) {
               return null;
             }

             @Override
             public <T> T remove(String key) {
               return null;
             }

             @Override
             public TestContext assertNull(Object expected) {
               return null;
             }

             @Override
             public TestContext assertNull(Object expected, String message) {
               return null;
             }

             @Override
             public TestContext assertNotNull(Object expected) {
               return null;
             }

             @Override
             public TestContext assertNotNull(Object expected, String message) {
               return null;
             }

             @Override
             public TestContext assertTrue(boolean condition) {
               return null;
             }

             @Override
             public TestContext assertTrue(boolean condition, String message) {
               return null;
             }

             @Override
             public TestContext assertFalse(boolean condition) {
               return null;
             }

             @Override
             public TestContext assertFalse(boolean condition, String message) {
               return null;
             }

             @Override
             public TestContext assertEquals(Object expected, Object actual) {
               return null;
             }

             @Override
             public TestContext assertEquals(Object expected, Object actual, String message) {
               return null;
             }

             @Override
             public TestContext assertInRange(double expected, double actual, double delta) {
               return null;
             }

             @Override
             public TestContext assertInRange(double expected, double actual, double delta, String message) {
               return null;
             }

             @Override
             public TestContext assertNotEquals(Object first, Object second) {
               return null;
             }

             @Override
             public TestContext assertNotEquals(Object first, Object second, String message) {
               return null;
             }

             @Override
             public void fail() {

             }

             @Override
             public void fail(String message) {

             }

             @Override
             public void fail(Throwable cause) {

             }

             @Override
             public Async async() {
               return null;
             }

             @Override
             public Async async(int count) {
               return null;
             }

             @Override
             public <T> Handler<AsyncResult<T>> asyncAssertSuccess() {
               return null;
             }

             @Override
             public <T> Handler<AsyncResult<T>> asyncAssertSuccess(Handler<T> resultHandler) {
               return null;
             }

             @Override
             public <T> Handler<AsyncResult<T>> asyncAssertFailure() {
               return null;
             }

             @Override
             public <T> Handler<AsyncResult<T>> asyncAssertFailure(Handler<Throwable> causeHandler) {
               return null;
             }

             @Override
             public Handler<Throwable> exceptionHandler() {
               return null;
             }
           };
           basicInsertTest(context1);
         }


        });


      } catch (Exception e) {
        e.printStackTrace();
      }
    }



    public boolean checkIfPropertyIsNull(String entry_id, String table_name, String property_name) {
      Cluster cluster = null;
      try {

        cluster = Cluster.builder()                                                    // (1)
          .addContactPoint("127.0.0.1")
          .build();
      } catch (Exception e) {
        fail(e.getMessage());
      }
      Boolean return_value = false;
      try {
        Session session = cluster.connect();
        ResultSet resultSet = session.execute("select "+property_name+" from entry_keyspace.entries_table_" + table_name + " where entry_id=" +
          entry_id + ";");

        String value = resultSet.one().getString(property_name);
        //System.out.println(value.getClass().toString());
        if (value.equals("null")) {
          return_value = true;
        }
        return return_value;
      } finally {
        if (cluster!=null) {cluster.close();}
      }
    }

}
