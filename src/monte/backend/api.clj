(ns monte.backend.api
	"Monte Remote Call API"
	(:use [cemerick.shoreleave.rpc :only [defremote]])
	(:require 
			[monte.runtime :as runtime]   			  
			[fs.core :as fs]))


(defremote get-app-status [] 
  (runtime/get-app-status))

; tbd: introduce new api

;;;;;;;;;;;;;;;;;;;
; update routines

(defremote get-workspace [last-changed] 
  (cond (nil? last-changed) 
        (runtime/full-refresh)
  :else (runtime/partial-refresh last-changed)))

;;;;;;;;;;;;  
; actions

(defremote set-project [project-hash]
	(runtime/set-project project-hash))


(defremote run-miners [project-hash]
  (runtime/run-miners project-hash))