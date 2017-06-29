(ns fcc-tracker.components.members
  (:require [ajax.core :as ajax]
            [reagent.core :as r]
            [fcc-tracker.components.new-member :as nm]
            [reagent.session :as session]))

(defn- partition-members [members]
  (when (not-empty members)
    (vec (partition-all 10 members))))

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

(defn- sort-members [k]
  (session/update!
   :members-list
   #(sort-by k (session/get :members-list))))

(defn- remove-member [username]
  (session/update!
   :members-list
   (fn [members]
     (remove #(= (:fcc_username %) username) members))))

(defn- delete-member [username]
  (ajax/DELETE (str "/members/" username)
    {:handler (remove-member username)}))

(defn- members-table [members]
  [:table.table.table-striped
   [:thead
    [:tr
     [:th [:a.heading {:on-click #(sort-members :name)}
           "Name " [:i.fa.fa-sort]]]
     [:th [:a.heading {:on-click #(sort-members :progress)}
           "Progress " [:i.fa.fa-sort]]]
     [:th ""]]]
   [:tbody
    (for [member members]
      (let [username (:fcc_username member)]
        ^{:key username}
        [:tr
         [:td [:a {:href (str "http://www.freecodecamp.com/" username)
                   :title username}
               (when-let [img (:profile-img member)]
                 [:img.profile {:src img}])
               (:name member)]]
         [:th (:progress member)]
         [:td [:a.delete {:on-click #(delete-member username)}
               [:i.fa.fa-times.text-danger]]]]))]])

(defn- init-members-list [res]
  (session/put! :members-list (mapv nm/member-data res)))

(defn fetch-member-list! []
  (ajax/GET "/members"
    {:handler init-members-list}))

(defn members-page []
  (let [page (r/atom 0)]
    (fn []
      [:div.container
       [:h2 "Organization Members"]
       (if-let [members (partition-members (session/get :members-list))]
         [:div.row>div.col-md-12
          [pager (count members) page]
          [members-table (members @page)]]
         [:div.row>div.col-md-12
          [:p "It doesn't look like you have any members yet. Click the button below
to add some."]])
       [:div.row>div.col-md-12
        [nm/new-member-button]]])))

