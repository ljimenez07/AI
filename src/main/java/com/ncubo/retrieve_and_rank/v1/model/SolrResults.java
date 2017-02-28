package com.ncubo.retrieve_and_rank.v1.model;

import java.util.List;

import com.google.gson.Gson;
import com.ncubo.retrieve_and_rank.v1.model.RankResult;

/**
 * An object that encapsulate Solr results
 */
public class SolrResults {
  
  private List<String> ids;
  private List<RankResult> result;
  private int numberOfResults;
  public List<String> getIds() {
    return ids;
  }
  public void setIds(List<String> ids) {
    this.ids = ids;
  }
  public List<RankResult> getResult() {
    return result;
  }
  public void setResult(List<RankResult> result) {
    this.result = result;
  }
  public int getNumberOfResults() {
    return numberOfResults;
  }
  public void setNumberOfResults(int numberOfResults) {
    this.numberOfResults = numberOfResults;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
