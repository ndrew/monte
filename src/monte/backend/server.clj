(ns monte.backend.server
	"Monte noir server"
	(:use noir.core)
  	(:require [noir.server :as server]
  			  [noir.shoreleave.rpc :as rpc]
  			  [monte.backend.api]))


(server/load-views "src/monte/views/")

(rpc/activate-remotes!)
(rpc/remote-ns 'monte.backend.api :as "api")

(defn start [port]
	(server/start port))

(defn stop [instance]
	(server/stop instance))

(def handler (server/gen-handler {	:mode :dev
									:ns 'monte.views}))