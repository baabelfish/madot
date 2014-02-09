(ns ai.common
  (:require [madot.core :as madot]))

(defn get-dimensions
  "Returns the size of the grid."
  []
  [madot/grid-x madot/grid-y])

(defn get-food-density
  "Returns the current density of food dropouts."
  []
  @madot/food-density)

(defn get-food
  "Returns a set of all food positions on the grid."
  []
  @madot/food)

(defn get-nonpassable
  "Returns a set of positions of non-passable terrain."
  []
  @madot/nonpassable)

(defn get-round-number
  "Returns the current round-number."
  []
  @madot/round-number)

(defn get-heads
  "Returns a headset (:D) of all alive worms."
  []
  (set (map #(madot/ai-func % (fn [ai as cs head blocks exec] head))
            (vals ai-list))))

(defn is-passabele
  ;; TODO
  "Returns true if the coordinate is free."
  [x y]
  true)
