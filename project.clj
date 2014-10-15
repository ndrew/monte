(defproject monte "0.0.3"
  :description "Monte is a software visualisation tool created as
            	a part of Andrew Sernyak master degree' thesis"
  :url "https://github.com/ndrew/monte"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.logic "0.7.5"] ; 0.8.8 - fail datalog
                 [org.clojure/data.json "0.2.5"]

                 [datalog               "0.1.1"]
                 [me.raynes/fs          "1.4.4"]
                 [org.clojure/tools.cli "0.3.1"]
                 [myguidingstar/clansi "1.3.0"]
                 [cheshire "5.3.1"]
                 [org.clojure/tools.namespace "0.2.7"]
                 [criterium "0.4.3"]
                 [org.codehaus.jsr166-mirror/jsr166y "1.7.0"]
                 ]

  :repl-options {
    ;:init-ns monte.launcher
    :init (do
      ;; <!> add some hacks later
    )}

  :javac-options     ["-target" "1.6" "-source" "1.6"]
  :jvm-opts ["-Xms1024m", "-Xmx1024m",
             ;"-XX:-UseGCOverheadLimit", "-XX:+UseConcMarkSweepGC", "-XX:+CMSIncrementalMode",
             ;"-XX:+DoEscapeAnalysis", "-XX:+UseBiasedLocking",
             "-XX:PermSize=64M", "-XX:MaxPermSize=256M"]

)
