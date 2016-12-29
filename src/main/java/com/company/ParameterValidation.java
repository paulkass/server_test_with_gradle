package com.company;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

/**
 * Created by SalmonKiller on 12/28/16.
 */
public class ParameterValidation {
  public static JsonObject validate(Buffer data) {
    JsonObject return_json = data.toJsonObject();

    if (return_json.getString("body") == null) {
      throw new RequiredParamsMissingException();
    }

    if (return_json.getString("expires") == null) {
      return_json.put("expires", "-1");
    }

    return return_json;
  }
}
