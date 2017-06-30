(ns fcc-tracker.test.db.core
  (:require [fcc-tracker.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [fcc-tracker.config :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'fcc-tracker.config/env
     #'fcc-tracker.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-users
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-org!
              t-conn
              {:id         "1"
               :pass       "pass"})))
    (is (= {:id         "1"
            :last_login nil
            :org_name nil
            :is_active nil
            :pass       "pass"}
           (db/get-org t-conn {:id "1"})))
    (is (= 1 (db/delete-org!
              t-conn
              {:id "1"})))
    (is (= nil (db/get-org t-conn {:id "1"})))))

(deftest test-members
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-member!
              t-conn
              {:organization "1"
               :name "test"
               :fcc_username "test"})))
    (is (= [{:name "test"
             :fcc_username "test"}]
           (db/list-members
            t-conn
            {:organization "1"})))
    (is (= 1 (db/delete-member!
              t-conn
              {:organization "1"
               :fcc_username "test"})))
    (is (= [] (db/list-members
               t-conn
               {:organization "1"})))))
