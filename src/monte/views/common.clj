(ns monte.views.common 
  "common template for other monte views"
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

; todo: add some css
(defpartial layout [& content]
  (html5 
    [:head 
      [:title "Monte"]
      (include-css "/css/monte.css")
    ]
    [:body content ]
    (include-js "/js/cljs.js")
  )
)