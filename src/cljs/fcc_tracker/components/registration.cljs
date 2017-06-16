(ns fcc-tracker.components.registration
  (:require [ajax.core :as ajax]
            [fcc-tracker.components.common :as c]
            [fcc-tracker.validation :as v]
            [reagent.core :as r]
            [reagent.session :as session]))

(defn register! [fields errors]
  (reset! errors (v/registration-errors @fields))
  (when-not @errors
    (ajax/POST "/register"
               {:params @fields
                :handler #(do
                            (session/put! :identity (:id @fields))
                            (reset! fields {})
                            (session/remove! :modal))
                :error-handler #(reset!
                                 errors
                                 {:server-error (get-in % [:response :message])})})))

(defn registration-form []
  (let [fields (r/atom {})
        error (r/atom nil)]
    (fn []
      [c/modal
       [:div "FreeCodeCamp Tracker Registration"]
       [:div
        [:div.well.well-sm
         [:strong "* required field"]]
        [c/text-input "name" :id "enter a user name" fields]
        (when-let [errors (first (:id @error))]
          [:div.alert.alert-danger error])
        [c/password-input "password" :pass "enter a password" fields]
        (when-let [errors (first (:pass @error))]
          [:div.alert.alert-danger error])
        [c/password-input "password" :pass-confirm "re-enter the password" fields]
        (when-let [errors (first (:pass-confirm @error))]
          [:div.alert.alert-danger error])
        (when-let [error (:server-error @error)]
          [:div.alert.alert-danger error])]
       [:div
        [:button.btn.btn-primary
         {:on-click #(register! fields error)}
         "Register"]
        [:button.btn.btn-danger
         {:on-click #(session/remove! :modal)}
         "Cancel"]]])))

(defn registration-button []
  [:a
   {:on-click #(session/put! :modal registration-form)}
   "register"])
