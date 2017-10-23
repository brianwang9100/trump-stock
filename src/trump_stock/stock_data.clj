(ns trump-stock.stock-data
  (:require [clj-http.client :as http]))

(def api-key "I2ZRMY7X3P412GBK")
(def func-type "TIME_SERIES_INTRADAY")

(defn gen-stock-api-req [q-params symb interval api-key]
  "Makes a request to our stock API"
  (http/get "https://www.alphavantage.co/query" {:query-params {"function" func-type,
                                                                "symbol" symb,
                                                                "interval" interval,
                                                                "apikey" api-key}}))

(def default-chrome-headers
  {:headers {"Accept" "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
             "Accept-Encoding" "gzip, deflate, br"
             "Accept-Language" "en-US,zh-CN;q=0.8,zh;q=0.6,en;q=0.4"
             "User-Agent" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.91 Safari/537.36"}})

(defn space-to-plus [orig-string]
  "Converts all spaces in a string to pluses"
  (apply str (map #(if (= % \space) \+ %) orig-string)))

(defn build-search-string [entity]
  "URL encodes entity and then searches"
  (->> entity space-to-plus (str "https://www.google.com/search?q=stock+")))

(defn find-stock-price [http-resp]
  (->> http-resp
       :body
       (re-find #"data-value=\"\d+?\.?\d*") ;grabs data value segment
       (re-find #"\d+?\.?\d*") ;grabs just the number from the expression
       Double/parseDouble))

(defn find-ticker-name [http-resp]
  "Currently ignoring LON and TYO exchanges to avoid currency conversion"
  (->> http-resp
       :body
       (re-find #"(NASDAQ|NYSE):? \w+")
       (re-find #"\w+$")))

(defn find-ticker-and-price [http-resp]
  {:ticker (find-ticker-name http-resp)
   :price (find-stock-price http-resp)})

(defn get-ticker-and-price-for-entity [entity]
  "If entity is a public company, return that company's ticker and share value"
  (-> entity
      build-search-string
      (http/get default-chrome-headers)
      find-ticker-and-price))
