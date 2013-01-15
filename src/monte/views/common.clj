(ns monte.views.common 
  "common template for other monte views"
  (:use 
        [hiccup.page :only [include-css include-js html5]]
        [hiccup.element :only [javascript-tag]]))

(defn layout [& content]
  (html5 
    [:head 
      [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
      [:meta {:name "description" :content "Monte: visualization tool"}]
      [:meta {:name "author"      :content "Andrew Sernyak"}]
      
      ; favicon
      ;      [:link {:rel "shortcut icon" :href "/i/favicon.ico" :type "image/x-icon"}]

      [:title "Monte"]
      (include-css "/css/jquery-ui.css")
      (include-css "/css/monte.css")
      (include-js "/extern/jquery-1.7.2.min.js")
      (include-js "/extern/jquery-ui-1.9.1.js")
      (include-js "/extern/raphael-min.js")
    
      (include-js "/extern/dracula_graph.js")
      (include-js "/extern/dracula_graffle.js")
      (include-js "/extern/dracula_algorithms.js")    
    
      (include-js "/js/js.js")
    ]
    [:body [:div {:id "viewport"} content ]]))