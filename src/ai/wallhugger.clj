(ns ai.wallhugger
  (:require [ai.common :refer :all]))

(defn- debug
  []
  (println (get-round-number)
           (get-food)
           (get-dimensions)
           (get-food-density)
           (get-nonpassable)
           (get-heads)))

;; AI :exec method should return the new direction it's
;; going at
(defn- exec [allowed-size current-size head blocks]
  ;; (println allowed-size current-size head blocks)
  ;; (debug)
  (let [up (sum-kw head :up)
        down (sum-kw head :down)
        left (sum-kw head :left)
        right (sum-kw head :right)]
    (if (is-ok up)
      :up
      (if (is-ok right)
        :right
        (if (is-ok down)
          :down
          :left)))))

(def init {:exec exec
           :name "Wallhugger"
           :color {:r 0
                   :g 255
                   :b 0}})
