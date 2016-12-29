package com.company;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.company.Utils.*;

/**
 * Created by SalmonKiller on 12/29/16.
 */
public class Handlers {
  static Handler<RoutingContext> BASIC_JSON_HANDLER = routingContext -> {
    routingContext.request().handler(data -> {
      JsonObject data_in_json = ParameterValidation.validate(data);
      System.out.println(data_in_json.toString());
      insert_values(MainVerticle.eb, data_in_json.getString("body"), data_in_json.getString("title"),
        data_in_json.getString("expires"), data_in_json.getString("private"), routingContext);
    });
  };

  static Handler<RoutingContext> BASIC_FORM_UNENCODED_HANDLER = routingContext -> {
    System.out.println("reached application form urlunencoded");
    HttpServerRequest request = routingContext.request();
    String type = request.getHeader("content-type");
    String[] return_array = {null, null, null, null}; // body, title, expire, private
    Map<String, String> map = new HashMap<String, String>();

    request.bodyHandler(data -> {
      try {
        JsonObject data_object = ParameterValidation.validate(data);
        insert_values(MainVerticle.eb, data_object.getString("body"), data_object.getString("title"),
          data_object.getString("expires"), data_object.getString("private"), routingContext);
        System.out.println("inserted values");
      } catch (RequiredParamsMissingException e) {
        Logger.getAnonymousLogger().log(Level.INFO, "Caught a RequiredParamsMissingException");
        routingContext.response().end(MainVerticle.missing_params_message);
      }
    });
  };

  static Handler<RoutingContext> GET_ALL_ENTRIES_HANDLER = routingContext -> {
    Cluster cluster = null;
    cluster = Cluster.builder()                                                    // (1)
      .addContactPoint("127.0.0.1")
      .build();

    try {
      Session session = cluster.connect();
      ResultSet resultSet = session.execute("select * from entry_keyspace.entries_table_public;");
      final String[] output_string = {""};

      resultSet.forEach(row -> {
        String myString = "\n" + row.getString("body");
        // System.out.println(myString);
        output_string[0] = output_string[0] + myString;
      });

      HttpServerResponse response = routingContext.response();
      response.putHeader("content-type", "text/plain");
      response.end(output_string[0]);
    } finally {
      if (cluster != null) cluster.close();
    }
  };

  static Handler<RoutingContext> SPECIFIC_ENTRY_HANDLER = routingContext -> {
    String entry_id = routingContext.request().getParam("entry_id");
    String body = routingContext.request().getParam("body");
    String title = routingContext.request().getParam("title");
    String expires = routingContext.request().getParam("expires");
    String private_string = routingContext.request().getParam("private");

    //System.out.println("someone wanted to put something");


    HttpServerResponse response = routingContext.response();
    response.putHeader("content-type", "text/plain");
    response.end("");

    Cluster cluster = null;
    cluster = Cluster.builder()                                                    // (1)
      .addContactPoint("127.0.0.1")
      .build();

    try {
      Session session = cluster.connect();

      ResultSet public_result_set = session.execute(get_execution_string(entry_id, "public"));

      ResultSet private_result_set = session.execute(get_execution_string(entry_id, "private"));

      ResultSet process_set = null;
      String old_table_name = "";

      if (!public_result_set.isExhausted()) {
        process_set = public_result_set;
        old_table_name = "public";
      } else if (!private_result_set.isExhausted()) {
        process_set = private_result_set;
        old_table_name = "private";
      } else {
        // ****** Throw exception that entry was not found
      }

      //Row entry_row = process_set.one();

      String new_table_name = "public";
      if (private_string.equals("true"))
        new_table_name = "private";

      if (new_table_name.equals(old_table_name)) {
        // ****** Use Update
        String execution_string = get_update_statement(entry_id, "entries_table_" + new_table_name, body, title,
          get_expiration_secs(expires));
        session.execute(execution_string);
      } else {
        // ******** Use Delete and Insert
        String delete_string = get_deletion_statement("entries_table_" + old_table_name, entry_id);
        session.execute(delete_string);

        String insert_string = get_insertion_statement(entry_id, "entries_table_" + new_table_name, body, title,
          get_expiration_secs(expires));
        session.execute(insert_string);
      }

    } finally {
      if (cluster != null) cluster.close();
    }
  };
}
