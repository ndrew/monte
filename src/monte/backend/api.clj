(ns monte.backend.api
	"Monte Remote Call API"
	(:require [monte.runtime :as runtime]
			  [fs.core :as fs])
	(:use [noir.fetch.remotes :only [defremote]]))


(defn child-directories [path]
	(fs/list-dir path)
)

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

(defremote get-workspace[& last-changed] 
	(cond
		(nil? last-changed) 
			@monte.runtime/workspace ; return full workspace
		:else 
			(runtime/workspace-diff (first last-changed))))
					
