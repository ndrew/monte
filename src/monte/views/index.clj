(ns monte.views.index "Monte '/' page"
  (:require 
  	[monte.views.common :as common])
  (:use [noir.core :only [defpage]]))

(defpage "/" []
  (common/layout
    [:article {:id "intro"
               :style "background-color: rgba(255,255,128,0.5);"}
           
      [:h1 "Monte"]
      [:p "Please open existing project, or create a new one" 
          [:span {:class "hint"} "(?)"]]
      
      [:ul
        [:li {:class "new"} 
             [:a {:href "#"} "add new"]]]]
        
            
      ;[:header {:id "top"}
         		;[:h3 "Monte"]
         		;[:ul
         		;	[:li [:a {:href "#"}"юх піу!"]]]]
           ;[:p "Hello World!"]
           ;[:hr]
           [:div {:id "workspace"}]
           ;[:hr]
           [:section {:id "repo"}
              [:ul
                [:li 
                  [:input {:type "text" :class "dir-chooser"}] 
                  [:button "load"]]]]
           ;"<input type='file' webkitdirectory directory multiple mozdirectory onChange='console.log(this.files)'>"
           ))