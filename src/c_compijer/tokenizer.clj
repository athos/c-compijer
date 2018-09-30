(ns c-compijer.tokenizer
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
