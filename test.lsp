(def make-thing 
	(lambda (name) 
		(lambda (option) 
			(if (isEqual 'name' option) name 'unknown option') 
)))
(def thing (make-thing 'will'))

(thing 'name')