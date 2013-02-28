(ns monte.views.settings "Monte '/settings' page"
  (:require 
    [monte.views.common :as common]
    [monte.runtime :as runtime])
  (:use 
    [hiccup.element :only [javascript-tag]]))

(defn settings-view[]
  (common/layout 
    [:h1 "Settings"]
    
      [:div {:class "list"}
        [:h4 "Workspace"]
        [:table {:id "workspace_table" :class=""}
  
        ]]
      

      (javascript-tag "var view = \"monte.settings_ui.init\" ;")        
    )
  )