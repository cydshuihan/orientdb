/*
 *
 *  * Copyright 2010-2014 Orient Technologies LTD (info(at)orientechnologies.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.orientechnologies.orient.etl.transform;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.etl.OETLProcessor;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class OVertexTransformer extends OAbstractTransformer {
  protected boolean         tx = true;
  protected OrientBaseGraph graph;

  @Override
  public ODocument getConfiguration() {
    return new ODocument().fromJSON("{parameters:[{tx:{optional:true,description:'Transactional or not'}},"
        + "input:['ODocument'],output:'OrientVertex'}");
  }

  @Override
  public void configure(final ODocument iConfiguration) {
    if (iConfiguration.containsField("tx"))
      tx = Boolean.parseBoolean((String) iConfiguration.field("tx"));
  }

  @Override
  public void init(OETLProcessor iProcessor, final ODatabaseDocumentTx iDatabase) {
    super.init(iProcessor, iDatabase);
    if (tx)
      graph = new OrientGraph(iDatabase);
    else
      graph = new OrientGraphNoTx(iDatabase);
  }

  @Override
  public String getName() {
    return "vertex";
  }

  @Override
  public Object executeTransform(final Object input, final OCommandContext iContext) {
    return graph.getVertex(input);
  }
}
