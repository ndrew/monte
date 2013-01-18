(ns monte.datalog-test
 
  (:use [clojure.test]
        
        [clojure.core.logic :only (binding-map binding-map* prep defrel fact run* fresh) ]
        
        [datalog.datalog :only (build-work-plan run-work-plan)]
        [datalog.rules :only (<- ?- rules-set)]
        [datalog.database :only (make-database add-tuples)]
        [datalog.util :only (*trace-datalog*)]))


(defn query [rule xs]  
  (let [prule (prep rule)]
    (map #(binding-map* prule (prep %)) xs)))


(defn query-seq
  "Wraps each member of a collection in vector before calling query"
  [rule xs]
  (query rule (map vector xs)))


(defn query-f
  "Applies f to each result of a query"
  [rule f xs]
  (filter #(not (nil? %)) (map f (query rule xs))))
 
 
 
(defmacro defquery [relname find rels]
  (let [idx-syms (->> (iterate inc 0)
                      (map (partial + 97))
                      (map #(str (char %)))
                      (map symbol)
                      (map #(with-meta % {:index :t})))
        relname (fn [r] (symbol (str relname "-" (->> r (map name) (interpose "-") (reduce str)))))
        lvars (fn [r] (->> r (map name) (map symbol)))
        defrels (for [r rels] `(defrel ~(relname r) ~@(take (count r) idx-syms)))        
        joins (for [r rels] `(~(relname r) ~@(lvars r)))]
    `(do
       ~@defrels
       (defn ~(relname [:run]) []         
         (run* [q#]
               (fresh [~@(set (mapcat lvars rels))]
                      ~@joins
                      (== q# [~@(lvars find)]))))))) 
 
(deftest core-logic-tests
  
  (println (binding-map '(?answer) [42]))
  (println (query '(?last ?first) [["Doe" "John"]]))
  (println (query-seq '(?first) ["ole" "dole" "doff"]))
  (println
    (query-f '(?first ?last) #(get % '?first)
         [["John" "Doe"]
          ["Jane" "Doe"]]))
  
  (println(query-f '(?a :firstName ?first ?b) #(get % '?first)
         [[1 :firstName "John" 42]
          [1 :lastName "Doe" 42]]))
  
  ;; simple in-memory join, two relation bindings
  (defn join-test [xs ys]
  (let [rx (query '(?last ?first ?email) xs)
        ry (query '(?email ?height) ys)
        r (clojure.set/join rx ry)]
    (map (juxt '?first '?height) r)))
 
    (println(join-test
     [["Doe" "John" "jdoe@example.com"]
      ["Doe" "Jane" "jane@example.com"]]
     [["jane@example.com" 73]
      ["jdoe@example.com" 71]]))
    
    (println (->> [["Stock" 225]
      ["Spud" 80]
      ["Rocket" 400]
      ["Stock" 225]
      ["Clunker" 40]]     
     (query '(?car ?speed))
     set
     (filter #(> (get % '?speed) 100))
     (map (juxt '?car '?speed))))
    
    
    (println (->> 
     {:D 67.3 :A 99.5 :B 67.4 :C 67.5}
     (sort-by second))
     )
    
    ; why this fails?
    (comment (do
      (defquery join2 [:firstName :height] [[:e1 :firstName] [:e1 :email] [:e2 :email] [:e2 :height]])
    
        (fact join2-e1-firstName 1 "John")
        (fact join2-e1-email 1 "jdoe@example.com")
        (fact join2-e1-firstName 2 "Jane")
        (fact join2-e1-email 2 "jane@example.com")
        (fact join2-e2-email 100 "jane@example.com")
        (fact join2-e2-height 100 73)
        (fact join2-e2-email 101 "jdoe@example.com")
        (fact join2-e2-height 101 71)
      (join2-run)))
  )
 

(deftest miner-defining
  ; simple datalog stuff

  (def db-base (make-database
                 (relation :last-first-email [:last :first :email])
                 (relation :email-height [:email :height])))

  (def db
    (add-tuples db-base
              [:last-first-email :last "Doe" :first "John" :email "jdoe@example.com"]
              [:last-first-email :last "Doe" :first "Jane" :email "jane@example.com"]
              [:email-height :email "jane@example.com" :height 73]
              [:email-height :email "jdoe@example.com" :height 71]))


    (def rules (rules-set
              (<- (:first-height :first ?f :height ?h)
                  (:last-first-email :last ?l :first ?f :email ?e)
                  (:email-height :email ?e :height ?h))))
  
  (def wp (build-work-plan
           rules
           (?- :first-height :first ?f :height ?h)))
  
  (println (run-work-plan wp db {}))
  ;; ({:height 73, :first "Jane"} {:height 71, :first "John"})
)




(comment 

(def db-scheme 
  (make-database
    (relation :git [:simple :composite ])))

(def db
  (add-tuples db-scheme)
    [:git   :simple "simple" :composite {:test "FFFFFFUUUU"}])



;(def rules
;  (rules-set
;    (<- (:simple-query :git ??x) ; head
        ; body 
;        (:git :simple ??x)
;        )
;    ))


(?- :git :simple )
)

(comment 

(def db-base
  (make-database
   (relation :employee [:id :name :position])
   (index :employee :name)

   (relation :boss [:employee-id :boss-id])
   (index :boss :employee-id)

   (relation :can-do-job [:position :job])
   (index :can-do-job :position)

   (relation :job-replacement [:job :can-be-done-by])
   ;;(index :job-replacement :can-be-done-by)

   (relation :job-exceptions [:id :job])))

(def db
  (add-tuples db-base
              [:employee :id 1  :name "Bob"    :position :boss]
              [:employee :id 2  :name "Mary"   :position :chief-accountant]
              [:employee :id 3  :name "John"   :position :accountant]
              [:employee :id 4  :name "Sameer" :position :chief-programmer]
              [:employee :id 5  :name "Lilian" :position :programmer]
              [:employee :id 6  :name "Li"     :position :technician]
              [:employee :id 7  :name "Fred"   :position :sales]
              [:employee :id 8  :name "Brenda" :position :sales]
              [:employee :id 9  :name "Miki"   :position :project-management]
              [:employee :id 10 :name "Albert" :position :technician]
              
              [:boss :employee-id 2  :boss-id 1]
              [:boss :employee-id 3  :boss-id 2]
              [:boss :employee-id 4  :boss-id 1]
              [:boss :employee-id 5  :boss-id 4]
              [:boss :employee-id 6  :boss-id 4]
              [:boss :employee-id 7  :boss-id 1]
              [:boss :employee-id 8  :boss-id 7]
              [:boss :employee-id 9  :boss-id 1]
              [:boss :employee-id 10 :boss-id 6]
              
              [:can-do-job :position :boss               :job :management]
              [:can-do-job :position :accountant         :job :accounting]
              [:can-do-job :position :chief-accountant   :job :accounting]
              [:can-do-job :position :programmer         :job :programming]
              [:can-do-job :position :chief-programmer   :job :programming]           
              [:can-do-job :position :technician         :job :server-support]
              [:can-do-job :position :sales              :job :sales]
              [:can-do-job :position :project-management :job :project-management]

              [:job-replacement :job :pc-support :can-be-done-by :server-support]
              [:job-replacement :job :pc-support :can-be-done-by :programming]
              [:job-replacement :job :payroll    :can-be-done-by :accounting]

              [:job-exceptions :id 4 :job :pc-support]))

(def rules
  (rules-set
   (<- (:works-for :employee ?x :boss ?y) (:boss :employee-id ?e-id :boss-id ?b-id)
       (:employee :id ?e-id :name ?x)
       (:employee :id ?b-id :name ?y))
   (<- (:works-for :employee ?x :boss ?y) (:works-for :employee ?x :boss ?z)
       (:works-for :employee ?z :boss ?y))
   (<- (:employee-job* :employee ?x :job ?y) (:employee :name ?x :position ?pos)
       (:can-do-job :position ?pos :job ?y))
   (<- (:employee-job* :employee ?x :job ?y) (:job-replacement :job ?y :can-be-done-by ?z)
       (:employee-job* :employee ?x  :job ?z))
   (<- (:employee-job* :employee ?x :job ?y) (:can-do-job :job ?y)
       (:employee :name ?x :position ?z)
       (if = ?z :boss))
   (<- (:employee-job :employee ?x :job ?y) (:employee-job* :employee ?x :job ?y)
       (:employee :id ?id :name ?x)
       (not! :job-exceptions :id ?id :job ?y))
   (<- (:bj :name ?x :boss ?y) (:works-for :employee ?x :boss ?y)
       (not! :employee-job :employee ?y :job :pc-support))))

(def wp-1 (build-work-plan rules (?- :works-for :employee '??name :boss ?x)))
(run-work-plan wp-1 db {'??name "Albert"})
;;({:boss "Li", :employee "Albert"} {:boss "Sameer", :employee "Albert"} {:boss "Bob", :employee "Albert"})

(def wp-2 (build-work-plan rules (?- :employee-job :employee '??name :job ?x)))
;(binding [*trace-datalog* true]
  (run-work-plan wp-2 db {'??name "Li"})
  ;)
;; ({:job :server-support, :employee "Li"} {:job :pc-support, :employee "Li"})

(def wp-3 (build-work-plan rules (?- :bj :name '??name :boss ?x)))
(run-work-plan wp-3 db {'??name "Albert"})
;; ({:boss "Sameer", :name "Albert"})

(def wp-4 (build-work-plan rules (?- :works-for :employee ?x :boss ?y)))
(run-work-plan wp-4 db {})
;; ({:boss "Bob", :employee "Miki"} {:boss "Li", :employee "Albert"} {:boss "Sameer", :employee "Lilian"} {:boss "Bob", :employee "Li"} {:boss "Bob", :employee "Lilian"} {:boss "Fred", :employee "Brenda"} {:boss "Bob", :employee "Fred"} {:boss "Bob", :employee "John"} {:boss "Mary", :employee "John"} {:boss "Sameer", :employee "Albert"} {:boss "Bob", :employee "Sameer"} {:boss "Bob", :employee "Albert"} {:boss "Bob", :employee "Brenda"} {:boss "Bob", :employee "Mary"} {:boss "Sameer", :employee "Li"})

)
