(ns fcc-tracker.components.members
  (:require [ajax.core :as ajax]
            [reagent.core :as r]
            [fcc-tracker.components.new-member :as nm]
            [reagent.session :as session]))

(defn- partition-members [members]
  (when (not-empty members)
    (vec (partition-all 20 members))))

(defn- back [i]
  (if (pos? i) (dec i) i))

(defn- nav-link [page i]
  [:li.page-item>a.page-link.btn.btn-primary
   {:on-click #(reset! page i)
    :class (when (= i @page) "active")}
   [:span i]])

(defn- forward [i pages]
  (if (< i (dec pages)) (inc i) i))

(defn pager [pages page]
  (when (> pages 1)
    (into
     [:div.text-xs-center>ul.pagination.pagination-lg]
     (concat
      [[:li.page-item>a.page-link.btn.btn-primary
        {:on-click #(swap! page back pages)
         :class (when (= @page 0) "disabled")}
        [:span "<<"]]]
      (map (partial nav-link page) (range pages))
      [[:li.page-item>a.page-link.btn.btn-primary
        {:on-click #(swap! page forward pages)
         :class (when (= @page (dec pages)) "disabled")}
        [:span ">>"]]]))))

(defn- members-table [members]
  [:table.table.table-striped
   [:thead
    [:tr
     [:th "Name"]
     [:th "Progress"]]]
   [:tbody
    (for [member members]
      ^{:key (member :fcc_username)}
      [:tr
       [:td (member :name)]
       [:th (member :progress)]])]])

(defn members-page []
  (let [page (r/atom 0)]
    (fn []
      [:div.container
       (when-let [members (partition-members (session/get :members-list))]
         [:div.row>div.col-md-12
          [pager (count members) page]
          [members-table (members @page)]])
       [:div.row>div.col-md-12
        [nm/new-member-button]]])))

(defn- parse-progress-response [res]
  (->> res
       (re-find #"<h1 .+?>\[ (.+) \].+?h1>")
       last))

(defn- update-user-progress [username progress]
  (let [imembers (map-indexed vector (session/get :members-list))
        i (->> imembers
               (filter #(= username (:fcc_username (second %))))
               first
               first)]
    (session/update-in! [:members-list i :progress](fn [_] progress))))

(defn- update-progress [username res]
  (if-let [progress (parse-progress-response res)]
    (update-user-progress username progress)
    (update-user-progress username "Not Found")))

(defn- get-progress [member]
  (let [username (:fcc_username member)]
    (ajax/GET (str "https://www.freecodecamp.com/" username)
      {:handler (partial update-progress username)
       :error-handler println})))

(defn- init-members-list [res]
  (doseq [member res] (get-progress member))
  (let [l (vec (map #(assoc % :progress "Loading...") res))]
    (session/put! :members-list l)))

(defn fetch-member-list! []
  (ajax/GET "/members"
    {:handler init-members-list}))

