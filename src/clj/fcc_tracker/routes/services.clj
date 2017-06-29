(ns fcc-tracker.routes.services
  (:require [fcc-tracker.routes.services.auth :as auth]
            [fcc-tracker.routes.services.members :as members]
            [ring.middleware.format-params :refer [wrap-restful-params]]
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
  {:fcc_username String
   :name String})

(defapi public-service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "FreeCodeCamp Tracker API"
                           :description "Public Services"}}}}
  (POST "/register" req
    :summary "register a new organization"
    :body [user OrgRegistration]
    :return Result
    (auth/register! req user))
  (POST "/login" req
    :summary "log in the user and create a session"
    :header-params [authorization :- String]
    :return Result
    (auth/login! req authorization))
  (POST "/logout" []
    :summary "remove user session"
    :return Result
    (auth/logout!)))

(defapi private-service-routes
  {:swagger {:ui "/swagger-ui-private"
             :spec "/swagger-private.json"
             :data {:info {:version "1.0.0"
                           :title "FreeCodeCamp Tracker API"
                           :description "Private Services"}}}}
  (context "/members" []
    (resource
     {:tags ["members"]
      :post {:summary "creates a new member for the current organization"
             :parameters {:body-params Member}
             :responses {http-status/ok {:schema Result}}
             :handler (fn [{member :body-params org :identity}]
                        (members/create-member! org member))}
      :get {:summary "returns a list of all users for the current organization"
            :responses {http-status/ok {:schema [Member]}}
            :handler (fn [{org :identity}]
                       (members/list-members org))}}))
  (context "/members/:username" []
    :path-params [username :- String]
    (resource
     {:tags ["members"]
      :delete {:summary "delete the given member from the current org"
               :responses {http-status/ok {:schema Result}}
               :handler (fn [{org :identity}]
                          (members/delete-member! org username))}})))

(def service-routes
  (middleware [[wrap-restful-params
                :formats [:json-kw :edn :json :transit-json]]]
              public-service-routes
              (middleware [[m/wrap-auth]]
                          private-service-routes)))
