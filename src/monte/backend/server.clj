(ns monte.backend.server
	"Monte noir server"
	(:use noir.core)
  	(:require [noir.server :as server]
  			   monte.backend.api))

(server/load-views "src/monte/views/")

(defn start [port]
	(server/start port))

(defn stop [instance]
	(server/stop instance))

(def handler (server/gen-handler {	:mode :dev
									:ns 'monte.views}))