(ns fcc-tracker.core
  (:require [ajax.core :refer [POST]]
            [fcc-tracker.ajax :refer [load-interceptors!]]
            [fcc-tracker.components.login :as l]
            [fcc-tracker.components.registration :as reg]
            [fcc-tracker.components.new-member :as nm]
            [fcc-tracker.components.members :as m]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li.nav-item
   {:class (when (= page (session/get :page)) "active")}
   [:a.nav-link
    {:href uri
     :on-click #(reset! collapsed? true)} title]])

(defn user-menu []
  (if-let [id (session/get :identity)]
    [:ul.nav.navbar-nav.float-xs-right
     [:li.nav-item.btn-header
      [:a.dropdown-item
       {:on-click #(POST
                     "/logout"
                     {:handler (fn [] (session/remove! :identity))})}
       [:i.fa.fa-user] " " id " | sign out"]]]
    [:ul.nav.float-xs-right.navbar-nav
     [:li.nav-item.btn-header [l/login-button]]
     [:li.nav-item.btn-header [reg/registration-button]]]))

(defn navbar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav.navbar.navbar-dark.bg-green
       [:button.navbar-toggler.hidden-sm-up
        {:on-click #(swap! collapsed? not)}]
       [:div.collapse.navbar-toggleable-xs.float-xs-left
        (when-not @collapsed? {:class "in"})
        [:a.navbar-brand {:href "#/"} "freeCodeCamp Tracker"]
        [:ul.nav.navbar-nav.float-xs-left
         [nav-link "#/" "Home" :home collapsed?]
         (when (session/get :identity)
           [nav-link "#/members-list" "Members" :members collapsed?])]]
       [user-menu]])))

(defn about-page []
  [:div "this is the story of picture-gallery... work in progress"])

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Welcome to freeCodeCamp Tracker"]]
   [:div.row
    [:div.col-md-12
     [:h2 "TODO: add description"]]]
   (when (session/get :identity)
     [:div.row
      [:div.col-md-12
       [nm/new-member-button]]])])

(def pages
  {:home  #'home-page
   :members #'m/members-page
   :about #'about-page})

(defn modal []
  (when-let [session-modal (session/get :modal)]
    [session-modal]))

(defn page []
  [:div
   [modal]
   [(pages (session/get :page))]])
;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/members-list" []
  (when (session/get :identity)
    (m/fetch-member-list!)
    (session/put! :page :members)))

(secretary/defroute "/about" []
  (session/put! :page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (hook-browser-navigation!)
  (session/put! :identity js/identity)
  (mount-components))
