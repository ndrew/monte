(ns monte.backend.api
	"Monte Remote Call API"
	(:require [monte.runtime :as runtime]
			  [fs.core :as fs])
	(:use [noir.fetch.remotes :only [defremote]]))



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



(defremote get-workspace [& last-changed] 
  (cond (or (nil? last-changed) (nil? (first last-changed))) 
        (runtime/full-refresh)
  :else (runtime/partial-refresh (first last-changed))))