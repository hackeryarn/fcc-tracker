(ns fcc-tracker.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [fcc-tracker.core-test]))

(doo-tests 'fcc-tracker.core-test)

