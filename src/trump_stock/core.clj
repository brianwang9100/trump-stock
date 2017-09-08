(ns trump-stock.core
  (:require [crypto.random])
  (:require [oauth.client :as oauth])
  (:require [clj-http.client :as http]))

; (defn oauth-creds []
;   {:oauth_consumer_key "94bPomC4amy5zeYoBw9zL3q2K"
;    :oauth_nonce (nonce)
;    :oauth_signature ""
;    :oauth_signature_method "HMAC-SHA1"
;    :oauth_timestamp ""
;    :oauth_token ""
;    :oauth_version "1.0"})

(def consumer (oauth/make-consumer "94bPomC4amy5zeYoBw9zL3q2K"
                                   "A9kR74FslUoEBQGzAPVprV9WnCyiXFu8tpKXdmVmdH76sWZDTI"
                                   "https://api.twitter.com/oauth/request_token"
                                   "https://api.twitter.com/oauth/access_token"
                                   "https://api.twitter.com/oauth/authorize"
                                   :hmac-sha1))
; (def request-token (oauth/request-token consumer "oob"))
; (oauth/user-approval-uri consumer (:oauth_token request-token))
; (def access-token-response (oauth/access-token consumer request-token))

(def access-token-response
  {:oauth_token "2680914770-OGO02YNrktlgxzHNjQ0dhpDuKqt33avvBtP9JKQ"
   :oauth_token_secret "HrrwcQoQHmkhN51b7dtFOK4IR6nl8H4psoPLHWb5fj3oT"})

(def url "https://api.twitter.com/1.1/statuses/user_timeline.json")
(def user-params {:screen_name "realDonaldTrump" :count 10})
; (def url "https://api.twitter.com/1.1/statuses/update.json")
; (def user-params {:status "lol another status"})
(def credentials (oauth/credentials consumer
                                    (:oauth_token access-token-response)
                                    (:oauth_token_secret access-token-response)
                                    :POST
                                    url
                                    user-params))
(http/post url {:query-params (merge credentials user-params)})

; (try
;   (http/post url {:query-params (merge credentials user-params)})(catch clojure.lang.ExceptionInfo e (prn "caught" e)))

(try
  (http/get url {:headers
                 {:Authorization (oauth-string "OAuth" credentials)}}) (catch clojure.lang.ExceptionInfo e (prn "caught" e)))

(defn oauth-string [auth-type credentials]
  (str auth-type " " (reduce #(str %1 " " %2) (map (fn [[k v]] (str (name k) "=" "\"" v "\"")) credentials))))
                ;  (catch clojure.lang.ExceptionInfo e (prn "caught" e))}))
