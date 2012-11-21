(ns monte.views.common 
  "common template for other monte views"
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5 javascript-tag]]))


(defn template [& content]
  (html5 
    [:head 
      [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
      ; favicon
      ;      [:link {:rel "shortcut icon" :href "/i/favicon.ico" :type "image/x-icon"}]

      [:title "Monte"]
      (include-css "/css/jquery-ui.css")
      (include-css "/css/monte.css")
      (include-js "/extern/jquery-1.7.2.min.js")
      (include-js "/extern/jquery-ui-1.9.1.js")
      (include-js "/extern/raphael-min.js")
    ]
    [:body content ]

    ; TODO: no clojure deps 
    ;
    ; (javascript-tag "goog.require('hello')")
    (include-js "/js/cljs.js")))


(defpartial layout [& content]
  (template content))