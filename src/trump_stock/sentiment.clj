(ns trump-stock.sentiment
  (:require [clj-http.client :as http]
            [cheshire.core :refer [generate-string parse-string]]))

(def url "https://language.googleapis.com/v1beta2/documents:analyzeEntitySentiment?key=AIzaSyBVi22_roqgKLBA_RvWY6JidSj_O_YeraY")

(defn load-google-cloud-api-key []
  "Fetches GC API key from env variables"
  (System/getenv "TRUMP_STOCKS_GC_API_KEY"))

(defn build-req-body [content]
  "Creates our request body from some content"
  (let [document {:type "PLAIN_TEXT"
                  :language "en"
                  :content content}]
    (generate-string {:document document
                      :encodingType "UTF8"})))

(defn analyze-entity-sentiment [tweet]
  "Makes a request to the Google Cloud NLP API, returns info on the document"
  (http/post url {:body (build-req-body tweet)}))
