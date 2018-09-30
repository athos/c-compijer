(ns c-compijer.parser
  (:require [clojure.string :as str]))

(defn tokenize [s]
  (letfn [(step [s]
            (lazy-seq
             (when-let [s (some-> s str/triml not-empty)]
               (condp re-matches s
                 #"([+-])(.*)"
                 :>> (fn [[_ op more]]
                       (cons {:type :op :value (first op)}
                             (step more)))
                 #"(\d+)(.*)"
                 :>> (fn [[_ n more]]
                       (cons {:type :number :value (Long/parseLong n)}
                             (step more)))
                 [{:type :error :value s}]))))]
    (step s)))

(defn number [[token & tokens]]
  (when-not (= (:type token) :number)
    (throw (str "number expected, but got " (:value token))))
  {:node {:type :number :value (:value token)}
   :next tokens})

(defn expr [tokens]
  (let [res (number tokens)]
    (loop [[op & tokens] (:next res) ret (:node res)]
      (cond (nil? op) {:node ret :next nil}
            (not= (:type op) :op) (throw (str "stray token: " (:value op)))
            :else
            (case (:value op)
              \+ (let [res (number tokens)]
                   (recur (:next res)
                          {:type :add :lhs ret :rhs (:node res)}))
              \- (let [res (number tokens)]
                   (recur (:next res)
                          {:type :sub :lhs ret :rhs (:node res)})))))))

(defn parse [s]
  (let [tokens (tokenize s)]
    (:node (expr tokens))))
