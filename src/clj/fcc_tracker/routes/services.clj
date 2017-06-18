(ns fcc-tracker.routes.services
  (:require [fcc-tracker.routes.services.auth :as auth]
            [fcc-tracker.routes.services.members :as members]
            [ring.util.http-response :refer :all]
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

(s/defschema MemberCreation
  {:fcc_username String
   :name String})

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
    (auth/register! req user))
  (POST "/login" req
    :header-params [authorization :- String]
    :summary "log in the user and create a session"
    :return Result
    (auth/login! req authorization))
  (POST "/logout" []
    :summary "remove user session"
    :return Result
    (auth/logout!))
  (middleware [[m/wrap-auth]]
              (POST "/members" req
                :body [member MemberCreation]
                :return Result
                :summary "creates a new member for the current organization"
                (members/create-member! (:identity req) member))))
