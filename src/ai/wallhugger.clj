(ns ai.wallhugger
  (:require [madot.helpers :refer :all]))

(defn- sumvec
  [[x y] dir]
  (vecoper [x y] (dir directions) +))

(defn- exec
  [data]
  (let [size (:size data)
        head (:head data)
        dimensions (:dimensions data)
        blocks (:blocks data)
        food-density (:food-density data)
        round-number (:round-number data)
        nonpassable (:nonpassable data)
        food (:food data)
        wormblocks (:wormblocks data)
        look (:look data)

        up (sumvec head :up)
        down (sumvec head :down)
        left (sumvec head :left)
        right (sumvec head :right)]
    (if (look up) :up
      (if (look left) :left
        (if (look down) :down
          :right)))))

(def init {:exec exec
           :name "Wallhugger"
           :color {:r 0
                   :g 255
                   :b 0}})
