(ns monte.logger)

(def debug false)

(def tab-num (atom 0))
(def tab-prev (atom 0))

(defn dbg[& args]
  (when debug
    (let [tabs @tab-num]
      (reset! tab-num (+ 1 tabs))
      (println (str (if-not (= tabs @tab-prev) "\n") 
                  (apply str (repeat tabs "\t"))
                  "DBG: "(pr-str args)))
      (reset! tab-prev tabs)
      (reset! tab-num (- @tab-num 1 )))))

(defmulti err class)

(defmethod err java.lang.Throwable [e]
  (do
    (print "ERR: ")
    (.printStackTrace e)))

(defmethod err :default [e]
  (do
    (println (str "ERR: " (pr-str e)))))