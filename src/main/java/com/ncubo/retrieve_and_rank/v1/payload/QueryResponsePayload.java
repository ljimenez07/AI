package com.ncubo.retrieve_and_rank.v1.payload;


import java.util.List;

import com.ncubo.retrieve_and_rank.v1.model.RankResult;


/**
 * The query response that contains:<br>
 * <ul>
 * <li>The original query sent from the client</li>
 * <li>The number of results which the Solr portion of the service returned</li>
 * <li>The list of results as returned by Solr</li>
 * <li>The list of results from Solr re-ranked using the ranker portion of the service.</li>
 * </ul>
 */
public class QueryResponsePayload {

 

  /**
   * number of results returned by Solr
   * 
   */
  private int num_solr_results;

  /**
   * Original text from the client
   * 
   */
  private String query;

  /**
   * an ordered list of results as re-ranked by the Ranker
   * 
   */
  private List<RankResult> ranked_results;



  /**
   * an ordered list of results from the Solr query.
   * 
   */
  private List<RankResult> solr_results;



  /**
   * Returns the number of results returned by the Solr search
   * 
   * @return
   */
  public int getNumSolrResults() {
    return num_solr_results;

  }

  /**
   * Returns the original query sent from the client
   * 
   * @return
   */
  public String getQuery() {
    return query;

  }

  /**
   * Returns an ordered list of results, which is the Solr list re-ranked by the Ranker service
   * 
   * @return
   */
  public List<RankResult> getRankedResults() {
    return ranked_results;

  }

  /**
   * Returns an ordered list of results as they were returned by the Solr search.
   * 
   * @return
   */
  public List<RankResult> getSolrResults() {
    return solr_results;

  }

  /**
   * Set the number of results returned by Solr
   * 
   * @param numSolrResults
   */
  public void setNumSolrResults(int numSolrResults) {
    this.num_solr_results = numSolrResults;

  }

  /**
   * Set the original query sent from the client
   * 
   * @param query
   */
  public void setQuery(String query) {
    this.query = query;

  }

  /**
   * Sets an ordered list of results as ranked by the Ranker service.
   * 
   * @param rankedResults
   */
  public void setRankedResults(List<RankResult> rankedResults) {
    this.ranked_results = rankedResults;

  }

  /**
   * Sets an ordered list of results as they were returned by Solr
   * 
   * @param solrResults
   */
  public void setSolrResults(List<RankResult> solrResults) {
    this.solr_results = solrResults;

  }
}
