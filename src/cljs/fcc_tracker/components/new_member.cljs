(ns fcc-tracker.components.new-member
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [fcc-tracker.components.common :as c]
            [fcc-tracker.validation :as v]))

(defn new-member-form []
  (let [fields (r/atom {})
        error (r/atom nil)]
    (fn []
      [c/modal
       [:div "Add a member to your org"]
       [:div
        [:div.well.well-sm
         [:strong "*required field"]]
        (when-let [error (first (:organization @error))]
          [:div.alert.alert-danger error])
        [c/text-input "name" :id "enter member's name" fields]
        (when-let [error (first (:name @error))]
          [:div.alert.alert-danger error])
        [c/text-input "freeCodeCamp username" :fcc_username
         "enter member's freeCodeCamp username" fields]
        (when-let [error (first (:fcc_username @error))]
          [:div.alert.alert-danger error])
        (when-let [error (:server-error @error)]
          [:div.alert.alert-danger error])]
       [:div
        [:button.btn.btn-primary
         "Create"]
        [:button.btn.btn-danger
         {:on-click #(session/remove! :modal)}
         "Cancel"]]])))

(defn new-member-button []
  [:button.btn.btn-primary.float-xs-right
   {:on-click #(session/put! :modal new-member-form)}
   "add member"])

