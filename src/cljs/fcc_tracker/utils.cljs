(ns fcc-tracker.utils
  (:require [secretary.core :as s]))

(defn redirect!
  "Set the location hash of a js/window object."
  [v]
  (aset (.-location js/window) "hash" v)
  (s/dispatch! v))
