/*
 *
 *  *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 *
 */

package com.orientechnologies.orient.server.distributed;

import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Start 3 servers and wait for external commands.
 */
public class ServerClusterRemoteDocumentIT extends AbstractServerClusterTest {
  final static int SERVERS = 3;

  public String getDatabaseName() {
    return "distributed-remote-docs3";
  }

  @Test
  public void test() throws Exception {
    init(SERVERS);
    prepare(false);
    execute();
  }

  @Override
  protected void executeTest() throws Exception {
    try (OrientDB orientDB = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig())) {
      String id = String.valueOf(Math.random());
      ODatabaseDocument db = orientDB.open(getDatabaseName(), "admin", "admin");
      db.createClass("Client");
      db.createClass("Matter");
      db.begin();
      try {
        ODocument client = new ODocument("Client");
        ODocument matter = new ODocument("Matter");
        matter.field("client", client);
        matter.field("id", id);
        List clientMatters = new ArrayList();
        clientMatters.add(matter);
        client.field("matters", clientMatters);
        client.save();
        matter.save();
        db.commit();
      } finally {
        db.close();
      }

      ODatabaseDocument db2 = orientDB.open(getDatabaseName(), "admin", "admin");
      db2.begin();
      try {
        ODocument matter = null;
        for (ODocument doc : db2.browseClass("Matter")) {
          if (doc.field("id").equals(id)) {
            matter = doc;
            break;
          }
        }
        if (matter == null) {
          throw new Exception("Matter not found with id" + id);
        }
        matter.field("client", new ODocument().save(db2.getClusterNameById(db2.getDefaultClusterId())));
        matter.save();
        db2.commit();
      } finally {
        db2.close();
      }
    }
  }
}
