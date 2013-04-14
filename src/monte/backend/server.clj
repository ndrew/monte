(ns monte.backend.server
  "Monte server routines"
  (:use compojure.core
  		  [monte.views.common :as common]
        [monte.logger :only [dbg err]]
  	    [ring.adapter.jetty :only [run-jetty]]
        sandbar.stateful-session
  )
  (:require [monte.views.index :as view]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [cemerick.shoreleave.rpc :as rpc]))


(defn index-page-handler[] 
  (when-not (session-get :runtime)
    ; init runtime
    (session-put! :user :default)
    (session-put! :runtime nil)

    
    (session-put! :current-view :index-page)
    (session-put! :init-cfg ["testo pesto"])
  )    
  
  (let [title "Monte"
        view-id  (session-get :current-view :index-page)
        init-cfg (session-get :init-cfg)]
  
    (common/gen-html view-id title init-cfg)))
 
 
(defroutes monte-routes
  ; page routes
  (GET "/"    [] (index-page-handler))
  ; api routes 
  (GET "/api" [] (do 
                   (dbg "TEST<br>")
                   "API"))
  
  
  ;(GET "/" [] (view/intro-view))
  ;(GET "/settings" [] (settings-view))
  ;(GET "/project/:hash" [hash] (project-view hash))
  ;(GET "/ui-tests" [] (common/gen-html :ui-test "UI Test" []))
  
  (route/files "/" {:root "public"})
  (route/not-found "<h1>Page not found</h1>"))
 
 
(def monte-routing (-> #'monte-routes
                       wrap-stateful-session
                       rpc/wrap-rpc
                       handler/site))
 
  
(defn start [port]
  (try  
  	(dbg "Starting server..")	
  	(run-jetty #'monte-routing {:port port})
  	(catch Exception e 
      (err e))))