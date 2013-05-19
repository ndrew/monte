(ns monte.views.project 
  "Monte '/project' page"
  (:require 
  	[monte.views.common :as common]
    [monte.runtime :as runtime])
  (:use 
    [hiccup.element :only [javascript-tag]]))

;(defpage "/project/:project" {:keys [project] }

(defn project-view[project] ; project – hash

  (common/layout nil
    [:article {:id "project"}
      [:div {:class "status"} "STATUS HERE"]
      [:nav 
        [:h1 {:class "proj-name"} "Opening..."] 
        [:h2 {:class "view-nav"}
           ; todo: migrate to lists
           "&nbsp;1. " [:a {:href "#" :id "miner"} "Miners"] 
           "&nbsp;2. " [:a {:href "#" :id "refine"} "Refine data"] ; todo: better naming
           "&nbsp;3. " [:a {:href "#" :id "visualization"} "Visualization"]]]
      
      [:div {:class "actions"}
                  [:button {:id "run_miners"} "Run miners"]
                  [:button {:id "redraw"} "Redraw"]]
      
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
            [:span {:class "hint"} "(?)"]]
            
            [:div {:class "list"}
                  [:h4 "Entities"]
                  [:table {:id "entities_table"}
                         [:tr [:td {:class="new" :colspan="3"}
                                   [:a {:href "#" :id "new_miner"} "add new"]]]]]

            [:div {:class "list"}
                  [:h4 "Connections"]
                  [:table {:id "connections_table"}
                         [:tr [:td {:class="new" :colspan="3"}
                                   [:a {:href "#" :id "new_miner"} "add new"]]]]]

                        
            ]
      
      
      [:div {:id "visualization_view" :class "view"}
            [:div  
              [:label "Query: "]
              [:input {:type "text" :class "filter_box"} ]
              [:span {:class "hint"} "(?)"]]
            [:div
              [:div {:id "legend"}
                 [:div {:class "list" :style "display: block; width: 100%;"}
                   [:h4 "Legend"]
                   [:table {:id "legend_table"} [:tbody ]]
                 ]]
              [:div {:id "canvas"}]]]]
      
    ; just some info for debugging
    [:pre {:id "debug"}]
    ; todo: find a better way to do this
    (javascript-tag (str
                      "monte.ui.project_view = true;" 
                      "monte.ui.project_hash = " project ";"))
    )
  )