(ns c-compijer.main
  (:require [c-compijer.parser :as parser]))

(defn- fail [{:keys [value]}]
  (binding [*out* *err*]
    (println (str "unexpected token: " value)))
  (System/exit 1))

(def regs
  (ref ["rdi" "rsi" "r10" "r11" "r12" "r13" "r14" "r15"]))

(defn fresh-reg []
  (dosync
   (let [[reg] @regs]
     (alter regs next)
     reg)))

(defn gen [node]
  (if (= (:type node) :number)
    (if-let [reg (fresh-reg)]
      (do (println (str "  mov " reg ", " (:value node)))
          reg)
      (throw (ex-info "register exhausted" {})))
    (let [dst (gen (:lhs node))
          src (gen (:rhs node))]
      (case (:type node)
        :add (do (println (str "  add " dst ", " src))
                 dst)
        :sub (do (println (str "  sub " dst ", " src))
                 dst)
        (throw (ex-info "unknown operator" {}))))))

(defn -main [& args]
  (when-not (seq args)
    (binding [*out* *err*]
      (println "Usage: c-compijer <code>"))
    (System/exit 1))
  (let [node (parser/parse (nth args 0))]
    (println ".intel_syntax noprefix")
    (println ".global main")
    (println "main:")
    (println (str "  mov rax, " (gen node)))
    (println "  ret")))
