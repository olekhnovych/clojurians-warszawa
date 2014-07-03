(defproject clojure-bidi-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [bidi "1.10.4"]
                 [clj-http "0.7.8" :exclusions [commons-codec]]
                 [compojure "1.1.6"]
                 [ring/ring-json "0.2.0"]
                 [ring/ring-jetty-adapter "1.2.1"]]

  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler clojure-bidi-test.core/handler
         :auto-reload? true})
