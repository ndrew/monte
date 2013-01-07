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
    (javascript-tag "monte.ui.list_projects()")))


(defpage "/project/:project" {:keys [project] }
  (runtime/set-project project)
  (common/layout 
    [:article {:id "project"
               :style "background-color: rgba(255,255,128,0.5);"}
    
      [:h1 {:style "display: inline;"} project] 
      [:h2 {:style "display: inline; padding-left: 1.5em;"}
         " 1. " [:a {:href "#" :id "miner"} "Miners"] 
         " 2. " [:a {:href "#" :id "refine"} "Refine data"] ; todo: better naming
         " 3. " [:a {:href "#" :id "visualization"} "Visualization"]]]
      [:div {:id "miner_view" :style "display: none;"}]
      [:div {:id "refine_view" :style "display: none;"}]
      [:div {:id "visualization_view" :style "display: none;"}]
      
      
      
      
    ; just some info for debugging
    [:pre {:id "debug"}]
    (javascript-tag (str "monte.ui.project_details('" project "');" ))))