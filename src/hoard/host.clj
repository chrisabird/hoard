(ns hoard.host
    (:gen-class)
    (:use 
      ring.adapter.jetty 
      hoard.core 
      [clojure.tools.cli :only [cli]]))
(defn -main [& args]
  (println "Hoard 1.0")
  (let [[options args banner] (cli args
    ["-p" "--port" "Listen on this port" :parse-fn #(Integer. %) :default 3000])]
    (if (:port options)
        (run-jetty app {:port (:port options)})
      (println banner))))
