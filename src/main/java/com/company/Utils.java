package com.company;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.eaio.uuid.UUID;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;

import java.sql.Timestamp;

/**
 * Created by SalmonKiller on 12/29/16.
 */
public class Utils {
  public static void insert_values(EventBus eb, String body, String title, String expires, String private_string, RoutingContext routingContext) {
    UUID uuid = new UUID();

   // EventBus eb = vertx.eventBus();

    Cluster cluster = null;
    cluster = Cluster.builder()                                                    // (1)
      .addContactPoint("127.0.0.1")
      .build();
    try {
      Session session = cluster.connect(); // (2)

      String table_name = "entries_table_public";
      if (private_string.equals("true")) {
        table_name = "entries_table_private";
      }


//                System.out.println(new Timestamp(System.currentTimeMillis()).toString());
//                System.out.println(Timestamp.valueOf(expires).toString());
//                System.out.println(expirationTime);
      String executionString="";

      if (expires.equals("-1")) {
        executionString = get_insertion_statement(uuid.toString(), table_name, body, title);
      } else {
        executionString = get_insertion_statement(uuid.toString(), table_name, body, title, get_expiration_secs(expires));
      }
      session.execute(executionString);
      if (private_string.equals("false")) {
        //String[] array = {body, title, expires};
        JsonArray array = new JsonArray();
        array.add(body);
        array.add(title);
        array.add(expires);
        eb.publish("new_public_message", array);
      }
    } catch (Exception e) {
      System.out.println(e.toString());
    } finally {
      if (cluster != null) cluster.close();                                          // (5)
    }
    routingContext.response().end(uuid.toString());
  }

  public static Double get_expiration_secs(String expires) {
    Timestamp timestamp = Timestamp.valueOf(expires);
    timestamp = new Timestamp(timestamp.getTime() - System.currentTimeMillis());
    return Double.valueOf(timestamp.getTime() / 1000.0);
  }

  public static String get_insertion_statement(String code, String table_name, String body, String title) {
    // ***** handle optional entries here
    return "insert into entry_keyspace." + table_name + " (entry_id, body, title) " +
      "values (" + code + ", '" + body + "', '" + title + "') ;";
  }

  public static String get_insertion_statement(String code, String table_name, String body, String title, Double expirationTime) {
    // ***** handle optional entries here
    return "insert into entry_keyspace." + table_name + " (entry_id, body, title) " +
      "values (" + code + ", '" + body + "', '" + title + "') using ttl " + expirationTime.intValue() + ";";
  }

  public static String get_update_statement(String entry_id, String table_name, String body, String title, Double expirationTime) {
    // ***** handle optional entries here
    return "update entry_keyspace." + table_name + " using ttl " + expirationTime.intValue() + " set body='" + body +
      "', title='" + title + "' where entry_id=" + entry_id + ";";
  }

  public static String get_deletion_statement(String table_name, String entry_id) {
    return "delete from entry_keyspace." + table_name + " where entry_id=" + entry_id + ";";
  }

  public static String get_execution_string(String entry_id, String data_set) { // data_set can be either 'public' or 'private'
    String return_string = "select * from entry_keyspace.entries_table_" + data_set + " where entry_id=" + entry_id + ";";
    return return_string;
  }
}
