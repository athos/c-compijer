(ns c-compijer.main
  (:require [c-compijer.tokenizer :as token]))

(defn- fail [{:keys [value]}]
  (binding [*out* *err*]
    (println (str "unexpected token: " value)))
  (System/exit 1))

(defn -main [& args]
  (when-not (seq args)
    (binding [*out* *err*]
      (println "Usage: c-compijer <code>"))
    (System/exit 1))
  (let [[token & tokens] (token/tokenize (nth args 0))]
    (println ".intel_syntax noprefix")
    (println ".global main")
    (println "main:")
    (when-not (= (:type token) :number)
      (fail token))
    (println (str "  mov rax, " (:value token)))
    (loop [[token & tokens] tokens]
      (when token
        (case (:type token)
          :op (let [[token' & tokens] tokens]
                (when-not (= (:type token') :number)
                  (fail token'))
                (case (:value token)
                  \+ (do (println (str "  add rax, " (:value token')))
                         (recur tokens))
                  \- (do (println (str "  sub rax, " (:value token')))
                         (recur tokens))))
          (fail token))))
    (println "  ret")))
