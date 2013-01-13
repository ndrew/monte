(ns monte.backend.api
	"Monte Remote Call API"
	(:use [cemerick.shoreleave.rpc :only [defremote]])
	(:require 
			[monte.runtime :as runtime]   			  
			[fs.core :as fs]))

(defremote get-workspace [last-changed] 
  (cond (nil? last-changed) 
        (runtime/full-refresh)
  :else (runtime/partial-refresh last-changed)))
  
; actions

(defremote set-project [project-hash]
	(runtime/set-project project-hash))