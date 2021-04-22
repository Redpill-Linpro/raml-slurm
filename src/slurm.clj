(ns slurm
  (:require [clojure.java.io :as io]
            [clojure.string :refer [split-lines]]
            [jsonista.core :as j])
  (:gen-class))

(set! *warn-on-reflection* true)

(defn to-url [s]
  (try
    (io/as-url s)
    (catch java.net.MalformedURLException e nil)))

(defn to-obj [^amf.client.validate.ValidationResult result]
  {:level ^String (.level result)
                  :location (some-> (.location result) (. get ) str)
                  :message ^String (.message result)
                  :position (str (.position result))
;                  :source (.source %1) ; uncomment this if you want a full Scala stack trace
                  :targetnode ^String (.targetNode result)
                  :targetproperty ^String (.targetProperty result)
                  :validationid ^String (.validationId result)
                  })
(defn parse-results [results]
  (doall (map to-obj (seq results))))

(defn validate-raml [s]
  (-> (str s)
      (webapi.Raml10/parse)
      (. get)
      (webapi.Raml10/validate)
      (. get)
      (#(do {
             :url (str s)
             :model (str (.profile ^amf.client.validate.ValidationReport %1))
             :valid (.conforms ^amf.client.validate.ValidationReport %1)
             :results (parse-results (.results ^amf.client.validate.ValidationReport %1))
             }))))

(defn validate-file [f]
  (doall (try
    (as-> (clojure.core/slurp f) v
      (split-lines v)
      (map to-url v)
      (filter some? v)
      (map validate-raml v)
      (j/write-value-as-string v))
    (catch java.io.FileNotFoundException e
      (format "File %s not found. Check spelling." f)))))

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
