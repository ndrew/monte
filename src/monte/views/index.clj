(ns monte.views.index "Monte '/' page"
  (:require 
  	[monte.views.common :as common]
    [monte.runtime :as runtime])
  (:use [noir.core :only [defpage]]
        [hiccup.page-helpers :only [javascript-tag]]))


(defpage "/" []
  (runtime/to-initial-state) 
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
    (javascript-tag "monte.ui.intro_view = true;")
  ))


(defpage "/project/:project" {:keys [project] }
  (common/layout 
    [:article {:id "project"
               :style "background-color: rgba(255,255,128,0.5);"}
    
      [:h1 {:style "display: block;"} "Opening..."] 
      [:h2 {:style "display: block; padding-left: 1.5em;"}
         " 1. " [:a {:href "#" :id "miner"} "Miners"] 
         " 2. " [:a {:href "#" :id "refine"} "Refine data"] ; todo: better naming
         " 3. " [:a {:href "#" :id "visualization"} "Visualization"]]
      
      [:div {:id "miner_view" :class "view"}
            [:p "Please select and configure miners. " 
            [:span {:class "hint"} "(?)"]]
            
            [:div {:class "list"}
                  [:h4 "Miners"]
                  [:table {:id "miner_table"}
                         [:tr [:td {:class="new" :colspan="3"}
                                   [:a {:href "#" :id "new_miner"} "add new"]]]]]

            [:div {:class "list"}
                  [:h4 "Variables"]
                  [:table {:id "var_table"}
                         [:tr [:td {:class="new" :colspan="3"}
                                   [:a {:href "#" :id "new_var"} "add new"]]]]]
      ]
      
    
    
      [:div {:id "refine_view" :class "view"} 
            [:p "Connect the data sets from miners. " 
            [:span {:class "hint"} "(?)"]]]
      
      [:div {:id "visualization_view" :class "view"}
            [:p "Define the views and configure data presentation. " 
            [:span {:class "hint"} "(?)"]]]]

      
      
    ; just some info for debugging
    [:pre {:id "debug"}]
    ; todo: find a better way to do this
    (javascript-tag (str
                      "monte.ui.project_view = true;" 
                      "monte.ui.project_hash = " project ";"))
    ))