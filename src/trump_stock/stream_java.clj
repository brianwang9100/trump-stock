(ns trump-stock.stream-java
  (:import (java.util.concurrent LinkedBlockingQueue)
           (com.twitter.hbc ClientBuilder)
           (com.twitter.hbc.core Client Constants)
           (com.twitter.hbc.core.processor StringDelimitedProcessor)
           (com.twitter.hbc.core.endpoint StatusesFilterEndpoint)
           (com.twitter.hbc.httpclient.auth Authentication OAuth1)

  (:require [cheshire.core :refer :all]))]

(def auth (atom nil))
(def endpoint (atom nil))
(def queue (atom nil))
(def client (atom nil))

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

; // Establish a connection
(defn consumer-messages []
  (if (not (.isDone (get-client)))
    (-> (get-queue)
        (.take)
        (parse-string))))

(defn start-streaming! []
  (.connect (get-client))
  (.start (Thread. #(doall (repeatedly consumer-messages)))))

(defn stop-streaming! []
  (.stop (get-client)))

(defn reconnect! []
  (.reconnect (get-client)))
  (.reconnect (get-client))
