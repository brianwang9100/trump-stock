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
