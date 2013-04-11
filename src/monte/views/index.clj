(ns monte.views.index "Monte '/' page"
  (:require 
  	[monte.views.common :as common])
  (:use 
    [hiccup.element :only [javascript-tag]]))

;tdb migrate to gen-html

(defn intro-view[]
  (common/layout nil
    [:article {:id "intro"}
      [:h1 {:class "proj-name"} "Monte"]
      [:a {:href "/settings" :style "float: right; display: inline;"} "Settings"]
      
      [:p "Please open existing project, or create a new one" 
          [:span {:class "hint"} "(?)"]]
      
      ;; TODO: use macro for this
      [:ul {:class "projects"}
        [:li {:class "new"} 
             [:a {:href "#"} "add new"]]]]
      
        
    ; just some info for debugging
    [:pre {:id "debug"}]
    ; todo: refactor
    (javascript-tag "monte.ui.intro_view = true;")))