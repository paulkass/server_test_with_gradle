package com.company;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

/**
 * Created by SalmonKiller on 12/29/16.
 */
public class DatabaseAccess {
  Cluster cluster = null;
  public DatabaseAccess() {
    initializeCluster();
  }

  public void initializeCluster() {
    if (cluster==null) {
      cluster = Cluster.builder()                                                    // (1)
        .addContactPoint("127.0.0.1")
        .build();
    }
  }

  public void decommissionCluster() {
    if (cluster!=null) {cluster.close();}
  }

  public void executeStatement(String execution_string, databaseCallback callback) {
    initializeCluster();

    Session session = cluster.connect();

    ResultSet resultSet = session.execute(execution_string);

    callback.operation(resultSet);

    decommissionCluster();
  }

  interface databaseCallback {
    void operation(ResultSet resultSet);
  }
}
