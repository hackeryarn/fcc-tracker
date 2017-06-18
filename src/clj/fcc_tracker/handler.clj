(ns fcc-tracker.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [fcc-tracker.layout :refer [error-page]]
            [fcc-tracker.routes.home :refer [home-routes]]
            [fcc-tracker.routes.services :refer [service-routes]]
            [compojure.route :as route]
            [fcc-tracker.env :refer [defaults]]
            [mount.core :as mount]
            [fcc-tracker.middleware :as middleware]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
   #'service-routes
   (-> #'home-routes
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-formats))
   (route/not-found
    (:body
     (error-page {:status 404
                  :title "page not found"})))))

(defn app [] (middleware/wrap-base #'app-routes))
