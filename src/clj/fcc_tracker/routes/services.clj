(ns fcc-tracker.routes.services
  (:require [fcc-tracker.routes.services.auth :as auth]
            [fcc-tracker.routes.services.members :as members]
            [ring.util.http-response :refer :all]
            [ring.util.http-status :as http-status]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [fcc-tracker.middleware :as m]))

(s/defschema Result
  {:result s/Keyword
   (s/optional-key :message) String})

(s/defschema OrgRegistration
  {:id String
   :pass String
   :pass-confirm String})

(s/defschema Member
  {:organization String
   :fcc_username String
   :name String})

(s/defschema NewMember
  (dissoc Member :organization))

(def auth-routes
  (context "/" []
    (POST "/register" req
      :return Result
      :body [user OrgRegistration]
      :summary "register a new organization"
      (auth/register! req user))
    (POST "/login" req
      :header-params [authorization :- String]
      :summary "log in the user and create a session"
      :return Result
      (auth/login! req authorization))
    (POST "/logout" []
      :summary "remove user session"
      :return Result
      (auth/logout!))))

(def members-routes
  (context "/members" []
    :middleware [[m/wrap-auth]]
    (resource
     {:post {:parameters {:body-params NewMember}
             :responses {http-status/ok {:schema Result}}
             :summary "creates a new member for the current organization"
             :handler (fn [{member :body-params org :identity}]
                        (members/create-member! org member))}})))

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "FreeCodeCamp Tracker API"
                           :description "Services"}}}}
  auth-routes
  members-routes)

