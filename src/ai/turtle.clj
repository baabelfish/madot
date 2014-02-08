(ns ai.turtle)

;; AI :exec method should return the new direction it's
;; going at
(defn- exec []
  :down)

(def init {:exec exec
           :name "Turtlebot"
           :color {:r 0
                   :g 255
                   :b 0}})
