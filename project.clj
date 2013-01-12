(defproject monte "0.0.0-SNAPSHOT"
  :description "Monte is a software visualisation tool created as 
            	a part of Andrew Sernyak master degree' thesis"
  :url "https://github.com/ndrew/monte"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [fs                  "1.2.0"]
                 [noir                "1.2.1" 
                    :exclusions [org.clojure/clojure]]
                 [org.clojure/tools.cli "0.2.2"]
                 [fetch               "0.1.0-alpha2"]
                 [crate               "0.2.3"]
                 [jayq                "0.1.0-alpha4"]
                 ; maybe use these instead of fetch
                 ;[shoreleave          "0.2.2"]
                 ;[shoreleave/shoreleave-remote-noir "0.2.2"]
                 ]
  :plugins [[lein-cljsbuild   "0.2.9"]
            [lein-ring        "0.7.1"]]
  :hooks [leiningen.cljsbuild]
  
  :cljsbuild {
    :builds [{
        :jar true
		:source-path "src-cljs"
        :compiler {
            :output-to "resources/public/js/js.js"
            :optimizations :whitespace
            :externs ["resources/public/extern/raphael-min.js"]
            :pretty-print true}
    }]}

  :repl-options { 
    :init-ns monte.launcher 
    :init (do
      ;; <!> add some hacks later
    )}

  :ring         {:handler monte.backend.server/handler
                 :auto-reload? true
                 :auto-refresh true}

  :main monte.launcher

  :profiles {
    :dev { :main ^{:skip-aot true} monte.launcher}
    :deploy {:main ^{:skip-aot false} monte.launcher :aot [monte.launcher]}}
)