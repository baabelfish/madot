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

(defn- look [[x y]]
  (and (not (contains? @nonpassable [x y]))
       (not (contains? @wormblocks [x y]))
       (on-grid? [x y] [grid-x grid-y])))

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
  ([func for-all]
  (doseq [ai (vals @ai-list)]
    (when (or for-all @(:is-alive ai))
      (ai-func ai func))))
  ([func] (for-ai func false)))

(defn- kill-ai
  [ai]
  (reset! (:is-alive ai) false))

(defn- check-collisions
  ;; FIXME: Use wormblocks and nonpassable
  "Check if collisions happen between the worms and kill them."
  []
  (doseq [ai (vals @ai-list)
          aic (vals @ai-list)]
    (when (and @(:is-alive ai) @(:is-alive ai))
      (let [aib @(:blocks ai)
            aicb @(:blocks aic)]
        (if (and (not= ai aic) (= (first aib) (first aicb)))
          (do (kill-ai ai) (kill-ai aic))
          (when (or (in? (rest aicb) (first aib))
                    (not (on-grid? (first aib) [grid-x grid-y])))
            (kill-ai ai)))))))

(defn- update-food
  "Updates food in game"
  []
  (for-ai (fn [ai as cs head blocks exec]
            (when (contains? @food head)
              (swap! as inc)
              (swap! food #(disj % head)))) false)
  (when (zero? (mod @round-number @food-density))
    (swap! food #(conj % (random-point grid-x grid-y)))))

(defn- update-collisionmap
  []
  (reset! wormblocks #{})
  (for-ai (fn [ai as cs head blocks exec]
            (doseq [block @blocks]
              (swap! wormblocks #(conj % block)))) true))

(defn- update-ai
  []
  (for-ai (fn [ai as cs head blocks exec]
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
                              :look look})]
              (if (contains? (set (keys directions)) move)
                (let [newpos (vecoper head (move directions) +)]
                  (swap! blocks #(conj % newpos))
                  (when (>= cs @as)
                    (swap! blocks butlast)))
                (kill-ai ai)))))
  (update-collisionmap)
  (check-collisions))

(defn- game-ended?
  "Checks if game has ended."
  []
  (every? #(false? @(:is-alive %)) (vals @ai-list)))

(defn- game
  "Basic game-loop with drawing and updating."
  []
  (when (not (game-ended?))
    (swap! round-number inc)
    (update-food))
  (draw-game grid-x grid-y @ai-list @round-number @food)
  (update-ai))

(defn -main
  []
  (get-sketch grid-x grid-y game)
  (reset-game)
  (println "Matomähinä käynnis!"))
