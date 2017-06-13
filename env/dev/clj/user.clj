(ns user
  (:require [mount.core :as mount]
            [fcc-tracker.figwheel :refer [start-fw stop-fw cljs]]
            fcc-tracker.core))

(defn start []
  (mount/start-without #'fcc-tracker.core/repl-server))

(defn stop []
  (mount/stop-except #'fcc-tracker.core/repl-server))

(defn restart []
  (stop)
  (start))


