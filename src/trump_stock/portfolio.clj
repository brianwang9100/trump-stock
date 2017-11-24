(ns trump-stock.portfolio

  (:require [clj-time.core :as t]
            [clj-time.periodic :as p]
            [clj-time.local :as l]
            [trump-stock.stock-data :refer [get-ticker-and-price-for-entity]]))
  ; (:require [crypto.random])
  ; (:require [oauth.client :as oauth])
  ; (:require [clj-http.client :as http]))

(def total (atom 1000000))
(def short-positions (atom []))
(def long-positions (atom []))
(def history (atom []))

(defn now-and-then [days-later]
  (take 2 (p/periodic-seq (l/local-now) (t/days days-later))))

(defn position [entity ticker shares start-date end-date buy-price sell-price]
  {:entity entity
   :ticker ticker
   :shares shares
   :start-date start-date
   :end-date end-date
   :buy-price buy-price
   :sell-price sell-price})

(defn update-total [amount]
  (swap! total #(+ % amount)))

(defn create-long-position [entity ticker price shares days]
  "buys shares, and updates total (should be net loss)"
  (let [[start-date end-date] (now-and-then days)
        new-position (position entity ticker shares start-date end-date price nil)]
    (update-total (* price shares -1))
    (swap! long-positions #(conj % new-position))))

(defn create-short-position [entity ticker price shares days]
  "sells borrowed shares, and updates total (should be net gain)"
  (let [[start-date end-date] (now-and-then days)
        new-position (position entity ticker shares start-date end-date nil price)]
    (update-total (* price shares))
    (swap! short-positions #(conj % new-position))))

(defn is-finished? [position]
  (t/before? (:end-date position) (l/local-now)))

(defn finish-long-position [position]
  (let [entity (:entity position)
        shares (:shares position)
        price (:price (get-ticker-and-price-for-entity entity))]
    (update-total (* price shares))
    (assoc position :sell-price price)))

(defn finish-long-position-debug [position]
  (let [entity (:entity position)
        shares (:shares position)
        price (:price (get-ticker-and-price-for-entity entity))]
    (println entity)
    (println shares)
    (println price)))
    ; (update-total (* price shares))
    ; (assoc position :sell-price price)))

(defn finish-short-position [position]
  (let [entity (:entity position)
        shares (:shares position)
        price (:price (get-ticker-and-price-for-entity entity))]
    (update-total (* price shares -1))
    (assoc position :buy-price price)))

(defn finish-short-position-debug [position]
  (let [entity (:entity position)
        shares (:shares position)
        price (:price (get-ticker-and-price-for-entity entity))]
    (println entity)
    (println shares)
    (println price)))
    ;(update-total (* price shares -1))
    ;(assoc position :buy-price price)))

(defn calculate-net [{:keys [buy-price sell-price]} position]
  (- sell-price buy-price))

(defn update-positions []
  (let [finished-longs (vec (filter is-finished? @long-positions))
        finished-shorts (vec (filter is-finished? @short-positions))
        updated-longs (map finish-long-position finished-longs)
        updated-shorts (map finish-short-position finished-shorts)]
    (swap! long-positions #(remove is-finished? %))
    (swap! short-positions #(remove is-finished? %))
    (swap! history #(concat % updated-longs updated-shorts))))

(defn calculate-num-shares [price total percent]
  "calculates number of shares to buy from percentage of total"
  (try
    (int (Math/floor (/ (* total percent) price)))
    (catch Exception e
      (println (str "exception in calculate-num-shares: " (.getMessage e))))))

(defn purchase-shares [entity ticker price sentiment]
  "evaluates and creates positions from ticker price and sentiment. number of shares bought is 50%"
  (let [shares (calculate-num-shares price @total 0.5)]
    (println (str "Purchasing " shares " shares for " entity ", " ticker ", " price ", " sentiment))
    (cond
      (< sentiment -0.6) (create-short-position entity ticker price shares 3)
      (< sentiment -0.1) (create-short-position entity ticker price shares 1)
      (> sentiment 0.6) (create-long-position entity ticker price shares 3)
      (> sentiment 0.1) (create-long-position entity ticker price shares 1))))

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
