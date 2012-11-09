(ns monte.core
	"Monte core stuff"
	(:use [clojure.contrib.generic.functor]))

(defn repo "Constructs repository by directory"
	([dir] ; TODO: loading configuration
		(repo dir {})
		)
	([dir config] { 
		:config config
		:path dir
	})
)

; Super repository is a container for ALL code under analysis — like VCS' root).  

(defn super_repo[ & dirs]
	{ 
		:repos (fmap repo (apply vector dirs))
		; todo: add miners and other stuff
	}
)