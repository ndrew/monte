(ns monte.launcher
  "Launcher and updater for Monte"
  (:gen-class :main true)
  (:use [clojure.tools.cli :only [cli]]
        [monte.logger :only [dbg err]])
  (:require 
    [monte.runtime :as runtime]
    [monte.backend.server :as server]
    [clojure.tools.cli :as cli]
    [me.raynes.fs :as fs]
    [clojure.java.browse :as browser]))


(def default-settings { :version 0.0 }) 
(def home             (str (System/getProperty "user.home") "/.monte"))
(def settings         (atom default-settings))
(def settings-loc     (str home "/settings.clj"))


(defn init [opts]
  (when-not (fs/exists? home) 
    (fs/mkdir home)
    (spit settings-loc (pr-str default-settings)))
  
  (swap! settings
         (fn[_](merge (if (fs/exists? settings-loc) 
                        (read-string (slurp settings-loc)) default-settings) opts))))


(defn- print-logo[]
  (io! (println (rand-nth monte.logger/logos)))) 


(defn- check-for-updates[]
  (when-not (= 0.0 (:version @settings))
    ; todo: fetch new version from server
    (throw (Exception. "Fetching from server not implemented yet!"))))


(defn -main [& args]
  (print-logo)
  
  (let [[opts args banner]
        (cli args
             ["-p" "--port" "Port to listen on"  :default 8899 :parse-fn #(Integer. %)]
             ["-a" "--[no-]auto-open"            :default false :flag true]
             ["-c" "--[no-]color"                :default false :flag true]
             ["-d" "--[no-]debug"                :default false :flag true]
             ["-h" "--help" "Show help"          :default false :flag true])]
    
    ; TODO: add http proxy settings (http?)
    ; pass url like protocol://username:password@server:1234
    (comment
      (.put (System/getProperties) "http.proxyHost" "http://wp.sixt.de")
      (.put (System/getProperties) "http.proxyPort" "8080");
      (.put (System/getProperties) "http.proxyUser", "rz_sixt\\sernyaka")
      (.put (System/getProperties) "http.proxyPassword", "DriveN0w14"))

    
    (init opts)
    
    (when (:help opts)
      (println banner)
      (System/exit 0))
    
    (alter-var-root #'monte.logger/*color* (constantly (:color opts)))
    (alter-var-root #'monte.logger/*debug* (constantly (:debug opts)))
    
    (check-for-updates)
    
    (runtime/to-initial-state)
    
    (dbg "Starting Monte backend..")
    
    (let [port (:port @settings)
          url (str "http://localhost:" port)
          server_instance (future (server/start port))]
      
      (if (:auto-open @settings)
        (browser/browse-url url)
        (dbg "Server is running at " url))
      
      (dbg "server started!")
      (dbg "Press Cmd^C or Control^C to stop Monte! Or type some crap")
      
      (read-line) ; todo: find out why this doesn't work from console
      (.stop @server_instance)
      
      (dbg "Bye"))))