(ns monte.backend.api
	"Monte Remote Call API"
	(:require [monte.runtime :as runtime]
			  [fs.core :as fs])
	(:use [noir.fetch.remotes :only [defremote]]))



(defremote get-workspace [last-changed] 
  (cond (nil? last-changed) 
        (runtime/full-refresh)
  :else (runtime/partial-refresh last-changed)))



; actions

(defremote set-project [project-hash]
	(runtime/set-project project-hash)           
)



;; TODO: move to ioutils, or smth like that

(defn child-directories [path]
	(fs/list-dir path))

(defremote list-dirs[& root-path]
	(cond
		(nil? root-path) 
			(let [current (str (. System getProperty "user.dir") 
							   (. System getProperty "file.separator"))
				  dirs (child-directories current)]
				[current]
					; todo: directory listing for autoloading 
			)
		:else 
			(child-directories (first root-path))))
