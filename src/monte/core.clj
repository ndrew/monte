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

(defn super-repo[ & dirs]
	{ 
		:repos (fmap repo dirs)
		; todo: add miners and other stuff
	}
)


(defn miner[ super-repo & data]
	data
)

(defn file-miner[ super-repo ]
	;
)