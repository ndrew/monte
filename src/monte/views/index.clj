(ns monte.views.index "Monte '/' page"
  (:require 
  	[monte.views.common :as common]
    [monte.runtime :as runtime])
  (:use [noir.core :only [defpage]]
        [hiccup.page-helpers :only [javascript-tag]]))

(defpage "/" []
  (common/layout
    [:article {:id "intro"
               :style "background-color: rgba(255,255,128,0.5);"}
           
      [:h1 "Monte"]
      [:p "Please open existing project, or create a new one" 
          [:span {:class "hint"} "(?)"]]
      
      [:ul {:class "projects"}
        [:li {:class "new"} 
             [:a {:href "#"} "add new"]]]]
        
      ; just some info for debugging
      [:pre {:id "debug"}]
      (javascript-tag "monte.ui.list_projects()") ;; ?
      ))


(defpage "/project/:project" {:keys [project] }
  (runtime/set-project project)
  (common/layout 
    [:h1 project]
    (javascript-tag (str "monte.ui.project_details('" project "');" ))))