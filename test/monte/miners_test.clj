(ns monte.miners-test
  (:use clojure.test
        monte.logger
       	monte.miners.core))


(defminer MinerForTest [:dummy-meta]          (f [this] :f))

(defminer-map MinerForTestMap [:dummy-meta]   {:f (fn [this] :f)})


(deftest miner-id
  "tests anything to miner-id convertion"
  (is (= :monte.miners.impl.MinerForTest (m-id :MinerForTest)))
  (is (= :monte.miners.impl.MinerForTest (m-id :monte.miners.impl.MinerForTest)))
  
  (is (= :monte.miners.impl.MinerForTest (m-id "MinerForTest")))
  (is (= :monte.miners.impl.MinerForTest (m-id "monte.miners.impl.MinerForTest")))
  
  (is (= :monte.miners.impl.MinerForTest (m-id 'monte.miners.impl.MinerForTest)))
  (is (= :monte.miners.impl.MinerForTest (m-id 'MinerForTest)))
  
  (is (= :monte.miners.impl.MinerForTest (m-id monte.miners.impl.MinerForTest))))


(deftest miner-defining
  (testing "if defminer macro generates miner class in monte.miners.impl correctly"
 	  (is (not (nil? monte.miners.impl.MinerForTest)))
 	  ; test direct class construction. 
    (let [miner (monte.miners.impl.MinerForTest. {:data-passed :in-constructor} [:dummy-meta])]
      ; test miner doing it's job
      (is (= :f (f miner)) "test miner should return excpected harcoded value")
      ; test if miner has proper config
      (is (= {:data-passed :in-constructor} (.data miner)))
      ; test if miner was initialized with proper schema 
      (is (= [:dummy-meta] (.meta miner)))))
  
  (testing "if defminer macro generates miner class via map in monte.miners.impl correctly"
    (is (not (nil? monte.miners.impl.MinerForTestMap)))
    ; test direct class construction. 
    (let [miner (monte.miners.impl.MinerForTestMap. {:data-passed :in-constructor} [:dummy-meta])]
      ; test miner doing it's job
      (is (= :f (f miner)) "test miner should return excpected harcoded value")
      ; test if miner has proper config
      (is (= {:data-passed :in-constructor} (.data miner)))
      ; test if miner was initialized with proper schema 
      (is (= [:dummy-meta] (.meta miner))))))

 
(deftest miner-meta
  "tests if meta-data is retrieved correctly"
  (is (= [:dummy-meta] (m-meta "MinerForTest")))
  (is (= [:dummy-meta] (m-meta "MinerForTestMap"))))


(deftest miner-listing
  (let [ids (map #(let [[id constructor] % ]
              (is (keyword? id))
              (is (var? constructor))
            
              id)
            (list-types-implementing Miner))]

    (is (some (zipmap 
                [(m-id :MinerForTest)
                (m-id :MinerForTestMap)] (repeat true)) ids))))


(load-extern-miners "test/monte/extern-miners.clj")

  
(deftest external-miners
  (is (some (zipmap [(m-id "ExternMiner")
                     (m-id :MinerForTest)
                     (m-id :MinerForTestMap)] (repeat true)) (m-list-ids))))

 
(deftest miner-creating
  (let [c (m-constr "ExternMiner")]
    (is (var? c))
    (is (fn? @c))
    
    (let [arity (count (first (:arglists (meta c))))]
      (is (= arity 2))
      ; direct creation
      (let [miner (c {:data-passed :in-constructor} [:dummy-meta])]
        (is (= :f (f miner)))
        (is (= [:dummy-meta] (m-meta miner))))))
  
  (is (var? (m-constr "MinerForTest")))
  (is (var? (m-constr "MinerForTestMap")))

  (binding [*miner-init-data* [:init-data]
            *miners-impls* (atom {})]
    (let [miner (m-impl "MinerForTest")]
      (is (= miner ((m-id "MinerForTest") @*miners-impls*)))
      (is (= [:init-data] (.data miner)))
      (is (= [:dummy-meta] (.meta miner)))
      (is (= (m-meta "MinerForTest") (.meta miner))))))

