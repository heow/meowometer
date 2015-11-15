(defproject hello-world "0.0.1"
  :main meowometer.web ; Heroku main
  :dependencies [
                 [org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.5"]
                 [ring/ring-jetty-adapter "0.3.9"]
                 [com.mysql/connectorj "5.1.12"]
                 [clojureql "1.0.0"]
                 [clj-aws "0.0.1-SNAPSHOT"]
                 [org.clojars.kovasb/clj-time "0.4.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.3.0-SNAPSHOT"]])