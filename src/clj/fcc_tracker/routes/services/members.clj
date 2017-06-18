(ns fcc-tracker.routes.services.members
  (:require [fcc-tracker.db.core :as db]
            [fcc-tracker.routes.services.utils :as utils]
            [ring.util.http-response :refer :all]))

(defn create-member! [org {:keys [fcc_username, name]}]
  (try
    (db/create-member! {:organization org
                        :fcc_username fcc_username
                        :name name})
    (ok {:result :ok})
    (catch Exception e
      (utils/handle-duplicate-error
       e
       "member with the selected FreeCodeCamp username already exists"))))
