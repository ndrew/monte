(ns monte.launcher
  "Launcher and updater for Monte"
  (:require [fs.core :as fs]))

(def home (str (System/getProperty "user.home") "/.monte"))

(def settings-loc (str home "/settings.clj"))

(defn check-dirs []
    (when-not 
      (fs/exists? home)
      (fs/mkdirs home)
      (spit settings-loc (pr-str @settings))
))

(defn read-settings [cfg] 
	(let [cfg (or cfg {})]
		(try
      		(merge cfg (read-string (slurp settings-loc)))
		(catch Exception e
        	(println "Error while loading settings, returning defaults")
			cfg
        ))))

(defn latest-version [] 
	0.0 ; todo: get from server
)

(defn get-jar-file [version]
  (let [path (str home "/monte-" version ".jar")]
    (when-not (fs/exists? path)
      (throw (Exception. (str path " does not exist!"))))
    (fs/file path)))

(defn run [version]
	(let [jar (get-jar-file version)]
		; todo: add jar to class path and launch server and client
    )
)

(def settings (atom
  (read-settings {:version 0.0})))

(when-not (= (:version @settings) (latest-version))
	; todo: fetch new version from server
  	(throw (Exception. "Fetching from server not implemented yet!"))
)

(run (:version @settings))