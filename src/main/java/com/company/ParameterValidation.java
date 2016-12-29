package com.company;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.net.URLDecoder;

/**
 * Created by SalmonKiller on 12/28/16.
 */
public class ParameterValidation {

  public static JsonObject validate(Buffer data) {

    JsonObject return_json = new JsonObject();
    try {
      String decoded_string = URLDecoder.decode(data.toString(), "utf-8");
      String[] x = decoded_string.split("\\&");
      if (x.length == 1) {
        // Means the data arrived in JSON format
        return_json = data.toJsonObject();
      } else {
        for (int i = 0; i < x.length; i++) {
          String[] param = x[i].split("\\=");
          //System.out.println(param[0] + "," + param[1]);
          return_json.put(param[0], param[1]);
        }
      }


        if (return_json.getString("body") == null) {
          throw new RequiredParamsMissingException();
       }

        if (return_json.getString("expires") == null) {
          return_json.put("expires", "-1");
        }
    } catch (UnsupportedEncodingException e) {
      Logger.getLogger(ParameterValidation.class.getName()).log(Level.SEVERE, "Invalid data input format");
    } catch (RequiredParamsMissingException e) {
      throw e;
    } catch (Exception e) {
      Logger.getLogger(ParameterValidation.class.getName()).log(Level.SEVERE, e.getMessage());
    }

    return return_json;
  }
}
