(ns clojars.stats
  (:require [clojars.config :as config]
            [clojure.java.io :as io]
            [clojure.core.memoize :as memo]))

(defn all* []
  (let [path (str (config/config :stats-dir) "/all.edn")]
    (if (.exists (io/as-file path))
      (read (java.io.PushbackReader. (java.io.FileReader.
                                      (str (config/config :stats-dir)
                                           "/all.edn"))))
      {})))

(def all (memo/ttl all* :ttl/threshold (* 60 60 1000))) ;; 1 hour

(defn download-count [dls group-id artifact-id & [version]]
  (let [ds (dls [group-id artifact-id])]
    (or (if version
          (get ds version)
          (->> ds
               (map second)
               (apply +)))
        0)))

(defn total-downloads [dls]
  (apply +
         (for [[[g a] vs] dls
               [v c] vs]
           c)))
