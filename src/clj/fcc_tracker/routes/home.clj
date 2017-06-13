(ns fcc-tracker.routes.home
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [fcc-tracker.layout :as layout]
            [ring.util.http-response :as response]))

(defn home-page []
  (layout/render "home.html"))

(defroutes home-routes
  (GET "/" []
       (home-page))
  (GET "/docs" []
       (-> (response/ok (-> "docs/docs.md" io/resource slurp))
       (response/header "Content-Type" "text/plain; charset=utf-8"))))
