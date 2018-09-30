(ns c-compijer.main)

(defn -main [& args]
  (when-not (seq args)
    (binding [*out* *err*]
      (println "Usage: c-compijer <code>"))
    (System/exit 1))
  (println ".intel_syntax noprefix")
  (println ".global main")
  (println "main:")
  (println (str "  mov rax, " (nth args 0)))
  (println "  ret"))
