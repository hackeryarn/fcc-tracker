(ns fcc-tracker.validation
  (:require [struct.core :as st]))

(def min-count
  (letfn [(validate [v c]
            {:pre [(number? c)]}
            (and (string? v)
                 (<= c (count v))))]
    {:message "not long enough"
     :optional true
     :validate validate}))

(defn registration-errors [{:keys [pass-confirm] :as params}]
  (first
   (st/validate
    params
    {:id [st/required]
     :pass [st/required
            [min-count 7 :message "password must contain at least 8 characters"]
            [st/identical-to :pass-confirm]]})))

(defn member-creation-errors [params]
  (first
   (st/validate
    params
    {:fcc_username [st/required]
     :name [st/required]})))

