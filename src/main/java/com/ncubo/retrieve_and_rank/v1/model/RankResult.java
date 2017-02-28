package com.ncubo.retrieve_and_rank.v1.model;

import com.google.gson.Gson;

/**
 * An object that encapsulate Ranker results
 */
public class RankResult {
  /**
   * the Solr answer id
   * 
   */
  private String answerId;

  /**
   * the body of the document returned by Solr
   * 
   */
  private String body;

  /**
   * the confidence assigned by the ranker to the result
   * 
   */
  private double confidence;

  /**
   * the index of the result in the ranked results
   * 
   */
  private int finalRank;

  /**
   * the Ground Truth relevance.
   * 
   */
  private int relevance = -1;

  /**
   * the score assigned by Solr/Ranker
   * 
   */
  private float score;

  /**
   * the index of the result in the solr results
   * 
   */
  private int solrRank;

  /**
   * The title of the document returned by Solr
   * 
   */
  private String title;


  /**
   * Returns the Solr answer id
   * 
   * @return
   */
  public String getAnswerId() {
    return answerId;

  }

  /**
   * Returns the Solr document body for the result
   * 
   * @return
   */
  public String getBody() {
    return body;

  }

  /**
   * Returns the confidence assigned by the ranker service to the result
   * 
   * @return
   */
  public double getConfidence() {
    return confidence;

  }

  /**
   * Returns the position of this result in the list of final results from the ranker
   * 
   * @return
   */
  public int getFinalRank() {
    return finalRank;

  }

  /**
   * Returns the Ground Truth relevance of this result
   * 
   * @return
   */
  public int getRelevance() {
    return relevance;

  }

  /**
   * Returns the score assigned by the system to the result
   * 
   * @return
   */
  public float getScore() {
    return score;

  }

  /**
   * Gets the position this result appears in the list of results from Solr
   * 
   * @return
   */
  public int getSolrRank() {
    if (solrRank > -1) {
      solrRank++;

    }
    return solrRank;

  }

  /**
   * Returns the Solr document title for the result
   * 
   * @return
   */
  public String getTitle() {
    return title;

  }

  /**
   * Sets the Solr answer id
   * 
   * @param answerId
   */
  public void setAnswerId(String answerId) {
    this.answerId = answerId;

  }

  /**
   * Sets the Solr document body for the result
   * 
   * @param body
   */
  public void setBody(String body) {
    this.body = body;

  }

  /**
   * Sets the confidence assigned by the ranker service to the result
   * 
   * @param confidence
   */
  public void setConfidence(double confidence) {
    this.confidence = confidence;

  }

  /**
   * Sets the position of this result in the list of final results from the ranker
   * 
   * @param finalRank
   */
  public void setFinalRank(int finalRank) {
    if (finalRank > -1) {
      finalRank++;

    }
    this.finalRank = finalRank;

  }

  /**
   * Sets the Ground Truth relevance of this result
   * 
   * @param relevance
   */
  public void setRelevance(int relevance) {
    if (relevance < 1) {
      relevance = 0;

    } else if (relevance == 1) {
      relevance = 4;

    } else if (relevance == 2) {
      relevance = 3;

    } else if (relevance == 3) {
      relevance = 2;

    } else if (relevance == 4) {
      relevance = 1;

    }
    this.relevance = relevance;

  }

  /**
   * Sets the score assigned by the system to the result
   * 
   * @param score
   */
  public void setScore(float score) {
    this.score = score;

  }

  /**
   * Sets the position this result appears in the list of results from Solr
   * 
   * @param solrRank
   */
  public void setSolrRank(int solrRank) {
    this.solrRank = solrRank;

  }

  /**
   * Returns the Solr document title for the result
   * 
   * @param title
   */
  public void setTitle(String title) {
    this.title = title;

  }
  
  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
