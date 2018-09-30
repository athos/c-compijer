(ns c-compijer.main)

(defn -main [& args]
  (when-not (seq args)
    (binding [*out* *err*]
      (println "Usage: c-compijer <code>"))
    (System/exit 1))
  (println ".intel_syntax noprefix")
  (println ".global main")
  (println "main:")
  (let [[_ n more] (re-matches #"(\d+)(.*)" (nth args 0))]
    (println (str "  mov rax, " n))
    (loop [more more]
      (when (seq more)
        (if-let [[_ op m more] (re-matches #"([+-])(\d+)(.*)" more)]
          (case (first op)
            \+ (do (println (str "  add rax, " m))
                   (recur more))
            \- (do (println (str "  sub rax, " m))
                   (recur more)))
          (binding [*out* *err*]
            (println (str "unexpected character: " (nth more 0))))))))
  (println "  ret"))
