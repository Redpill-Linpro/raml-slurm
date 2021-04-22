(ns slurm
  (:require [clojure.java.io :as io]
            [clojure.string :refer [split-lines]]
            [jsonista.core :as j])
  (:gen-class))

(defn to-url [s]
  (try
    (io/as-url s)
    (catch java.net.MalformedURLException e nil)))

(defn parse-results [results]
  (let [fun #(do {:level (. %1 level)
                  :location (. %1 location)
                  :message (. %1 message)
                  :position (. %1 position)
;                  :source (. %1 source) ; uncomment this if you want a full Scala stack trace
                  :targetnode (. %1  targetNode)
                  :targetproperty (. %1 targetProperty)
                  :validationid (. %1 validationId)
                  })]
    (map fun (seq results))))

(defn validate-raml [s]
  (-> (str s)
      (webapi.Raml10/parse)
      (. get)
      (webapi.Raml10/validate)
      (. get)
      (#(do {
             :url s
             :model (str (. %1 profile))
             :valid (. %1 conforms)
             :results (parse-results (. %1 results))
             }))))

(defn validate-file [f]
  (try
    (as-> (clojure.core/slurp f) v
    (split-lines v)
    (map to-url v)
    (filter some? v)
    (doall (map validate-raml v))
    (j/write-value-as-string v))
    (catch java.io.FileNotFoundException e
      (format "File %s not found. Check spelling." f))))
    
(def usage-str "usage: slurm infile outfile\n
  slurm will attempt to read infile and validate each file/URL per line.\n
  if an outfile is supplied it will try to write the results there.")  
(defn -main [& args]
  (if-let [s (seq args)]
    (case (count s)
      1 (prn (validate-file (first s)))
      2 (spit (second s) (validate-file (first s)))
      (prn usage-str))
    (prn usage-str)))
