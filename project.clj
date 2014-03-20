(defproject test3 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojurescript "0.0-2080"]      ;; 1889 does not work -> 0.0-1859
                 [org.clojure/core.async "0.1.256.0-1bf8cf-alpha"]
                 [org.clojure/core.match "0.2.0-rc5"]
                ;; [core.async "0.1.0-SNAPSHOT"]
                 [enfocus "2.0.2"]] ;; 0-SNAPSHOT"]]

  :plugins [[lein-cljsbuild "1.0.2"]] ;; 0.3.3

  :cljsbuild
  {:builds
   [{:id "test"
      :source-paths ["src/cljs/"]
      :compiler {
                :output-to "resources/public/js/calculator.js"}}
    {:id "production"
      :source-paths ["src/cljs/"]
      :compiler {:optimizations :advanced
                :pretty-print false
                :static-fns true
                :output-to "resources/public/js/calculator.js"}}]}
                )


