(ns monte.launcher
  "Launcher and updater for Monte"
  (:gen-class :main true)
  (:use [clojure.tools.cli :only [cli]])
  (:require 
    [monte.backend.server :as server]
    [monte.runtime :as runtime]
    [monte.core :as core]
    [clojure.tools.cli :as cli]
    [fs.core :as fs]
    [clojure.java.browse :as browser]
    [clojure.contrib.generic.functor :as f]
    ))

(def default-settings {
  :version 0.0
}) 

(def settings     (atom default-settings))
(def home         (str (System/getProperty "user.home") "/.monte"))
(def settings-loc (str home "/settings.clj"))

(defn init [options]
    (cond 
      (fs/exists? home) (
        reset! settings (merge (merge default-settings options) (read-string (slurp settings-loc))))
      :else (do(
        (fs/mkdirs home)
        (spit settings-loc (pr-str @settings))
      ))))

; these are waiting for better times
;
;(defn get-jar-file [version]
;  (let [path (str home "/monte-" version ".jar")]
;    (when-not (fs/exists? path)
;      (throw (Exception. (str path " does not exist!"))))
;    (fs/file path)))

;(defn run [version]
;	(let [jar (get-jar-file version)]
		; todo: add jar to class path and launch server and client
;    )
;)

(defn -main [& args]
  
   (let [[options args banner]
        (cli args
          ["-p" "--port" "Port to listen on" :default 8899]
          ["-a" "--[no-]auto-open" :default true]
          )]
      (init options)
      (when-not (= 0.0 (:version @settings))
        (throw (Exception. "Fetching from server not implemented yet!")) ; todo: fetch new version from server
      )

    (println (f.fmap (fn [x] x)  args))

;    (println (core/super-repo(apply vector args))

    (System/exit 0)
    (instance? args)
    (class args)

      (if args
        (if (every? (fn [path] (and (fs/exists? path) (fs/directory? path))) args)
            (reset! runtime/workspace (core/super-repo args) ))) 
      
      (def server_instance (server/start (:port @settings)))
      (if (:auto-open @settings)
        (browser/browse-url (str "http://localhost:" (:port @settings))))

      (println "Press Cmd^C or Control^C to stop Monte! Or type some crap")
      (read-line) ; todo: find out why this doesn't work from console
      (server/stop server_instance)
      (println "Bye")
    )
  )