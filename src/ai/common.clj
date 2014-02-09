(ns ai.common
  (:require [madot.core :as madot]
            [madot.helpers :as helpers]))

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

(defn on-grid?
  [[x y]]
  (helpers/on-grid? [x y] [madot/grid-x madot/grid-y]))

(defn sum-kw
  [[x y] kw]
  (helpers/vecoper [x y] (kw madot/directions) +))

(defn is-ok
  "Returns true if the coordinate is free."
  [[x y]]
  (and (not (contains? @madot/nonpassable [x y]))
       (not (contains? @madot/wormblocks [x y]))
       (on-grid? [x y])))
