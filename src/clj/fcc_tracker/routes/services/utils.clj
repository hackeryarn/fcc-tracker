(ns fcc-tracker.routes.services.utils
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response :refer :all]))

(defn handle-duplicate-error [e message]
  (if (and
       (instance? java.sql.SQLException e)
       (-> e
           (.getNextException)
           (.getMessage)
           (.startsWith "ERROR: duplicate key value")))
    (response/precondition-failed
     {:result :error
      :message message})
    (do
      (log/error e)
      (response/internal-server-error
       {:result :error
        :message "server error occusered"}))))
