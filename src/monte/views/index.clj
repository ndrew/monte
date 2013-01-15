(ns monte.views.index "Monte '/' page"
  (:require 
  	[monte.views.common :as common]
    [monte.runtime :as runtime])
  (:use 
    [hiccup.element :only [javascript-tag]]))


(defn intro-view[]
  (runtime/to-initial-state) 
  (common/layout
    [:article {:id "intro"}
           
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