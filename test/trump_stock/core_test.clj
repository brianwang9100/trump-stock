(ns trump-stock.core-test
  (:require [clojure.test :refer :all]
            [trump-stock.stream-java :refer :all]
            [trump-stock.portfolio :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))


(deftest create-position
  (testing "with short position"
    (purchase-shares "Google" "GOOG" 900 0.4)
    (is (= 1 (count long-positions)))))
  ;   ; (is (= (first long-positions) {
  ;   ;                                :entity "Google"
  ;   ;                                :ticker "GOOG"
  ;   ;                                :shares (/ 500000 900)
  ;   ;                                :start-date})))
  ; (testing "with long position"))


(purchase-shares "Google" "GOOG" 1100 -0.5)
(def p (assoc (first @short-positions) :end-date (l/local-now)))
(reset! short-positions [p])
(is-finished? (first @short-positions))
(update-positions)


(purchase-shares "Twitter" "TWTR" 15 0.9)
(def p (assoc (first @long-positions) :end-date (l/local-now)))
(reset! long-positions [p])
(is-finished? (first @long-positions))

(def finished-longs (vec (filter is-finished? @long-positions)))
