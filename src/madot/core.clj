(ns madot.core
  (:require [quil.core :refer :all]
            [ai.index :as index]
            [madot.helpers :refer :all]
            [madot.drawing :refer :all]))

(def grid-x 40)
(def grid-y 40)
(def starting-length 20)

(def food-density (atom 1))
(def round-number (atom 0))
(def nonpassable (atom #{}))
(def food (atom #{}))
(def wormblocks (atom #{}))

(def ai-list (atom {}))

(defn- reset-game
  []
  (reset! food-density 1)
  (reset! round-number 0)
  (reset! nonpassable #{})
  (reset! food #{})
  (reset! wormblocks #{})
  (reset! ai-list (zipmap
                    (map inc (range))
                    (->> (index/ai-index)
                         (map #(merge % {:blocks (atom (conj '() (random-point grid-x grid-y)))
                                         :size (atom starting-length)
                                         :is-alive (atom true)}))))))

(defn- ai-func
  [ai func]
  (let [blocks (:blocks ai)
        exec (:exec ai)
        head (first @blocks)
        allowed-size (:size ai)
        current-size (count @blocks)]
      (func ai allowed-size current-size head blocks exec)))

(defn for-ai
  "Runs a function to all AIs. If for-all is true, it will run the function for dead worms too."
  ([ailist func for-all]
  (doseq [ai (vals @ailist)]
    (when (or for-all @(:is-alive ai))
      (ai-func ai func))))
  ([ailist func] (for-ai ailist func false)))

(defn- kill-ai
  [ai]
  (reset! (:is-alive ai) false))

(defn- collides?
  [grid ai]
  (let [head (first @(:blocks ai))]
    (and (true? @(:is-alive ai))
         (or (not (on-grid? head [grid-x grid-y])) (contains? grid head)))))

(defn- check-collisions
  "Check if collisions happen between the worms and kill them."
  []
  (doseq [ai (filter (partial collides? (clojure.set/union @nonpassable @wormblocks)) (vals @ai-list))]
    (kill-ai ai)))

(defn- update-food
  "Updates food in game"
  []
  (for-ai ai-list (fn [ai as cs head blocks exec]
                    (when (contains? @food head)
                      (swap! as inc)
                      (swap! food #(disj % head)))))
  (when (zero? (mod @round-number @food-density))
    (swap! food #(conj % (random-point grid-x grid-y)))))

(defn- update-collisionmap
  []
  (reset! wormblocks #{})
  (for-ai ai-list (fn [ai as cs head blocks exec]
                    (doseq [block (rest @blocks)]
                      (swap! wormblocks #(conj % block)))) true))

(defn- update-ai
  []
  (for-ai ai-list (fn [ai as cs head blocks exec]
                    (let [on-grid-helper (fn [[x y]]
                                           (on-grid? [x y] [grid-x grid-y]))
                          move (exec {:size @as
                                      :head head
                                      :dimensions [grid-x grid-y]
                                      :blocks @blocks
                                      :food-density @food-density
                                      :round-number @round-number
                                      :nonpassable @nonpassable
                                      :food @food
                                      :wormblocks @wormblocks
                                      :look (fn [[x y]]
                                              (and (not (contains? @nonpassable [x y]))
                                                   (not (contains? @wormblocks [x y]))
                                                   (on-grid? [x y] [grid-x grid-y])))})]
                      (if (contains? (set (keys directions)) move)
                        (let [newpos (vecoper head (move directions) +)]
                          (when (>= cs @as)
                            (swap! blocks butlast))
                          (swap! blocks #(conj % newpos)))
                        (kill-ai ai))))))

(defn- game-ended?
  "Checks if game has ended."
  [ailist]
  (every? #(false? @(:is-alive %)) (vals ailist)))

(defn- game
  "Basic game-loop with drawing and updating."
  []
  (when (not (game-ended? @ai-list))
    (swap! round-number inc)
    (update-food))
  (draw-game grid-x grid-y @ai-list @round-number @food)
  (update-ai)
  (update-collisionmap)
  (check-collisions))

(defn -main
  []
  (get-sketch grid-x grid-y game)
  (reset-game)
  (println "Matomähinä käynnis!"))
