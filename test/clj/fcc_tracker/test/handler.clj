(ns fcc-tracker.test.handler
  (:require [buddy.hashers :as hashers]
            [cheshire.core :refer [parse-string generate-string]]
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
  (when (= id "foo")
    {:id "foo"
     :pass (hashers/encrypt "bar")}))

(defn register-request [id pass pass-confirm]
  (-> (request :post "/register")
      (content-type "application/json")
      (body (generate-string {:id id
                              :pass pass
                              :pass-confirm pass-confirm}))))

(defn mock-create-org [{:keys [id pass pass-confirm ]}]
  (when (and (= id "foo")
             (= pass "password1")
             (= pass-confirm "password1"))
    {:id "foo"
     :pass (hashers/encrypt "bar")}))

(defn parse-response [body]
  (-> body slurp (parse-string true)))

(deftest test-auth
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response)))))

  ;; registration
  (testing "registration success"
    (with-redefs [fcc-tracker.db.core/create-org! mock-create-org]
      (let [{:keys [body status]}
            ((app) (register-request "foo" "password1" "password1"))]
        (is
         (= 200 status))
        (is
         (= {:result "ok"}
            (parse-response body))))))

  (testing "registration duplicate user"
    (with-redefs [fcc-tracker.db.core/create-org! mock-create-org]
      (let [{:keys [body status]}
            ((app) (register-request "foo" "bar" "bar"))]
        (is
         (= 412 status))
        (is
         (= {:result "error"
             :message {:pass "password must contain at least 8 characters"}}
            (parse-response body))))))

  ;; login
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
