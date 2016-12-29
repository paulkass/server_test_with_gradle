package com.company;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/*
For code commented for debugging:
//*************
//Code for debugging only goes here
 */

/**
 * Created by SalmonKiller on 10/22/16.
 */
public class MainVerticle extends AbstractVerticle {
  static HttpServer server;
  static EventBus eb;
  static String missing_params_message = "Sorry, some of the essential parameters are missing.";

  @Override
  public void start() throws Exception {
    server = vertx.createHttpServer();

    //************
    //System.out.println(new File("").getCanonicalPath());
    //System.out.println("Started the server");

    eb = vertx.eventBus();

    Router router = Router.router(vertx);

    router.post("/entries/").consumes("application/x-www-form-urlencoded").handler(Handlers.BASIC_FORM_UNENCODED_HANDLER);
    router.post("/entries/").handler(Handlers.BASIC_JSON_HANDLER);
    router.get("/entries/").handler(Handlers.GET_ALL_ENTRIES_HANDLER);
    router.put("/entries/:entry_id/").handler(Handlers.SPECIFIC_ENTRY_HANDLER);
    router.delete("/entries/:entry_id/").handler(Handlers.DELETE_ENTRY_HANDLER);

    router.route("/static/*").handler(StaticHandler.create("webroot"));
    server.websocketHandler(Handlers.WEBSOCKET_HANDLER);

    server.requestHandler(router::accept).listen(8000);
  }

  //**************
  /*
    @FunctionalInterface
  public interface ThrowingConsumer<T> extends Handler<T> {

    @Override
    default void handle(final T elem) {
      try {
        acceptThrows(elem);
      } catch (final RequiredParamsMissingException e) {
        // Implement your own exception handling logic here..
        // For example:
        //System.out.println("handling an exception...");
        // Or ...
        throw e;
      }
    }

    void acceptThrows(T elem) throws RequiredParamsMissingException;

  }
   */

  public static String getMissing_params_message() {
    return missing_params_message;
  }

}

