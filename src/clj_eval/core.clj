(ns clj-eval.core
  (:require [clj-eval.util :refer [log] :as u]))

(defn my-eval [env exp]
  (do (log "***MY-EVAL_env,exp->" env exp)
    (letfn [(eval-list [env [op & args :as exp]]
                (if (= 'let op)
                    (eval-let env exp)
                    (let [ vargs (->> (map #(my-eval env %) args) vec) 
                        _ (log "EVAL-LIST-exp-op-vargs:" exp op vargs)]
                        (condp = op
                            'if (if (vargs 0) (vargs 1) (vargs 2))

                            'do (do (doall vargs) 
                                    (last vargs))

                            (let [op (my-eval env op)]
                                (if (fn? op) (apply op vargs)
                                    (cons op vargs)))))))

            (eval-let [env [_ bindings & body]]
              (do (log "EVAL-LET-binds-body:" bindings body)
              (->> (apply list 'do body)
                   (my-eval (letbind>env bindings env)))))
              
            (letbind>env [letbind env]
                    (let [ _ (log "LETRESOLVE-bindings,env" letbind env)
                           res
                            (->>(partition 2 letbind) 
                                (reduce (fn [env [k v]] 
                                           (assoc env k (my-eval env v))) env))
                                _ (log "LETRESOLVE-res:" res)] res)) ]
  (cond 
     (symbol? exp) (get env exp exp)
     (seq? exp) (do (log "SEQ **** env exp" env exp) (eval-list env exp))
     (map? exp) (do (log "MAP **** env exp" env exp) 
                    (into (empty exp) (map 
                                       (fn [[k v]] 
                                         (do (log "mapping [k v] -> " k v)
                                         [(my-eval env k) 
                                          (my-eval env v)])) exp)))
     (coll? exp) (do (log "COLL **** env exp" env exp) (into (empty exp) (map #(my-eval env %) exp)))
     :else exp))))

;; (my-eval {'x 1 'y 2} '(* x (+ (let [y 5] y) 3)))

