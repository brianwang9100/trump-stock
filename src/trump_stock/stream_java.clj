(ns trump-stock.stream-java
  (:import (java.util.concurrent LinkedBlockingQueue)
           (com.twitter.hbc ClientBuilder)
           (com.twitter.hbc.core Client Constants)
           (com.twitter.hbc.core.processor StringDelimitedProcessor)
           (com.twitter.hbc.core.endpoint StatusesFilterEndpoint)
           (com.twitter.hbc.httpclient.auth Authentication OAuth1))
  (:require [cheshire.core :refer :all]
            [trump-stock.sentiment :refer [analyze-entity-sentiment]]))

(def auth (atom nil))
(def endpoint (atom nil))
(def queue (atom nil))
(def client (atom nil))
(def consumer-thread (atom nil)) ; future
(def sentiment-results (atom nil))

(def consumer-key "94bPomC4amy5zeYoBw9zL3q2K")
(def consumer-secret "A9kR74FslUoEBQGzAPVprV9WnCyiXFu8tpKXdmVmdH76sWZDTI")
(def access-token "2680914770-OGO02YNrktlgxzHNjQ0dhpDuKqt33avvBtP9JKQ")
(def access-secret "HrrwcQoQHmkhN51b7dtFOK4IR6nl8H4psoPLHWb5fj3oT")

(defn get-auth []
  (if @auth
    @auth
    (reset! auth (OAuth1. consumer-key consumer-secret access-token access-secret))))

(defn get-endpoint []
  (if @endpoint
    @endpoint
    (reset! endpoint (-> (StatusesFilterEndpoint.)
                         (.followings (java.util.ArrayList. [2680914770]))))))

(defn get-queue []
  (if @queue
    @queue
    (reset! queue (LinkedBlockingQueue. 10000))))

(defn get-client []
  (if @client
    @client
    (reset! client (-> (ClientBuilder.)
                       (.hosts Constants/STREAM_HOST)
                       (.endpoint (get-endpoint))
                       (.authentication (get-auth))
                       (.processor (StringDelimitedProcessor. (get-queue)))
                       (.build)))))

; extract entities from analysis network response
(defn extract-entity-results [m]
  (-> m :body (parse-string true) :entities))

; fn used for reducing entities to [[entity, score]]
(defn build-entity-score-tuples [agg, cur]
  (conj agg [(:name cur), (-> cur :sentiment :score)]))
  
; input twitter reults
; output [[entity, score]]
(defn process-twitter-results [m]
  (->> m
      :text
      analyze-entity-sentiment
      extract-entity-results
      (reduce build-entity-score-tuples [])))


(defn consume-message []
  (if (not (.isDone (get-client)))
    (-> (get-queue)
        (.take)
        (parse-string true)
        (process-twitter-results)
        (println))))


; get and start future
(defn start-consumer-thread! []
  (if @consumer-thread
    @consumer-thread
    (reset! consumer-thread (future (doall (repeatedly consume-message))))))

(defn stop-consumer-thread! []
  (when @consumer-thread
    (future-cancel @consumer-thread)
    (reset! consumer-thread nil)))

(defn consumer-thread-running? []
  (if @consumer-thread
    (not (future-cancelled? @consumer-thread))
    false))

(defn start-streaming! []
  (.connect (get-client))
  (start-consumer-thread!))

(defn stop-streaming! []
  (when @client
    (.stop (get-client)))
  (stop-consumer-thread!)
  (reset! client nil)
  (reset! queue nil))

; (defn reconnect! []
;   (.reconnect (get-client))
;   (start-consumer-thread!))
