(ns monte.views.common 
  "common template for other monte views"
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))


(defn template [& content]
  (html5 
    [:head 
      [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
      ; favicon
      ;      [:link {:rel "shortcut icon" :href "/i/favicon.ico" :type "image/x-icon"}]

      [:title "Monte"]
      (include-css "/css/monte.css")
    ]
    [:body content ]
    (include-js "/js/cljs.js")))

(defpartial layout [& content]
  (template content)
)