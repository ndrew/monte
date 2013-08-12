(ns monte.data.diff
  "Monte diff data operations")

(defprotocol Diffable
  "a generic diff"
  (to-human-format [x])
  (is? [x pred])
)

(deftype SimpleDiff [a b t]
  Diffable
    (to-human-format [x] 
        (cond
          (= :time t)
            (let [time-diff (- a b)]
                (str (secs a) " vs " (secs b) "\t" (secs time-diff)  "s. (" (format "%.1f" (/ a b)) " times " (time-fmt time-diff) ")"))
          (= :number t)
            (let [num-diff (- a b)]
                (str a " vs " b "\t\t\t" num-diff 
                     (if-not (= 0 b)
                       (str " (" (format "%.1f" (- 100 (* (double (/ a b)) 100))) "% " (num-fmt num-diff) ")")
                       "")))
          (= :tests t) (do
                         
                         (let [failed-a (filter #(= "FAILED" (:status %)) a)
                               failed-b (filter #(= "FAILED" (:status %)) b)]
                         
                                (if (> (+ (count failed-b) (count failed-a)) 0)
                                  (str (count failed-a) " failing tests vs " (count failed-b) " failing test")
                                  "XXXXX"
                                  )               
                            )
                         )
          :else ""
          ))
    
    (is? [x pred] true))