(defproject miners-exec-viz "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [prismatic/dommy "0.1.1"]]
  :min-lein-version "2.0.0"

  :source-paths ["src/clj" "src/cljs"]

  :plugins [[lein-cljsbuild "0.3.0"]]

:cljsbuild {:builds [{:source-paths ["src/cljs"]
                        :compiler { :output-to "public/viz.js"
                                    :pretty-print true
                                    :optimizations :simple}}]})
