(ns trump-stock.core
  (:require [crypto.random]))

(defn oauth-creds []
  {:oauth_consumer_key "94bPomC4amy5zeYoBw9zL3q2K"
   :oauth_nonce (nonce)
   :oauth_signature ""
   :oauth_signature_method "HMAC-SHA1"
   :oauth_timestamp ""
   :oauth_token ""
   :oauth_version "1.0"})

(defn nonce []
  (apply str (filter alpha-numeric?
                     (crypto.random/base64 32))))

(defn alpha-numeric? [x]
  (or (Character/isDigit x) (Character/isLetter x)))
