(ns fcc-tracker.components.new-member
  (:require [ajax.core :as ajax]
            [fcc-tracker.components.common :as c]
            [fcc-tracker.validation :as v]
            [hickory.core :as hickory]
            [hickory.select :as s]
            [reagent.core :as r]
            [reagent.session :as session]))

(defn- parse-progress-response [res]
  (->> res
       (re-find #"<h1 .+?>\[ (.+) \].+?h1>")
       last))

(defn- update-member [username key value]
  (let [imembers (map-indexed vector (session/get :members-list))
        i (->> imembers
               (filter #(= username (:fcc_username (second %))))
               first
               first)]
    (session/update-in! [:members-list i key] (fn [_] value))))

(defn- parse-profile-img [res]
  (let [body (-> res hickory/parse hickory/as-hickory)]
    (-> (s/select (s/class "public-profile-img") body)
        first
        :attrs
        :src)))

(defn- update-data [username res]
  (if-let [progress (parse-progress-response res)]
    (update-member username :progress progress)
    (update-member username :progress "Not Found"))
  (when-let [img (parse-profile-img res)]
    (update-member username :profile-img img)))

(defn- get-data [member]
  (let [username (:fcc_username member)]
    (ajax/GET (str "https://www.freecodecamp.com/" username)
      {:handler (partial update-data username)
       :error-handler println})))

(defn member-data [member]
  (get-data member)
  (assoc member :progress "Loading..."))

(defn- add-member [member list]
  (->> member
       member-data
       (conj list)))

(defn- handler [fields res]
  (session/update! :members-list (partial add-member @fields))
  (reset! fields {})
  (session/remove! :modal))

(defn new-member! [fields errors]
  (reset! errors (v/new-member-errors @fields))
  (when-not @errors
    (ajax/POST "/members"
      {:params @fields
       :handler (partial handler fields)
       :error-handler #(reset!
                        errors
                        {:server-error (get-in % [:response :message])})})))

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
        [c/text-input "name" :name "enter member's name" fields]
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
         {:on-click #(new-member! fields error)}
         "Create"]
        [:button.btn.btn-danger
         {:on-click #(session/remove! :modal)}
         "Cancel"]]])))

(defn new-member-button []
  [:button.btn.btn-primary.float-xs-right
   {:on-click #(session/put! :modal new-member-form)}
   "add member"])

