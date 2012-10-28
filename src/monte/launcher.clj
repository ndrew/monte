(ns monte.launcher
  "Launcher and updater for Monte"
  (:gen-class :main true)
  (:require 
    [monte.backend.server :as server]
    [fs.core :as fs]))

(def default-settings {
  :version 0.0
  :port 8899
}) 

(def settings     (atom default-settings))
(def home         (str (System/getProperty "user.home") "/.monte"))
(def settings-loc (str home "/settings.clj"))

(defn init []
    (cond 
      (fs/exists? home) (
        reset! settings (merge default-settings (read-string (slurp settings-loc))))
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

(defn -main []
  (init)
  (when-not (= 0.0 (:version @settings))
    (throw (Exception. "Fetching from server not implemented yet!")) ; todo: fetch new version from server
  )
  (def server_instance (server/start (:port @settings)))
  (println "Press Enter to stop Monte!")
  (read-line) ; todo: find out why this doesn't work from console
  (server/stop server_instance)
  (println "Bye"))