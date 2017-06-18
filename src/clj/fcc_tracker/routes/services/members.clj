(ns fcc-tracker.routes.services.members
  (:require [fcc-tracker.db.core :as db]
            [fcc-tracker.routes.services.utils :as utils]
            [fcc-tracker.validation :as v]
            [ring.util.http-response :as response :refer :all]))

(defn create-member! [org member]
  (if-let [error-message (v/member-creation-errors member)]
    (response/precondition-failed {:result :error
                                   :message error-message})
    (try
      (db/create-member!
       (assoc member :organization org))
      (ok {:result :ok})
      (catch Exception e
        (utils/handle-duplicate-error
         e
         "member with the selected FreeCodeCamp username already exists")))))
