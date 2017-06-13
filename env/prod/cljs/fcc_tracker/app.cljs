(ns fcc-tracker.app
  (:require [fcc-tracker.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
