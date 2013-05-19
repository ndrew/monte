(ns monte.views.common 
  "common template for other monte views"
  (:use 
        [hiccup.page :only [include-css include-js html5]]
        [hiccup.element :only [javascript-tag]]))

(defn- include-edn [var-name init-cfg]
  (let [vname (clojure.lang.Compiler/munge var-name)
        value (pr-str init-cfg)]
          (javascript-tag (str "var " vname " = '" value "';"))))

(def edn-storage-jsvar "MonteInitCfg")

(defn gen-html [view-id title init-cfg]
  (html5 
    [:head 
      [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
      [:meta {:name "description" :content "Monte: visualization tool"}]
      [:meta {:name "author"      :content "Andrew Sernyak"}]      
      
      [:title title]
      
      (include-css "/css/jquery-ui.css")
      (include-css "/css/monte.css")
      
      (include-js "/extern/jquery-1.7.2.min.js")
      (include-js "/extern/jquery-ui-1.9.1.js")
      
      (include-js "/extern/raphael-min.js")
      
      (include-js "/extern/dracula_graph.js")
      (include-js "/extern/dracula_graffle.js")
      (include-js "/extern/dracula_algorithms.js")      

      (include-edn edn-storage-jsvar {:view view-id 
                                      :cfg init-cfg})
      
      (include-js "/js/js.js")
      ;[:link {:rel "shortcut icon" :href "/i/favicon.ico" :type "image/x-icon"}] ; favicon
    ]
    [:body 
      [:div {:id "logo"}]
      [:div {:id "viewport"}]
      [:div {:id "debug"}]
      ]))


(defn layout [on-ready & content]
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
    
      (include-js "/js/js.js")]
    [:body [:div {:id "viewport"} content ]]))