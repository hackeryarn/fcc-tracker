(ns fcc-tracker.routes.services.auth
  (:require [buddy.hashers :as hashers]
            [fcc-tracker.db.core :as db]
            [fcc-tracker.routes.services.utils :as utils]
            [fcc-tracker.validation :refer [registration-errors]]
            [ring.util.http-response :as response]))

(defn register! [{:keys [session]} user]
  (if-let [error-message (registration-errors user)]
    (response/precondition-failed {:result :error
                                   :message error-message})
    (try
     (db/create-org!
      (-> user
          (dissoc :pass-confirm)
          (update :pass hashers/encrypt)))
     (-> {:result :ok}
         (response/ok)
         (assoc :session (assoc session :identity (:id user))))
     (catch Exception e
       (utils/handle-duplicate-error
        e
        "user with the selected ID already exists")))))

(defn- decode-auth [encoded]
  (let [auth (second (.split encoded " "))]
    (-> (.decode (java.util.Base64/getDecoder) auth)
        (String. (java.nio.charset.Charset/forName "UTF-8"))
        (.split ":"))))

(defn- authenticate [[id pass]]
  (when-let [org (db/get-org {:id id})]
    (when (hashers/check pass (:pass org))
      id)))

(defn login! [{:keys [session]} auth]
  (if-let [id (authenticate (decode-auth auth))]
    (-> {:result :ok}
        (response/ok)
        (assoc :session (assoc session :identity id)))
    (response/unauthorized {:result :unauthorized
                            :message "login failed"})))

(defn logout! []
  (-> {:result :ok}
      (response/ok)
      (assoc :session nil)))


