(ns monte.logger
  [:use 
   clojure.pprint
   clojure.stacktrace
   clansi.core])

(def ^:dynamic *debug* true) ; defalt true is set for debugging before launcher 
                             ;   binds a value from console arguments or defaults

(def ^:dynamic *color* false)

(def ^:dynamic *store-logs* true)

(def ^:dynamic output (atom []))

(add-watch #'monte.logger/*color* :watch-change 
  (fn [key _ _ new-val]
    (alter-var-root #'clansi.core/*use-ansi* (constantly new-val))
  ; (println (str "new change!: key= " key "; old=" old-val "; new=" new-val))
  ; todo: merge change to workspace
  ))
 
                             
(def tab-num (atom 0))
(def tab-prev (atom 0))

;; todo
(defn dbg[& args]
  (when *debug*
    ;(let [tabs @tab-num]
    ;  (swap! tab-num inc)
      (let [msg (str (System/currentTimeMillis) ":"
                     (.getName (Thread/currentThread))
                     " "
                     (pr-str args))]
      (if-not *store-logs*
        (println msg)  
        (swap! output conj msg))
      
      ;(reset! tab-prev tabs)
      ;(swap! tab-num dec))
      )))

(defn get-output[] 
  @output)


#_(reduce (fn[a s]
          (let [matcher (re-matcher #"(\d+):(.*)?\s\((.+)\)" s)
                [_ timestamp thread text] (re-find matcher)]
                (assoc a thread (assoc (get a thread (sorted-map)) timestamp text)))
          ) (sorted-map) (monte.logger/get-output))


(defmulti err class)

(defmethod err java.lang.Throwable [tr]
  (do
    (print "ERR: ")
    (print-throwable tr)
    (print-stack-trace tr)))

(defmethod err :default [e]
  (do
    (println (str "ERR: " (pr-str e)))))


(def logos [
"                          __             
                         /\\ \\__          
  ___ ___     ___     ___\\ \\ ,_\\    __   
/' __` __`\\  / __`\\ /' _ `\\ \\ \\/  /'__`\\ 
/\\ \\/\\ \\/\\ \\/\\ \\_\\ \\/\\ \\/\\ \\ \\ \\_/\\  __/ 
\\ \\_\\ \\_\\ \\_\\ \\____/\\ \\_\\ \\_\\ \\__\\ \\____\\
 \\/_/\\/_/\\/_/\\/___/  \\/_/\\/_/\\/__/\\/____/\n"
 
 "   __  _______  _  ____________
  /  |/  / __ \\/ |/ /_  __/ __/
 / /|_/ / /_/ /    / / / / _/  
/_/  /_/\\____/_/|_/ /_/ /___/  
                               "
" __  __             _       
|  \\/  |           | |      
| \\  / | ___  _ __ | |_ ___ 
| |\\/| |/ _ \\| '_ \\| __/ _ \\
| |  | | (_) | | | | ||  __/
|_|  |_|\\___/|_| |_|\\__\\___|
                            "     
                            
".::       .::                     .::            
.: .::   .:::                     .::            
.:: .:: . .::   .::    .:: .::  .:.: .:   .::    
.::  .::  .:: .::  .::  .::  .::  .::   .:   .:: 
.::   .:  .::.::    .:: .::  .::  .::  .::::: .:
.::       .:: .::  .::  .::  .::  .::  .:        
.::       .::   .::    .:::  .::   .::   .::::   
                                                 "
                                                 
"   __  ___          __     
  /  |/  /__  ___  / /____ 
 / /|_/ / _ \\/ _ \\/ __/ -_)
/_/  /_/\\___/_//_/\\__/\\__/ 
                           
"                                                                                     
])