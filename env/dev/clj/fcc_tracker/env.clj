(ns fcc-tracker.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [fcc-tracker.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[fcc_tracker started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[fcc_tracker has shut down successfully]=-"))
   :middleware wrap-dev})
