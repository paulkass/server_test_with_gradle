package com.company;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static com.company.Utils.insert_values;

/**
 * Created by SalmonKiller on 12/29/16.
 */
public class Handlers {
  public static Handler<RoutingContext> JSON_HANDLER = routingContext -> {
    routingContext.request().handler(data -> {
      JsonObject data_in_json = ParameterValidation.validate(data);
      System.out.println(data_in_json.toString());
      insert_values(MainVerticle.eb, data_in_json.getString("body"), data_in_json.getString("title"),
        data_in_json.getString("expires"), data_in_json.getString("private"), routingContext);
    });
  };
}
