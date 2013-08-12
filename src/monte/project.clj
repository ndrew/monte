(ns monte.project
  "monte project handling routines"
  (:require [me.raynes.fs.core :as fs]))


(def ^:dynamic *home* (str (System/getProperty "user.home") "/.monte"))

(def ^:dynamic *project-dir* (str *home* "/projects"))

(def def-project-cfg {})


(defn- escape-project-name[s]
  (clojure.string/replace s #"\/|\\|\." "_"))


(defn- prepare-dirs[] 
  (when-not (fs/exists? *home*)
    (fs/mkdirs *home*))
  
  (when-not (fs/exists? *project-dir*)
    (fs/mkdirs *project-dir*)))


(defn- child-directories [path]
  (fs/list-dir path))


(defn get-project[name]
  (let [escaped-name (escape-project-name name)
        project-dir (str *project-dir* "/" escaped-name)
        proj-cfg (str project-dir "/project.edn")
        proj-temp-dir (str project-dir "/temp")]
    (when-not (fs/exists? project-dir)
      (fs/mkdirs project-dir)
      (fs/mkdirs proj-temp-dir)
      (spit proj-cfg
            (merge def-project-cfg
                   {:name escaped-name})))
    (assoc 
      (read-string (slurp proj-cfg))
      :mod-time (fs/mod-time proj-cfg)
      :hash 12345
      )))


(defn list-projects[] 
  (prepare-dirs)  
  (map get-project (child-directories *project-dir*)))



