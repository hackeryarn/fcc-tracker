(ns fcc-tracker.routes.services
  (:require [fcc-tracker.routes.services.auth :as auth]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema OrgRegistration
  {:id String
   :pass String
   :pass-confirm String})

(s/defschema Result
  {:result s/Keyword
   (s/optional-key :message) String})

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "FreeCodeCamp Tracker API"
                           :description "Public Services"}}}}

  (POST "/register" req
        :return Result
        :body [user OrgRegistration]
        :summary "register a new organization"
        (auth/register! req user)))
