(defproject trump-stock "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.7.0"]
                 [twitter-api "1.8.0"]
                 [crypto-random "1.2.0"]
                 [clj-oauth "1.5.5"]
                 [com.twitter/hbc-core "2.2.0"]
                 [com.fzakaria/slf4j-timbre "0.3.7"]
                 [cheshire "5.8.0"]
                 [proto-repl "0.3.1"]
                 [clj-time "0.14.0"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "trump-stock-standalone.jar"
  :profiles {:production {:env {:production true}}})
