package com.company.util;

import com.company.MainVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by abdlquadri on 11/19/16.
 */
public class DeployerVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    vertx.deployVerticle(new MainVerticle());
  }
}
