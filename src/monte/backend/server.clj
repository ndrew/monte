(ns monte.backend.server
  "Monte server routines"
  (:use compojure.core
  		  [monte.views.common :as common]
        [monte.logger :only [dbg err]]
  	    [ring.adapter.jetty :only [run-jetty]])
  (:require [monte.views.index :as view]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [cemerick.shoreleave.rpc :as rpc]))

 
(defroutes monte-routes
  
  (GET "/" [] (common/gen-html :ui-test "It's alive!" []))
  
  ;(GET "/" [] (view/intro-view))
  ;(GET "/settings" [] (settings-view))
  ;(GET "/project/:hash" [hash] (project-view hash))
  ;(GET "/ui-tests" [] (common/gen-html :ui-test "UI Test" []))
  
  (route/files "/" {:root "public"})
  (route/not-found "<h1>Page not found</h1>"))
 
 
(def monte-routing (-> #'monte-routes
                       rpc/wrap-rpc
                       handler/site))
 
  
(defn start [port]
  (try  
  	(dbg "Starting server..")	
  	(run-jetty #'monte-routing {:port port})
  	(catch Exception e 
      (err e))))