(ns monte.launcher
  "Launcher and updater for Monte"
  (:gen-class :main true)
  (:use [clojure.tools.cli :only [cli]]
        [monte.logger :only [debug dbg err]])
  (:require 
    [monte.runtime :as runtime]
    [monte.backend.server :as server]
    [clojure.tools.cli :as cli]
    [fs.core :as fs]
    [clojure.java.browse :as browser]))


(def default-settings { :version 0.0 }) 
(def home             (str (System/getProperty "user.home") "/.monte"))
(def settings         (atom default-settings))
(def settings-loc     (str home "/settings.clj"))


(defn init [opts]
  (when-not (fs/exists? home) 
     (fs/mkdir home)
     (spit settings-loc (pr-str default-settings)))
  
  (reset! settings
    (merge (if (fs/exists? settings-loc) 
      (read-string (slurp settings-loc)) default-settings) opts)))


(defn -main [& args]
  (let [[opts args banner]
        (cli args
          ["-p" "--port" "Port to listen on"  :default 8899 :parse-fn #(Integer. %)]
          ["-a" "--[no-]auto-open"            :default false :flag true]
          ["-d" "--debug"                     :default false :flag true]
          ["-h" "--help" "Show help"          :default false :flag true])]
      
      (init opts)
      
      (when (:help opts)
        (println banner)
        (System/exit 0))
      
      (when-let [new-debug (:debug opts)] 
        (alter-var-root (var debug) (constantly new-debug)))
              
      (when-not (= 0.0 (:version @settings))
        (throw (Exception. "Fetching from server not implemented yet!"))) ; todo: fetch new version from server

      (runtime/to-initial-state)
      
      (dbg "Starting Monte backend..")

      (let [port (:port @settings)
            url (str "http://localhost:" port)
            server_instance (future (server/start port))]
        
        (if (:auto-open @settings)
          (browser/browse-url url)
          (println (str "Server is running at " url)))
  
        (dbg "server started!")
        (println "Press Cmd^C or Control^C to stop Monte! Or type some crap")
      
        (read-line) ; todo: find out why this doesn't work from console
        (.stop @server_instance)
      
        (println "Bye"))))