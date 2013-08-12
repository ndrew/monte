(ns monte.backend.api
	"Monte Remote Call API"
	(:use [cemerick.shoreleave.rpc :only [defremote]])
	(:require 
			[monte.runtime :as runtime]   			  
			[me.raynes.fs :as fs]))


(defremote get-app-status [last-changed]
  ; tbd: use last-changes if there will be status versioning
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

;;;;;;;;;;;;;;;;;;;;;;
; 

(defremote new-project [project-name]
  (runtime/new-project project-name))
    ;(let [proj (runtime/new-project project-name)]
    ;    (runtime/set-project (:hash proj))))