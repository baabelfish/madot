(ns ai.wizard)

(defn- exec [data]
  :up)

(def init {:exec exec
           :name "Wizard"
           :color {:r 255
                   :g 0
                   :b 0}})
