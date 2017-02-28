/**
 * Copyright 2015 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ncubo.retrieve_and_rank.v1.model;

import com.google.gson.Gson;

/**
 * An object that encapsulate a Solr result.
 */
public class SolrResult {
  
  private String body;
  private String id;
  private String title;
  
  /**
   * Gets the body.
   *
   * @return the body
   */
  public String getBody() {
    return body;
  }
  
  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }
  
  /**
   * Gets the title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }
  
  /**
   * Sets the body.
   *
   * @param body the new body
   */
  public void setBody(String body) {
    this.body = body;
  }
  
  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
  
  
}
