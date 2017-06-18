(ns fcc-tracker.routes.services.members
  (:require [clojure.tools.logging :as log]
            [fcc-tracker.db.core :as db]
            [ring.util.http-response :refer :all]))

(defn create-member! [org {:keys [fcc_username, name]}]
  (try
    (db/create-member! {:organization org
                        :fcc_username fcc_username
                        :name name})
    (ok {:result :ok})
    (catch Exception e
      (log/error e)
      (internal-server-error "error"))))
