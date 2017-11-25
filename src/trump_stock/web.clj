(ns trump-stock.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]
            [trump-stock.stream-java :refer [start-streaming!]
            [trump-stock.portfolio :as portfolio]]]))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str "Total Money (Starting w/ 1M): " @portfolio.total)})

(defroutes app
  (GET "/" []
       (splash))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (println "Starting Webserver...")
    (jetty/run-jetty (site #'app) {:port port :join? false})
    (println "Starting Twitter-Buying EventLoop...")
    (start-streaming!)))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
