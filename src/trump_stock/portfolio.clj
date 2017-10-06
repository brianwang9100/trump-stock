(ns trump-stock.portfolio

  (:require [clj-time.core :as t]
            [clj-time.periodic :as p]
            [clj-time.local :as l]))
  ; (:require [crypto.random])
  ; (:require [oauth.client :as oauth])
  ; (:require [clj-http.client :as http]))

;; TODO:
;; 1. ensure we are getting correct stock with entity instead of ticker
;; 2. complete finish long + short positions
;; 3. write fn to update total money amt

(def total (atom 1000000))
(def short-positions (atom []))
(def long-positions (atom []))
(def history (atom []))

(defn now-and-then [days-later]
  (take 2 (p/periodic-seq (l/local-now) (t/days days-later))))

(defn position [ticker shares start-date end-date buy-price sell-price]
  {:ticker ticker
   :shares shares
   :start-date start-date
   :end-date end-date
   :buy-price buy-price
   :sell-price sell-price})

(defn create-long-position [ticker price shares days]
  (let [[start-date end-date] (now-and-then days)
        new-position (position ticker shares start-date end-date price nil)]
    (swap! long-positions #(conj % new-position))))

(defn create-short-position [ticker price shares days]
  (let [[start-date end-date] (now-and-then days)
        new-position (position ticker shares start-date end-date nil price)]
    (swap! short-positions #(conj short-positions new-position))))

(defn is-finished? [position]
  (t/before? (:end-date position) (l/local-now)))

;; change position map to store the original entity name
(defn finish-long-position [position]
  (let [ticker (:ticker position)
        {} (get-stock-ticker-and-cost ticker)]))

(defn finish-short-position [position])
(defn update-positions []
  (let [finished-longs (filter is-finished? long-positions)
        finished-shorts (filter is-finished? short-positions)
        updated-longs (map finish-long-position finished-longs)
        updated-shorts (map finish-short-position finished-shorts)]
    (swap total .............................)
    (swap! history #(-> % (conj updated-longs) (conj updated-shorts)))))

(defn calculate-num-shares [price total percent]
  "calculates number of shares to buy from percentage of total"
  (try
    (/ (* total percent) price)
    (catch Exception e
      (println (str "exception in calculate-num-shares: " (.getMessage e))))))

(defn evaluate [ticker price sentiment]
  "evaluates and creates positions from ticker price and sentiment. number of shares bought is 50%"
  (let [shares (calculate-num-shares price @total 0.5)]
    (cond
      (< sentiment -0.6) (create-short-position ticker price shares 3)
      (< sentiment -0.1) (create-short-position ticker price shares 1)
      (> sentiment 0.6) (create-long-position ticker price shares 3)
      (> sentiment 0.1) (create-long-position ticker price shares 1))))

(defn print-history []
  (println history))

; [{
;    ticker-name: "NSYE"
;    numshares: "5"
;    buy-price: "783.58"
;    sell-price: ""
;    date-buy: ""
;    date-sell: ""}]
;
; 1 3 day thing
; ;[0.1 - 0.6) 1 day
; ;[0.6 - 1.0] 3 day
;
; short
; 1)sell-price - 2)buy-price
;
; long
; 2)sell-price - 1)buy-price
;
