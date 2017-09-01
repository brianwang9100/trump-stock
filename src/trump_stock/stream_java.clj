(ns trump-stock.stream-java
  (import java.util.concurrent [BlockingQueue LinkedBlockingQueue]
          com.twitter.hbc [ClientBuilder]
          com.twitter.hbc.core [Client Constants]
          com.twitter.hbc.core.processor [StringDelimitedProcessor]
          com.twitter.hbc.core.endpoint [StatusesFilterEndpoint]
          com.twitter.hbc.httpclient.auth [Authenticaiton OAuth1]))


    ; BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
    ; StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint()));
;;    // add some track terms
    ; endpoint.trackTerms(Lists.newArrayList("twitterapi", "#yolo"))));

    ; Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret)));
;;    // Authentication auth = new BasicAuth(username, password)));

(def auth (atom nil))
(def endpoint (atom nil))
(def client (atom nil))
(def queue (atom nil))

(def consumer-key "94bPomC4amy5zeYoBw9zL3q2K")
(def consumer-secret "A9kR74FslUoEBQGzAPVprV9WnCyiXFu8tpKXdmVmdH76sWZDTI")
(def access-token "2680914770-OGO02YNrktlgxzHNjQ0dhpDuKqt33avvBtP9JKQ")
(def access-secret "2680914770-OGO02YNrktlgxzHNjQ0dhpDuKqt33avvBtP9JKQ")

(defn get-auth []
  (if @auth
    @auth
    (reset! auth (OAuth1. consumer-key consumer-secret access-token access-secret))))

(defn get-endpoint []
  (if @endpoint
    @endpoint
    (reset! endpoint (-> (StatusesFilterEndpoint.)
                         (.followings (java.util.ArrayList. [25073877]))))))

(defn get-client []
  (if @client
    @client
    (reset! client (-> (Client.)
                       (.hosts Constants.STREAM_HOST)
                       (.endpoint endpoint)
                       (.authentication auth)
                       (.processor (StringDelimitedProcessor. queue))
                       (.build)))))

(defn get-queue []
  (if @queue
    @queue
    (reset! queue (LinkedBlockingQueue. 10000))))

; // Establish a connection
; client.connect();

(defn start-streaming! []
  (.connect @client))
