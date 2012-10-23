(ns monte.server
	"Monte noir server"
	(:use noir.core)
  	(:require [noir.server :as server])
)

(server/load-views "src/monte/views/")

;(defpage "/" []
;    "Hello Monte!")
;(defpage [:get "/test"] [] 
;	"This is a test!")


(defn start [port]
	(server/start port)
)


(defn stop [instance]
	(server/stop instance)
)