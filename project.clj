(defproject monte "0.0.0-SNAPSHOT"
  :description "Monte is a software visualisation tool created as 
            	a part of Andrew Sernyak master degree' thesis"
  :url "https://github.com/ndrew/monte"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [fs                    "1.2.0"]
                 [compojure             "1.1.3"]
                 [lib-noir              "0.3.3"]
                 [org.clojure/tools.cli "0.2.2"]
                 [crate                 "0.2.3"]
                 [jayq                  "0.1.0-alpha4"]
                 
                 [shoreleave            "0.2.2"]
                 [com.cemerick/shoreleave-remote-ring "0.0.2"]]
  :plugins [[lein-cljsbuild   "0.2.10"]
            [lein-ring        "0.7.1"]]
  :hooks [leiningen.cljsbuild]
  
  :cljsbuild {
    :builds [{
        :incremental false
        :jar true
		    :source-path "src-cljs"
        :compiler {
            :output-to "resources/public/js/js.js"
            :optimizations :whitespace
            :externs ["resources/public/extern/raphael-min.js"
                      "resources/public/extern/dracula_graph.js"
                      "resources/public/extern/dracula_graffle.js"
                      "resources/public/extern/dracula_algorithms.js"
                      ]
            :pretty-print true}
    }]}

  :repl-options { 
    :init-ns monte.launcher 
    :init (do
      ;; <!> add some hacks later
    )}

  :ring         {:handler monte.backend.server/monte-routing
                 :auto-reload? true
                 :auto-refresh true}

  :main monte.launcher

  :profiles {
    :dev { :main ^{:skip-aot true} monte.launcher}
    :deploy {:main ^{:skip-aot false} monte.launcher :aot [monte.launcher]}}
)