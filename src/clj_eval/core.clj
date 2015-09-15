(ns clj-eval.core)

(println "Cleanliness is next to Godliness")

(defn my-eval [env exp]
    (let [eval-list (fn [[sym & args :as exp]]
            (let [ vargs (vec args) _ (println "EVAL-LIST:" exp)]
                (condp = sym
                    'if (if (vargs 0) (vargs 1) (vargs 2))

                    'do (do (doall args) 
                            (last args))

                    'let (let [ [bindings & body] args _ (println "LET-binds-bod:" bindings body) ]
                            (my-eval (apply assoc {} bindings) (apply list 'do body)))

                    (if (fn? sym) (apply sym args)
                         exp))))
           ;;process only binding right-values for a let-form,
           ;;and regular my-eval otherwise
           special-eval (fn [env exp]
                          (if (coll? exp)
                            (let [ [op bindings & body] exp 
                                   _ (println "SPECIAL-op-binds-body" op bindings body)]
                              (if (= 'let op)
                                (let [res
                                 (->> (partition 2 bindings) 
                                      (map (fn [[bk bexp]]
                                             [bk (my-eval env bexp)]))
                                      (apply concat) 
                                      vec 
                                      list
                                      (#(concat % body))
                                      (cons 'let )) 
                                      _(println "SPECIAL-res:" res)] res)
                                (let [res2
                                 (my-eval env exp)
                                      _ (println "SPECIAL-res, inner else:" res2)] res2)
                                                   )))
                            (my-eval env exp)) ]
  (cond 
     (symbol? exp) (get env exp exp)
     (seq? exp) (do (println "SEQ **** env exp" env exp) (eval-list (map #(special-eval env %) exp)))
     (coll? exp) (do (println "COLL **** env exp" env exp) (into (empty exp) (map #(my-eval env %) exp)))
     :else exp)))

;; (my-eval {'x 1 'y 2} '(* x (+ (let [y 5] y) 3)))
