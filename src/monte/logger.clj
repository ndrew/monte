(ns monte.logger)

(def debug false)

(defn dbg[& args]
  (when debug
    (println (str "DBG: "(pr-str args)))))

(defmulti err class)

(defmethod err java.lang.Throwable [e]
  (do
    (print "ERR: ")
    (.printStackTrace e)))

(defmethod err :default [e]
  (do
    (println (str "ERR: " (pr-str e)))))