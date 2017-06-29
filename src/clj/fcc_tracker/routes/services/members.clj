(ns fcc-tracker.routes.services.members
  (:require [fcc-tracker.db.core :as db]
            [fcc-tracker.routes.services.utils :as utils]
            [fcc-tracker.validation :as v]
            [ring.util.http-response :as response :refer :all]))

(defn create-member! [org member]
  (let [org-member (assoc member :organization org)]
    (if-let [error-message (v/member-creation-errors org-member)]
      (response/precondition-failed {:result :error
                                     :message error-message})
      (try
        (db/create-member!
         org-member)
        (ok {:result :ok})
        (catch Exception e
          (utils/handle-duplicate-error
           e
           "member with the selected FreeCodeCamp username already exists"))))))

(defn list-members [org]
  (ok (db/list-members {:organization org})))

(defn delete-member! [org username]
  (db/delete-member! {:organization org :fcc_username username})
  (ok {:result :ok}))
