(ns fcc-tracker.test.handler
  (:require [buddy.hashers :as hashers]
            [cheshire.core :refer [parse-string]]
            [clojure.test :refer :all]
            [fcc-tracker.handler :refer :all]
            [ring.mock.request :refer :all]))

(defn encode-auth [org pass]
  (->> (str org ":" pass)
       (.getBytes)
       (.encodeToString (java.util.Base64/getEncoder))
       (str "Basic ")))

(defn login-request [id pass]
  (-> (request :post "/login")
      (header "Authorization" (encode-auth id pass))))

(defn mock-get-org [{:keys [id]}]
  (if (= id "foo")
    {:id "foo"
     :pass (hashers/encrypt "bar")}))

(defn parse-response [body]
  (-> body slurp (parse-string true)))


(deftest test-app
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response)))))

  (testing "login success"
    (with-redefs [fcc-tracker.db.core/get-org mock-get-org]
      (let [{:keys [body status]} ((app) (login-request "foo" "bar"))]
        (is
         (= 200 status))
        (is
         (= {:result "ok"}
            (parse-response body))))))

  (testing "password mismatch"
    (with-redefs [fcc-tracker.db.core/get-org mock-get-org]
      (let [{:keys [body status]} ((app) (login-request "foo" "xxx"))]
        (is
         (= 401 status))
        (is
         (= {:result "unauthorized" :message "login failed"}
            (parse-response body)))))))
