(ns monte.views.project "Monte '/project' page"
  (:require 
  	[monte.views.common :as common]
    [monte.runtime :as runtime])
  (:use 
    [hiccup.element :only [javascript-tag]]))

;(defpage "/project/:project" {:keys [project] }

(defn project-view[project] ; project – hash

  (common/layout 
    [:article {:id "project"}
      [:div {:class "status"} "STATUS HERE"]
    
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
            [:div {:class "actions"}
                  [:button {:id "run_miners"} "Run miners"]]
      ]
      
    
    
      [:div {:id "refine_view" :class "view"} 
            [:p "Connect the data sets from miners. " 
            [:span {:class "hint"} "(?)"]]]
      
      
      [:div {:id "visualization_view" :class "view"}
            [:p "Define the views and configure data presentation. " 
            [:span {:class "hint"} "(?)"]]
            [:label "Filter: "][:input {:type "text" :class "filter_box"} ][:button {:id "redraw"} "Redraw"]
            [:div
              [:div {:id "legend"}]
              [:div {:id "canvas"}]]
            ]]

      
      
    ; just some info for debugging
    [:pre {:id "debug"}]
    ; todo: find a better way to do this
    (javascript-tag (str
                      "monte.ui.project_view = true;" 
                      "monte.ui.project_hash = " project ";"))
    ))