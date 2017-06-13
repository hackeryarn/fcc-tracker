(ns fcc-tracker.app
  (:require [fcc-tracker.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
