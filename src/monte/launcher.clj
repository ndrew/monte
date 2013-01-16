(ns monte.launcher
  "Launcher and updater for Monte"
  (:gen-class :main true)
  (:use [clojure.tools.cli :only [cli]])
  (:require 
    [monte.backend.server :as server]
    [monte.runtime :as runtime]
    [clojure.tools.cli :as cli]
    [fs.core :as fs]
    [clojure.java.browse :as browser]))

(def default-settings {
  :version 0.0}) 

(def settings     (atom default-settings))
(def home         (str (System/getProperty "user.home") "/.monte"))
(def settings-loc (str home "/settings.clj"))

(defn init [options]
  (when-not (fs/exists? home) 
     (fs/mkdir home)
     (spit settings-loc (pr-str default-settings)))
  
  (reset! settings
    (merge (if (fs/exists? settings-loc) 
      (read-string (slurp settings-loc)) default-settings) options)))


(defn -main [& args]
  (print "Starting Monte backend..")
  (let [[options args banner]
        (cli args
          ["-p" "--port" "Port to listen on" :default 8899]
          ["-a" "--[no-]auto-open" :default true])]
    
      (init options)
      (when-not (= 0.0 (:version @settings))
        (throw (Exception. "Fetching from server not implemented yet!"))) ; todo: fetch new version from server

      ;(if (seq args)
      ;  (if (every? (fn [path] (and (fs/exists? path) (fs/directory? path))) args)
      ;      (reset! runtime/workspace (core/super-repo (apply vector args)) ))) 
      
      (def server_instance (future (server/start (:port @settings))))
      (if (:auto-open @settings)
        (browser/browse-url (str "http://localhost:" (:port @settings))))

      (println "STARTED")
      (println "Press Cmd^C or Control^C to stop Monte! Or type some crap")
      
      (read-line) ; todo: find out why this doesn't work from console
      (.stop @server_instance)
      
      (println "Bye")))